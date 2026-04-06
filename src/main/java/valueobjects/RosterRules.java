package valueobjects;

import java.io.Serializable;

public class RosterRules implements Serializable {
    private final int rosterSize;
    private final int benchSize;
    private final int substitutionLimitPerMatch;

    public RosterRules(int rosterSize, int benchSize, int substitutionLimitPerMatch) {
        this.rosterSize = rosterSize;
        this.benchSize = benchSize;
        this.substitutionLimitPerMatch = substitutionLimitPerMatch;
    }

    public static RosterRules defaults() {
        return new RosterRules(18, 7, 3);
    }

    public int getRosterSize()                { return rosterSize; }
    public int getBenchSize()                 { return benchSize; }
    public int getSubstitutionLimitPerMatch() { return substitutionLimitPerMatch; }
    public int getStartingLineupSize()        { return rosterSize - benchSize; }
}
