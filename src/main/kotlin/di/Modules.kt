package di

import DatabaseDriverFactory
import app.softwork.uuid.sqldelight.UuidByteArrayAdapter
import data.local.database.CardDb
import data.local.database.Card_deck
import data.local.database.Database
import data.network.ScryfallApi
import data.network.ScryfallApiImpl
import data.repository.CardsRepositoryImpl
import data.repository.DeckRepositoryImpl
import data.source.CardsDao
import data.source.CardsDaoImpl
import data.source.DecksDao
import data.source.DecksDaoImpl
import data.sqldelight.CustomAdaptersImpl
import domain.repository.CardsRepository
import domain.repository.DecksRepository
import domain.usecases.cards.CardsUseCases
import domain.usecases.cards.GetCardByNameUseCase
import domain.usecases.cards.GetCardsSearchResultsUseCase
import domain.usecases.deck.DecksUseCases
import domain.usecases.deck.DeleteDeckUseCase
import domain.usecases.deck.FetchAndUpdateCardsUseCase
import domain.usecases.deck.GetAllDecksUseCase
import domain.usecases.deck.GetDeckUseCase
import domain.usecases.deck.ImportDeckUseCase
import domain.usecases.deck.SaveDeckUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import presentation.decks.DecksViewModel
import presentation.editdeck.viewmodel.EditDeckViewModel

val module =
    module {
        single<CoroutineDispatcher> { Dispatchers.Default }
        single {
            Database(
                driver = DatabaseDriverFactory().createDriver(),
                cardDbAdapter =
                    CardDb.Adapter(
                        idAdapter = UuidByteArrayAdapter,
                        colorsAdapter = CustomAdaptersImpl().listStringAdapter(),
                        legalitiesAdapter = CustomAdaptersImpl().legalitiesAdapter(),
                    ),
                card_deckAdapter =
                    Card_deck.Adapter(
                        cardIdAdapter = UuidByteArrayAdapter,
                    ),
            )
        }

        singleOf(::CardsDaoImpl).bind<CardsDao>()

        singleOf(::DecksDaoImpl).bind<DecksDao>()

        singleOf(::ScryfallApiImpl).bind<ScryfallApi>()
        singleOf(::CardsRepositoryImpl).bind<CardsRepository>()

        singleOf(::DeckRepositoryImpl).bind<DecksRepository>()

        singleOf(::DecksUseCases)
        singleOf(::GetDeckUseCase)
        singleOf(::GetAllDecksUseCase)
        singleOf(::SaveDeckUseCase)
        singleOf(::FetchAndUpdateCardsUseCase)
        singleOf(::ImportDeckUseCase)
        singleOf(::DeleteDeckUseCase)

        singleOf(::CardsUseCases)
        singleOf(::GetCardsSearchResultsUseCase)
        singleOf(::GetCardByNameUseCase)

        viewModel { (deckId: Long) -> EditDeckViewModel(get(), get(), deckId) }

        viewModelOf(::DecksViewModel)
    }

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(module)
    }
}
