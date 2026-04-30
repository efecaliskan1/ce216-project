package ui.views;

import core.domain.EventType;
import core.domain.Match;
import core.domain.MatchEvent;
import core.domain.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.App;
import ui.TeamLogoFactory;

import java.util.HashMap;
import java.util.Map;

public class PostMatchView {

    private final App app;
    private final Match match;
    private final Runnable onContinue;
    private final VBox root;

    public PostMatchView(App app, Match match, Runnable onContinue) {
        this.app   = app;
        this.match = match;
        this.onContinue = onContinue;
        this.root  = build();
    }

    public Parent getRoot() { return root; }

    private VBox build() {
        VBox page = new VBox(18);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setStyle("-fx-background-color: #f3f4f6;");

        Label title = new Label("Full time");
        title.getStyleClass().add("h1");

        // Big result banner
        HBox banner = new HBox(28);
        banner.setAlignment(Pos.CENTER);
        banner.getStyleClass().add("card");

        VBox home = new VBox(10);
        home.setAlignment(Pos.CENTER);
        home.getChildren().addAll(
            TeamLogoFactory.create(match.getHomeTeam().getName(), 80),
            namedLabel(match.getHomeTeam().getName())
        );
        VBox away = new VBox(10);
        away.setAlignment(Pos.CENTER);
        away.getChildren().addAll(
            TeamLogoFactory.create(match.getAwayTeam().getName(), 80),
            namedLabel(match.getAwayTeam().getName())
        );

        Label score = new Label(match.getResult().getHomeScore() + " : " + match.getResult().getAwayScore());
        score.setStyle("-fx-font-size: 56px; -fx-font-weight: 900; -fx-text-fill: #2563eb; -fx-font-family: 'Consolas', monospace;");

        banner.getChildren().addAll(home, score, away);

        // Two-col split: scorers + events
        HBox split = new HBox(16);
        split.getChildren().addAll(scorersCard(), eventsCard());
        HBox.setHgrow(split.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(split.getChildren().get(1), Priority.ALWAYS);
        VBox.setVgrow(split, Priority.ALWAYS);

        Button cont = new Button("Continue");
        cont.getStyleClass().add("primary");
        cont.setPrefHeight(40);
        cont.setPrefWidth(220);
        cont.setOnAction(e -> onContinue.run());
        HBox btmRow = new HBox(cont);
        btmRow.setAlignment(Pos.CENTER);

        page.getChildren().addAll(title, banner, split, btmRow);
        return page;
    }

    private Label namedLabel(String s) {
        Label l = new Label(s);
        l.setStyle("-fx-text-fill: #111827; -fx-font-size: 14px; -fx-font-weight: 700;");
        return l;
    }

    private VBox scorersCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        Label h = new Label("Top scorers");
        h.getStyleClass().add("h3");

        Map<Player, Integer> goals = new HashMap<>();
        for (MatchEvent e : match.getEventLog()) {
            if (e.getType() == EventType.GOAL && e.getPlayer() != null) {
                goals.merge(e.getPlayer(), 1, Integer::sum);
            }
        }

        VBox list = new VBox(6);
        if (goals.isEmpty()) {
            Label empty = new Label("No scorers recorded.");
            empty.getStyleClass().add("subtitle");
            list.getChildren().add(empty);
        } else {
            goals.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(8)
                .forEach(en -> {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    Label name = new Label(en.getKey().getName());
                    name.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px;");
                    name.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(name, Priority.ALWAYS);
                    Label cnt = new Label(en.getValue() + (en.getValue() > 1 ? " goals" : " goal"));
                    cnt.getStyleClass().addAll("badge", "badge-success");
                    row.getChildren().addAll(name, cnt);
                    list.getChildren().add(row);
                });
        }
        card.getChildren().addAll(h, list);
        return card;
    }

    private VBox eventsCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        Label h = new Label("Key events");
        h.getStyleClass().add("h3");

        VBox list = new VBox(4);
        for (MatchEvent e : match.getEventLog()) {
            if (e.getType() == EventType.GOAL || e.getType() == EventType.INJURY
             || e.getType() == EventType.RED_CARD) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                Label time = new Label(e.getMinute() + "'");
                time.setStyle("-fx-font-family: 'Consolas', monospace; -fx-text-fill: #4b5563; -fx-font-size: 11px; -fx-font-weight: 700;");
                time.setMinWidth(40);
                Label desc = new Label(describe(e));
                desc.setStyle("-fx-text-fill: #111827; -fx-font-size: 12px;");
                row.getChildren().addAll(time, desc);
                list.getChildren().add(row);
            }
        }
        ScrollPane sp = new ScrollPane(list);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        card.getChildren().addAll(h, sp);
        return card;
    }

    private String describe(MatchEvent e) {
        String who = e.getPlayer() != null ? e.getPlayer().getName() : "?";
        String tm  = e.getTeam()   != null ? e.getTeam().getName()   : "";
        return switch (e.getType()) {
            case GOAL -> "⚽ Goal: " + who + " (" + tm + ")";
            case INJURY -> "🚑 Injury: " + who + " (" + tm + ")";
            case RED_CARD -> "🟥 Red card: " + who + " (" + tm + ")";
            default -> who + " — " + tm;
        };
    }
}
