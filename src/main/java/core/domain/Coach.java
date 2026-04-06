package core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coach implements Serializable {
    private String name;
    private String specialty;
    private List<TrainingPlan> trainingPlans;

    public Coach(String name, String specialty) {
        this.name         = name;
        this.specialty    = specialty;
        this.trainingPlans = new ArrayList<>();
    }

    public void addTrainingPlan(TrainingPlan plan) {
        trainingPlans.add(plan);
    }

    public void trainPlayers(List<Player> players) {
        for (TrainingPlan plan : trainingPlans) {
            if (!plan.getTargetAttribute().equals(specialty)) continue;
            for (Player p : players) {
                if (p.isAvailable()) p.train(plan);
            }
        }
    }

    public String getName()                       { return name; }
    public String getSpecialty()                  { return specialty; }
    public List<TrainingPlan> getTrainingPlans()  { return trainingPlans; }
}
