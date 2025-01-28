package domain.usecases.deck

import domain.model.Deck
import domain.repository.DecksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn

class GetMatchingDeckUseCase(
    private val decksRepository: DecksRepository
) {
    suspend operator fun invoke(deck: Deck): Deck {
        println("launched GetMatchingDeckUseCase")
        val allDecks = decksRepository.fetchAllDecks()
            .stateIn(
                scope = CoroutineScope(Dispatchers.IO),
            )
            .value

        println("allDecks: $allDecks")
        val matchingDeck = allDecks.find {
            it.mainDeck == deck.mainDeck && it.sideboard == deck.sideboard
        }

        if (matchingDeck != null) {
            println("Matching deck found: $matchingDeck")
            return matchingDeck
        }
        else {
            val id = decksRepository.createDeck(
                deck.name,
                deck.imageSource,
                deck.mainDeck,
                deck.sideboard,
            )

            println("Created deck $id")
            return decksRepository.fetchDeckById(id)!!
        }
    }
}