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

# Run all tests (26 test classes, ~126 tests)
mvn test
```

## Building a Windows installer

Windows-only. Requires:

- **JDK 17+ with `jpackage`** (tested with JDK 25). `JAVA_HOME` must point to the JDK install folder. Example: `setx JAVA_HOME "C:\Program Files\Java\jdk-25"`
- **Apache Maven 3.6+** on `PATH`
- **WiX Toolset 5+** with the Util and UI extensions, installed via the .NET CLI:

  ```cmd
  dotnet tool install --global wix --version 5.0.2
  wix extension add -g WixToolset.Util.wixext/5.0.0
  wix extension add -g WixToolset.UI.wixext/5.0.0
  ```

  (.NET SDK 8.0+ is needed for `dotnet tool install`; download from <https://dotnet.microsoft.com/download> if missing.)

Then from the project root:

```cmd
build-installer.bat
```

The script does three things:

1. `mvn clean package` — builds the application JAR
2. `mvn dependency:copy-dependencies` — stages JavaFX jars into `target/deps`
3. `jpackage` — produces the `.msi` using a jlinked runtime image that includes the JavaFX modules (`controls`, `fxml`, `graphics`)

The resulting `.msi` lands in `dist/` (~36 MB) and bundles its own Java runtime plus the JavaFX modules, so the target machine does not need a JDK. **Tested on Windows 11 (64-bit).**

See `setup.txt` for the full prerequisite list and troubleshooting notes.

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
