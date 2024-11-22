package presentation.edit_deck.util

import domain.model.DeckList
import domain.model.MutableDeckList

class FormattedDeckList(
    private val list: DeckList,
) {
    data class Group(
        val groupName: String,
        val list: DeckList,
    )

    companion object {
        private val groupTypes = mapOf(
            "Creatures" to "Creature",
            "Planeswalkers" to "Planeswalker",
            "Instants" to "Instant",
            "Sorceries" to "Sorcery",
            "Enchantments" to "Enchantment",
            "Artifacts" to "Artifact",
            "Battles" to "Battle",
            "Lands" to "Land",
        )
    }

    val groups: List<Group> by lazy {
        val listLeft = list.toMutableDeckList()
        val groups: MutableList<Group> = mutableListOf()

        groupTypes.forEach { (groupName, groupType) ->
            val currentList = MutableDeckList()

            val iterator = listLeft.iterator()
            while (iterator.hasNext()) {
                val (card, count) = iterator.next()

                if (card.type.contains(groupType)) {
                    currentList.addCard(card, count)
                    iterator.remove()
                }
            }

            if (currentList.isNotEmpty()) {
                groups.add(Group(groupName, currentList.toDeckList()))
            }
        }

        groups.toList()
    }
}

fun DeckList.groups(): List<FormattedDeckList.Group> =
    FormattedDeckList(this).groups