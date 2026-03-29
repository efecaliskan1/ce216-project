package core.domain;

public class MatchEvent {
    private final EventType type;
    private final int minute;
    private final Player player;
    private final Team team;

    public MatchEvent(EventType type, int minute, Player player, Team team) {
        this.type = type;
        this.minute = minute;
        this.player = player;
        this.team = team;
    }

    public EventType getType() {
        return type;
    }

    public int getMinute() {
        return minute;
    }

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }
}
