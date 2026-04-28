package ui.views;

import core.domain.Player;
import core.domain.Team;
import interfaces.ITacticStrategy;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tactics.TacticFactory;
import ui.App;
import ui.TeamLogoFactory;

public class SquadView {

    private final App  app;
    private final VBox root;

    public SquadView(App app) {
        this.app  = app;
        this.root = build();
    }

    public Parent getRoot() { return root; }

    private VBox build() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(36, 48, 36, 48));
        page.setStyle("-fx-background-color: #0a0e1a;");

        Label title = new Label("My Squad");
        title.getStyleClass().add("h1");
        Label sub = new Label("Pick any team in the league to inspect its players, coach, and tactic.");
        sub.getStyleClass().add("subtitle");

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        Label teamLbl = new Label("Team:");
        teamLbl.getStyleClass().add("caption");
        ComboBox<Team> teamBox = new ComboBox<>();
        teamBox.setItems(FXCollections.observableArrayList(app.getController().getSeason().getLeague().getTeams()));
        teamBox.setConverter(new TeamConverter());
        teamBox.setValue(teamBox.getItems().get(0));
        teamBox.setPrefWidth(260);

        Label tacticLbl = new Label("Tactic:");
        tacticLbl.getStyleClass().add("caption");
        ComboBox<String> tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll("defensive", "balanced", "highpress", "counterattack");
        tacticBox.setPrefWidth(180);

        Button apply = new Button("Apply tactic");
        apply.getStyleClass().add("primary");

        controls.getChildren().addAll(teamLbl, teamBox, tacticLbl, tacticBox, apply);

        // Team header with big logo + coach info
        HBox teamHeader = new HBox(20);
        teamHeader.setAlignment(Pos.CENTER_LEFT);
        teamHeader.getStyleClass().add("card");

        StackPane logoHolder = new StackPane();
        logoHolder.setPrefSize(96, 96);
        logoHolder.setMinSize(96, 96);

        VBox coachInfo = new VBox(4);
        HBox.setHgrow(coachInfo, Priority.ALWAYS);

        teamHeader.getChildren().addAll(logoHolder, coachInfo);

        TableView<Player> roster = buildRosterTable();

        Runnable refresh = () -> {
            Team t = teamBox.getValue();
            if (t == null) return;
            roster.setItems(FXCollections.observableArrayList(t.getPlayers()));

            // Update header
            logoHolder.getChildren().setAll(TeamLogoFactory.create(t.getName(), 96));
            coachInfo.getChildren().clear();
            Label teamName = new Label(t.getName());
            teamName.getStyleClass().add("h2");
            coachInfo.getChildren().add(teamName);
            if (t.getCoach() != null) {
                Label coach = new Label("Coach: " + t.getCoach().getName()
                        + "  ·  Specialty: " + t.getCoach().getSpecialty());
                coach.getStyleClass().add("subtitle");
                coachInfo.getChildren().add(coach);
            }
            Label tacticInfo = new Label("Current tactic: " +
                    (t.getCurrentTactic() != null ? tacticNameOf(t.getCurrentTactic()) : "none"));
            tacticInfo.getStyleClass().add("caption");
            coachInfo.getChildren().add(tacticInfo);

            ITacticStrategy cur = t.getCurrentTactic();
            if (cur != null) tacticBox.setValue(tacticNameOf(cur));
        };
        refresh.run();

        teamBox.valueProperty().addListener((obs, a, b) -> refresh.run());
        apply.setOnAction(e -> {
            Team t = teamBox.getValue();
            String name = tacticBox.getValue();
            if (t != null && name != null) {
                t.setCurrentTactic(TacticFactory.create(name));
                refresh.run();
                new Alert(Alert.AlertType.INFORMATION,
                        t.getName() + " is now playing " + name + ".").showAndWait();
            }
        });

        VBox.setVgrow(roster, Priority.ALWAYS);
        page.getChildren().addAll(title, sub, controls, teamHeader, roster);
        return page;
    }

    private TableView<Player> buildRosterTable() {
        TableView<Player> tv = new TableView<>();
        tv.setPlaceholder(new Label("No players."));

        TableColumn<Player, Number> shirt = numCol("#",  Player::getShirtNumber, 50);
        TableColumn<Player, String> name  = strCol("NAME",     Player::getName,     220);
        TableColumn<Player, String> pos   = strCol("POSITION", Player::getPosition, 130);
        TableColumn<Player, Number> age   = numCol("AGE",  Player::getAge,  55);
        TableColumn<Player, Number> fat   = numCol("FATIGUE", Player::getFatigueLevel, 80);
        TableColumn<Player, String> stat  = strCol("STATUS", p ->
                p.isInjured() ? "Injured (" + p.getInjuredGamesRemaining() + ")" : "Available", 140);

        tv.getColumns().addAll(shirt, name, pos, age, fat, stat);
        return tv;
    }

    private static TableColumn<Player, Number> numCol(String title,
            java.util.function.ToIntFunction<Player> getter, double width) {
        TableColumn<Player, Number> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> new SimpleIntegerProperty(getter.applyAsInt(cd.getValue())));
        c.setPrefWidth(width); c.setMinWidth(width);
        c.setStyle("-fx-alignment: CENTER;");
        return c;
    }
    private static TableColumn<Player, String> strCol(String title,
            java.util.function.Function<Player, String> getter, double width) {
        TableColumn<Player, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        c.setPrefWidth(width); c.setMinWidth(width);
        return c;
    }

    private String tacticNameOf(ITacticStrategy s) {
        String cls = s.getClass().getSimpleName().toLowerCase();
        if (cls.contains("defensive"))   return "defensive";
        if (cls.contains("highpress"))   return "highpress";
        if (cls.contains("counter"))     return "counterattack";
        return "balanced";
    }

    private static class TeamConverter extends javafx.util.StringConverter<Team> {
        @Override public String toString(Team t) { return t == null ? "" : t.getName(); }
        @Override public Team fromString(String s) { return null; }
    }
}
