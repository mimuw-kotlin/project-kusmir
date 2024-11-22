package domain.model

class MutableDeckList(
    private val cardMap: MutableMap<Card, Int> = mutableMapOf(),
) : MutableMap<Card, Int> {
    fun toDeckList(): DeckList = DeckList(cardMap.toMap())

    fun addCard(card: Card, count: Int = 1) {
        cardMap[card] = cardMap.getOrDefault(card, 0) + count
    }

    fun removeCard(card: Card, count: Int = 1) {
        val currentCount = cardMap[card] ?: return
        if (currentCount > count) {
            cardMap[card] = currentCount - count
        } else {
            cardMap.remove(card)
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<Card, Int>>
        get() = cardMap.entries
    override val keys: MutableSet<Card>
        get() = cardMap.keys
    override val size: Int
        get() = cardMap.size
    override val values: MutableCollection<Int>
        get() = cardMap.values

    override fun clear() = cardMap.clear()
    override fun isEmpty(): Boolean = cardMap.isEmpty()
    override fun putAll(from: Map<out Card, Int>) = cardMap.putAll(from)
    override fun put(key: Card, value: Int): Int? = cardMap.put(key, value)
    override fun get(key: Card): Int? = cardMap[key]
    override fun containsValue(value: Int): Boolean = cardMap.containsValue(value)
    override fun containsKey(key: Card): Boolean = cardMap.containsKey(key)
    override fun remove(key: Card): Int? = cardMap.remove(key)
}

data class DeckList(
    private val cardMap: Map<Card, Int> = emptyMap()
): Map<Card, Int> {
    constructor(cards: List<Card>) : this(
        cards.groupingBy { it }.eachCount()
    )

    fun toMutableDeckList(): MutableDeckList =
        MutableDeckList(cardMap.toMutableMap())

    fun toCardsList(): List<Card> =
        cardMap.flatMap { (key, count) -> List(count) { key } }.also{
            println(it.map{card -> card.name})
        }

    override val entries: Set<Map.Entry<Card, Int>>
        get() = cardMap.entries
    override val keys: Set<Card>
        get() = cardMap.keys
    override val size: Int
        get() = cardMap.size
    override val values: Collection<Int>
        get() = cardMap.values

    override fun isEmpty(): Boolean = cardMap.isEmpty()
    override fun get(key: Card): Int? = cardMap[key]
    override fun containsValue(value: Int): Boolean = cardMap.containsValue(value)
    override fun containsKey(key: Card): Boolean = cardMap.containsKey(key)
}

data class Deck(
    val id: Long = -1,
    val name: String = "",
    val mainDeck: DeckList,
    val sideboard: DeckList,
) {
    enum class ListType { MainDeck, Sideboard }
}