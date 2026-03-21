package interfaces;

import core.domain.Match;

// contract for communication between ui and application layer
public interface IUIController {

    // creates new season with teams, players and fixtures
    void startSeason(String sportName);

    // runs training, simulates fixtures and updates standings for the week
    void nextWeek();

    // displays current league standings table
    void showStandings();

    // displays detail view for a specific match
    void showMatch(Match match);
}