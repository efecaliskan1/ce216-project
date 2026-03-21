package core.domain;

// immutable roster constraints for a sport
public final class RosterRules {

    private final int rosterSize;
    private final int benchSize;
    private final int substitutionLimitPerMatch;

    // defines squad sizes and sub limits
    public RosterRules(int rosterSize, int benchSize, int substitutionLimitPerMatch) {
        if (rosterSize <= 0) {
            throw new IllegalArgumentException("rosterSize must be positive");
        }
        if (benchSize < 0 || benchSize >= rosterSize) {
            throw new IllegalArgumentException(
                    "benchSize must be >= 0 and < rosterSize");
        }
        if (substitutionLimitPerMatch < 0) {
            throw new IllegalArgumentException(
                    "substitutionLimitPerMatch must be >= 0");
        }
        this.rosterSize = rosterSize;
        this.benchSize = benchSize;
        this.substitutionLimitPerMatch = substitutionLimitPerMatch;
    }

    public int getRosterSize() {
        return rosterSize;
    }

    public int getBenchSize() {
        return benchSize;
    }

    // returns starters count
    public int getStartingLineupSize() {
        return rosterSize - benchSize;
    }

    public int getSubstitutionLimitPerMatch() {
        return substitutionLimitPerMatch;
    }

    @Override
    public String toString() {
        return "RosterRules{rosterSize=" + rosterSize
                + ", benchSize=" + benchSize
                + ", subLimit=" + substitutionLimitPerMatch + '}';
    }
}