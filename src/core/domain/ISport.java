package interfaces;

import core.domain.MatchResult;
import core.domain.StandingEntry;
import core.domain.Team;

import java.util.List;

// standings and tiebreaker logic contract
public interface IStandingsCalculator {

    // updates standings with new match result
    void update(MatchResult result);

    // returns sorted standings using tiebreaker rules
    List<StandingEntry> getStandings();

    // compares two teams based on points and tiebreakers
    int compareTeams(Team a, Team b);

    // seeds rng for deterministic tiebreaking
    void setSeed(long seed);
}