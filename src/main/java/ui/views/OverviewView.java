package ui.views;

import core.domain.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.App;
import ui.TeamLogoFactory;

import java.util.List;

public class OverviewView {

    private final App app;
    private final ScrollPane root;

    public OverviewView(App app) {
        this.app  = app;
        this.root = build();
    }

    public Parent getRoot() { return root; }

    private ScrollPane build() {
        Season season = app.getController().getSeason();

        VBox page = new VBox(20);
        page.setPadding(new Insets(36, 48, 36, 48));

        Label title = new Label(season.getLeague().getName());
        title.getStyleClass().add("h1");
        Label subtitle = new Label("Season " + season.getSeasonNumber()
                + " · Week " + (season.getCurrentWeek() + 1) + " of " + season.getGameWeeks().size()
                + " · " + season.getSport().getName());
        subtitle.getStyleClass().add("subtitle");

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
                statCard("WEEK",    String.valueOf(season.getCurrentWeek() + 1)),
                statCard("TEAMS",   String.valueOf(season.getLeague().getTeams().size())),
                statCard("MATCHES", String.valueOf(season.getLeague().getFixtures().size())),
                statCard("STATUS",  season.isFinished() ? "Finished" : "In progress")
        );

        HBox split = new HBox(16);
        split.getChildren().addAll(topOfTableCard(), nextFixturesCard());
        HBox.setHgrow(split.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(split.getChildren().get(1), Priority.ALWAYS);

        page.getChildren().addAll(title, subtitle, stats, split);

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #0a0e1a; -fx-background: #0a0e1a;");
        return sp;
    }

    private VBox statCard(String label, String value) {
        VBox v = new VBox(6);
        v.getStyleClass().addAll("card", "card-accent");
        v.setPrefWidth(200);
        Label l = new Label(label);
        l.getStyleClass().add("stat-label");
        Label val = new Label(value);
        val.getStyleClass().add("stat-value");
        v.getChildren().addAll(l, val);
        return v;
    }

    private VBox topOfTableCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        Label h = new Label("Top of the table");
        h.getStyleClass().add("h3");

        List<StandingEntry> standings = app.getController().getStandingsService().getTable();
        VBox list = new VBox(8);
        int rank = 1;
        for (StandingEntry e : standings) {
            if (rank > 5) break;
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            Label rankLbl = new Label("#" + rank);
            rankLbl.getStyleClass().add("caption");
            rankLbl.setMinWidth(30);

            var logo = TeamLogoFactory.create(e.getTeam().getName(), 32);

            Label name = new Label(e.getTeam().getName());
            name.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(name, Priority.ALWAYS);

            Label pts = new Label(e.getPoints() + " pts");
            pts.getStyleClass().addAll("badge", "badge-accent");

            row.getChildren().addAll(rankLbl, logo, name, pts);
            list.getChildren().add(row);
            rank++;
        }
        if (standings.isEmpty()) {
            Label empty = new Label("No matches played yet.");
            empty.getStyleClass().add("subtitle");
            list.getChildren().add(empty);
        }
        card.getChildren().addAll(h, list);
        return card;
    }

    private VBox nextFixturesCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        Label h = new Label("Next fixtures");
        h.getStyleClass().add("h3");

        Season season = app.getController().getSeason();
        GameWeek gw = season.getCurrentGameWeek();

        VBox list = new VBox(8);
        if (gw == null) {
            Label empty = new Label("Season is finished.");
            empty.getStyleClass().add("subtitle");
            list.getChildren().add(empty);
        } else {
            int shown = 0;
            for (Match m : gw.getFixtures()) {
                if (shown++ >= 6) break;
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER);

                var homeLogo = TeamLogoFactory.create(m.getHomeTeam().getName(), 28);
                Label home = new Label(m.getHomeTeam().getName());
                home.setMaxWidth(Double.MAX_VALUE);
                home.setStyle("-fx-alignment: CENTER_RIGHT;");
                HBox.setHgrow(home, Priority.ALWAYS);

                Label vs = new Label("vs");
                vs.getStyleClass().add("subtitle");

                Label away = new Label(m.getAwayTeam().getName());
                away.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(away, Priority.ALWAYS);
                var awayLogo = TeamLogoFactory.create(m.getAwayTeam().getName(), 28);

                row.getChildren().addAll(home, homeLogo, vs, awayLogo, away);
                list.getChildren().add(row);
            }
        }
        card.getChildren().addAll(h, list);
        return card;
    }
}
