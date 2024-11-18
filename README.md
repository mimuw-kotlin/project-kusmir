[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/M0kyOMLZ)
# MTGO Insight

## Authors
- Michał Kuśmirek (@Kusmir at github)

## Description
MTGO Insight jest narzędziem do gry karcianej [Magic: The Gathering Online](https://www.mtgo.com/en/mtgo), pozwalającym użytkownikowi
na śledzenie statystyk i swoich postępów związanych z grą.

Podobne narzędzie już istnieje ([MTGO-Tracker](https://cderickson.io/mtgo-tracker/)), ale posiada dość toporne UI i zasypuje użytkownika 
ogromną ilością danych i tabelek, często trudnych do zinterpretowania oczami z poziomu aplikacji. Duży przekrój kolekcjonowanych danych jest niewątpliwie
siłą tego projektu, bo umożliwia zaangażowanym użytkownikom łatwe eksportowanie do narzędzi typu pandas i własną analizę niemal
wszystkiego co podczas gry się dzieje. Niedzielnego użytkownika, niezainteresowanego aż tak dogłębną analizą, potrafi to jednak przytłoczyć.

Celem tego projektu jest implementacja podobnego narzędzia, nieco ograniczonego, celem podania najważniejszych informacji i
statystyk w sposób bardziej przystępny dla użytkownika.

## Features
### Edytor talii
- importowanie talii z plików zapisywanych przez klienta gry jak i stron katalogujących popularne talie
- możliwość edytowania zaimportowanych talii
- udostępnianie i aktualizowanie bazy kart dostępnych w grze przy pomocy [Scryfall API](https://scryfall.com/docs/api)
### Zbieranie statystyk
- parsowanie logów pozostawianych przez klienta gry
- automatyczna detekcja talii używanej przez użytkownika w partii, na bazie wcześniej zaimportowanych talii
- historia rozegranych partii
- prezentacja najważniejszych statystyk, takich jak winratio poszczególnych talii, dystrybucja archetypów talii przeciwników,
- możliwość sortowania i filtrowania po trybie gry, archetypie talii przeciwnika, dacie rozegrania partii itd.

## Additional Features
Poniższe featuery byłyby mile widziane, jednak nie wiem czy wszystkie są implementowalne w dość ograniczonym czasie
- automatyczna detekcja archetypów talii, zarówno używanych przez użytkownika jak i przeciwników

## Plan
W pierwszej części zajmiemy się edytorem talii, w drugiej wszystkim związanmy ze zbieraniem i prezentacją statystyk.

Wszystko zostanie spięte w jedną aplikację okienkową z frontendem napisanym przy użyciu frameworka 
[Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)

## Libraries
- koin (https://github.com/InsertKoinIO/koin)
- ktor (https://github.com/ktorio/ktor)
- kotlinx-serialization (https://github.com/Kotlin/kotlinx.serialization)
- coil (https://github.com/coil-kt/coil)
- sqldelight (https://github.com/sqldelight/sqldelight)
