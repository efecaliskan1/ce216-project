package sports.volleyball;

import abstracts.AbstractMatchSimulator;
import core.domain.*;
import valueobjects.PeriodResult;
import valueobjects.TacticResult;

import java.util.ArrayList;
import java.util.List;

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

        MatchResult result = new MatchResult(
                homeSets, awaySets,
                match.getHomeTeam(), match.getAwayTeam(),
                allEvents, match.getWeekNumber());

        match.setResult(result);
        match.setPlayed(true);
        if (observer != null) observer.onMatchEnd(result);
        return result;
    }

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

                MatchEvent e = new MatchEvent(EventType.GOAL, rallyMinute,
                        randomPlayer(home.getStartingLineup()), home);
                events.add(e); fireEvent(e);
            } else {
                awayScore++;
                MatchEvent e = new MatchEvent(EventType.GOAL, rallyMinute,
                        randomPlayer(away.getStartingLineup()), away);
                events.add(e); fireEvent(e);
            }

            if (random.nextInt(10) == 0) rollAndApplyInjury(home, events, rallyMinute);
            if (random.nextInt(10) == 0) rollAndApplyInjury(away, events, rallyMinute);

            if (homeScore >= target && homeScore - awayScore >= WIN_BY) break;
            if (awayScore >= target && awayScore - homeScore >= WIN_BY) break;

            if (rallyMinute > 200) break;
        }
        return new PeriodResult(homeScore, awayScore, events);
    }

    private void rollAndApplyInjury(Team team, List<MatchEvent> events, int rallyMinute) {
        List<Player> lineup = team.getStartingLineup();
        if (lineup.isEmpty()) return;
        Player p = lineup.get(random.nextInt(lineup.size()));
        if (rollInjury(p)) {
            p.applyInjury(random.nextInt(2) + 1);
            MatchEvent e = new MatchEvent(EventType.INJURY, rallyMinute, p, team);
            events.add(e); fireEvent(e);
        }
    }

    private void applyPostMatchFatigue(Team home, Team away, int setsPlayed) {

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
