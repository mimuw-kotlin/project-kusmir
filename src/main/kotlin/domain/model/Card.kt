package domain.model

import kotlin.uuid.Uuid

class Card (
    val id: Uuid,
    val name: String,
    val colorIdentity: Set<MtgColor>,
    val legalities: Map<MtgFormat, Boolean>,
    val imageSource: String,
)