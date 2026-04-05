package core.domain;

import interfaces.ISport;
import interfaces.IStandingsCalculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Season implements Serializable {

    private int currentWeek;
    private League league;
    private ISport sport;
    private List<GameWeek> gameWeeks;
    private boolean finished;
    private int seasonNumber;
    private IStandingsCalculator calculator;

    public Season(ISport sport, League league, int seasonNumber) {
        this.sport        = sport;
        this.league       = league;
        this.seasonNumber = seasonNumber;
        this.gameWeeks    = new ArrayList<>();
    }

    public void advanceWeek() {
        currentWeek++;
        if (currentWeek >= gameWeeks.size()) finished = true;
    }

    public Optional<Team> getWinner() {
        if (calculator == null) return Optional.empty();
        List<StandingEntry> standings = calculator.getStandings();
        if (standings == null || standings.isEmpty()) return Optional.empty();
        return Optional.of(standings.get(0).getTeam());
    }

    public GameWeek getCurrentGameWeek() {
        if (currentWeek >= gameWeeks.size()) return null;
        return gameWeeks.get(currentWeek);
    }

    public void addGameWeek(GameWeek gw) { gameWeeks.add(gw); }

    public int               getCurrentWeek() { return currentWeek; }
    public League            getLeague()       { return league; }
    public ISport            getSport()        { return sport; }
    public List<GameWeek>    getGameWeeks()    { return gameWeeks; }
    public int               getSeasonNumber() { return seasonNumber; }
    public IStandingsCalculator getCalculator(){ return calculator; }
    public void              setCalculator(IStandingsCalculator c){ this.calculator = c; }
    public boolean           isFinished()      { return finished; }
}
