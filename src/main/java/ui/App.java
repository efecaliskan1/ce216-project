package ui;

import core.app.SeasonController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.views.MainView;
import ui.views.StartView;
import ui.views.TeamSelectionView;

public class App extends Application {

    private static App instance;

    private Stage            primaryStage;
    private SeasonController controller;
    private StackPane        rootContainer;

    public static void main(String[] args) { launch(args); }

    public static App getInstance() { return instance; }

    @Override
    public void start(Stage stage) {
        instance          = this;
        primaryStage      = stage;
        controller        = new SeasonController();
        rootContainer     = new StackPane();

        Scene scene = new Scene(rootContainer, 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        stage.setTitle("Sport Management System");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        try {
            var iconStream = getClass().getResourceAsStream("/logos/app.png");
            if (iconStream != null) stage.getIcons().add(new Image(iconStream));
        } catch (Exception ignored) {}
        stage.show();

        showStart();
    }

    public void showStart() {
        StartView view = new StartView(this);
        rootContainer.getChildren().setAll(view.getRoot());
    }

    public void showTeamSelection() {
        TeamSelectionView view = new TeamSelectionView(this);
        rootContainer.getChildren().setAll(view.getRoot());
    }

    public void showMain() {
        MainView view = new MainView(this);
        rootContainer.getChildren().setAll(view.getRoot());
    }

    public void showMain(String initialSection) {
        MainView view = new MainView(this, initialSection);
        rootContainer.getChildren().setAll(view.getRoot());
    }

    public void showCustom(Parent root) {
        rootContainer.getChildren().setAll(root);
    }

    public SeasonController getController() { return controller; }
    public Stage            getStage()      { return primaryStage; }
}
