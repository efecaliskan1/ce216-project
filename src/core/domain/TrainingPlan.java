package core.domain;

import java.io.Serializable;

public class TrainingPlan implements Serializable {
    private final String targetAttribute;
    private final int intensity; // 1-10

    public TrainingPlan(String targetAttribute, int intensity) {
        this.targetAttribute = targetAttribute;
        this.intensity = Math.max(1, Math.min(10, intensity));
    }

    public int    getAttributeGain()    { return intensity; }
    public String getTargetAttribute()  { return targetAttribute; }
    public int    getIntensity()        { return intensity; }
}
