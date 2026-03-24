package core.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private final String name;
    private final String position;
    private final int age;
    private final int shirtNumber;
    private final Map<String, Integer> attributes;
    private int fatigueLevel;
    private boolean injuryStatus;
    private int injuredGamesRemaining;

    public Player(String name, String position, int age, int shirtNumber) {
        this(name, position, age, shirtNumber, null);
    }

    public Player(String name, String position, int age, int shirtNumber, Map<String, Integer> attributes) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("position cannot be blank");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("age must be positive");
        }
        if (shirtNumber <= 0) {
            throw new IllegalArgumentException("shirtNumber must be positive");
        }

        this.name = name;
        this.position = position;
        this.age = age;
        this.shirtNumber = shirtNumber;
        this.attributes = normalizeAttributes(attributes);
        this.fatigueLevel = 0;
        this.injuryStatus = false;
        this.injuredGamesRemaining = 0;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getAge() {
        return age;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public Map<String, Integer> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public int getAttribute(String attributeName) {
        return attributes.getOrDefault(attributeName, 0);
    }

    public void setAttribute(String attributeName, int value) {
        if (attributeName == null || attributeName.trim().isEmpty()) {
            throw new IllegalArgumentException("attributeName cannot be blank");
        }
        attributes.put(attributeName, Math.max(0, Math.min(100, value)));
    }

    public int getFatigueLevel() {
        return fatigueLevel;
    }

    public boolean isInjured() {
        return injuryStatus;
    }

    public int getInjuredGamesRemaining() {
        return injuredGamesRemaining;
    }

    public void applyInjury(int games) {
        if (games <= 0) {
            throw new IllegalArgumentException("games must be positive");
        }

        injuryStatus = true;
        injuredGamesRemaining = games;
    }

    public void decrementInjury() {
        if (injuredGamesRemaining > 0) {
            injuredGamesRemaining--;
        }
        if (injuredGamesRemaining == 0) {
            injuryStatus = false;
        }
    }

    public boolean isAvailable() {
        return !injuryStatus;
    }

    public void increaseFatigue(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        fatigueLevel = Math.min(100, fatigueLevel + amount);
    }

    public void recoverFatigue(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        fatigueLevel = Math.max(0, fatigueLevel - amount);
    }

    public void train(TrainingPlan plan) {
        if (plan == null || !isAvailable()) {
            return;
        }

        int currentValue = attributes.getOrDefault(plan.getTargetAttribute(), 0);
        attributes.put(plan.getTargetAttribute(), Math.min(100, currentValue + plan.getAttributeGain()));
    }

    private Map<String, Integer> normalizeAttributes(Map<String, Integer> source) {
        Map<String, Integer> normalized = new HashMap<>();
        if (source == null) {
            return normalized;
        }

        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (key == null || key.trim().isEmpty() || value == null) {
                continue;
            }
            normalized.put(key, Math.max(0, Math.min(100, value)));
        }

        return normalized;
    }
}
