package ui.views;

import core.domain.Season;
import core.services.NameDataService;
import core.services.SaveLoadService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ui.App;
import ui.TeamLogoFactory;

import java.util.Random;

public class StartView {

    private final App app;
    private final BorderPane root;

    public StartView(App app) {
        this.app  = app;
        this.root = build();
    }

    public Parent getRoot() { return root; }

    private BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0a0e1a;");

        // Left panel: gradient + hero + sample logos grid
        StackPane leftCol = new StackPane();
        leftCol.setPrefWidth(520);
        Rectangle bg = new Rectangle(520, 820);
        bg.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#f59e0b")),
                new Stop(0.5, Color.web("#b45309")),
                new Stop(1.0, Color.web("#0a0e1a"))));
        bg.widthProperty().bind(leftCol.widthProperty());
        bg.heightProperty().bind(leftCol.heightProperty());

        VBox heroCopy = new VBox(14);
        heroCopy.setPadding(new Insets(60, 56, 60, 56));
        heroCopy.setAlignment(Pos.TOP_LEFT);

        Text brand = new Text("SPORT MANAGER");
        brand.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-fill: white; -fx-opacity: 0.8;");
        Text hero = new Text("Take control.\nBuild a dynasty.");
        hero.setStyle("-fx-font-size: 40px; -fx-font-weight: 900; -fx-fill: white; -fx-line-spacing: -6;");
        Text tagline = new Text("A tactical management simulator for football and volleyball.");
        tagline.setStyle("-fx-font-size: 13px; -fx-fill: white; -fx-opacity: 0.88;");

        // Logo preview grid (sample of real team names)
        GridPane logos = new GridPane();
        logos.setHgap(14);
        logos.setVgap(14);
        logos.setPadding(new Insets(28, 0, 0, 0));
        var sample = NameDataService.pickTeamNames(12, new Random(42));
        for (int i = 0; i < sample.size(); i++) {
            Canvas c = TeamLogoFactory.create(sample.get(i), 60);
            logos.add(c, i % 4, i / 4);
        }

        heroCopy.getChildren().addAll(brand, hero, tagline, logos);
        leftCol.getChildren().addAll(bg, heroCopy);
        StackPane.setAlignment(heroCopy, Pos.TOP_LEFT);

        // Right panel
        VBox rightCol = new VBox(24);
        rightCol.setPadding(new Insets(80, 80, 80, 80));
        rightCol.setAlignment(Pos.CENTER_LEFT);
        rightCol.setStyle("-fx-background-color: #0a0e1a;");
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        Label welcome = new Label("Welcome, manager");
        welcome.getStyleClass().add("h1");
        Label sub = new Label("Start a new career or pick up where you left off.");
        sub.getStyleClass().add("subtitle");

        Label sportLabel = new Label("CHOOSE SPORT");
        sportLabel.getStyleClass().add("caption");
        ComboBox<String> sportBox = new ComboBox<>();
        sportBox.getItems().addAll("Football", "Volleyball");
        sportBox.setValue("Football");
        sportBox.setMaxWidth(320);
        sportBox.setPrefHeight(42);

        Button startBtn = new Button("Start new season →");
        startBtn.getStyleClass().add("primary");
        startBtn.setPrefWidth(320);
        startBtn.setPrefHeight(46);
        startBtn.setOnAction(e -> {
            app.getController().startSeason(sportBox.getValue().toLowerCase());
            app.showMain();
        });

        Separator sep = new Separator();
        sep.setPadding(new Insets(8, 0, 8, 0));
        sep.setMaxWidth(320);

        Label loadLabel = new Label("CONTINUE A SAVED GAME");
        loadLabel.getStyleClass().add("caption");

        HBox loadRow = new HBox(10);
        loadRow.setAlignment(Pos.CENTER_LEFT);
        ComboBox<Integer> slotBox = new ComboBox<>();
        slotBox.setPrefWidth(120);
        slotBox.setPrefHeight(42);
        SaveLoadService svc = new SaveLoadService();
        for (int i = 1; i <= 5; i++) if (svc.slotExists(i)) slotBox.getItems().add(i);
        if (!slotBox.getItems().isEmpty()) slotBox.setValue(slotBox.getItems().get(0));

        Button loadBtn = new Button("Load slot");
        loadBtn.setPrefHeight(42);
        loadBtn.setPrefWidth(190);
        loadBtn.setDisable(slotBox.getItems().isEmpty());
        loadBtn.setOnAction(e -> {
            Integer slot = slotBox.getValue();
            if (slot == null) return;
            try {
                Season s = svc.load(slot);
                app.getController().loadExistingSeason(s);
                app.showMain();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Could not load slot " + slot + ": " + ex.getMessage()).showAndWait();
            }
        });
        loadRow.getChildren().addAll(slotBox, loadBtn);

        if (slotBox.getItems().isEmpty()) {
            Label noSaves = new Label("No save slots found.");
            noSaves.getStyleClass().add("subtitle");
            loadRow.getChildren().add(noSaves);
        }

        rightCol.getChildren().addAll(
            welcome, sub,
            spacer(8),
            sportLabel, sportBox, startBtn,
            sep,
            loadLabel, loadRow
        );

        HBox container = new HBox(leftCol, rightCol);
        bp.setCenter(container);
        return bp;
    }

    private Region spacer(double h) { Region r = new Region(); r.setMinHeight(h); return r; }
}
