package core.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Player implements Serializable {

    private String name;
    private String position;
    private int age;
    private int shirtNumber;
    private Map<String, Integer> attributes;
    private int fatigueLevel;          // 0-100
    private boolean injuryStatus;
    private int injuredGamesRemaining; // game-based

    public Player(String name, String position, int age, int shirtNumber) {
        this.name       = name;
        this.position   = position;
        this.age        = age;
        this.shirtNumber = shirtNumber;
        this.attributes = new HashMap<>();
    }

    public void applyInjury(int games) {
        this.injuryStatus = true;
        this.injuredGamesRemaining = games;
    }

    /** Call AFTER each match the player misses. */
    public void decrementInjury() {
        if (injuredGamesRemaining > 0) injuredGamesRemaining--;
        if (injuredGamesRemaining == 0) injuryStatus = false;
    }

    public boolean isAvailable() { return !injuryStatus; }

    public void increaseFatigue(int amount) {
        fatigueLevel = Math.min(100, fatigueLevel + amount);
    }

    public void recoverFatigue(int amount) {
        fatigueLevel = Math.max(0, fatigueLevel - amount);
    }

    public void train(TrainingPlan plan) {
        String attr = plan.getTargetAttribute();
        int current  = attributes.getOrDefault(attr, 0);
        attributes.put(attr, Math.min(100, current + plan.getAttributeGain()));
    }

    public void setAttribute(String key, int value) { attributes.put(key, value); }
    public int  getAttribute(String key)            { return attributes.getOrDefault(key, 0); }

    public String getName()                 { return name; }
    public String getPosition()             { return position; }
    public int    getAge()                  { return age; }
    public int    getShirtNumber()          { return shirtNumber; }
    public Map<String, Integer> getAttributes() { return attributes; }
    public int    getFatigueLevel()         { return fatigueLevel; }
    public boolean isInjured()              { return injuryStatus; }
    public int    getInjuredGamesRemaining(){ return injuredGamesRemaining; }
}
