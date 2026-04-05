package core.services;

import core.domain.GameWeek;
import core.domain.League;
import core.domain.Match;
import core.domain.Team;

import java.util.ArrayList;
import java.util.List;

public class FixtureGenerator {

    /**
     * Generates a full double round-robin schedule.
     * For N teams: (N-1)*2 game weeks, each with N/2 matches.
     * Each team plays every other team exactly twice (home + away).
     */
    public List<GameWeek> generate(League league) {
        List<Team> teams = new ArrayList<>(league.getTeams());
        if (teams.size() % 2 != 0) teams.add(null); // dummy for odd count
        int n = teams.size();

        List<GameWeek> gameWeeks = new ArrayList<>();
        List<List<Match>> firstRound  = buildRound(teams, 0);
        List<List<Match>> secondRound = buildRound(teams, firstRound.size());

        // reverse home/away for second round
        for (int i = 0; i < secondRound.size(); i++) {
            List<Match> reversed = new ArrayList<>();
            for (Match m : secondRound.get(i)) {
                if (m != null) reversed.add(new Match(m.getAwayTeam(), m.getHomeTeam(),
                        firstRound.size() + i));
            }
            secondRound.set(i, reversed);
        }

        int weekNum = 0;
        for (List<Match> round : firstRound) {
            GameWeek gw = new GameWeek(weekNum++);
            for (Match m : round) { gw.addFixture(m); league.addFixture(m); }
            gameWeeks.add(gw);
        }
        for (List<Match> round : secondRound) {
            GameWeek gw = new GameWeek(weekNum++);
            for (Match m : round) { gw.addFixture(m); league.addFixture(m); }
            gameWeeks.add(gw);
        }
        return gameWeeks;
    }

    private List<List<Match>> buildRound(List<Team> teams, int weekOffset) {
        int n = teams.size();
        List<List<Match>> rounds = new ArrayList<>();
        Team fixed = teams.get(0);
        List<Team> rotating = new ArrayList<>(teams.subList(1, n));

        for (int r = 0; r < n - 1; r++) {
            List<Match> round = new ArrayList<>();
            Team t1 = fixed;
            Team t2 = rotating.get(0);
            if (t1 != null && t2 != null)
                round.add(new Match(t1, t2, weekOffset + r));

            for (int i = 1; i < n / 2; i++) {
                t1 = rotating.get(i);
                t2 = rotating.get(n - 1 - i);
                if (t1 != null && t2 != null)
                    round.add(new Match(t1, t2, weekOffset + r));
            }
            rounds.add(round);
            rotating.add(0, rotating.remove(rotating.size() - 1));
        }
        return rounds;
    }
}
