package core.app;

import core.domain.*;
import interfaces.IMatchSimulator;
import interfaces.ISport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class LiveMatchController {

    public enum State { READY, RUNNING, PAUSED, FINISHED }

    private final ISport       sport;
    private final Match        match;
    private final Random       rng;
    private final boolean      isVolleyball;
    private Team               userControlledTeam;

    private int minute;
    private static final int FOOTBALL_TOTAL = 90;

    private int currentSet;
    private int homeSets, awaySets;
    private int rallyHome, rallyAway;
    private int rallyCount;

    private int homeScore, awayScore;
    private final List<MatchEvent> events = new ArrayList<>();
    private State state = State.READY;
    private Consumer<MatchEvent> onEvent;
    private Consumer<State>      onStateChange;

    public LiveMatchController(ISport sport, Match match, long seed) {
        this.sport         = sport;
        this.match         = match;
        this.rng           = new Random(seed);
        this.isVolleyball  = sport.getName().equalsIgnoreCase("Volleyball");
        this.currentSet    = 0;
    }

    public void setOnEvent(Consumer<MatchEvent> cb) { this.onEvent = cb; }
    public void setOnStateChange(Consumer<State> cb) { this.onStateChange = cb; }

    public int    getMinute()      { return minute; }
    public int    getCurrentSet()  { return currentSet; }
    public int    getHomeScore()   { return homeScore; }
    public int    getAwayScore()   { return awayScore; }
    public int    getHomeSets()    { return homeSets; }
    public int    getAwaySets()    { return awaySets; }
    public int    getRallyHome()   { return rallyHome; }
    public int    getRallyAway()   { return rallyAway; }
    public State  getState()       { return state; }
    public boolean isVolleyball()  { return isVolleyball; }
    public List<MatchEvent> getEvents() { return events; }
    public Team   getHomeTeam()    { return match.getHomeTeam(); }
    public Team   getAwayTeam()    { return match.getAwayTeam(); }
    public Match  getMatch()       { return match; }
    public void   setUserControlledTeam(Team team) { this.userControlledTeam = team; }

    public void start()  { setState(State.RUNNING); }
    public void pause()  { if (state == State.RUNNING) setState(State.PAUSED); }
    public void resume() { if (state == State.PAUSED)  setState(State.RUNNING); }

    public void tick() {
        if (state == State.FINISHED) return;
        if (state != State.RUNNING)  return;
        if (isVolleyball) tickVolleyball();
        else              tickFootball();
    }

    public void skip(int ticks) {
        State previous = state;
        setState(State.RUNNING);
        for (int i = 0; i < ticks && state == State.RUNNING; i++) tick();
        if (state == State.RUNNING && previous == State.PAUSED) setState(State.PAUSED);
    }

    private void tickFootball() {
        minute++;

        double homeAtk = teamAttack(match.getHomeTeam());
        double awayAtk = teamAttack(match.getAwayTeam());

        if (rng.nextDouble() < homeAtk * 0.022) {
            homeScore++;
            fireEvent(EventType.GOAL, minute, randomFromLineup(match.getHomeTeam()), match.getHomeTeam());
        }
        if (rng.nextDouble() < awayAtk * 0.022) {
            awayScore++;
            fireEvent(EventType.GOAL, minute, randomFromLineup(match.getAwayTeam()), match.getAwayTeam());
        }

        if (rng.nextInt(100) < 1) tryInjury(match.getHomeTeam(), minute);
        if (rng.nextInt(100) < 1) tryInjury(match.getAwayTeam(), minute);

        if (rng.nextInt(200) < 1) {
            fireEvent(EventType.YELLOW_CARD, minute, randomFromLineup(match.getHomeTeam()), match.getHomeTeam());
        }

        if (minute >= FOOTBALL_TOTAL) finish();
    }

    private void tickVolleyball() {
        rallyCount++;
        double homeStr = teamAttack(match.getHomeTeam());
        double awayStr = teamAttack(match.getAwayTeam());
        double pHome   = clamp(homeStr / (homeStr + awayStr + 1e-6), 0.18, 0.82);

        if (rng.nextDouble() < pHome) {
            rallyHome++;
            fireEvent(EventType.GOAL, rallyCount, randomFromLineup(match.getHomeTeam()), match.getHomeTeam());
        } else {
            rallyAway++;
            fireEvent(EventType.GOAL, rallyCount, randomFromLineup(match.getAwayTeam()), match.getAwayTeam());
        }

        if (rng.nextInt(40) == 0) tryInjury(match.getHomeTeam(), rallyCount);
        if (rng.nextInt(40) == 0) tryInjury(match.getAwayTeam(), rallyCount);

        int target = (currentSet == 4) ? 15 : 25;
        if (rallyHome >= target && rallyHome - rallyAway >= 2) {
            homeSets++;
            announceSetEnd();
        } else if (rallyAway >= target && rallyAway - rallyHome >= 2) {
            awaySets++;
            announceSetEnd();
        }

        if (homeSets == 3 || awaySets == 3) {
            homeScore = homeSets;
            awayScore = awaySets;
            finish();
        }
    }

    private void announceSetEnd() {
        currentSet++;
        rallyHome = 0;
        rallyAway = 0;
        rallyCount = 0;
    }

    public boolean substitute(Team team, Player out, Player in) {
        if (state == State.FINISHED)             return false;
        if (out == null || in == null)           return false;
        if (in.isInjured())                       return false;

        boolean ok = team.makeSub(out, in);
        if (!ok) return false;

        int when = isVolleyball ? rallyCount : minute;
        int period = isVolleyball ? currentSet : (minute < 45 ? 0 : 1);
        Substitution s = new Substitution(out, in, period);
        match.addSubstitution(s);
        fireEvent(EventType.SUBSTITUTION, when, in, team);
        return true;
    }

    public int subsRemaining(Team team) {
        if (isVolleyball) return Integer.MAX_VALUE;
        return sport.getRosterRules().getSubstitutionLimitPerMatch()
             - team.getSubstitutionsMadeThisMatch();
    }

    public void changeTactic(Team team, interfaces.ITacticStrategy newTactic) {
        team.setCurrentTactic(newTactic);
    }

    private void tryInjury(Team team, int when) {
        List<Player> lineup = team.getStartingLineup();
        if (lineup.isEmpty()) return;
        Player p = lineup.get(rng.nextInt(lineup.size()));
        if (p.isInjured()) return;

        if (rng.nextInt(100) < 15) {
            p.applyInjury(rng.nextInt(2) + 1);
            fireEvent(EventType.INJURY, when, p, team);
            autoReplaceInjuredOpponent(team, p, when);
        }
    }

    private void autoReplaceInjuredOpponent(Team team, Player injured, int when) {
        if (team == null || injured == null) return;
        if (userControlledTeam != null && team == userControlledTeam) return;

        Player replacement = team.findAvailableBenchReplacementFor(injured);
        if (replacement == null) return;

        if (!team.makeSub(injured, replacement)) return;

        int period = isVolleyball ? currentSet : (minute < 45 ? 0 : 1);
        Substitution substitution = new Substitution(injured, replacement, period);
        match.addSubstitution(substitution);
        fireEvent(EventType.SUBSTITUTION, when, replacement, team);
    }

    private Player randomFromLineup(Team team) {
        List<Player> l = team.getStartingLineup();
        if (l.isEmpty()) return null;
        return l.get(rng.nextInt(l.size()));
    }

    private double teamAttack(Team team) {
        List<Player> l = team.getStartingLineup();
        if (l.isEmpty()) return 1.0;
        return l.stream().mapToInt(p -> {
            int finishing = p.getAttribute("finishing");
            int passing   = p.getAttribute("passing");
            int spiking   = p.getAttribute("spiking");
            return Math.max(Math.max(finishing, passing), spiking);
        }).average().orElse(50.0) / 50.0;
    }

    private double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

    private void fireEvent(EventType type, int when, Player p, Team t) {
        if (p == null || t == null) return;
        MatchEvent e = new MatchEvent(type, when, p, t);
        events.add(e);
        match.getEventLog().add(e);
        if (onEvent != null) onEvent.accept(e);
    }

    private void finish() {

        MatchResult r = new MatchResult(homeScore, awayScore,
                match.getHomeTeam(), match.getAwayTeam(),
                new ArrayList<>(events), match.getWeekNumber());
        match.setResult(r);
        match.setPlayed(true);

        applyPostMatchFatigue();
        setState(State.FINISHED);
    }

    private void applyPostMatchFatigue() {
        int basePenalty = isVolleyball ? 30 : 25;
        for (Player p : match.getHomeTeam().getStartingLineup()) p.increaseFatigue(basePenalty);
        for (Player p : match.getAwayTeam().getStartingLineup()) p.increaseFatigue(basePenalty);
    }

    private void setState(State s) {
        this.state = s;
        if (onStateChange != null) onStateChange.accept(s);
    }
}
