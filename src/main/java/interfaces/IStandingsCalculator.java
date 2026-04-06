package interfaces;

import core.domain.MatchResult;
import core.domain.StandingEntry;
import core.domain.Team;

import java.util.List;

public interface IStandingsCalculator {
    void update(MatchResult result);
    List<StandingEntry> getStandings();
    int compareTeams(Team a, Team b);
    void setSeed(long seed);
}
