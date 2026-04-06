package valueobjects;

import java.io.Serializable;

public class ScoringConfig implements Serializable {
    private final int winPoints;
    private final int drawPoints;
    private final int losePoints;

    public ScoringConfig(int winPoints, int drawPoints, int losePoints) {
        this.winPoints  = winPoints;
        this.drawPoints = drawPoints;
        this.losePoints = losePoints;
    }

    public static ScoringConfig footballDefaults() {
        return new ScoringConfig(3, 1, 0);
    }

    public int getWinPoints()  { return winPoints; }
    public int getDrawPoints() { return drawPoints; }
    public int getLosePoints() { return losePoints; }
}
