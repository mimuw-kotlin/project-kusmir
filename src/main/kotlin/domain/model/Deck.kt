package domain.model

class Deck(
    val id: Long,
    val name: String,
    val mainDeck: List<Card>,
    val sideboard: List<Card>
) {
    enum class ListType { MainDeck, Sideboard }
}