package core.domain;

public class TrainingPlan {
    private final String targetAttribute;
    private final int intensity;

    public TrainingPlan(String targetAttribute, int intensity) {
        if (targetAttribute == null || targetAttribute.trim().isEmpty()) {
            throw new IllegalArgumentException("targetAttribute cannot be blank");
        }
        if (intensity < 1 || intensity > 10) {
            throw new IllegalArgumentException("intensity must be between 1 and 10");
        }

        this.targetAttribute = targetAttribute;
        this.intensity = intensity;
    }

    public String getTargetAttribute() {
        return targetAttribute;
    }

    public int getIntensity() {
        return intensity;
    }

    public int getAttributeGain() {
        return intensity;
    }
}
