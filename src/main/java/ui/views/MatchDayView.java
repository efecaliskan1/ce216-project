package ui.views;

import core.app.LiveMatchController;
import core.app.SeasonController;
import core.domain.GameWeek;
import core.domain.Match;
import core.domain.MatchResult;
import core.domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.App;
import ui.TeamLogoFactory;

public class MatchDayView {

    private final App      app;
    private final Runnable afterAdvance;
    private final VBox     root;

    public MatchDayView(App app, Runnable afterAdvance) {
        this.app          = app;
        this.afterAdvance = afterAdvance;
        this.root         = build();
    }

    public Parent getRoot() { return root; }

    private VBox build() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setStyle("-fx-background-color: #f3f4f6;");

        SeasonController ctrl = app.getController();
        Team userTeam = ctrl.getUserTeam();

        Label title = new Label("Match Day");
        title.getStyleClass().add("h1");
        Label sub = new Label(userTeam != null
                ? "Play your fixture or auto-simulate the whole week."
                : "Auto-simulate the week.");
        sub.getStyleClass().add("subtitle");

        if (ctrl.getSeason().isFinished()) {
            VBox doneCard = new VBox(12);
            doneCard.getStyleClass().add("card");

            Label done = new Label("Season complete.");
            done.getStyleClass().add("h2");
            Label info = new Label("You can continue with the same squad next season. Overall ratings stay, and every player's age increases by 1.");
            info.getStyleClass().add("subtitle");

            Button nextSeason = new Button("Start Season " + (ctrl.getSeason().getSeasonNumber() + 1));
            nextSeason.getStyleClass().add("primary");
            nextSeason.setOnAction(e -> {
                ctrl.startNextSeason();
                app.showMain("overview");
            });

            doneCard.getChildren().addAll(done, info, nextSeason);
            page.getChildren().addAll(title, doneCard);
            return page;
        }

        GameWeek gw = ctrl.getSeason().getCurrentGameWeek();
        Label weekTitle = new Label("Week " + (gw.getWeekNumber() + 1));
        weekTitle.getStyleClass().add("h2");

        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        VBox list = new VBox(2);
        for (Match m : gw.getFixtures()) list.getChildren().add(matchRow(m, userTeam));
        card.getChildren().addAll(weekTitle, list);

        ScrollPane sp = new ScrollPane(card);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        Button autoAll = new Button("⏩ Auto-simulate whole week");
        autoAll.getStyleClass().add("primary");
        autoAll.setPrefHeight(40);
        autoAll.setPrefWidth(280);
        autoAll.setOnAction(e -> {
            ctrl.nextWeek();
            afterAdvance.run();
        });
        HBox btmRow = new HBox(autoAll);
        btmRow.setAlignment(Pos.CENTER);

        page.getChildren().addAll(title, sub, weekTitle, sp, btmRow);
        return page;
    }

    private HBox matchRow(Match m, Team userTeam) {
        boolean isUserMatch = userTeam != null
                && (m.getHomeTeam() == userTeam || m.getAwayTeam() == userTeam);

        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10, 16, 10, 16));
        if (isUserMatch && !m.isPlayed()) {
            row.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 6;");
        }

        Label home = new Label(m.getHomeTeam().getName());
        home.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px; -fx-font-weight: 600;");
        home.setMaxWidth(Double.MAX_VALUE);
        home.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(home, Priority.ALWAYS);

        var homeLogo = TeamLogoFactory.create(m.getHomeTeam().getName(), 36);

        Label mid;
        if (m.isPlayed()) {
            mid = new Label(m.getResult().getHomeScore() + " : " + m.getResult().getAwayScore());
            mid.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #2563eb;");
        } else {
            mid = new Label("VS");
            mid.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #9ca3af; -fx-letter-spacing: 1;");
        }
        mid.setMinWidth(80);
        mid.setMaxWidth(80);
        mid.setAlignment(Pos.CENTER);

        var awayLogo = TeamLogoFactory.create(m.getAwayTeam().getName(), 36);

        Label away = new Label(m.getAwayTeam().getName());
        away.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px; -fx-font-weight: 600;");
        away.setMaxWidth(Double.MAX_VALUE);
        away.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(away, Priority.ALWAYS);

        Button play = new Button(m.isPlayed() ? "✓ Played" : (isUserMatch ? "▶ Play" : "AI"));
        play.setMinWidth(90);
        if (m.isPlayed() || !isUserMatch) {
            play.setDisable(true);
        } else {
            play.getStyleClass().add("primary");
            play.setOnAction(e -> startMatchFlow(m));
        }

        row.getChildren().addAll(home, homeLogo, mid, awayLogo, away, play);
        return row;
    }

    private void startMatchFlow(Match m) {

        SeasonController ctrl = app.getController();
        ctrl.simulateOtherMatches(m);

        LineupSelectionView lineup = new LineupSelectionView(app, m, confirmedMatch -> {

            LiveMatchController liveCtrl = new LiveMatchController(
                    ctrl.getSport(), confirmedMatch, System.currentTimeMillis());
            liveCtrl.setUserControlledTeam(ctrl.getUserTeam());
            LiveMatchView live = new LiveMatchView(app, liveCtrl, finishedMatch -> {

                MatchResult r = finishedMatch.getResult();
                if (r != null) ctrl.getStandingsService().processResult(r);

                ctrl.finishWeekAfterUserMatch(finishedMatch);

                PostMatchView post = new PostMatchView(app, finishedMatch, () -> app.showMain("fixtures"));
                app.showCustom(post.getRoot());
            });
            app.showCustom(live.getRoot());
        });
        app.showCustom(lineup.getRoot());
    }
}
