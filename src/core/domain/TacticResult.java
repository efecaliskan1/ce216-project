package core.domain;

// immutable output from tactic strategies
public final class TacticResult {

    private final float pressureScore;
    private final float widthScore;
    private final String formation;

    // stores computed tactical scores and formation
    public TacticResult(float pressureScore, float widthScore, String formation) {
        if (pressureScore < 0f || pressureScore > 1f) {
            throw new IllegalArgumentException(
                    "pressureScore must be in [0, 1]");
        }
        if (widthScore < 0f || widthScore > 1f) {
            throw new IllegalArgumentException(
                    "widthScore must be in [0, 1]");
        }
        if (formation == null || formation.trim().isEmpty()) {
            throw new IllegalArgumentException("formation must not be blank");
        }
        this.pressureScore = pressureScore;
        this.widthScore = widthScore;
        this.formation = formation;
    }

    public float getPressureScore() {
        return pressureScore;
    }

    public float getWidthScore() {
        return widthScore;
    }

    public String getFormation() {
        return formation;
    }

    @Override
    public String toString() {
        return "TacticResult{pressure=" + pressureScore
                + ", width=" + widthScore
                + ", formation='" + formation + "'}";
    }
}