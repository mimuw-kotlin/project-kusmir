package data.repository.util

import app.softwork.uuid.toUuid
import com.google.gson.JsonObject
import data.local.database.CardDb
import domain.model.Card
import domain.model.GameResult
import domain.model.MatchReport
import domain.model.MtgColor
import domain.model.MtgFormat
import kotlin.uuid.Uuid

fun CardDb.toDomain(): Card =
    Card(
        id = id,
        mtgoId = mtgoId,
        name = name,
        colorIdentity = parseColorsList(colors),
        legalities =
            legalities
                .mapKeys { (key, _) -> key.toMtgFormat() },
        type = type,
        imageSource = imageSource,
        cropImageSource = cropImageSource,
    )

fun JsonObject.toDatabase(): CardDb? {
    val id = this.get("oracle_id").asString.toUuid()

    // Some cards are not present in mtgo, but appear in paper game,
    // we simply ignore them.
    val mtgoId = this.get("mtgo_id")?.asLong ?: return null

    val name = this.get("name").asString
    val colorIdentity =
        this
            .getAsJsonArray("color_identity")
            .map { it.asString }

    val formatsList =
        listOf(
            "standard",
            "future",
            "historic",
            "timeless",
            "gladiator",
            "pioneer",
            "explorer",
            "modern",
            "legacy",
            "pauper",
            "vintage",
            "penny",
            "commander",
            "oathbreaker",
            "standardbrawl",
            "brawl",
            "alchemy",
            "paupercommander",
            "duel",
            "oldschool",
            "premodern",
            "predh",
        )

    val legalities =
        formatsList.associateWith { format ->
            get("legalities")
                .asJsonObject
                .get(format)
                .asString
                .let { parseLegalityString(it) }
        }

    val type = get("type_line").asString

    val layout = get("layout").asString
    val imageUris =
        if (layout.equals("transform") ||
            layout.equals("modal_dfc") ||
            layout.equals("reversible_card") ||
            layout.equals("art_series") ||
            layout.equals("double_faced_token")
        ) {
            this
                .get("card_faces")
                .asJsonArray
                .get(0)
                .asJsonObject // we're interested in front face only.
                .get("image_uris")
                .asJsonObject
        } else {
            this.get("image_uris").asJsonObject
        }

    val imageSource = imageUris.get("png").asString
    val cropImageSource = imageUris.get("art_crop").asString

    return CardDb(id, mtgoId, name, colorIdentity, legalities, type, imageSource, cropImageSource)
}

fun String.toMtgColor(): MtgColor =
    when (this) {
        "W" -> MtgColor.WHITE
        "R" -> MtgColor.RED
        "U" -> MtgColor.BLUE
        "B" -> MtgColor.BLACK
        "G" -> MtgColor.GREEN
        "C" -> MtgColor.COLORLESS
        else -> throw IllegalStateException("Color $this is invalid")
    }

fun String.toMtgFormat(): MtgFormat =
    when (this) {
        "standard" -> MtgFormat.STANDARD
        "future" -> MtgFormat.FUTURE
        "historic" -> MtgFormat.HISTORIC
        "timeless" -> MtgFormat.TIMELESS
        "gladiator" -> MtgFormat.GLADIATOR
        "pioneer" -> MtgFormat.PIONEER
        "explorer" -> MtgFormat.EXPLORER
        "modern" -> MtgFormat.MODERN
        "legacy" -> MtgFormat.LEGACY
        "pauper" -> MtgFormat.PAUPER
        "vintage" -> MtgFormat.VINTAGE
        "penny" -> MtgFormat.PENNY
        "commander" -> MtgFormat.COMMANDER
        "oathbreaker" -> MtgFormat.OATHBREAKER
        "standardbrawl" -> MtgFormat.STANDARDBRAWL
        "brawl" -> MtgFormat.BRAWL
        "alchemy" -> MtgFormat.ALCHEMY
        "paupercommander" -> MtgFormat.PAUPERCOMMANDER
        "duel" -> MtgFormat.DUEL
        "oldschool" -> MtgFormat.OLDSCHOOL
        "premodern" -> MtgFormat.PREMODERN
        "predh" -> MtgFormat.PREDH
        else -> MtgFormat.UNKNOWN
    }


fun parseLegalityString(legalityStr: String): Boolean =
    when (legalityStr) {
        "legal" -> true
        "not_legal" -> false
        "banned" -> false
        "restricted" -> true // TODO: This one technically doesn't work like that
        else -> throw IllegalStateException("Invalid format legality $legalityStr")
    }

fun parseColorsList(colors: List<String>?): Set<MtgColor> = colors?.map { it.toMtgColor() }?.toSet() ?: emptySet()

fun MatchReport.Structure.toDatabase(): Long =
    when(this) {
        MatchReport.Structure.Bo1 -> 1
        MatchReport.Structure.Bo3 -> 3
    }

fun GameResult.toDatabase(): Long =
    when(this) {
        GameResult.WON -> 1
        GameResult.DRAW -> 0
        GameResult.LOST -> -1
    }

fun MtgFormat.toDatabase(): String =
    this.toString().lowercase()

fun List<Card>.toDatabase(): List<Uuid> = this.map { it.id }