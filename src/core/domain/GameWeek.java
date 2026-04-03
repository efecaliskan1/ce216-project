package core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameWeek implements Serializable {
    private final int weekNumber;
    private final List<Match> fixtures;
    private final List<TrainingEvent> trainingEvents;
    private boolean completed;

    public GameWeek(int weekNumber) {
        if (weekNumber < 0) {
            throw new IllegalArgumentException("weekNumber cannot be negative");
        }

        this.weekNumber = weekNumber;
        this.fixtures = new ArrayList<>();
        this.trainingEvents = new ArrayList<>();
    }

    public void runTraining() {
        for (TrainingEvent trainingEvent : trainingEvents) {
            trainingEvent.execute();
        }
    }

    public void addFixture(Match match) {
        if (match != null) {
            fixtures.add(match);
        }
    }

    public void addTrainingEvent(TrainingEvent trainingEvent) {
        if (trainingEvent != null) {
            trainingEvents.add(trainingEvent);
        }
    }

    public void markCompleted() {
        completed = true;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public List<Match> getFixtures() {
        return Collections.unmodifiableList(fixtures);
    }

    public List<TrainingEvent> getTrainingEvents() {
        return Collections.unmodifiableList(trainingEvents);
    }

    public boolean isCompleted() {
        return completed;
    }
}
