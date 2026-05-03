package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Procedurally draws a crest-like logo for any team name.
 * Same team name always produces the same logo (deterministic).
 */
public final class TeamLogoFactory {

    private TeamLogoFactory() {}

    /** Palette of brand-friendly duo gradients. */
    private static final Color[][] PALETTE = {
        { Color.web("#ef4444"), Color.web("#991b1b") }, // red
        { Color.web("#3b82f6"), Color.web("#1e40af") }, // blue
        { Color.web("#22c55e"), Color.web("#166534") }, // green
        { Color.web("#f59e0b"), Color.web("#b45309") }, // amber
        { Color.web("#a855f7"), Color.web("#6b21a8") }, // purple
        { Color.web("#14b8a6"), Color.web("#115e59") }, // teal
        { Color.web("#ec4899"), Color.web("#9d174d") }, // pink
        { Color.web("#6366f1"), Color.web("#3730a3") }, // indigo
        { Color.web("#f97316"), Color.web("#9a3412") }, // orange
        { Color.web("#84cc16"), Color.web("#3f6212") }, // lime
        { Color.web("#0ea5e9"), Color.web("#075985") }, // sky
        { Color.web("#78350f"), Color.web("#451a03") }, // bronze
    };

    /** Six crest shapes: 0=shield, 1=circle, 2=hex, 3=diamond, 4=rounded-square, 5=pentagon. */
    public static Canvas create(String teamName, double size) {
        Canvas canvas = new Canvas(size, size);
        GraphicsContext g = canvas.getGraphicsContext2D();

        int hash = Math.abs(teamName.hashCode());
        Color c1 = PALETTE[hash % PALETTE.length][0];
        Color c2 = PALETTE[hash % PALETTE.length][1];
        int shape = (hash / 7) % 6;
        int accent = (hash / 31) % 3;   // extra band/dot/ring

        // Gradient fill
        LinearGradient grad = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, c1), new Stop(1, c2)
        );

        g.setFill(grad);
        drawShape(g, shape, size);

        // Inner accent
        g.setGlobalAlpha(0.35);
        g.setFill(Color.WHITE);
        if (accent == 0) {
            // horizontal band
            g.fillRect(size * 0.1, size * 0.46, size * 0.8, size * 0.08);
        } else if (accent == 1) {
            // inner ring
            g.setStroke(Color.WHITE);
            g.setLineWidth(size * 0.04);
            g.strokeOval(size * 0.22, size * 0.22, size * 0.56, size * 0.56);
        } else {
            // corner dot
            g.fillOval(size * 0.72, size * 0.15, size * 0.14, size * 0.14);
        }
        g.setGlobalAlpha(1.0);

        // Letter
        String letter = extractInitials(teamName);
        g.setFill(Color.WHITE);
        g.setFont(Font.font("System", FontWeight.EXTRA_BOLD, size * 0.46));
        g.setTextAlign(TextAlignment.CENTER);
        g.fillText(letter, size / 2.0, size * 0.66);

        return canvas;
    }

    private static void drawShape(GraphicsContext g, int shape, double s) {
        switch (shape) {
            case 0 -> { // shield
                double[] xs = { s*0.1, s*0.9, s*0.9, s*0.5, s*0.1 };
                double[] ys = { s*0.05, s*0.05, s*0.6, s*0.95, s*0.6 };
                g.fillPolygon(xs, ys, 5);
            }
            case 1 -> g.fillOval(0, 0, s, s); // circle
            case 2 -> { // hexagon
                double[] xs = new double[6], ys = new double[6];
                for (int i = 0; i < 6; i++) {
                    double a = Math.PI / 3 * i - Math.PI / 2;
                    xs[i] = s/2 + Math.cos(a) * s*0.45;
                    ys[i] = s/2 + Math.sin(a) * s*0.45;
                }
                g.fillPolygon(xs, ys, 6);
            }
            case 3 -> { // diamond
                double[] xs = { s/2, s*0.95, s/2, s*0.05 };
                double[] ys = { s*0.05, s/2, s*0.95, s/2 };
                g.fillPolygon(xs, ys, 4);
            }
            case 4 -> g.fillRoundRect(s*0.05, s*0.05, s*0.9, s*0.9, s*0.25, s*0.25);
            case 5 -> { // pentagon
                double[] xs = new double[5], ys = new double[5];
                for (int i = 0; i < 5; i++) {
                    double a = 2 * Math.PI / 5 * i - Math.PI / 2;
                    xs[i] = s/2 + Math.cos(a) * s*0.45;
                    ys[i] = s/2 + Math.sin(a) * s*0.45;
                }
                g.fillPolygon(xs, ys, 5);
            }
        }
    }

    private static String extractInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
    }
}
