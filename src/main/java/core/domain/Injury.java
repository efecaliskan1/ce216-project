package core.domain;

import java.io.Serializable;

public class Injury implements Serializable {
    private final Player player;
    private final int gamesOut;
    private final String cause;

    public Injury(Player player, int gamesOut, String cause) {
        this.player   = player;
        this.gamesOut = gamesOut;
        this.cause    = cause;
    }

    public Player getPlayer()   { return player; }
    public int    getGamesOut() { return gamesOut; }
    public String getCause()    { return cause; }
}
