package sports.volleyball;

import abstracts.AbstractMatchSimulator;
import core.domain.*;
import valueobjects.PeriodResult;
import valueobjects.TacticResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Volleyball match simulator.
 * Rules implemented:
 *  - Best of 5 sets.
 *  - Sets 1-4: first to 25 points, must win by 2.
 *  - Set 5 (tiebreak): first to 15 points, must win by 2.
 *  - A match ends as soon as one team wins 3 sets.
 *
 * The "score" returned in MatchResult is the number of SETS won,
 * not total points, so the standings calculator can see 3-0, 3-1, 3-2, 2-3, 1-3, 0-3.
 */
public class VolleyballMatchSimulator extends AbstractMatchSimulator {

    private static final int REGULAR_SET_TARGET = 25;
    private static final int TIEBREAK_TARGET    = 15;
    private static final int WIN_BY             = 2;

    @Override
    public MatchResult simulate(Match match) {
        match.getHomeTeam().resetMatchState();
        match.getAwayTeam().resetMatchState();

        int homeSets = 0, awaySets = 0;
        List<MatchEvent> allEvents = new ArrayList<>();
        int setsPlayed = 0;

        for (int period = 0; period < 5 && homeSets < 3 && awaySets < 3; period++) {
            match.setCurrentPeriod(period);
            match.applyCurrentTactics();

            PeriodResult pr = simulatePeriod(
                    match.getHomeTeam(), match.getAwayTeam(),
                    match.getHomeAppliedTactic(), match.getAwayAppliedTactic(),
                    period);

            if (pr.getHomeScore() > pr.getAwayScore()) homeSets++;
            else                                      awaySets++;

            match.getEventLog().addAll(pr.getEvents());
            allEvents.addAll(pr.getEvents());
            setsPlayed++;

            if (observer != null)
                observer.onPeriodEnd(period, homeSets, awaySets);
        }

        applyPostMatchFatigue(match.getHomeTeam(), match.getAwayTeam(), setsPlayed);

        // Score in MatchResult = sets won (volleyball-specific interpretation)
        MatchResult result = new MatchResult(
                homeSets, awaySets,
                match.getHomeTeam(), match.getAwayTeam(),
                allEvents, match.getWeekNumber());

        match.setResult(result);
        match.setPlayed(true);
        if (observer != null) observer.onMatchEnd(result);
        return result;
    }

    /** Simulates a single set rally-by-rally until one team reaches the target with a 2-point lead. */
    @Override
    public PeriodResult simulatePeriod(Team home, Team away,
                                       TacticResult ht, TacticResult at,
                                       int period) {
        int target = (period == 4) ? TIEBREAK_TARGET : REGULAR_SET_TARGET;

        double homeAtk = avg(home, "spiking")   * ht.getPressureScore();
        double homeDef = avg(home, "blocking")  * (1 - ht.getWidthScore() * 0.3);
        double awayAtk = avg(away, "spiking")   * at.getPressureScore();
        double awayDef = avg(away, "blocking")  * (1 - at.getWidthScore() * 0.3);

        double homeStrength = (homeAtk + homeDef) / 2.0;
        double awayStrength = (awayAtk + awayDef) / 2.0;
        double probHomeWinsRally = clamp(
                homeStrength / (homeStrength + awayStrength + 1e-6), 0.15, 0.85);

        int homeScore = 0, awayScore = 0;
        List<MatchEvent> events = new ArrayList<>();
        int rallyMinute = 0;

        while (true) {
            rallyMinute++;
            if (random.nextDouble() < probHomeWinsRally) {
                homeScore++;
                // Fire a GOAL event for each point scored (treated as "point" in volleyball)
                MatchEvent e = new MatchEvent(EventType.GOAL, rallyMinute,
                        randomPlayer(home.getStartingLineup()), home);
                events.add(e); fireEvent(e);
            } else {
                awayScore++;
                MatchEvent e = new MatchEvent(EventType.GOAL, rallyMinute,
                        randomPlayer(away.getStartingLineup()), away);
                events.add(e); fireEvent(e);
            }

            // injury rolls - sparingly, roughly once per 10 rallies per side
            if (random.nextInt(10) == 0) rollAndApplyInjury(home, events, rallyMinute);
            if (random.nextInt(10) == 0) rollAndApplyInjury(away, events, rallyMinute);

            // Check set-end: target reached AND 2+ point lead
            if (homeScore >= target && homeScore - awayScore >= WIN_BY) break;
            if (awayScore >= target && awayScore - homeScore >= WIN_BY) break;

            // Safety cap - volleyball sets can theoretically go forever, cap at 40
            if (rallyMinute > 200) break;
        }
        return new PeriodResult(homeScore, awayScore, events);
    }

    private void rollAndApplyInjury(Team team, List<MatchEvent> events, int rallyMinute) {
        List<Player> lineup = team.getStartingLineup();
        if (lineup.isEmpty()) return;
        Player p = lineup.get(random.nextInt(lineup.size()));
        if (rollInjury(p)) {
            p.applyInjury(random.nextInt(3) + 1);
            MatchEvent e = new MatchEvent(EventType.INJURY, rallyMinute, p, team);
            events.add(e); fireEvent(e);
        }
    }

    private void applyPostMatchFatigue(Team home, Team away, int setsPlayed) {
        // More sets = more fatigue. Base 6 per set.
        int fatiguePerSet = 6;
        for (Player p : home.getStartingLineup())
            p.increaseFatigue((int)(setsPlayed * fatiguePerSet * home.getCurrentTactic().getFatigueMultiplier()));
        for (Player p : away.getStartingLineup())
            p.increaseFatigue((int)(setsPlayed * fatiguePerSet * away.getCurrentTactic().getFatigueMultiplier()));
    }

    private double avg(Team team, String attr) {
        List<Player> lineup = team.getStartingLineup();
        if (lineup.isEmpty()) return 50.0;
        return lineup.stream().mapToInt(p -> p.getAttribute(attr)).average().orElse(50.0);
    }

    private double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

    private Player randomPlayer(List<Player> players) {
        if (players.isEmpty()) return null;
        return players.get(random.nextInt(players.size()));
    }
}
