package core.domain;

public class Substitution {
    private final Player playerOut;
    private final Player playerIn;
    private final int period;

    public Substitution(Player playerOut, Player playerIn, int period) {
        if (playerOut == null) {
            throw new IllegalArgumentException("playerOut cannot be null");
        }
        if (playerIn == null) {
            throw new IllegalArgumentException("playerIn cannot be null");
        }
        if (period < 0) {
            throw new IllegalArgumentException("period cannot be negative");
        }

        this.playerOut = playerOut;
        this.playerIn = playerIn;
        this.period = period;
    }

    public Player getPlayerOut() {
        return playerOut;
    }

    public Player getPlayerIn() {
        return playerIn;
    }

    public int getPeriod() {
        return period;
    }
}
