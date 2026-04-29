package ui.views;

import core.app.SeasonController;
import core.domain.GameWeek;
import core.domain.Match;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.App;
import ui.TeamLogoFactory;

public class MatchDayView {

    private final App app;
    private final Runnable afterAdvance;
    private final VBox root;

    public MatchDayView(App app, Runnable afterAdvance) {
        this.app          = app;
        this.afterAdvance = afterAdvance;
        this.root         = build();
    }

    public Parent getRoot() { return root; }

    private VBox build() {
        VBox page = new VBox(18);
        page.setPadding(new Insets(36, 48, 36, 48));
        page.setStyle("-fx-background-color: #0a0e1a;");

        SeasonController ctrl = app.getController();

        Label title = new Label("Match Day");
        title.getStyleClass().add("h1");
        Label sub = new Label("Review the fixtures, set tactics, then advance the week.");
        sub.getStyleClass().add("subtitle");

        if (ctrl.getSeason().isFinished()) {
            Label done = new Label("Season complete.");
            done.getStyleClass().add("h2");
            page.getChildren().addAll(title, done);
            return page;
        }

        GameWeek gw = ctrl.getSeason().getCurrentGameWeek();
        Label weekTitle = new Label("Week " + (gw.getWeekNumber() + 1));
        weekTitle.getStyleClass().add("h2");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        VBox fixtureList = new VBox(8);
        populateFixtures(fixtureList, gw);

        Button advance = new Button("▶  Simulate week");
        advance.getStyleClass().add("primary");
        advance.setPrefHeight(42);
        advance.setPrefWidth(240);

        Label status = new Label("");
        status.getStyleClass().add("subtitle");

        HBox actions = new HBox(12, advance, status);
        actions.setAlignment(Pos.CENTER_LEFT);

        advance.setOnAction(e -> {
            advance.setDisable(true);
            status.setText("Simulating…");
            Platform.runLater(() -> {
                try {
                    ctrl.nextWeek();
                    fixtureList.getChildren().clear();
                    populateFixtures(fixtureList, gw);
                    status.setText("Week simulated. Click Continue.");
                    Button back = new Button("Continue →");
                    back.getStyleClass().add("primary");
                    back.setOnAction(ev -> afterAdvance.run());
                    actions.getChildren().setAll(back, status);
                } catch (Exception ex) {
                    status.setText("Error: " + ex.getMessage());
                    advance.setDisable(false);
                }
            });
        });

        card.getChildren().addAll(weekTitle, fixtureList);

        ScrollPane sp = new ScrollPane(card);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: #0a0e1a;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        page.getChildren().addAll(title, sub, sp, actions);
        return page;
    }

    private void populateFixtures(VBox list, GameWeek gw) {
        boolean isVb = app.getController().getSport().getName().equalsIgnoreCase("Volleyball");
        for (Match m : gw.getFixtures()) {
            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER);
            row.setPadding(new Insets(10, 12, 10, 12));

            Label home = new Label(m.getHomeTeam().getName());
            home.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(home, Priority.ALWAYS);
            home.setStyle("-fx-alignment: CENTER_RIGHT; -fx-font-weight: 600;");

            var homeLogo = TeamLogoFactory.create(m.getHomeTeam().getName(), 40);

            Label mid;
            if (m.isPlayed()) {
                mid = new Label(m.getResult().getHomeScore() + " : " + m.getResult().getAwayScore()
                        + (isVb ? "  (sets)" : ""));
                mid.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #f59e0b; -fx-alignment: CENTER;");
                mid.getStyleClass().add("mono");
            } else {
                mid = new Label("vs");
                mid.getStyleClass().add("subtitle");
            }
            mid.setMinWidth(140);

            var awayLogo = TeamLogoFactory.create(m.getAwayTeam().getName(), 40);

            Label away = new Label(m.getAwayTeam().getName());
            away.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(away, Priority.ALWAYS);
            away.setStyle("-fx-font-weight: 600;");

            row.getChildren().addAll(home, homeLogo, mid, awayLogo, away);
            list.getChildren().add(row);
        }
    }
}
