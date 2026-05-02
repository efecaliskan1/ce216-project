package ui.views;

import core.domain.Match;
import core.domain.Player;
import core.domain.Team;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tactics.TacticFactory;
import ui.App;
import ui.TeamLogoFactory;

import java.util.function.Consumer;

public class LineupSelectionView {

    private final App      app;
    private final Match    match;
    private final Consumer<Match> onConfirmed;
    private final VBox     root;
    private final int      lineupSize;
    private final int      benchSize;

    public LineupSelectionView(App app, Match match, Consumer<Match> onConfirmed) {
        this.app         = app;
        this.match       = match;
        this.onConfirmed = onConfirmed;
        this.lineupSize  = app.getController().getSport().getRosterRules().getStartingLineupSize();
        this.benchSize   = app.getController().getSport().getRosterRules().getBenchSize();
        this.root        = build();
    }

    public Parent getRoot() { return root; }

    private VBox build() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setStyle("-fx-background-color: #f3f4f6;");

        Label title = new Label("Pre-Match");
        title.getStyleClass().add("h1");
        Label sub = new Label(match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName()
                + "   ·   Week " + (match.getWeekNumber() + 1));
        sub.getStyleClass().add("subtitle");

        // Determine which team the user controls (only one is editable)
        Team userTeam = app.getController().getUserTeam();
        boolean userHome = userTeam != null && userTeam == match.getHomeTeam();
        boolean userAway = userTeam != null && userTeam == match.getAwayTeam();

        HBox panels = new HBox(18);
        VBox homePanel = teamPanel(match.getHomeTeam(), true,  userHome);
        VBox awayPanel = teamPanel(match.getAwayTeam(), false, userAway);
        HBox.setHgrow(homePanel, Priority.ALWAYS);
        HBox.setHgrow(awayPanel, Priority.ALWAYS);
        panels.getChildren().addAll(homePanel, awayPanel);
        VBox.setVgrow(panels, Priority.ALWAYS);

        Button start = new Button("▶  Start Match");
        start.getStyleClass().add("primary");
        start.setPrefHeight(44);
        start.setPrefWidth(220);
        start.setOnAction(e -> {
            if (validateLineups()) onConfirmed.accept(match);
        });

        page.getChildren().addAll(title, sub, panels, start);
        return page;
    }

    private boolean validateLineups() {
        Team a = match.getHomeTeam(), b = match.getAwayTeam();
        Team userTeam = app.getController().getUserTeam();

        if (userTeam != a) a.autoReplaceInjuredStarters();
        if (userTeam != b) b.autoReplaceInjuredStarters();

        if (a.getStartingLineup().size() != lineupSize || b.getStartingLineup().size() != lineupSize) {
            new Alert(Alert.AlertType.WARNING,
                    "Both teams must field exactly " + lineupSize + " starters.").showAndWait();
            return false;
        }
        for (Player p : a.getStartingLineup()) if (p.isInjured()) {
            new Alert(Alert.AlertType.WARNING,
                p.getName() + " is injured and can't start. Move them to the bench first.").showAndWait();
            return false;
        }
        for (Player p : b.getStartingLineup()) if (p.isInjured()) {
            new Alert(Alert.AlertType.WARNING,
                p.getName() + " is injured and can't start.").showAndWait();
            return false;
        }
        return true;
    }

    private VBox teamPanel(Team team, boolean home, boolean editable) {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("card");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        var logo = TeamLogoFactory.create(team.getName(), 44);
        Label name = new Label(team.getName());
        name.getStyleClass().add("h2");
        Label tag = new Label(home ? "HOME" : "AWAY");
        tag.getStyleClass().addAll("badge", home ? "badge-accent" : "badge-info");
        header.getChildren().addAll(logo, name, tag);

        if (editable) {
            Label you = new Label("YOUR CLUB");
            you.getStyleClass().addAll("badge", "badge-success");
            header.getChildren().add(you);
        } else {
            Label ai = new Label("AI");
            ai.getStyleClass().addAll("badge");
            header.getChildren().add(ai);
        }

        // Tactic
        HBox tacticRow = new HBox(8);
        tacticRow.setAlignment(Pos.CENTER_LEFT);
        Label tactLbl = new Label("Tactic:");
        tactLbl.getStyleClass().add("caption");
        ComboBox<String> tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll(tacticOptions());
        tacticBox.setValue(tacticNameOf(team));
        tacticBox.setDisable(!editable);
        tacticBox.valueProperty().addListener((o, a, b) -> {
            if (b != null) team.setCurrentTactic(TacticFactory.create(b));
        });
        tacticRow.getChildren().addAll(tactLbl, tacticBox);

        Label split = new Label("STARTING " + lineupSize + "  /  BENCH " + benchSize
                              + (editable ? "" : "  ·  read-only"));
        split.getStyleClass().add("caption");

        Label startersLbl = new Label("Starters");
        startersLbl.getStyleClass().add("h3");
        TableView<Player> starters = playerTable(team.getStartingLineup());

        Label poolLbl = new Label("Bench / available");
        poolLbl.getStyleClass().add("h3");
        TableView<Player> pool = playerTable(team.getBench());

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);
        Button toBench = new Button("↓ Move to bench");
        Button toStart = new Button("↑ Promote to starter");
        toBench.setDisable(!editable);
        toStart.setDisable(!editable);

        toBench.setOnAction(e -> {
            Player starter = starters.getSelectionModel().getSelectedItem();
            Player benchPlayer = pool.getSelectionModel().getSelectedItem();
            if (starter == null) {
                new Alert(Alert.AlertType.INFORMATION, "Pick a starter first.").showAndWait();
                return;
            }
            if (benchPlayer == null) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Pick a bench player to swap in before removing a starter.").showAndWait();
                return;
            }
            if (benchPlayer.isInjured()) {
                new Alert(Alert.AlertType.WARNING, benchPlayer.getName() + " is injured.").showAndWait();
                return;
            }
            if (team.usesFootballFormationRules()
                    && !starter.getPosition().equalsIgnoreCase(benchPlayer.getPosition())) {
                new Alert(Alert.AlertType.WARNING,
                        "Football lineups must keep the 1-4-4-2 shape. Swap with a bench player in the same position.")
                        .showAndWait();
                return;
            }
            if (!team.swapStarterWithBench(starter, benchPlayer)) {
                new Alert(Alert.AlertType.WARNING,
                        "Lineup change failed. Select one starter and one healthy bench player.").showAndWait();
                return;
            }
            refreshTables(team, starters, pool);
        });
        toStart.setOnAction(e -> {
            Player benchPlayer = pool.getSelectionModel().getSelectedItem();
            Player starter = starters.getSelectionModel().getSelectedItem();
            if (benchPlayer == null) {
                new Alert(Alert.AlertType.INFORMATION, "Pick a bench player first.").showAndWait();
                return;
            }
            if (starter == null) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Pick the starter you want to replace first.").showAndWait();
                return;
            }
            if (benchPlayer.isInjured()) {
                new Alert(Alert.AlertType.WARNING, benchPlayer.getName() + " is injured.").showAndWait();
                return;
            }
            if (team.usesFootballFormationRules()
                    && !starter.getPosition().equalsIgnoreCase(benchPlayer.getPosition())) {
                new Alert(Alert.AlertType.WARNING,
                        "Football lineups must keep the 1-4-4-2 shape. Swap with a bench player in the same position.")
                        .showAndWait();
                return;
            }
            if (!team.swapStarterWithBench(starter, benchPlayer)) {
                new Alert(Alert.AlertType.WARNING,
                        "Lineup change failed. Select one starter and one healthy bench player.").showAndWait();
                return;
            }
            refreshTables(team, starters, pool);
        });
        actions.getChildren().addAll(toBench, toStart);

        VBox.setVgrow(starters, Priority.ALWAYS);
        VBox.setVgrow(pool, Priority.ALWAYS);

        panel.getChildren().addAll(header, tacticRow, split, startersLbl, starters, actions, poolLbl, pool);
        return panel;
    }

    private void refreshTables(Team team, TableView<Player> starters, TableView<Player> pool) {
        starters.setItems(FXCollections.observableArrayList(team.getStartingLineup()));
        pool.setItems(FXCollections.observableArrayList(team.getBench()));
    }

    private TableView<Player> playerTable(java.util.List<Player> players) {
        TableView<Player> tv = new TableView<>();
        tv.setItems(FXCollections.observableArrayList(players));
        tv.setPlaceholder(new Label("No players."));

        TableColumn<Player, Number> shirt = new TableColumn<>("#");
        shirt.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getShirtNumber()));
        shirt.setPrefWidth(40);

        TableColumn<Player, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        name.setPrefWidth(180);

        TableColumn<Player, String> pos = new TableColumn<>("Pos");
        pos.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPosition()));
        pos.setPrefWidth(70);

        TableColumn<Player, Number> ovr = new TableColumn<>("Ovr");
        ovr.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getOverall()));
        ovr.setPrefWidth(60);

        TableColumn<Player, String> stat = new TableColumn<>("Status");
        stat.setCellValueFactory(cd -> {
            Player p = cd.getValue();
            String s = p.isInjured()
                ? "INJ (" + p.getInjuredGamesRemaining() + ")"
                : "Fit · Fatigue " + p.getFatigueLevel();
            return new SimpleStringProperty(s);
        });
        stat.setPrefWidth(140);

        tv.setRowFactory(t -> new TableRow<>() {
            @Override protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) setStyle("");
                else if (p.isInjured()) setStyle("-fx-background-color: #fee2e2;");
                else if (p.getFatigueLevel() > 70) setStyle("-fx-background-color: #fef3c7;");
                else setStyle("");
            }
        });

        tv.getColumns().addAll(shirt, name, pos, ovr, stat);
        return tv;
    }

    private String tacticNameOf(Team team) {
        if (team.getCurrentTactic() == null) return "balanced";
        String cls = team.getCurrentTactic().getClass().getSimpleName().toLowerCase();
        if (isVolleyball()) {
            if (cls.contains("defensive")) return "blockfocus";
            if (cls.contains("highpress") || cls.contains("counter")) return "servepressure";
            return "balanced";
        }
        if (cls.contains("defensive")) return "defensive";
        if (cls.contains("highpress")) return "highpress";
        if (cls.contains("counter"))   return "counterattack";
        return "balanced";
    }

    private java.util.List<String> tacticOptions() {
        return isVolleyball()
                ? java.util.List.of("blockfocus", "balanced", "servepressure")
                : java.util.List.of("defensive", "balanced", "highpress", "counterattack");
    }

    private boolean isVolleyball() {
        return app.getController().getSport().getName().equalsIgnoreCase("Volleyball");
    }
}
