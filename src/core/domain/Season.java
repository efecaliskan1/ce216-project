package core.domain;

import interfaces.ISport;
import interfaces.IStandingsCalculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Season implements Serializable {
    private int currentWeek;
    private final League league;
    private final ISport sport;
    private final List<GameWeek> gameWeeks;
    private boolean finished;
    private final int seasonNumber;
    private IStandingsCalculator calculator;

    public Season(ISport sport, League league, int seasonNumber) {
        if (sport == null) {
            throw new IllegalArgumentException("sport cannot be null");
        }
        if (league == null) {
            throw new IllegalArgumentException("league cannot be null");
        }
        if (seasonNumber <= 0) {
            throw new IllegalArgumentException("seasonNumber must be positive");
        }

        this.sport = sport;
        this.league = league;
        this.seasonNumber = seasonNumber;
        this.gameWeeks = new ArrayList<>();
    }

    public void advanceWeek() {
        currentWeek++;
        if (currentWeek >= gameWeeks.size()) {
            finished = true;
        }
    }

    public Optional<Team> getWinner() {
        if (calculator == null) {
            return Optional.empty();
        }

        List<StandingEntry> standings = calculator.getStandings();
        if (standings == null || standings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(standings.get(0).getTeam());
    }

    public GameWeek getCurrentGameWeek() {
        if (currentWeek >= gameWeeks.size()) {
            return null;
        }
        return gameWeeks.get(currentWeek);
    }

    public void addGameWeek(GameWeek gameWeek) {
        if (gameWeek != null) {
            gameWeeks.add(gameWeek);
        }
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public League getLeague() {
        return league;
    }

    public ISport getSport() {
        return sport;
    }

    public List<GameWeek> getGameWeeks() {
        return Collections.unmodifiableList(gameWeeks);
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public IStandingsCalculator getCalculator() {
        return calculator;
    }

    public void setCalculator(IStandingsCalculator calculator) {
        this.calculator = calculator;
    }

    public boolean isFinished() {
        return finished;
    }
}
