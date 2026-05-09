package ui.views;

import core.app.LiveMatchController;
import core.app.LiveMatchController.State;
import core.domain.EventType;
import core.domain.Match;
import core.domain.MatchEvent;
import core.domain.Player;
import core.domain.Team;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import tactics.TacticFactory;
import ui.App;
import ui.TeamLogoFactory;

import java.util.function.Consumer;

public class LiveMatchView {

    private final App                 app;
    private final LiveMatchController ctrl;
    private final Consumer<Match>     onMatchEnd;
    private final BorderPane          root;

    private Label  homeScoreLbl, awayScoreLbl, clockLbl, statusLbl;
    private Label  setLbl;
    private VBox   eventLog;
    private ScrollPane eventScroll;
    private Button tickBtn, skip5Btn, skip10Btn, fastFwdBtn, subHomeBtn, subAwayBtn, tacticHomeBtn, tacticAwayBtn, finishBtn;
    private Timeline autoTimeline;

    public LiveMatchView(App app, LiveMatchController ctrl, Consumer<Match> onMatchEnd) {
        this.app        = app;
        this.ctrl       = ctrl;
        this.onMatchEnd = onMatchEnd;
        this.root       = build();
        wireController();
        ctrl.start();
        refreshSnapshot();
    }

    public Parent getRoot() { return root; }

    private BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #f3f4f6;");
        bp.setPadding(new Insets(20, 28, 20, 28));

        bp.setTop(buildScoreboard());

        bp.setCenter(buildEventArea());

        bp.setBottom(buildControls());

        return bp;
    }

    private VBox buildScoreboard() {
        VBox board = new VBox(8);
        board.setAlignment(Pos.CENTER);
        board.setPadding(new Insets(0, 0, 18, 0));

        Label title = new Label("LIVE MATCH");
        title.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: 800; -fx-letter-spacing: 2;");

        HBox row = new HBox(28);
        row.setAlignment(Pos.CENTER);

        VBox homeBox = new VBox(8);
        homeBox.setAlignment(Pos.CENTER);
        homeBox.getChildren().addAll(
            TeamLogoFactory.create(ctrl.getHomeTeam().getName(), 80),
            namedLabel(ctrl.getHomeTeam().getName())
        );

        homeScoreLbl = bigScoreLabel("0");
        Label colon = new Label(":");
        colon.setStyle("-fx-font-size: 56px; -fx-font-weight: 300; -fx-text-fill: #9ca3af;");
        awayScoreLbl = bigScoreLabel("0");

        VBox awayBox = new VBox(8);
        awayBox.setAlignment(Pos.CENTER);
        awayBox.getChildren().addAll(
            TeamLogoFactory.create(ctrl.getAwayTeam().getName(), 80),
            namedLabel(ctrl.getAwayTeam().getName())
        );

        row.getChildren().addAll(homeBox, homeScoreLbl, colon, awayScoreLbl, awayBox);

        clockLbl = new Label(ctrl.isVolleyball() ? "Set 1 · 0–0" : "0'");
        clockLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #111827; -fx-font-family: 'Consolas', monospace;");

        setLbl = new Label("");
        setLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");

        statusLbl = new Label("Match in progress…");
        statusLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af; -fx-font-weight: 600;");

        board.getChildren().addAll(title, row, clockLbl, setLbl, statusLbl);
        return board;
    }

    private Label namedLabel(String s) {
        Label l = new Label(s);
        l.setStyle("-fx-text-fill: #111827; -fx-font-size: 14px; -fx-font-weight: 700;");
        return l;
    }

    private Label bigScoreLabel(String s) {
        Label l = new Label(s);
        l.setStyle("-fx-font-size: 64px; -fx-font-weight: 900; -fx-text-fill: #111827; -fx-font-family: 'Consolas', monospace;");
        l.setMinWidth(80);
        l.setAlignment(Pos.CENTER);
        return l;
    }

    private VBox buildEventArea() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        VBox.setVgrow(card, Priority.ALWAYS);

        Label title = new Label("MATCH EVENTS");
        title.getStyleClass().add("caption");

        eventLog = new VBox(6);
        eventScroll = new ScrollPane(eventLog);
        eventScroll.setFitToWidth(true);
        eventScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(eventScroll, Priority.ALWAYS);

        card.getChildren().addAll(title, eventScroll);
        return card;
    }

    private VBox buildControls() {
        VBox wrap = new VBox(10);
        wrap.setPadding(new Insets(14, 0, 0, 0));

        HBox playRow = new HBox(8);
        playRow.setAlignment(Pos.CENTER);
        tickBtn   = new Button(ctrl.isVolleyball() ? "▶ Next rally" : "▶ Next minute");
        skip5Btn  = new Button(ctrl.isVolleyball() ? "+5 rallies" : "+5 min");
        skip10Btn = new Button(ctrl.isVolleyball() ? "+10 rallies" : "+10 min");
        fastFwdBtn= new Button("⏩ Skip to end");
        tickBtn.getStyleClass().add("primary");
        for (Button b : new Button[]{tickBtn, skip5Btn, skip10Btn, fastFwdBtn}) b.setPrefHeight(38);

        tickBtn.setOnAction(e -> {
            ctrl.tick();
            refreshSnapshot();
        });
        skip5Btn.setOnAction(e -> {
            ctrl.skip(5);
            refreshSnapshot();
        });
        skip10Btn.setOnAction(e -> {
            ctrl.skip(10);
            refreshSnapshot();
        });
        fastFwdBtn.setOnAction(e -> {

            while (ctrl.getState() == State.RUNNING) ctrl.tick();
            refreshSnapshot();
        });

        playRow.getChildren().addAll(tickBtn, skip5Btn, skip10Btn, fastFwdBtn);

        HBox manageRow = new HBox(20);
        manageRow.setAlignment(Pos.CENTER);

        VBox homeMgmt = teamMgmtCol(ctrl.getHomeTeam(), true);
        VBox awayMgmt = teamMgmtCol(ctrl.getAwayTeam(), false);

        manageRow.getChildren().addAll(homeMgmt, awayMgmt);

        finishBtn = new Button("Continue → View result");
        finishBtn.getStyleClass().add("primary");
        finishBtn.setPrefHeight(40);
        finishBtn.setPrefWidth(260);
        finishBtn.setVisible(false);
        finishBtn.setOnAction(e -> onMatchEnd.accept(ctrl.getMatch()));
        HBox finishRow = new HBox(finishBtn);
        finishRow.setAlignment(Pos.CENTER);

        wrap.getChildren().addAll(playRow, manageRow, finishRow);
        return wrap;
    }

    private VBox teamMgmtCol(Team team, boolean home) {
        VBox col = new VBox(6);
        col.setAlignment(Pos.CENTER);
        col.setStyle("-fx-padding: 8; -fx-background-color: #ffffff; -fx-background-radius: 8; -fx-border-color: #e5e7eb; -fx-border-radius: 8;");
        Team userTeam = app.getController().getUserTeam();
        boolean userManaged = userTeam != null && userTeam == team;

        Label name = new Label((home ? "HOME · " : "AWAY · ") + team.getName());
        name.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #4b5563;");

        HBox btns = new HBox(8);
        btns.setAlignment(Pos.CENTER);

        Button sub = new Button("⇄ Substitute");
        Button tactic = new Button("◎ Tactic");
        if (home) { subHomeBtn = sub; tacticHomeBtn = tactic; }
        else      { subAwayBtn = sub; tacticAwayBtn = tactic; }

        sub.setDisable(!userManaged);
        tactic.setDisable(!userManaged);
        if (userManaged) {
            sub.setOnAction(e -> openSubDialog(team));
            tactic.setOnAction(e -> openTacticDialog(team));
        }

        Label subsLeft = new Label();
        subsLeft.setStyle("-fx-font-size: 10px; -fx-text-fill: #9ca3af;");
        updateSubsLabel(team, subsLeft, userManaged);
        sub.setUserData(subsLeft);

        btns.getChildren().addAll(sub, tactic);
        col.getChildren().addAll(name, btns, subsLeft);
        return col;
    }

    private void updateSubsLabel(Team team, Label lbl, boolean userManaged) {
        if (!userManaged) {
            lbl.setText("AI-managed substitutions");
            return;
        }
        if (ctrl.isVolleyball()) {
            lbl.setText("Subs: unlimited (volleyball)");
        } else {
            lbl.setText("Subs remaining: " + ctrl.subsRemaining(team));
        }
    }

    private void openSubDialog(Team team) {
        SubstitutionDialog dlg = new SubstitutionDialog(team, ctrl);
        dlg.showAndWait().ifPresent(pair -> {
            if (pair[0] != null && pair[1] != null) {
                boolean ok = ctrl.substitute(team, pair[0], pair[1]);
                if (!ok) {
                    new Alert(Alert.AlertType.WARNING, "Substitution failed (limit reached or invalid players).").showAndWait();
                }

                Button btn = (team == ctrl.getHomeTeam()) ? subHomeBtn : subAwayBtn;
                Label  lbl = (Label) btn.getUserData();
                if (lbl != null) updateSubsLabel(team, lbl, true);
            }
        });
    }

    private void openTacticDialog(Team team) {
        ChoiceDialog<String> dlg = new ChoiceDialog<>(
            tacticNameOf(team), tacticOptions()
        );
        dlg.setTitle("Change tactic");
        dlg.setHeaderText(team.getName() + " — pick a new tactic");
        dlg.setContentText("Tactic:");
        dlg.showAndWait().ifPresent(t ->
            ctrl.changeTactic(team, TacticFactory.create(t)));
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

    private void wireController() {
        ctrl.setOnEvent(this::renderEvent);
        ctrl.setOnStateChange(s -> Platform.runLater(() -> updateUIForState(s)));
    }

    private void renderEvent(MatchEvent e) {
        Platform.runLater(() -> {
            HBox row = new HBox(8);
            row.setPadding(new Insets(6, 8, 6, 8));
            row.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 4;");

            String when = ctrl.isVolleyball() ? ("Set " + (ctrl.getCurrentSet() + 1) + " · " + e.getMinute())
                                              : (e.getMinute() + "'");
            Label time = new Label(when);
            time.setStyle("-fx-font-family: 'Consolas', monospace; -fx-text-fill: #4b5563; -fx-font-size: 11px; -fx-font-weight: 700;");
            time.setMinWidth(70);

            Label icon = new Label(iconFor(e.getType()));
            icon.setStyle("-fx-font-size: 14px;");
            icon.setMinWidth(24);

            String text = describeEvent(e);
            Label lbl = new Label(text);
            lbl.setStyle("-fx-text-fill: #111827; -fx-font-size: 12px;");

            row.getChildren().addAll(time, icon, lbl);
            eventLog.getChildren().add(0, row);

            if (ctrl.isVolleyball()) {
                homeScoreLbl.setText(String.valueOf(ctrl.getHomeSets()));
                awayScoreLbl.setText(String.valueOf(ctrl.getAwaySets()));
                clockLbl.setText("Set " + (ctrl.getCurrentSet() + 1) + " · " + ctrl.getRallyHome() + "–" + ctrl.getRallyAway());
            } else {
                homeScoreLbl.setText(String.valueOf(ctrl.getHomeScore()));
                awayScoreLbl.setText(String.valueOf(ctrl.getAwayScore()));
                clockLbl.setText(ctrl.getMinute() + "'");
            }
        });
    }

    private void updateUIForState(State s) {
        refreshSnapshot();
        switch (s) {
            case FINISHED -> {
                statusLbl.setText("FULL TIME");
                statusLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #16a34a; -fx-font-weight: 700;");
                tickBtn.setDisable(true);
                skip5Btn.setDisable(true);
                skip10Btn.setDisable(true);
                fastFwdBtn.setDisable(true);
                subHomeBtn.setDisable(true);
                subAwayBtn.setDisable(true);
                tacticHomeBtn.setDisable(true);
                tacticAwayBtn.setDisable(true);
                finishBtn.setVisible(true);
            }
            case PAUSED -> statusLbl.setText("Paused");
            case RUNNING -> statusLbl.setText("Match in progress…");
            default -> {}
        }
    }

    private void refreshSnapshot() {
        Platform.runLater(() -> {
            if (ctrl.isVolleyball()) {
                homeScoreLbl.setText(String.valueOf(ctrl.getHomeSets()));
                awayScoreLbl.setText(String.valueOf(ctrl.getAwaySets()));
                clockLbl.setText("Set " + (ctrl.getCurrentSet() + 1) + " · " + ctrl.getRallyHome() + "–" + ctrl.getRallyAway());
            } else {
                homeScoreLbl.setText(String.valueOf(ctrl.getHomeScore()));
                awayScoreLbl.setText(String.valueOf(ctrl.getAwayScore()));
                clockLbl.setText(ctrl.getMinute() + "'");
            }
        });
    }

    private String iconFor(EventType t) {
        return switch (t) {
            case GOAL -> "⚽";
            case INJURY -> "🚑";
            case SUBSTITUTION -> "⇄";
            case YELLOW_CARD -> "🟨";
            case RED_CARD -> "🟥";
            default -> "•";
        };
    }

    private String describeEvent(MatchEvent e) {
        String who = e.getPlayer() != null ? e.getPlayer().getName() : "?";
        String tm  = e.getTeam()   != null ? e.getTeam().getName()   : "";
        return switch (e.getType()) {
            case GOAL -> (ctrl.isVolleyball() ? "Point: " : "GOAL: ") + who + " (" + tm + ")";
            case INJURY -> "Injury: " + who + " — " + tm
                    + (e.getTeam() == app.getController().getUserTeam() ? " · use Substitute to replace" : " · AI will replace automatically");
            case SUBSTITUTION -> "Substitution: " + who + " comes on (" + tm + ")";
            case YELLOW_CARD -> "Yellow card: " + who + " (" + tm + ")";
            case RED_CARD -> "Red card: " + who + " (" + tm + ")";
            default -> who + " — " + tm;
        };
    }
}
