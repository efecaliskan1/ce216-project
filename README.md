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

# Run all tests (26 test classes, 142 tests)
mvn test
```

## Running the application (Windows)

The repository ships a `run.bat` launcher that uses your installed JDK/JRE and bundles the JavaFX dependencies through Maven. JavaFX is **not** part of standard JDK/JRE distributions, so `run.bat` stages the `javafx-controls`, `javafx-fxml`, and `javafx-graphics` jars from Maven Central and launches the application with the correct `--module-path` and `--add-modules` flags.

Prerequisites:

- **JDK or JRE 17+** installed (must be on `PATH` so `java` is callable, or `JAVA_HOME` set)
- **Apache Maven 3.6+** on `PATH` (only needed for the initial build)
- Internet connection on the **first build only** (Maven downloads JavaFX 21, ~50 MB)

Steps:

```cmd
git clone https://github.com/efecaliskan1/ce216-project.git
cd ce216-project
mvn clean package
run.bat
```

`run.bat` does two things:

1. On first run, calls `mvn dependency:copy-dependencies` to stage the JavaFX jars into `target/deps`
2. Launches the application JAR with `--module-path "target/deps"` and `--add-modules javafx.controls,javafx.fxml,javafx.graphics`

No external installer or `.exe` is produced — the launcher relies on the JDK/JRE that is already installed on the machine. JavaFX libraries are defined in `pom.xml` and pulled by Maven.

See `setup.txt` for a one-page walkthrough.

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
