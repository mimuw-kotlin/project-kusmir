package domain.usecases.deck

import domain.model.Card
import domain.model.Deck

class GetSideboardingDataUseCase {
    operator fun invoke(
        registeredDeck: Deck,
        currentDeck: Deck,
    ): Pair<List<Card>, List<Card>> {
        val originalList = registeredDeck.mainDeck
        val newList = currentDeck.mainDeck

        val added = newList.flatMap { (card, newCount) ->
            val previousCount = originalList[card] ?: 0
            List(newCount - previousCount) { card }
                .takeIf { newCount > previousCount }
                ?: emptyList()
        }

        val removed = originalList.flatMap { (card, originalCount) ->
            val newCount = newList[card] ?: 0
            List(originalCount - newCount) { card }
                .takeIf { originalCount > newCount }
                ?: emptyList()
        }

        return added to removed
    }
}