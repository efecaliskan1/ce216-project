package interfaces;

import core.domain.Match;

public interface IUIController {
    void startSeason(String sportName);
    void nextWeek();
    void showStandings();
    void showMatch(Match match);

    
}
