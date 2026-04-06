package core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class League implements Serializable {

    private final String name;
    private final List<Team> teams;
    private final List<Match> fixtures;
    private final List<MatchResult> matchHistory; // for head-to-head; no standings here

    public League(String name) {
        this.name         = name;
        this.teams        = new ArrayList<>();
        this.fixtures     = new ArrayList<>();
        this.matchHistory = new ArrayList<>();
    }

    public void addTeam(Team team)         { teams.add(team); }
    public void addFixture(Match match)    { fixtures.add(match); }
    public void addResult(MatchResult r)   { matchHistory.add(r); }

    public List<MatchResult> getMatchHistory() {
        return Collections.unmodifiableList(matchHistory);
    }

    public List<Match> getFixturesForWeek(int week) {
        return fixtures.stream()
                .filter(m -> m.getWeekNumber() == week)
                .collect(Collectors.toList());
    }

    public List<Match> getFixturesForTeam(Team team) {
        return fixtures.stream()
                .filter(m -> m.getHomeTeam().equals(team) || m.getAwayTeam().equals(team))
                .collect(Collectors.toList());
    }

    public String      getName()    { return name; }
    public List<Team>  getTeams()   { return Collections.unmodifiableList(teams); }
    public List<Match> getFixtures(){ return Collections.unmodifiableList(fixtures); }
}
