package di

import DatabaseDriverFactory
import app.softwork.uuid.sqldelight.UuidByteArrayAdapter
import data.data_source.*
import data.local.database.CardDb
import data.local.database.Database
import data.repository.CardsRepositoryImpl
import data.sqldelight.CustomAdaptersImpl
import domain.repository.CardsRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import data.local.database.Card_deck
import data.network.ScryfallApi
import org.koin.core.module.dsl.singleOf
import data.network.ScryfallApiImpl
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import presentation.edit_deck.viewmodel.EditDeckViewModel
import data.repository.DeckRepositoryImpl
import domain.repository.DecksRepository
import domain.use_cases.deck.DecksUseCases
import domain.use_cases.deck.GetDeckUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.compose.viewmodel.dsl.viewModelOf
import presentation.decks.DecksViewModel
import domain.use_cases.deck.GetAllDecksUseCase
import domain.use_cases.deck.FetchAndUpdateCardsUseCase
import domain.use_cases.deck.SaveDeckUseCase
import domain.use_cases.deck.ImportDeckUseCase
import domain.use_cases.deck.DeleteDeckUseCase
import domain.use_cases.cards.CardsUseCases
import domain.use_cases.cards.GetCardsSearchResultsUseCase
import domain.use_cases.cards.GetCardByNameUseCase

val module = module {
    single<CoroutineDispatcher> {Dispatchers.Default}
    single {
        Database(
            driver = DatabaseDriverFactory().createDriver(),
            cardDbAdapter = CardDb.Adapter(
                idAdapter = UuidByteArrayAdapter,
                colorsAdapter = CustomAdaptersImpl().ListStringAdapter(),
                legalitiesAdapter = CustomAdaptersImpl().legalitiesAdapter()
            ),
            card_deckAdapter = Card_deck.Adapter(
                cardIdAdapter = UuidByteArrayAdapter
            )
        )
    }

    singleOf(::CardsDaoImpl).bind<CardsDao>()

    singleOf(::DeckDaoImpl).bind<DecksDao>()

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