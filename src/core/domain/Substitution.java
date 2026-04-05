package core.domain;

import java.io.Serializable;

public class Substitution implements Serializable {
    private final Player playerOut;
    private final Player playerIn;
    private final int period;

    public Substitution(Player playerOut, Player playerIn, int period) {
        this.playerOut = playerOut;
        this.playerIn  = playerIn;
        this.period    = period;
    }

    public Player getPlayerOut() { return playerOut; }
    public Player getPlayerIn()  { return playerIn; }
    public int    getPeriod()    { return period; }
}
