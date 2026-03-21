package interfaces;

import core.domain.MatchResult;
import core.domain.StandingEntry;
import core.domain.Team;

import java.util.List;

// standings and tiebreaker logic contract [cite: 14, 733, 759]
public interface IStandingsCalculator {

    // updates standings with new match result [cite: 15, 270, 305]
    void update(MatchResult result);

    // returns sorted standings using tiebreaker rules [cite: 16, 274, 307]
    List<StandingEntry> getStandings();

    // compares two teams based on points and tiebreakers [cite: 17, 276, 821]
    int compareTeams(Team a, Team b);

    // seeds rng for deterministic tiebreaking [cite: 18, 269, 547]
    void setSeed(long seed);
}