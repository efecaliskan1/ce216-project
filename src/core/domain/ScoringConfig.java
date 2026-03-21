package core.domain;

// immutable point award settings
public final class ScoringConfig {

    private final int winPoints;
    private final int drawPoints;
    private final int losePoints;

    // defines win, draw and lose points
    public ScoringConfig(int winPoints, int drawPoints, int losePoints) {
        if (winPoints < drawPoints || drawPoints < losePoints) {
            throw new IllegalArgumentException(
                    "Points must satisfy: winPoints >= drawPoints >= losePoints");
        }
        this.winPoints = winPoints;
        this.drawPoints = drawPoints;
        this.losePoints = losePoints;
    }

    public int getWinPoints() {
        return winPoints;
    }

    public int getDrawPoints() {
        return drawPoints;
    }

    public int getLosePoints() {
        return losePoints;
    }

    @Override
    public String toString() {
        return "ScoringConfig{win=" + winPoints
                + ", draw=" + drawPoints
                + ", lose=" + losePoints + '}';
    }
}