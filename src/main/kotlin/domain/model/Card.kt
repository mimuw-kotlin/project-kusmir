package domain.model

import kotlin.uuid.Uuid

data class Card (
    val id: Uuid,
    val name: String,
    val colorIdentity: Set<MtgColor>,
    val legalities: Map<MtgFormat, Boolean>,
    val type: String,
    val imageSource: String,
    val cropImageSource: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}