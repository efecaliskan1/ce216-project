package ui.views;

import core.services.SaveLoadService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.App;

public class MainView {

    private final App        app;
    private final String     initialSection;
    private final BorderPane root;
    private final StackPane  content;
    private Button           activeBtn;

    public MainView(App app) {
        this(app, "overview");
    }

    public MainView(App app, String initialSection) {
        this.app     = app;
        this.initialSection = initialSection == null ? "overview" : initialSection.toLowerCase();
        this.content = new StackPane();
        this.content.setStyle("-fx-background-color: #f3f4f6;");
        this.root    = build();
    }

    public Parent getRoot() { return root; }

    private BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setLeft(buildSidebar());
        bp.setCenter(content);
        return bp;
    }

    private VBox buildSidebar() {
        VBox side = new VBox(2);
        side.getStyleClass().add("sidebar");
        side.setPrefWidth(220);

        // Brand with circular logo
        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(18, 20, 22, 20));
        Circle dot = new Circle(12);
        dot.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2563eb")),
                new Stop(1, Color.web("#1d4ed8"))));
        Label brandLabel = new Label("SPORT MANAGER");
        brandLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 12));
        brandLabel.setStyle("-fx-text-fill: #111827; -fx-letter-spacing: 1.2;");
        brandBox.getChildren().addAll(dot, brandLabel);

        Button overview = navBtn("Overview",     this::showOverview);
        Button table    = navBtn("League Table", this::showTable);
        Button fixtures = navBtn("Fixtures",     this::showFixtures);
        Button squad    = navBtn("My Squad",     this::showSquad);
        Button matchDay = navBtn("Match Day",    this::showMatchDay);

        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);

        Button save = navBtn("Save game",    this::showSave);
        Button load = navBtn("Load game",    this::showLoad);
        Button quit = navBtn("Quit to menu", this::confirmQuit);

        activateInitialSection(overview, table, fixtures, squad, matchDay);
        side.getChildren().addAll(brandBox, overview, table, fixtures, squad, matchDay, grow, save, load, quit);
        return side;
    }

    private Button navBtn(String text, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("nav-button");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> { setActive(b); action.run(); });
        return b;
    }

    private void setActive(Button b) {
        if (activeBtn != null) activeBtn.getStyleClass().remove("active");
        activeBtn = b;
        if (!b.getStyleClass().contains("active")) b.getStyleClass().add("active");
    }

    private void activateInitialSection(Button overview, Button table, Button fixtures, Button squad, Button matchDay) {
        switch (initialSection) {
            case "table", "league table", "standings" -> {
                setActive(table);
                showTable();
            }
            case "fixtures" -> {
                setActive(fixtures);
                showFixtures();
            }
            case "squad", "my squad" -> {
                setActive(squad);
                showSquad();
            }
            case "matchday", "match day" -> {
                setActive(matchDay);
                showMatchDay();
            }
            default -> {
                setActive(overview);
                showOverview();
            }
        }
    }

    private void showOverview() { content.getChildren().setAll(new OverviewView(app).getRoot()); }
    private void showTable()    { content.getChildren().setAll(new StandingsView(app).getRoot()); }
    private void showFixtures() { content.getChildren().setAll(new FixturesView(app).getRoot()); }
    private void showSquad()    { content.getChildren().setAll(new SquadView(app).getRoot()); }
    private void showMatchDay() { content.getChildren().setAll(new MatchDayView(app, this::showOverview).getRoot()); }

    private void showSave() {
        ChoiceDialog<Integer> dlg = new ChoiceDialog<>(1, 1, 2, 3, 4, 5);
        dlg.setTitle("Save game");
        dlg.setHeaderText("Pick a save slot");
        dlg.setContentText("Slot:");
        dlg.showAndWait().ifPresent(slot -> {
            try {
                new SaveLoadService().save(app.getController().getSeason(), slot);
                info("Saved to slot " + slot + ".");
            } catch (Exception ex) {
                error("Save failed: " + ex.getMessage());
            }
        });
    }

    private void showLoad() {
        SaveLoadService svc = new SaveLoadService();
        java.util.List<Integer> avail = new java.util.ArrayList<>();
        for (int i = 1; i <= 5; i++) if (svc.slotExists(i)) avail.add(i);
        if (avail.isEmpty()) { info("No save slots exist yet."); return; }
        ChoiceDialog<Integer> dlg = new ChoiceDialog<>(avail.get(0), avail);
        dlg.setTitle("Load game");
        dlg.setHeaderText("Pick a save slot to load");
        dlg.setContentText("Slot:");
        dlg.showAndWait().ifPresent(slot -> {
            try {
                app.getController().loadExistingSeason(svc.load(slot));
                app.showMain();
            } catch (Exception ex) {
                error("Load failed: " + ex.getMessage());
            }
        });
    }

    private void confirmQuit() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Unsaved progress will be lost. Return to main menu?",
                ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Quit to menu?");
        a.showAndWait().ifPresent(bt -> { if (bt == ButtonType.YES) app.showStart(); });
    }

    private void info(String m)  { new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
    private void error(String m) { new Alert(Alert.AlertType.ERROR, m).showAndWait(); }
}
