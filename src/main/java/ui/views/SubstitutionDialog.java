package ui.views;

import core.app.LiveMatchController;
import core.domain.Player;
import core.domain.Team;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SubstitutionDialog extends Dialog<Player[]> {

    private TableView<Player> outTable;
    private TableView<Player> inTable;

    public SubstitutionDialog(Team team, LiveMatchController ctrl) {
        setTitle("Substitution — " + team.getName());
        setHeaderText("Pick the player coming OFF and the player coming ON.\n"
                    + (ctrl.isVolleyball() ? "Volleyball: substitutions are unlimited."
                                           : "Football: " + ctrl.subsRemaining(team) + " substitutions remaining."));

        DialogPane pane = getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        pane.setPrefSize(720, 440);

        HBox split = new HBox(16);
        split.setPadding(new Insets(10));

        VBox outBox = new VBox(6);
        VBox inBox  = new VBox(6);
        Label outLbl = new Label("ON THE PITCH (going off)");
        outLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #4b5563;");
        Label inLbl  = new Label("BENCH (coming on)");
        inLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #4b5563;");

        outTable = playerTable(team.getStartingLineup(), false);
        inTable  = playerTable(team.getBench(),          true);

        VBox.setVgrow(outTable, Priority.ALWAYS);
        VBox.setVgrow(inTable, Priority.ALWAYS);

        outBox.getChildren().addAll(outLbl, outTable);
        inBox.getChildren().addAll(inLbl, inTable);
        HBox.setHgrow(outBox, Priority.ALWAYS);
        HBox.setHgrow(inBox,  Priority.ALWAYS);
        split.getChildren().addAll(outBox, inBox);

        pane.setContent(split);

        setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Player out = outTable.getSelectionModel().getSelectedItem();
                Player in  = inTable.getSelectionModel().getSelectedItem();
                if (out == null || in == null) return null;
                if (in.isInjured()) {
                    new Alert(Alert.AlertType.WARNING, in.getName() + " is injured and cannot come on.").showAndWait();
                    return null;
                }
                return new Player[]{ out, in };
            }
            return null;
        });
    }

    private TableView<Player> playerTable(java.util.List<Player> source, boolean grayInjured) {
        TableView<Player> tv = new TableView<>();
        tv.setItems(FXCollections.observableArrayList(source));
        tv.setPlaceholder(new Label("(empty)"));

        TableColumn<Player, Number> shirt = new TableColumn<>("#");
        shirt.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getShirtNumber()));
        shirt.setPrefWidth(40);

        TableColumn<Player, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        name.setPrefWidth(160);

        TableColumn<Player, String> pos = new TableColumn<>("Pos");
        pos.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPosition()));
        pos.setPrefWidth(60);

        TableColumn<Player, Number> ovr = new TableColumn<>("Ovr");
        ovr.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getOverall()));
        ovr.setPrefWidth(55);

        TableColumn<Player, String> stat = new TableColumn<>("Status");
        stat.setCellValueFactory(cd -> {
            Player p = cd.getValue();
            return new SimpleStringProperty(
                p.isInjured() ? "INJ (" + p.getInjuredGamesRemaining() + ")"
                              : "Fatigue " + p.getFatigueLevel());
        });
        stat.setPrefWidth(110);

        tv.setRowFactory(t -> new TableRow<>() {
            @Override protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) {
                    setStyle(""); setDisable(false);
                } else if (p.isInjured()) {
                    setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-opacity: 0.7;");
                    setDisable(grayInjured);
                } else {
                    setStyle(""); setDisable(false);
                }
            }
        });

        tv.getColumns().addAll(shirt, name, pos, ovr, stat);
        return tv;
    }
}
