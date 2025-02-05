package di

import MatchReportDaoImpl
import app.softwork.uuid.sqldelight.UuidByteArrayAdapter
import data.local.database.CardDb
import data.local.database.Card_deck
import data.local.database.Database
import data.local.database.GameReportDb
import data.network.ScryfallApi
import data.network.ScryfallApiImpl
import data.repository.CardsRepositoryImpl
import data.repository.DeckRepositoryImpl
import data.repository.MatchReportRepositoryImpl
import data.source.CardsDao
import data.source.CardsDaoImpl
import data.source.DecksDao
import data.source.DecksDaoImpl
import data.source.MatchReportDao
import data.sqldelight.CustomAdaptersImpl
import domain.repository.CardsRepository
import domain.repository.DecksRepository
import domain.repository.MatchReportRepository
import domain.usecases.cards.CardsUseCases
import domain.usecases.cards.FetchCardsDataUseCase
import domain.usecases.cards.GetCardByNameUseCase
import domain.usecases.cards.GetCardsSearchResultsUseCase
import domain.usecases.cards.GetLastFetchDateTimeUseCase
import domain.usecases.deck.DecksUseCases
import domain.usecases.deck.DeleteDeckUseCase
import domain.usecases.deck.GetAllDecksUseCase
import domain.usecases.deck.GetDeckUseCase
import domain.usecases.deck.GetMatchingDeckUseCase
import domain.usecases.deck.GetSideboardingDataUseCase
import domain.usecases.deck.ImportDeckUseCase
import domain.usecases.deck.SaveDeckUseCase
import domain.usecases.statistics.GetAllMatchReportsUseCase
import domain.usecases.statistics.SaveMatchReportUseCase
import domain.usecases.statistics.StatisticsUseCases
import domain.usecases.tracking.ParseMatchLogUseCase
import domain.usecases.tracking.ReadLogUseCase
import domain.usecases.tracking.TrackingUseCases
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
import presentation.decktracker.DeckTrackerViewModel
import presentation.editdeck.EditDeckViewModel
import presentation.home.HomeViewModel
import presentation.statistics.StatisticsViewModel
import util.DatabaseDriverFactory

val module =
    module {
        // Data layer
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
                gameReportDbAdapter =
                    GameReportDb.Adapter(
                        opponentRevealedCardsIdsAdapter = CustomAdaptersImpl().listUuidAdapter(),
                        playerDrawnCardsIdsAdapter = CustomAdaptersImpl().listUuidAdapter(),
                        cardsSidedInIdsAdapter = CustomAdaptersImpl().listUuidAdapter(),
                        cardsSidedOutIdsAdapter = CustomAdaptersImpl().listUuidAdapter(),
                    ),
            )
        }

        singleOf(::CardsDaoImpl).bind<CardsDao>()

        singleOf(::DecksDaoImpl).bind<DecksDao>()

        singleOf(::MatchReportDaoImpl).bind<MatchReportDao>()

        singleOf(::ScryfallApiImpl).bind<ScryfallApi>()
        singleOf(::CardsRepositoryImpl).bind<CardsRepository>()

        singleOf(::DeckRepositoryImpl).bind<DecksRepository>()

        singleOf(::MatchReportRepositoryImpl).bind<MatchReportRepository>()

        // Use cases
        singleOf(::DecksUseCases)
        singleOf(::GetDeckUseCase)
        singleOf(::GetAllDecksUseCase)
        singleOf(::SaveDeckUseCase)
        singleOf(::ImportDeckUseCase)
        singleOf(::DeleteDeckUseCase)

        singleOf(::CardsUseCases)
        singleOf(::GetCardsSearchResultsUseCase)
        singleOf(::GetCardByNameUseCase)
        singleOf(::FetchCardsDataUseCase)
        singleOf(::GetLastFetchDateTimeUseCase)
        singleOf(::GetMatchingDeckUseCase)
        singleOf(::GetSideboardingDataUseCase)

        singleOf(::TrackingUseCases)
        singleOf(::ReadLogUseCase)
        singleOf(::ParseMatchLogUseCase)

        singleOf(::StatisticsUseCases)
        singleOf(::SaveMatchReportUseCase)
        singleOf(::GetAllMatchReportsUseCase)

        // View models
        viewModel { (deckId: Long) -> EditDeckViewModel(get(), get(), deckId) }

        viewModelOf(::DecksViewModel)

        viewModelOf(::HomeViewModel)

        viewModelOf(::StatisticsViewModel)

        // Yes, I know. It's not really a viewmodel.
        singleOf(::DeckTrackerViewModel)
    }

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(module)
    }
}
