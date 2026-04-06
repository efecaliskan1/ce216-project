package sports.football;

import abstracts.AbstractMatchSimulator;
import core.domain.*;
import valueobjects.PeriodResult;
import valueobjects.TacticResult;

import java.util.ArrayList;
import java.util.List;

public class FootballMatchSimulator extends AbstractMatchSimulator {

    @Override
    public MatchResult simulate(Match match) {
        match.getHomeTeam().resetMatchState();
        match.getAwayTeam().resetMatchState();

        int homeTotal = 0, awayTotal = 0;
        List<MatchEvent> allEvents = new ArrayList<>();

        for (int period = 0; period < 2; period++) {
            match.currentPeriod = period;
            match.applyCurrentTactics();

            PeriodResult pr = simulatePeriod(
                    match.getHomeTeam(), match.getAwayTeam(),
                    match.homeAppliedTactic, match.awayAppliedTactic,
                    period);

            homeTotal += pr.getHomeScore();
            awayTotal += pr.getAwayScore();
            match.eventLog.addAll(pr.getEvents());
            allEvents.addAll(pr.getEvents());

            if (observer != null)
                observer.onPeriodEnd(period, homeTotal, awayTotal);
        }

        applyPostMatchFatigue(match.getHomeTeam(), match.getAwayTeam());

        MatchResult result = new MatchResult(
                homeTotal, awayTotal,
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
        double homeAtk  = avg(home, "finishing") * ht.getPressureScore();
        double awayDef  = avg(away, "tackling")  * (1 - at.getWidthScore() * 0.3);
        double awayAtk  = avg(away, "finishing") * at.getPressureScore();
        double homeDef  = avg(home, "tackling")  * (1 - ht.getWidthScore() * 0.3);

        double probHome = clamp(homeAtk / (homeAtk + awayDef + 1e-6), 0.05, 0.70);
        double probAway = clamp(awayAtk / (awayAtk + homeDef + 1e-6), 0.05, 0.70);

        int homeScore = 0, awayScore = 0;
        List<MatchEvent> events = new ArrayList<>();
        int offset = period * 45;

        for (int min = 1; min <= 45; min++) {
            if (random.nextDouble() < probHome / 45.0) {
                homeScore++;
                MatchEvent e = new MatchEvent(EventType.GOAL, offset + min,
                        randomPlayer(home.getStartingLineup()), home);
                events.add(e); fireEvent(e);
            }
            if (random.nextDouble() < probAway / 45.0) {
                awayScore++;
                MatchEvent e = new MatchEvent(EventType.GOAL, offset + min,
                        randomPlayer(away.getStartingLineup()), away);
                events.add(e); fireEvent(e);
            }
            for (Player p : home.getStartingLineup()) {
                if (rollInjury(p)) {
                    p.applyInjury(random.nextInt(3) + 1);
                    MatchEvent e = new MatchEvent(EventType.INJURY, offset + min, p, home);
                    events.add(e); fireEvent(e);
                }
            }
        }
        return new PeriodResult(homeScore, awayScore, events);
    }

    private void applyPostMatchFatigue(Team home, Team away) {
        for (Player p : home.getStartingLineup())
            p.increaseFatigue((int)(10 * home.getCurrentTactic().getFatigueMultiplier()));
        for (Player p : away.getStartingLineup())
            p.increaseFatigue((int)(10 * away.getCurrentTactic().getFatigueMultiplier()));
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
