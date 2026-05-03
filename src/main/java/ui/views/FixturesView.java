package ui.views;

import core.domain.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.App;
import ui.TeamLogoFactory;

public class FixturesView {

    private final App app;
    private final VBox root;

    public FixturesView(App app) {
        this.app  = app;
        this.root = build();
    }

    public Parent getRoot() { return root; }

    private VBox build() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(28, 32, 28, 32));
        page.setStyle("-fx-background-color: #f3f4f6;");

        Label title = new Label("Fixtures");
        title.getStyleClass().add("h1");
        Label sub = new Label("All " + app.getController().getSeason().getGameWeeks().size() + " match weeks");
        sub.getStyleClass().add("subtitle");

        ScrollPane scroller = new ScrollPane();
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox list = new VBox(12);
        int currentWeek = app.getController().getSeason().getCurrentWeek();
        for (GameWeek gw : app.getController().getSeason().getGameWeeks()) {
            list.getChildren().add(weekCard(gw, gw.getWeekNumber() == currentWeek));
        }
        scroller.setContent(list);
        VBox.setVgrow(scroller, Priority.ALWAYS);

        page.getChildren().addAll(title, sub, scroller);
        return page;
    }

    private VBox weekCard(GameWeek gw, boolean isCurrent) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        if (isCurrent) card.getStyleClass().add("card-accent");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label weekLbl = new Label("Week " + (gw.getWeekNumber() + 1));
        weekLbl.getStyleClass().add("h3");

        Label statusBadge = new Label(gw.isCompleted() ? "COMPLETED"
                                    : (isCurrent ? "UP NEXT" : "UPCOMING"));
        statusBadge.getStyleClass().add("badge");
        if (gw.isCompleted())  statusBadge.getStyleClass().add("badge-success");
        else if (isCurrent)    statusBadge.getStyleClass().add("badge-accent");
        else                   statusBadge.getStyleClass().add("badge-info");
        header.getChildren().addAll(weekLbl, statusBadge);

        VBox fixtures = new VBox(4);
        for (Match m : gw.getFixtures()) fixtures.getChildren().add(matchRow(m));

        card.getChildren().addAll(header, fixtures);
        return card;
    }

    private HBox matchRow(Match m) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: transparent;");

        Label homeName = new Label(m.getHomeTeam().getName());
        homeName.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px; -fx-font-weight: 500;");
        homeName.setMaxWidth(Double.MAX_VALUE);
        homeName.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(homeName, Priority.ALWAYS);

        var homeLogo = TeamLogoFactory.create(m.getHomeTeam().getName(), 28);

      
        Label score;
        if (m.isPlayed()) {
            score = new Label(m.getResult().getHomeScore() + " : " + m.getResult().getAwayScore());
            score.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #2563eb;");
        } else {
            score = new Label("vs");
            score.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af; -fx-font-weight: 600;");
        }
        score.setMinWidth(70);
        score.setAlignment(Pos.CENTER);

    
        var awayLogo = TeamLogoFactory.create(m.getAwayTeam().getName(), 28);

        Label awayName = new Label(m.getAwayTeam().getName());
        awayName.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px; -fx-font-weight: 500;");
        awayName.setMaxWidth(Double.MAX_VALUE);
        awayName.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(awayName, Priority.ALWAYS);

        row.getChildren().addAll(homeName, homeLogo, score, awayLogo, awayName);
        return row;
    }
}
