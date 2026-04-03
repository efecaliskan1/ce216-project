package core.domain;

public class StandingEntry {
    private final Team team;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;
    private int points;

    public StandingEntry(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("team cannot be null");
        }
        this.team = team;
    }

    public void addResult(MatchResult result, int winPoints, int drawPoints, int losePoints) {
        if (result == null) {
            return;
        }
        boolean teamInResult = result.getHomeTeam().equals(team) || result.getAwayTeam().equals(team);
        if (!teamInResult) {
            throw new IllegalArgumentException("result does not belong to this team");
        }

        played++;
        goalsFor += result.getGoalsFor(team);
        goalsAgainst += result.getGoalsAgainst(team);

        if (result.isDraw()) {
            drawn++;
            points += drawPoints;
            return;
        }

        if (result.getWinner().isPresent() && result.getWinner().get().equals(team)) {
            won++;
            points += winPoints;
        } else {
            lost++;
            points += losePoints;
        }
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public Team getTeam() {
        return team;
    }

    public int getPlayed() {
        return played;
    }

    public int getWon() {
        return won;
    }

    public int getDrawn() {
        return drawn;
    }

    public int getLost() {
        return lost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getPoints() {
        return points;
    }
}
