package sports.volleyball;

import core.domain.MatchResult;
import core.domain.StandingEntry;
import core.domain.Team;
import interfaces.IStandingsCalculator;

import java.util.*;

public class VolleyballStandingsCalculator implements IStandingsCalculator {

    private final Map<Team, StandingEntry> entries = new LinkedHashMap<>();
    private final List<MatchResult>        history = new ArrayList<>();
    private Random random;

    public VolleyballStandingsCalculator()          { this.random = new Random(); }
    public VolleyballStandingsCalculator(long seed) { this.random = new Random(seed); }

    @Override
    public void update(MatchResult result) {
        history.add(result);
        int homeSets = result.getHomeScore();
        int awaySets = result.getAwayScore();

        int homePoints, awayPoints;
        if (homeSets == 3 && awaySets <= 1) {
            homePoints = 3; awayPoints = 0;
        } else if (awaySets == 3 && homeSets <= 1) {
            homePoints = 0; awayPoints = 3;
        } else if (homeSets == 3 && awaySets == 2) {
            homePoints = 2; awayPoints = 1;
        } else if (awaySets == 3 && homeSets == 2) {
            homePoints = 1; awayPoints = 2;
        } else {

            homePoints = 0; awayPoints = 0;
        }

        if (homeSets > awaySets) {

            entry(result.getHomeTeam()).addResult(result, homePoints, 0, 0);
            entry(result.getAwayTeam()).addResult(result, 0, 0, awayPoints);
        } else {

            entry(result.getHomeTeam()).addResult(result, 0, 0, homePoints);
            entry(result.getAwayTeam()).addResult(result, awayPoints, 0, 0);
        }
    }

    @Override
    public List<StandingEntry> getStandings() {
        List<StandingEntry> list = new ArrayList<>(entries.values());
        list.sort((a, b) -> compareTeams(a.getTeam(), b.getTeam()));
        return list;
    }

    @Override
    public int compareTeams(Team a, Team b) {
        StandingEntry ea = entry(a), eb = entry(b);

        if (ea.getPoints() != eb.getPoints())
            return Integer.compare(eb.getPoints(), ea.getPoints());

        if (ea.getWon() != eb.getWon())
            return Integer.compare(eb.getWon(), ea.getWon());

        double ratioA = ratio(ea.getGoalsFor(), ea.getGoalsAgainst());
        double ratioB = ratio(eb.getGoalsFor(), eb.getGoalsAgainst());
        if (ratioA != ratioB) return Double.compare(ratioB, ratioA);

        int h2h = headToHead(a, b);
        if (h2h != 0) return h2h;

        return random.nextBoolean() ? -1 : 1;
    }

    @Override public void setSeed(long seed) { this.random = new Random(seed); }

    private StandingEntry entry(Team t) {
        return entries.computeIfAbsent(t, StandingEntry::new);
    }

    private double ratio(int setsFor, int setsAgainst) {
        if (setsAgainst == 0) return setsFor == 0 ? 0.0 : Double.MAX_VALUE;
        return (double) setsFor / (double) setsAgainst;
    }

    private int headToHead(Team a, Team b) {
        int ap = 0, bp = 0;
        for (MatchResult r : history) {
            boolean aVsB = r.getHomeTeam().equals(a) && r.getAwayTeam().equals(b);
            boolean bVsA = r.getHomeTeam().equals(b) && r.getAwayTeam().equals(a);
            if (!aVsB && !bVsA) continue;

            int homeSets = r.getHomeScore();
            int awaySets = r.getAwayScore();
            int winnerPts = (Math.max(homeSets, awaySets) == 3 && Math.min(homeSets, awaySets) <= 1) ? 3 : 2;
            int loserPts  = winnerPts == 3 ? 0 : 1;

            boolean homeWon = homeSets > awaySets;
            Team winner = homeWon ? r.getHomeTeam() : r.getAwayTeam();

            if (winner.equals(a)) { ap += winnerPts; bp += loserPts; }
            else                  { bp += winnerPts; ap += loserPts; }
        }
        return Integer.compare(bp, ap);
    }
}
