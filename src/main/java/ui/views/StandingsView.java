package ui.views;

import core.domain.StandingEntry;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.App;
import ui.TeamLogoFactory;

import java.util.ArrayList;
import java.util.List;

public class StandingsView {

    private final App app;
    private final VBox root;

    public StandingsView(App app) {
        this.app  = app;
        this.root = build();
    }

    public Parent getRoot() { return root; }

    private VBox build() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(36, 48, 36, 48));
        page.setStyle("-fx-background-color: #0a0e1a;");

        Label title = new Label("League Table");
        title.getStyleClass().add("h1");
        boolean isVb = app.getController().getSport().getName().equalsIgnoreCase("Volleyball");
        Label sub = new Label(isVb
                ? "Sorted by points, wins, set ratio, head-to-head"
                : "Sorted by points, head-to-head, goal difference, goals for");
        sub.getStyleClass().add("subtitle");

        TableView<Row> table = buildTable(isVb);
        VBox.setVgrow(table, Priority.ALWAYS);

        page.getChildren().addAll(title, sub, table);
        return page;
    }

    private TableView<Row> buildTable(boolean isVolleyball) {
        TableView<Row> tv = new TableView<>();
        tv.setPlaceholder(new Label("No matches played yet."));
        tv.setRowFactory(t -> {
            TableRow<Row> r = new TableRow<>();
            r.setPrefHeight(46);
            return r;
        });

        TableColumn<Row, Number> rank = numCol("#",  r -> r.rank, 50);

        // Logo column
        TableColumn<Row, Row> logoCol = new TableColumn<>("");
        logoCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue()));
        logoCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Row row, boolean empty) {
                super.updateItem(row, empty);
                if (empty || row == null) { setGraphic(null); }
                else {
                    setGraphic(TeamLogoFactory.create(row.team, 32));
                    setAlignment(Pos.CENTER);
                }
            }
        });
        logoCol.setPrefWidth(50);
        logoCol.setMinWidth(50);
        logoCol.setSortable(false);

        // Team name column
        TableColumn<Row, String> name = new TableColumn<>("TEAM");
        name.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().team));
        name.setPrefWidth(240);
        name.setMinWidth(240);

        TableColumn<Row, Number> played = numCol("P",   r -> r.played, 45);
        TableColumn<Row, Number> won    = numCol("W",   r -> r.won,    45);
        TableColumn<Row, Number> drawn  = numCol("D",   r -> r.drawn,  45);
        TableColumn<Row, Number> lost   = numCol("L",   r -> r.lost,   45);
        TableColumn<Row, Number> gf     = numCol(isVolleyball ? "SF" : "GF", r -> r.gf, 55);
        TableColumn<Row, Number> ga     = numCol(isVolleyball ? "SA" : "GA", r -> r.ga, 55);
        TableColumn<Row, Number> gd     = numCol(isVolleyball ? "SD" : "GD", r -> r.gd, 55);
        TableColumn<Row, Number> pts    = numCol("PTS", r -> r.points, 70);

        tv.getColumns().addAll(rank, logoCol, name, played, won, drawn, lost, gf, ga, gd, pts);

        List<Row> rows = new ArrayList<>();
        int r = 1;
        for (StandingEntry e : app.getController().getStandingsService().getTable()) {
            rows.add(new Row(r++, e));
        }
        tv.setItems(FXCollections.observableArrayList(rows));
        return tv;
    }

    private static TableColumn<Row, Number> numCol(String title,
            java.util.function.ToIntFunction<Row> getter, double width) {
        TableColumn<Row, Number> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> new SimpleIntegerProperty(getter.applyAsInt(cd.getValue())));
        c.setPrefWidth(width); c.setMinWidth(width);
        c.setStyle("-fx-alignment: CENTER;");
        return c;
    }

    public static class Row {
        final int rank, played, won, drawn, lost, gf, ga, gd, points;
        final String team;
        Row(int rank, StandingEntry e) {
            this.rank = rank;
            this.team = e.getTeam().getName();
            this.played = e.getPlayed();
            this.won = e.getWon();
            this.drawn = e.getDrawn();
            this.lost = e.getLost();
            this.gf = e.getGoalsFor();
            this.ga = e.getGoalsAgainst();
            this.gd = e.getGoalDifference();
            this.points = e.getPoints();
        }
    }
}
