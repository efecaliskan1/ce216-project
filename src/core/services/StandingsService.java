package core.services;

import core.domain.MatchResult;
import core.domain.StandingEntry;
import interfaces.IStandingsCalculator;

import java.util.List;

public class StandingsService {
    private final IStandingsCalculator calculator;

    public StandingsService(IStandingsCalculator calculator) {
        if (calculator == null) {
            throw new IllegalArgumentException("calculator cannot be null");
        }
        this.calculator = calculator;
    }

    public void processResult(MatchResult result) {
        if (result != null) {
            calculator.update(result);
        }
    }

    public List<StandingEntry> getTable() {
        return calculator.getStandings();
    }

    public IStandingsCalculator getCalculator() {
        return calculator;
    }
}
