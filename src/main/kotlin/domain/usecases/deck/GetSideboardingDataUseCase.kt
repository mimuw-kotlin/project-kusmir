package domain.usecases.deck

import domain.model.Card
import domain.model.Deck
import domain.model.DeckList

class GetSideboardingDataUseCase {
    operator fun invoke(
        registeredDeck: Deck,
        currentSideboard: DeckList,
    ): Pair<List<Card>, List<Card>> {
        /**
         *  Returns list of cards that were added and removed from the maindeck
         *  to achieve current sideboard state.
         */

        val registeredSideboard = registeredDeck.sideboard
        println("Get sideboarding data")
        println("registered: $registeredSideboard")
        println("current: $currentSideboard")

        // Cards that are currently in sideboard, but were not there
        // in the original list.
        val removed =
            currentSideboard.flatMap { (card, newCount) ->
                val previousCount = registeredSideboard[card] ?: 0
                if (newCount > previousCount) {
                    List(newCount - previousCount) { card }
                } else {
                    emptyList()
                }
            }

        // Cards that are missing from the sideboard, but were registered.
        val added =
            registeredSideboard.flatMap { (card, previousCount) ->
                val newCount = currentSideboard[card] ?: 0
                if (previousCount > newCount) {
                    List(previousCount - newCount) { card }
                } else {
                    emptyList()
                }
            }

        println("added: $added")
        println("removed: $removed")
        return added to removed
    }
}
