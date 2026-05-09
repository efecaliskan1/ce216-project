package ui.views;

import core.domain.Season;
import core.services.NameDataService;
import core.services.SaveLoadService;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ui.App;
import ui.TeamLogoFactory;

import java.util.List;
import java.util.Random;

/**
 * Cinematic, game-style start screen.
 *
 * Layout: a single full-bleed stack. Behind everything is a hand-painted
 * night-stadium scene (perspective pitch + floodlight glows + crowd glow).
 * On top of that, a centred "main menu" card hosts the sport choice and the
 * new/continue actions. A drifting band of team crests floats across the top
 * to give the feeling of a living world.
 */
public class StartView {

    private final App app;
    private final StackPane root;
    private Timeline crestDrift;

    public StartView(App app) {
        this.app  = app;
        this.root = build();
    }

    public Parent getRoot() { return root; }

    /* =====================================================================
       Root construction
       ===================================================================== */
    private StackPane build() {
        StackPane stack = new StackPane();
        stack.setStyle("-fx-background-color: #05070d;");

        /* Layer 1: stadium scene (auto-resizing canvas) */
        Canvas pitch = new Canvas();
        pitch.widthProperty().bind(stack.widthProperty());
        pitch.heightProperty().bind(stack.heightProperty());
        Runnable repaint = () -> drawStadium(pitch);
        pitch.widthProperty().addListener((o, a, b) -> repaint.run());
        pitch.heightProperty().addListener((o, a, b) -> repaint.run());
        repaint.run();

        /* Layer 2: vignette to focus the eye on the centre */
        Region vignette = new Region();
        vignette.setMouseTransparent(true);
        vignette.setStyle(
            "-fx-background-color: radial-gradient(center 50% 55%, radius 80%, " +
            "rgba(5,7,13,0.0) 0%, rgba(5,7,13,0.55) 65%, rgba(5,7,13,0.94) 100%);"
        );

        /* Layer 3: drifting team crests across the top */
        Pane crestBand = buildCrestBand();

        /* Layer 4: main UI (brand, title, menu card, footer) */
        VBox foreground = new VBox();
        foreground.setAlignment(Pos.TOP_CENTER);
        foreground.setPadding(new Insets(28, 40, 24, 40));

        HBox topBar = buildTopBar();
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);

        VBox heroBlock = buildHero();
        VBox menuCard  = buildMenuCard();
        VBox center    = new VBox(26, heroBlock, menuCard);
        center.setAlignment(Pos.CENTER);

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        HBox footer = buildFooter();

        foreground.getChildren().addAll(topBar, topSpacer, center, bottomSpacer, footer);

        stack.getChildren().addAll(pitch, crestBand, vignette, foreground);

        playEntrance(heroBlock, menuCard, topBar, footer);
        return stack;
    }

    /* =====================================================================
       Stadium scene
       ===================================================================== */
    private void drawStadium(Canvas c) {
        double w = c.getWidth();
        double h = c.getHeight();
        if (w <= 0 || h <= 0) return;
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);

        /* sky gradient: deep navy → near-black */
        LinearGradient sky = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0.00, Color.web("#0b1428")),
            new Stop(0.55, Color.web("#070b16")),
            new Stop(1.00, Color.web("#03050b")));
        g.setFill(sky);
        g.fillRect(0, 0, w, h);

        /* floodlight glows: warm amber + cool blue at upper corners */
        drawGlow(g, w * 0.18, h * 0.10, w * 0.55, Color.web("#f59e0b", 0.20));
        drawGlow(g, w * 0.82, h * 0.10, w * 0.55, Color.web("#3b82f6", 0.16));

        /* horizon stand silhouette */
        double horizon = h * 0.52;
        g.setFill(Color.web("#0a0f1c"));
        g.fillRect(0, horizon - 22, w, 22);

        /* crowd glow strip just under the horizon */
        LinearGradient crowd = new LinearGradient(0, horizon, 0, horizon + 18, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#fbbf24", 0.32)),
            new Stop(1.0, Color.web("#fbbf24", 0.0)));
        g.setFill(crowd);
        g.fillRect(0, horizon, w, 18);

        /* perspective pitch */
        double topY    = horizon;
        double botY    = h;
        double topHalf = w * 0.18;
        double botHalf = w * 0.95;
        double cx      = w / 2.0;

        /* mowed stripes */
        for (int i = 0; i < 14; i++) {
            double t0 = i / 14.0;
            double t1 = (i + 1) / 14.0;
            double y0 = topY + (botY - topY) * t0;
            double y1 = topY + (botY - topY) * t1;
            double l0 = topHalf + (botHalf - topHalf) * t0;
            double l1 = topHalf + (botHalf - topHalf) * t1;
            double[] xs = { cx - l0, cx + l0, cx + l1, cx - l1 };
            double[] ys = { y0, y0, y1, y1 };
            Color band = (i % 2 == 0) ? Color.web("#0f3d22") : Color.web("#0c3219");
            g.setFill(band);
            g.fillPolygon(xs, ys, 4);
        }

        /* warm sheen overlay */
        LinearGradient sheen = new LinearGradient(0, topY, 0, botY, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#fde68a", 0.10)),
            new Stop(1.0, Color.web("#fde68a", 0.00)));
        g.setFill(sheen);
        double[] outerX = { cx - topHalf, cx + topHalf, cx + botHalf, cx - botHalf };
        double[] outerY = { topY, topY, botY, botY };
        g.fillPolygon(outerX, outerY, 4);

        /* white pitch lines */
        g.setStroke(Color.web("#ffffff", 0.55));
        g.setLineWidth(1.6);

        /* far touchline */
        g.strokeLine(cx - topHalf, topY + 1, cx + topHalf, topY + 1);
        /* side lines (perspective) */
        g.strokeLine(cx - topHalf, topY, cx - botHalf, botY);
        g.strokeLine(cx + topHalf, topY, cx + botHalf, botY);

        /* centre circle (compressed ellipse for perspective) */
        double ccCenterY    = topY + (botY - topY) * 0.32;
        double ccHalfWidth  = (topHalf + (botHalf - topHalf) * 0.32) * 0.30;
        double ccHalfHeight = ccHalfWidth * 0.35;
        g.strokeOval(cx - ccHalfWidth, ccCenterY - ccHalfHeight, ccHalfWidth * 2, ccHalfHeight * 2);
        g.setFill(Color.web("#ffffff", 0.7));
        g.fillOval(cx - 2, ccCenterY - 1, 4, 2);

        /* foreground penalty box */
        double penY     = topY + (botY - topY) * 0.78;
        double penHalf  = (topHalf + (botHalf - topHalf) * 0.78) * 0.55;
        double goalY    = botY - 4;
        double goalHalf = botHalf * 0.18;
        g.strokeLine(cx - penHalf, penY, cx + penHalf, penY);
        g.strokeLine(cx - penHalf, penY, cx - goalHalf * 1.4, goalY);
        g.strokeLine(cx + penHalf, penY, cx + goalHalf * 1.4, goalY);

        /* ambient fog particles */
        g.setFill(Color.web("#ffffff", 0.06));
        Random rng = new Random(7);
        for (int i = 0; i < 60; i++) {
            double px = rng.nextDouble() * w;
            double py = topY + rng.nextDouble() * (botY - topY);
            double pr = 1 + rng.nextDouble() * 2;
            g.fillOval(px, py, pr, pr);
        }

        /* faint broadcast scanlines */
        g.setFill(Color.web("#000000", 0.06));
        for (double y = 0; y < h; y += 3) g.fillRect(0, y, w, 1);
    }

    private void drawGlow(GraphicsContext g, double cx, double cy, double r, Color c) {
        RadialGradient rg = new RadialGradient(0, 0, cx, cy, r, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, c),
            new Stop(1.0, Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.0)));
        g.setFill(rg);
        g.fillRect(cx - r, cy - r, r * 2, r * 2);
    }

    /* =====================================================================
       Drifting crests band
       ===================================================================== */
    private Pane buildCrestBand() {
        Pane outer = new Pane();
        outer.setMouseTransparent(true);
        outer.setPrefHeight(80);
        outer.setMaxHeight(80);

        HBox row = new HBox(28);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 24, 8, 24));

        List<String> sample = NameDataService.pickTeamNames(20, new Random(42));
        for (int pass = 0; pass < 2; pass++) {
            for (String name : sample) {
                Canvas c = TeamLogoFactory.create(name, 44);
                c.setOpacity(0.78);
                DropShadow ds = new DropShadow(14, Color.web("#000000", 0.55));
                ds.setOffsetY(2);
                c.setEffect(ds);
                row.getChildren().add(c);
            }
        }

        // edge fades so crests don't pop at the borders
        Region leftFade = new Region();
        leftFade.setMouseTransparent(true);
        leftFade.setPrefWidth(180);
        leftFade.setStyle("-fx-background-color: linear-gradient(to right, rgba(5,7,13,0.95), rgba(5,7,13,0.0));");
        Region rightFade = new Region();
        rightFade.setMouseTransparent(true);
        rightFade.setPrefWidth(180);
        rightFade.setStyle("-fx-background-color: linear-gradient(to left, rgba(5,7,13,0.95), rgba(5,7,13,0.0));");

        outer.getChildren().addAll(row, leftFade, rightFade);

        leftFade.prefHeightProperty().bind(outer.heightProperty());
        rightFade.prefHeightProperty().bind(outer.heightProperty());
        leftFade.setLayoutX(0);
        outer.widthProperty().addListener((o, a, b) ->
            rightFade.setLayoutX(b.doubleValue() - rightFade.getPrefWidth()));

        // place row vertically centred in outer
        row.layoutYProperty().bind(outer.heightProperty().subtract(row.heightProperty()).divide(2));

        // marquee animation: shift left by half row width, then loop
        row.layoutBoundsProperty().addListener((o, a, bnd) -> {
            if (crestDrift != null) crestDrift.stop();
            double half = bnd.getWidth() / 2.0;
            if (half <= 0) return;
            crestDrift = new Timeline(
                new KeyFrame(Duration.ZERO,           new KeyValue(row.translateXProperty(), 0)),
                new KeyFrame(Duration.seconds(60),    new KeyValue(row.translateXProperty(), -half))
            );
            crestDrift.setCycleCount(Animation.INDEFINITE);
            crestDrift.play();
        });

        StackPane.setAlignment(outer, Pos.TOP_CENTER);
        StackPane.setMargin(outer, new Insets(86, 0, 0, 0));
        return outer;
    }

    /* =====================================================================
       Top bar (brand + version chip)
       ===================================================================== */
    private HBox buildTopBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        StackPane shield = new StackPane();
        shield.setPrefSize(30, 32);
        Polygon p = new Polygon(0,0, 30,0, 30,18, 15,32, 0,18);
        p.setFill(new LinearGradient(0,0,1,1,true,CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#f59e0b")),
            new Stop(1, Color.web("#b45309"))));
        p.setStroke(Color.web("#fde68a", 0.6));
        p.setStrokeWidth(1.0);
        Text mono = new Text("SM");
        mono.setFill(Color.web("#0a0e1a"));
        mono.setStyle("-fx-font-size: 11px; -fx-font-weight: 900;");
        shield.getChildren().addAll(p, mono);

        VBox txt = new VBox(0);
        Text title = new Text("SPORT MANAGER");
        title.setFill(Color.web("#f9fafb"));
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: 800;");
        Text sub = new Text("FOOTBALL  ·  VOLLEYBALL");
        sub.setFill(Color.web("#94a3b8"));
        sub.setStyle("-fx-font-size: 9px; -fx-font-weight: 700;");
        txt.getChildren().addAll(title, sub);

        Region spring = new Region();
        HBox.setHgrow(spring, Priority.ALWAYS);

        Label version = new Label("v3.0  ·  SEASON '26");
        version.setStyle(
            "-fx-text-fill: #cbd5e1;" +
            "-fx-font-size: 10px; -fx-font-weight: 700;" +
            "-fx-background-color: rgba(255,255,255,0.06);" +
            "-fx-border-color: rgba(255,255,255,0.10);" +
            "-fx-border-radius: 999;" +
            "-fx-background-radius: 999;" +
            "-fx-padding: 5 12 5 12;"
        );

        bar.getChildren().addAll(shield, txt, spring, version);
        return bar;
    }

    /* =====================================================================
       Hero block (huge title + tagline)
       ===================================================================== */
    private VBox buildHero() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(900);

        Label kicker = new Label("●  MATCHDAY READY");
        kicker.setStyle(
            "-fx-text-fill: #fbbf24;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: 800;"
        );

        Text line1 = new Text("Take control.");
        Text line2 = new Text("Build a dynasty.");
        for (Text t : new Text[]{ line1, line2 }) {
            t.setFill(Color.WHITE);
            t.setStyle(
                "-fx-font-size: 60px;" +
                "-fx-font-weight: 900;"
            );
            DropShadow shadow = new DropShadow(28, Color.web("#000000", 0.85));
            shadow.setOffsetY(6);
            t.setEffect(shadow);
        }

        VBox titleStack = new VBox(-4, line1, line2);
        titleStack.setAlignment(Pos.CENTER);

        Label tagline = new Label("Tactics. Transfers. Trophies. — Run a club for as many seasons as you can survive.");
        tagline.setStyle(
            "-fx-text-fill: #cbd5e1;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;"
        );

        box.getChildren().addAll(kicker, titleStack, tagline);
        return box;
    }

    /* =====================================================================
       Centred glass menu card
       ===================================================================== */
    private VBox buildMenuCard() {
        VBox card = new VBox(16);
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(520);
        card.setPadding(new Insets(24, 28, 22, 28));
        card.setStyle(
            "-fx-background-color: rgba(15,23,42,0.78);" +
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;" +
            "-fx-border-color: rgba(255,255,255,0.10);" +
            "-fx-border-width: 1;"
        );
        DropShadow cardShadow = new DropShadow(40, Color.web("#000000", 0.55));
        cardShadow.setOffsetY(14);
        card.setEffect(cardShadow);

        /* sport selector pills */
        Label sportLabel = new Label("CHOOSE YOUR SPORT");
        sportLabel.setStyle(captionStyle());

        ToggleGroup sportGroup = new ToggleGroup();
        ToggleButton football   = sportPill("⚽   Football",   "football",   true);
        ToggleButton volleyball = sportPill("🏐   Volleyball", "volleyball", false);
        football.setToggleGroup(sportGroup);
        volleyball.setToggleGroup(sportGroup);
        sportGroup.selectedToggleProperty().addListener((o, a, b) -> { if (b == null) a.setSelected(true); });

        HBox sportRow = new HBox(10, football, volleyball);
        HBox.setHgrow(football,   Priority.ALWAYS);
        HBox.setHgrow(volleyball, Priority.ALWAYS);
        football.setMaxWidth(Double.MAX_VALUE);
        volleyball.setMaxWidth(Double.MAX_VALUE);

        /* primary CTA: New career */
        Button startBtn = new Button("START NEW CAREER  →");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setPrefHeight(52);
        startBtn.setStyle(primaryButtonStyle());
        startBtn.setOnMouseEntered(e -> startBtn.setStyle(primaryButtonStyleHover()));
        startBtn.setOnMouseExited (e -> startBtn.setStyle(primaryButtonStyle()));
        startBtn.setOnAction(e -> {
            String sport = (String) sportGroup.getSelectedToggle().getUserData();
            app.getController().startSeason(sport);
            app.showTeamSelection();
        });

        /* divider with "OR" label */
        HBox divider = new HBox(12);
        divider.setAlignment(Pos.CENTER);
        Region l1 = new Region(); l1.setPrefHeight(1); l1.setMaxHeight(1);
        l1.setStyle("-fx-background-color: rgba(255,255,255,0.10);");
        HBox.setHgrow(l1, Priority.ALWAYS);
        Label or = new Label("OR");
        or.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: 800;");
        Region l2 = new Region(); l2.setPrefHeight(1); l2.setMaxHeight(1);
        l2.setStyle("-fx-background-color: rgba(255,255,255,0.10);");
        HBox.setHgrow(l2, Priority.ALWAYS);
        divider.getChildren().addAll(l1, or, l2);

        /* continue saved game */
        Label loadLabel = new Label("CONTINUE A SAVED CAREER");
        loadLabel.setStyle(captionStyle());

        SaveLoadService svc = new SaveLoadService();
        ComboBox<Integer> slotBox = new ComboBox<>();
        slotBox.setPromptText("Select slot");
        slotBox.setPrefHeight(44);
        slotBox.setStyle(comboBoxStyle());
        for (int i = 1; i <= 5; i++) if (svc.slotExists(i)) slotBox.getItems().add(i);
        if (!slotBox.getItems().isEmpty()) slotBox.setValue(slotBox.getItems().get(0));

        Button loadBtn = new Button("Load slot");
        loadBtn.setPrefHeight(44);
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setDisable(slotBox.getItems().isEmpty());
        loadBtn.setStyle(loadBtn.isDisabled() ? secondaryButtonStyleDisabled() : secondaryButtonStyle());
        loadBtn.setOnMouseEntered(e -> { if (!loadBtn.isDisabled()) loadBtn.setStyle(secondaryButtonStyleHover()); });
        loadBtn.setOnMouseExited (e -> { if (!loadBtn.isDisabled()) loadBtn.setStyle(secondaryButtonStyle()); });
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

        HBox loadRow = new HBox(10, slotBox, loadBtn);
        loadRow.setAlignment(Pos.CENTER_LEFT);
        slotBox.setPrefWidth(170);
        HBox.setHgrow(loadBtn, Priority.ALWAYS);

        if (slotBox.getItems().isEmpty()) {
            Label empty = new Label("No saved careers yet — start a new one above.");
            empty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-style: italic;");
            VBox loadBox = new VBox(8, loadRow, empty);
            card.getChildren().addAll(sportLabel, sportRow, startBtn, divider, loadLabel, loadBox);
        } else {
            card.getChildren().addAll(sportLabel, sportRow, startBtn, divider, loadLabel, loadRow);
        }

        return card;
    }

    /* =====================================================================
       Footer
       ===================================================================== */
    private HBox buildFooter() {
        HBox foot = new HBox();
        foot.setAlignment(Pos.CENTER);
        foot.setSpacing(18);
        Label hint = new Label("Tip: every match is simulated minute-by-minute — your tactics actually matter.");
        hint.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: 500;");
        foot.getChildren().add(hint);
        return foot;
    }

    /* =====================================================================
       Entrance animation
       ===================================================================== */
    private void playEntrance(VBox hero, VBox menu, HBox top, HBox foot) {
        for (var node : new javafx.scene.Node[]{ top, hero, menu, foot }) {
            node.setOpacity(0);
            node.setTranslateY(18);
        }
        ParallelTransition pt = new ParallelTransition(
            fade(top,  450, 0),   slide(top,  450, 0),
            fade(hero, 550, 100), slide(hero, 550, 100),
            fade(menu, 550, 220), slide(menu, 550, 220),
            fade(foot, 450, 340), slide(foot, 450, 340)
        );
        pt.play();
    }

    private FadeTransition fade(javafx.scene.Node n, double ms, double delayMs) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), n);
        ft.setFromValue(0); ft.setToValue(1);
        ft.setDelay(Duration.millis(delayMs));
        return ft;
    }
    private TranslateTransition slide(javafx.scene.Node n, double ms, double delayMs) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), n);
        tt.setFromY(18); tt.setToY(0);
        tt.setDelay(Duration.millis(delayMs));
        tt.setInterpolator(Interpolator.EASE_OUT);
        return tt;
    }

    /* =====================================================================
       Inline style helpers (kept local so global CSS isn't touched)
       ===================================================================== */
    private String captionStyle() {
        return "-fx-text-fill: #94a3b8;" +
               "-fx-font-size: 10px;" +
               "-fx-font-weight: 800;";
    }

    private ToggleButton sportPill(String text, String userData, boolean selected) {
        ToggleButton tb = new ToggleButton(text);
        tb.setUserData(userData);
        tb.setSelected(selected);
        tb.setPrefHeight(46);
        tb.setStyle(pillStyle(selected));
        tb.selectedProperty().addListener((o, a, b) -> tb.setStyle(pillStyle(b)));
        tb.setOnMouseEntered(e -> { if (!tb.isSelected()) tb.setStyle(pillStyleHover()); });
        tb.setOnMouseExited (e -> tb.setStyle(pillStyle(tb.isSelected())));
        return tb;
    }
    private String pillStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: rgba(245,158,11,0.18);" +
                   "-fx-text-fill: #fde68a;" +
                   "-fx-border-color: rgba(245,158,11,0.65);" +
                   "-fx-border-width: 1.5;" +
                   "-fx-background-radius: 10;" +
                   "-fx-border-radius: 10;" +
                   "-fx-font-size: 13px;" +
                   "-fx-font-weight: 700;" +
                   "-fx-cursor: hand;";
        }
        return "-fx-background-color: rgba(255,255,255,0.04);" +
               "-fx-text-fill: #cbd5e1;" +
               "-fx-border-color: rgba(255,255,255,0.10);" +
               "-fx-border-width: 1;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: 600;" +
               "-fx-cursor: hand;";
    }
    private String pillStyleHover() {
        return "-fx-background-color: rgba(255,255,255,0.08);" +
               "-fx-text-fill: white;" +
               "-fx-border-color: rgba(255,255,255,0.18);" +
               "-fx-border-width: 1;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: 600;" +
               "-fx-cursor: hand;";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: linear-gradient(to right, #f59e0b, #d97706);" +
               "-fx-text-fill: #1a1208;" +
               "-fx-font-size: 14px;" +
               "-fx-font-weight: 800;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-cursor: hand;";
    }
    private String primaryButtonStyleHover() {
        return "-fx-background-color: linear-gradient(to right, #fbbf24, #f59e0b);" +
               "-fx-text-fill: #1a1208;" +
               "-fx-font-size: 14px;" +
               "-fx-font-weight: 800;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, rgba(245,158,11,0.55), 18, 0, 0, 4);";
    }

    private String secondaryButtonStyle() {
        return "-fx-background-color: rgba(255,255,255,0.06);" +
               "-fx-text-fill: #f1f5f9;" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: 700;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-border-color: rgba(255,255,255,0.14);" +
               "-fx-border-width: 1;" +
               "-fx-cursor: hand;";
    }
    private String secondaryButtonStyleHover() {
        return "-fx-background-color: rgba(255,255,255,0.12);" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: 700;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-border-color: rgba(255,255,255,0.22);" +
               "-fx-border-width: 1;" +
               "-fx-cursor: hand;";
    }
    private String secondaryButtonStyleDisabled() {
        return "-fx-background-color: rgba(255,255,255,0.03);" +
               "-fx-text-fill: #475569;" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: 700;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-border-color: rgba(255,255,255,0.06);" +
               "-fx-border-width: 1;";
    }

    private String comboBoxStyle() {
        return "-fx-background-color: rgba(255,255,255,0.06);" +
               "-fx-text-fill: white;" +
               "-fx-border-color: rgba(255,255,255,0.14);" +
               "-fx-border-width: 1;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-padding: 4 10 4 10;";
    }
}
