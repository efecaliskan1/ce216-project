# Sport Management System

A tactical sports-management game inspired by Football Manager / Championship Manager, built for CE216 at IUE. Supports **Football** and **Volleyball** through a plug-in sport architecture.

## Team

- Kutluay Aydın
- Arda Korkmaz
- Efe Çalışkan
- Toprak Ege Gündoğan

## Quick start

```bash
# Run the CLI demo (football + volleyball + save/load)
mvn exec:java -Dexec.mainClass=core.app.Main

# Run the JavaFX UI
mvn javafx:run

# Run all tests (26 test classes, 126 tests)
mvn test
```

## Building a Windows installer

Requires JDK 17+ with `jpackage`:

```bash
build-installer.bat
```

The resulting `.msi` lands in `dist/`.

## Project structure

```
src/main/java/
  interfaces/        # ISport, IMatchSimulator, IStandingsCalculator, ...
  abstracts/         # AbstractSport, AbstractMatchSimulator, ...
  core/
    domain/          # Player, Team, League, Match, Season, ...
    services/        # FixtureGenerator, StandingsService, SaveLoadService, NameDataService
    app/             # Main, SeasonController, SportFactory, MatchCoordinator
  sports/
    football/        # FootballSport, FootballMatchSimulator, FootballStandingsCalculator
    volleyball/      # VolleyballSport, VolleyballMatchSimulator, VolleyballStandingsCalculator
  tactics/           # Defensive, Balanced, HighPress, CounterAttack, TacticFactory
  valueobjects/      # RosterRules, ScoringConfig, TacticResult, PeriodResult
  observer/          # UIMatchObserver
  ui/                # JavaFX application and views
    views/           # Start, Main, Overview, Standings, Fixtures, Squad, MatchDay
src/main/resources/
  data/              # Name & team-name pools (CSV)
  styles/            # JavaFX CSS (main.css)
```

## OO principles applied

- **Dependency Inversion** – controllers and UI depend on `ISport`, never on `FootballSport` / `VolleyballSport`.
- **Open/Closed** – new sports register through `SportFactory.register()`; no existing code changes.
- **Strategy** – `ITacticStrategy` with four interchangeable tactics; `TacticFactory` resolves by name.
- **Template Method** – `AbstractMatchSimulator.simulate()` drives the match flow; sports only implement `simulatePeriod()`.
- **Observer** – `MatchObserver` receives live match events; both UI and stats collectors plug in.
- **Factory** – `SportFactory` uses a `Map<String, Supplier<ISport>>` registry.

## Save / Load

All domain classes are `Serializable`. `SaveLoadService` writes the whole `Season` object graph to `saves/slot_N.ser`. Loading creates a ready-to-use `SeasonController` state.
