package data.repository.util

import app.softwork.uuid.toUuid
import com.google.gson.JsonObject
import data.local.database.CardDb
import domain.model.Card
import domain.model.MtgColor
import domain.model.MtgFormat


fun CardDb.toDomain(): Card {
    return Card(
        id = id,
        name = name,
        colorIdentity = parseColorsList(colors),
        legalities = legalities
            .mapKeys { (key, _) -> parseMtgFormatString(key) },
        imageSource = imageSource
    )
}

fun JsonObject.toDatabase(): CardDb {
    val id = this.get("oracle_id").asString.toUuid()
    val name = this.get("name").asString
    val colorIdentity =
        this.getAsJsonArray("color_identity")
        .map { it.asString }

    val formatsList = listOf(
        "standard", "future", "historic", "timeless", "gladiator",
        "pioneer", "explorer", "modern", "legacy", "pauper",
        "vintage", "penny", "commander", "oathbreaker", "standardbrawl",
        "brawl", "alchemy", "paupercommander", "duel",
        "oldschool", "premodern", "predh"
    )

    val legalities = formatsList.associateWith { format ->
        get("legalities").asJsonObject
        .get(format).asString.let { parseLegalityString(it) }
    }

    val layout = get("layout").asString
    val imageSource = if (layout.equals("transform")
        || layout.equals("modal_dfc") || layout.equals("reversible_card")
        || layout.equals("art_series") || layout.equals("double_faced_token")) {
            this.get("card_faces").asJsonArray
            .get(0).asJsonObject // we're interested in front face only.
            .get("image_uris").asJsonObject
            .get("png").asString
    } else {
            this.get("image_uris").asJsonObject
            .get("png").asString
    }

    return CardDb(id, name, colorIdentity, legalities, imageSource)
}

private fun parseColor(colorStr: String): MtgColor {
    return when (colorStr) {
        "W" -> MtgColor.WHITE
        "R" -> MtgColor.RED
        "U" -> MtgColor.BLUE
        "B" -> MtgColor.BLACK
        "G" -> MtgColor.GREEN
        "C" -> MtgColor.COLORLESS
        else -> throw IllegalStateException("Color $colorStr is invalid")
    }

}

private fun parseMtgFormatString(mtgFormatStr: String): MtgFormat {
    return when(mtgFormatStr) {
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
}

private fun parseLegalityString(legalityStr: String): Boolean {
    return when (legalityStr) {
        "legal" -> true
        "not_legal" -> false
        "banned" -> false
        "restricted" -> true // TODO: This one technically doesn't work like that
        else -> throw IllegalStateException("Invalid format legality $legalityStr")
    }
}

private fun parseColorsList(colors: List<String>?): Set<MtgColor> {
    return colors?.map { s -> parseColor(s) } ?.toSet() ?: emptySet()
}
