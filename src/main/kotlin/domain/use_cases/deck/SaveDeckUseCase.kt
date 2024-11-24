package domain.use_cases.deck

import domain.model.Deck
import domain.repository.DecksRepository

class SaveDeckUseCase(
    private val repository: DecksRepository
) {
    suspend operator fun invoke(deck: Deck): Long {
        if (deck.id == -1L) {
            return repository.createDeck(deck.name, deck.imageSource, deck.mainDeck, deck.sideboard)
        } else {
            repository.saveDeck(deck)
            return deck.id
        }
    }
}