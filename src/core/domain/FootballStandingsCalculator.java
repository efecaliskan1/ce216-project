package sports.football;

import core.domain.MatchResult;
import core.domain.StandingEntry;
import core.domain.Team;
import interfaces.IStandingsCalculator;
import valueobjects.ScoringConfig;

import java.util.*;

public class FootballStandingsCalculator implements IStandingsCalculator {

    private final Map<Team, StandingEntry> entries  = new LinkedHashMap<>();
    private final List<MatchResult>        history  = new ArrayList<>();
    private final ScoringConfig            scoring  = ScoringConfig.footballDefaults();
    private Random random;

    public FootballStandingsCalculator()          { this.random = new Random(); }
    public FootballStandingsCalculator(long seed) { this.random = new Random(seed); }

    @Override
    public void update(MatchResult result) {
        history.add(result);
        entry(result.getHomeTeam()).addResult(result, scoring.getWinPoints(), scoring.getDrawPoints(), scoring.getLosePoints());
        entry(result.getAwayTeam()).addResult(result, scoring.getWinPoints(), scoring.getDrawPoints(), scoring.getLosePoints());
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

        // Step 1 – points
        if (ea.getPoints() != eb.getPoints())
            return Integer.compare(eb.getPoints(), ea.getPoints());

        // Step 2 – head-to-head
        int h2h = headToHead(a, b);
        if (h2h != 0) return h2h;

        // Step 3 – goal difference
        if (ea.getGoalDifference() != eb.getGoalDifference())
            return Integer.compare(eb.getGoalDifference(), ea.getGoalDifference());

        // Step 4 – goals scored
        if (ea.getGoalsFor() != eb.getGoalsFor())
            return Integer.compare(eb.getGoalsFor(), ea.getGoalsFor());

        // Step 5 – coin toss
        return random.nextBoolean() ? -1 : 1;
    }

    @Override public void setSeed(long seed) { this.random = new Random(seed); }

    private StandingEntry entry(Team t) {
        return entries.computeIfAbsent(t, StandingEntry::new);
    }

    private int headToHead(Team a, Team b) {
        int ap = 0, bp = 0;
        for (MatchResult r : history) {
            boolean relevant = (r.getHomeTeam().equals(a) && r.getAwayTeam().equals(b))
                            || (r.getHomeTeam().equals(b) && r.getAwayTeam().equals(a));
            if (!relevant) continue;
            if (r.isDraw()) { ap += scoring.getDrawPoints(); bp += scoring.getDrawPoints(); }
            else r.getWinner().ifPresent(w -> { /* handled below */ });

            if (!r.isDraw() && r.getWinner().isPresent()) {
                if (r.getWinner().get().equals(a)) ap += scoring.getWinPoints();
                else                               bp += scoring.getWinPoints();
            }
        }
        return Integer.compare(bp, ap);
    }
}
