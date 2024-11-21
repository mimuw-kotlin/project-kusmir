package domain.model

import kotlin.uuid.Uuid

data class Card (
    val id: Uuid,
    val name: String,
    val colorIdentity: Set<MtgColor>,
    val legalities: Map<MtgFormat, Boolean>,
    val type: String,
    val imageSource: String,
)