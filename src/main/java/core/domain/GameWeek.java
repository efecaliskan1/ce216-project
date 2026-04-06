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
        this.weekNumber     = weekNumber;
        this.fixtures       = new ArrayList<>();
        this.trainingEvents = new ArrayList<>();
    }

    /** Runs training for every coaching event registered this week. */
    public void runTraining() {
        for (TrainingEvent e : trainingEvents) e.execute();
    }

    public void addFixture(Match match)         { fixtures.add(match); }
    public void addTrainingEvent(TrainingEvent e){ trainingEvents.add(e); }
    public void markCompleted()                 { completed = true; }

    // runMatches / applyInjuries / updateStandings removed — orchestration is in SeasonController

    public int              getWeekNumber()     { return weekNumber; }
    public List<Match>      getFixtures()       { return Collections.unmodifiableList(fixtures); }
    public List<TrainingEvent> getTrainingEvents(){ return Collections.unmodifiableList(trainingEvents); }
    public boolean          isCompleted()       { return completed; }
}
