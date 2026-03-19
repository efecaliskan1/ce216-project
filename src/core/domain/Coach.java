package core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Coach {
    private final String name;
    private final String specialty;
    private final List<TrainingPlan> trainingPlans;

    public Coach(String name, String specialty, List<TrainingPlan> trainingPlans) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("specialty cannot be blank");
        }

        this.name = name;
        this.specialty = specialty;
        this.trainingPlans = new ArrayList<>(trainingPlans == null ? Collections.<TrainingPlan>emptyList() : trainingPlans);
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public List<TrainingPlan> getTrainingPlans() {
        return Collections.unmodifiableList(trainingPlans);
    }

    public void trainPlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return;
        }

        for (Player player : players) {
            if (player == null || !player.isAvailable()) {
                continue;
            }

            for (TrainingPlan plan : trainingPlans) {
                if (specialty.equalsIgnoreCase(plan.getTargetAttribute())) {
                    player.train(plan);
                }
            }
        }
    }
}
