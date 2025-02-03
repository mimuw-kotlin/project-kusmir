package domain.model

import kotlin.uuid.Uuid

data class Card(
    val id: Uuid,
    val mtgoId: Long,
    val name: String,
    val colorIdentity: Set<MtgColor>,
    val legalities: Map<MtgFormat, Boolean>,
    val type: String,
    val imageSource: String,
    val cropImageSource: String,
) {
    // Same cards may have different images, that's why we want to compare only by id.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = name
}
