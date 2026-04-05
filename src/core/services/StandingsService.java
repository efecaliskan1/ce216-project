package core.services;

import core.domain.MatchResult;
import core.domain.StandingEntry;
import interfaces.IStandingsCalculator;

import java.util.List;

public class StandingsService {

    private final IStandingsCalculator calculator;

    public StandingsService(IStandingsCalculator calculator) {
        this.calculator = calculator;
    }

    public void processResult(MatchResult result) {
        calculator.update(result);
    }

    public List<StandingEntry> getTable() {
        return calculator.getStandings();
    }

    public IStandingsCalculator getCalculator() {
        return calculator;
    }
}
