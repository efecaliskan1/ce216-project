package core.services;

import core.domain.GameWeek;
import core.domain.League;
import core.domain.Match;
import core.domain.Team;

import java.util.ArrayList;
import java.util.List;

public class FixtureGenerator {

    public List<GameWeek> generate(League league) {
        if (league == null) {
            throw new IllegalArgumentException("league cannot be null");
        }

        List<Team> teams = new ArrayList<>(league.getTeams());
        if (teams.isEmpty()) {
            return new ArrayList<>();
        }
        if (teams.size() % 2 != 0) {
            teams.add(null);
        }

        List<GameWeek> gameWeeks = new ArrayList<>();
        List<List<Match>> firstRound = buildRound(teams, 0);
        List<List<Match>> secondRound = buildRound(teams, firstRound.size());

        for (int i = 0; i < secondRound.size(); i++) {
            List<Match> reversed = new ArrayList<>();
            for (Match match : secondRound.get(i)) {
                if (match != null) {
                    reversed.add(new Match(match.getAwayTeam(), match.getHomeTeam(), firstRound.size() + i));
                }
            }
            secondRound.set(i, reversed);
        }

        int weekNumber = 0;
        for (List<Match> round : firstRound) {
            GameWeek gameWeek = new GameWeek(weekNumber++);
            for (Match match : round) {
                gameWeek.addFixture(match);
                league.addFixture(match);
            }
            gameWeeks.add(gameWeek);
        }

        for (List<Match> round : secondRound) {
            GameWeek gameWeek = new GameWeek(weekNumber++);
            for (Match match : round) {
                gameWeek.addFixture(match);
                league.addFixture(match);
            }
            gameWeeks.add(gameWeek);
        }

        return gameWeeks;
    }

    private List<List<Match>> buildRound(List<Team> teams, int weekOffset) {
        int teamCount = teams.size();
        List<List<Match>> rounds = new ArrayList<>();
        Team fixed = teams.get(0);
        List<Team> rotating = new ArrayList<>(teams.subList(1, teamCount));

        for (int roundIndex = 0; roundIndex < teamCount - 1; roundIndex++) {
            List<Match> round = new ArrayList<>();

            Team home = fixed;
            Team away = rotating.get(0);
            if (home != null && away != null) {
                round.add(new Match(home, away, weekOffset + roundIndex));
            }

            for (int i = 1; i < teamCount / 2; i++) {
                home = rotating.get(i);
                away = rotating.get(teamCount - 1 - i);
                if (home != null && away != null) {
                    round.add(new Match(home, away, weekOffset + roundIndex));
                }
            }

            rounds.add(round);
            rotating.add(0, rotating.remove(rotating.size() - 1));
        }

        return rounds;
    }
}
