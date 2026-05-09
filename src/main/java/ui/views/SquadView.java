package ui.views;

import core.domain.Player;
import core.domain.Team;
import core.domain.TrainingPlan;
import interfaces.ITacticStrategy;
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

import java.util.List;

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
        page.setStyle("-fx-background-color: #f3f4f6;");

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
        Team userTeam = app.getController().getUserTeam();
        teamBox.setValue(userTeam != null ? userTeam : teamBox.getItems().get(0));
        teamBox.setPrefWidth(260);

        Label tacticLbl = new Label("Tactic:");
        tacticLbl.getStyleClass().add("caption");
        ComboBox<String> tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll(tacticOptions());
        tacticBox.setPrefWidth(180);

        Button apply = new Button("Apply tactic");
        apply.getStyleClass().add("primary");

        controls.getChildren().addAll(teamLbl, teamBox, tacticLbl, tacticBox, apply);

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
        VBox trainingBox = new VBox(10);
        trainingBox.getStyleClass().add("card");
        trainingBox.setPrefWidth(280);
        trainingBox.setMinWidth(280);

        Label trainingTitle = new Label("Training");
        trainingTitle.getStyleClass().add("h3");
        Label trainingInfo = new Label();
        trainingInfo.getStyleClass().add("subtitle");
        trainingInfo.setWrapText(true);

        Label planTitle = new Label("Coach plan");
        planTitle.getStyleClass().add("caption");
        VBox trainingPlans = new VBox(6);

        Button trainAll = new Button("Train full squad");
        trainAll.getStyleClass().add("primary");
        trainAll.setMaxWidth(Double.MAX_VALUE);

        Label trainingHint = new Label("Every 10 training points increase overall by 1 for players aged 32 or below.");
        trainingHint.getStyleClass().add("caption");
        trainingHint.setWrapText(true);

        Label trainingResult = new Label();
        trainingResult.getStyleClass().add("caption");
        trainingResult.setWrapText(true);

        trainingBox.getChildren().addAll(trainingTitle, trainingInfo, planTitle, trainingPlans, trainAll, trainingHint, trainingResult);

        Runnable refresh = () -> {
            Team t = teamBox.getValue();
            if (t == null) return;
            roster.setItems(FXCollections.observableArrayList(t.getPlayers()));

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

            trainingPlans.getChildren().clear();
            if (t.getCoach() == null) {
                trainingInfo.setText("No coach assigned.");
                trainAll.setDisable(true);
            } else {
                int sessions = app.getController().getAvailableTrainingSessions();
                boolean canTrainThisTeam = app.getController().getUserTeam() == t;
                trainingInfo.setText("Available training rights: " + sessions
                        + (canTrainThisTeam ? " for " + t.getName() + "." : " — only your managed team can train."));
                trainAll.setDisable(!canTrainThisTeam || sessions <= 0);
                List<TrainingPlan> plans = t.getCoach().getTrainingPlans();
                if (plans.isEmpty()) {
                    Label none = new Label("No training plans.");
                    none.getStyleClass().add("caption");
                    trainingPlans.getChildren().add(none);
                } else {
                    for (TrainingPlan plan : plans) {
                        Label line = new Label(plan.getTargetAttribute() + "  ·  intensity " + plan.getIntensity());
                        line.getStyleClass().add("caption");
                        trainingPlans.getChildren().add(line);
                    }
                }
            }

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
        trainAll.setOnAction(e -> {
            Team t = teamBox.getValue();
            if (t == null || t.getCoach() == null) return;

            int beforeOverall = t.getPlayers().stream().mapToInt(Player::getOverall).sum();
            if (!app.getController().useTrainingSession(t)) {
                trainingResult.setText("No training right available right now. Finish a match to earn one.");
                refresh.run();
                return;
            }
            int afterOverall = t.getPlayers().stream().mapToInt(Player::getOverall).sum();
            long trainedPlayers = t.getPlayers().stream().filter(Player::isAvailable).count();

            roster.setItems(FXCollections.observableArrayList(t.getPlayers()));
            trainingResult.setText("Training completed for " + trainedPlayers + " available players. "
                    + "Team overall total changed by +" + Math.max(0, afterOverall - beforeOverall) + ".");

            if (afterOverall > beforeOverall) {
                trainingResult.setText(trainingResult.getText() + " Some players gained overall.");
            }
            refresh.run();
        });

        HBox contentRow = new HBox(16);
        contentRow.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(roster, Priority.ALWAYS);
        HBox.setHgrow(roster, Priority.ALWAYS);
        contentRow.getChildren().addAll(trainingBox, roster);

        VBox.setVgrow(roster, Priority.ALWAYS);
        page.getChildren().addAll(title, sub, controls, teamHeader, contentRow);
        return page;
    }

    private TableView<Player> buildRosterTable() {
        TableView<Player> tv = new TableView<>();
        tv.setPlaceholder(new Label("No players."));

        TableColumn<Player, Number> shirt = numCol("#",  Player::getShirtNumber, 50);
        TableColumn<Player, String> name  = strCol("NAME",     Player::getName,     220);
        TableColumn<Player, String> pos   = strCol("POSITION", Player::getPosition, 130);
        TableColumn<Player, Number> ovr   = numCol("OVR", Player::getOverall, 60);
        TableColumn<Player, Number> age   = numCol("AGE",  Player::getAge,  55);
        TableColumn<Player, Number> fat   = numCol("FATIGUE", Player::getFatigueLevel, 80);
        TableColumn<Player, String> stat  = strCol("STATUS", p ->
                p.isInjured() ? "Injured (" + p.getInjuredGamesRemaining() + ")" : "Available", 140);

        tv.getColumns().addAll(shirt, name, pos, ovr, age, fat, stat);
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
        if (isVolleyball()) {
            if (cls.contains("defensive"))   return "blockfocus";
            if (cls.contains("highpress") || cls.contains("counter")) return "servepressure";
            return "balanced";
        }
        if (cls.contains("defensive"))   return "defensive";
        if (cls.contains("highpress"))   return "highpress";
        if (cls.contains("counter"))     return "counterattack";
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

    private static class TeamConverter extends javafx.util.StringConverter<Team> {
        @Override public String toString(Team t) { return t == null ? "" : t.getName(); }
        @Override public Team fromString(String s) { return null; }
    }
}
