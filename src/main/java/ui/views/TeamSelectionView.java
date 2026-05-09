package ui.views;

import core.domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.App;
import ui.TeamLogoFactory;

import java.util.List;

public class TeamSelectionView {

    private final App app;
    private final BorderPane root;
    private Team   selected;
    private VBox   detailPane;
    private Button confirmBtn;

    public TeamSelectionView(App app) {
        this.app  = app;
        this.root = build();
    }

    public Parent getRoot() { return root; }

    private BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #f3f4f6;");
        bp.setPadding(new Insets(28, 32, 28, 32));

        VBox top = new VBox(6);
        Label title = new Label("Choose your club");
        title.getStyleClass().add("h1");
        Label sub = new Label("Pick the team you want to manage this season. The other 15 teams will be controlled by the AI.");
        sub.getStyleClass().add("subtitle");
        top.getChildren().addAll(title, sub);
        BorderPane.setMargin(top, new Insets(0, 0, 16, 0));
        bp.setTop(top);

        List<Team> teams = app.getController().getSeason().getLeague().getTeams();
        TilePane grid = new TilePane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPrefColumns(4);

        for (Team t : teams) {
            grid.getChildren().add(teamTile(t));
        }

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        bp.setCenter(sp);

        detailPane = new VBox(10);
        detailPane.getStyleClass().add("card");
        detailPane.setPrefWidth(280);
        detailPane.setPadding(new Insets(18));
        Label hint = new Label("Click a club to see details.");
        hint.getStyleClass().add("subtitle");
        detailPane.getChildren().add(hint);
        BorderPane.setMargin(detailPane, new Insets(0, 0, 0, 16));
        bp.setRight(detailPane);

        confirmBtn = new Button("Start career →");
        confirmBtn.getStyleClass().add("primary");
        confirmBtn.setPrefHeight(44);
        confirmBtn.setPrefWidth(240);
        confirmBtn.setDisable(true);
        confirmBtn.setOnAction(e -> {
            if (selected != null) {
                app.getController().setUserTeam(selected);
                app.showMain();
            }
        });
        HBox btmRow = new HBox(confirmBtn);
        btmRow.setAlignment(Pos.CENTER);
        btmRow.setPadding(new Insets(16, 0, 0, 0));
        bp.setBottom(btmRow);

        return bp;
    }

    private VBox teamTile(Team t) {
        VBox tile = new VBox(8);
        tile.getStyleClass().add("team-tile");
        tile.setAlignment(Pos.CENTER);
        tile.setPrefSize(160, 140);

        Canvas logo = TeamLogoFactory.create(t.getName(), 64);
        Label name = new Label(t.getName());
        name.setStyle("-fx-text-fill: #111827; -fx-font-size: 12px; -fx-font-weight: 600;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(140);

        tile.getChildren().addAll(logo, name);
        tile.setOnMouseClicked(e -> selectTeam(t, tile));
        return tile;
    }

    private void selectTeam(Team t, VBox tile) {
        selected = t;

        for (var n : ((TilePane) ((ScrollPane) root.getCenter()).getContent()).getChildren()) {
            n.setStyle("");
        }
        tile.setStyle("-fx-border-color: #2563eb; -fx-border-width: 2; -fx-background-color: #eff6ff;");
        confirmBtn.setDisable(false);
        renderDetails(t);
    }

    private void renderDetails(Team t) {
        detailPane.getChildren().clear();

        Canvas big = TeamLogoFactory.create(t.getName(), 96);
        HBox logoRow = new HBox(big);
        logoRow.setAlignment(Pos.CENTER);
        logoRow.setPadding(new Insets(0, 0, 6, 0));

        Label name = new Label(t.getName());
        name.getStyleClass().add("h2");
        name.setStyle(name.getStyle() + "; -fx-text-alignment: center;");

        Label coachLbl = new Label("COACH");
        coachLbl.getStyleClass().add("caption");
        Label coachName = new Label(t.getCoach() != null ? t.getCoach().getName() : "—");
        coachName.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px;");

        Label specLbl = new Label("SPECIALTY");
        specLbl.getStyleClass().add("caption");
        Label spec = new Label(t.getCoach() != null ? t.getCoach().getSpecialty() : "—");
        spec.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px;");

        Label squadLbl = new Label("SQUAD SIZE");
        squadLbl.getStyleClass().add("caption");
        Label squad = new Label(String.valueOf(t.getPlayers().size()));
        squad.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px;");

        detailPane.getChildren().addAll(logoRow, name, coachLbl, coachName, specLbl, spec, squadLbl, squad);
    }
}
