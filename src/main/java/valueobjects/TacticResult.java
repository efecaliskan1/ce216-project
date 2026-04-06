package valueobjects;

import java.io.Serializable;

public class TacticResult implements Serializable {
    private final float pressureScore;
    private final float widthScore;
    private final String formation;

    public TacticResult(float pressureScore, float widthScore, String formation) {
        this.pressureScore = pressureScore;
        this.widthScore    = widthScore;
        this.formation     = formation;
    }

    public float getPressureScore() { return pressureScore; }
    public float getWidthScore()    { return widthScore; }
    public String getFormation()    { return formation; }
}
