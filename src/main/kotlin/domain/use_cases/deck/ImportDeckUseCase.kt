package domain.use_cases.deck

import co.touchlab.stately.collections.ConcurrentMutableMap
import domain.model.Card
import domain.model.Deck
import domain.model.DeckList
import domain.repository.CardsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class ImportDeckUseCase(
    private val cardsRepository: CardsRepository
) {
    suspend operator fun invoke(input: String): Deck {
        val mainDeck: ConcurrentMutableMap<Card, Int> = ConcurrentMutableMap()
        val sideboard: ConcurrentMutableMap<Card, Int> = ConcurrentMutableMap()

        var currentDeck: ConcurrentMutableMap<Card, Int>? = null

        val jobs: MutableList<Job?> = mutableListOf()
        for (line in input.lines().map {it.trim()}) {
            if (line.isBlank()) continue

            if (line == "Deck") {
                currentDeck = mainDeck
                continue
            }

            if (line == "Sideboard") {
                currentDeck = sideboard
                continue
            }

            if (currentDeck == null) throw IllegalArgumentException("Invalid format")

            val tokens = line.split(' ').filter { it.isNotBlank() }

            val count = tokens[0].toIntOrNull() ?: throw IllegalArgumentException(
                "Invalid card count on line: $line"
            )
            val name = tokens.drop(1).joinToString(" ")
            val deckToModify = currentDeck

            val job = CoroutineScope(Dispatchers.IO).launch {
                val card = cardsRepository.getCardByName(name)
                card ?: throw IllegalArgumentException("Card $name not found. ")
                deckToModify[card] = count
            }

            jobs.add(job)
        }

        jobs.forEach { it?.join() }

        return Deck(
            mainDeck = DeckList(mainDeck),
            sideboard = DeckList(sideboard),
        )
    }
}