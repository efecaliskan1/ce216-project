package ui;

/**
 * Non-JavaFX entry point used by maven-jar-plugin and fat-jar installers.
 * Having a separate launcher avoids "JavaFX runtime components are missing" errors
 * when the main class extends Application.
 */
public class AppLauncher {
    public static void main(String[] args) {
        App.main(args);
    }
}
