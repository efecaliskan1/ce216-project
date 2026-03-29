package core.domain;

public class Injury {
    private final Player player;
    private final int gamesOut;
    private final String cause;

    public Injury(Player player, int gamesOut, String cause) {
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }
        if (gamesOut <= 0) {
            throw new IllegalArgumentException("gamesOut must be positive");
        }
        if (cause == null || cause.trim().isEmpty()) {
            throw new IllegalArgumentException("cause cannot be blank");
        }

        this.player = player;
        this.gamesOut = gamesOut;
        this.cause = cause;
    }

    public Player getPlayer() {
        return player;
    }

    public int getGamesOut() {
        return gamesOut;
    }

    public String getCause() {
        return cause;
    }
}
