package dev.quozul.minigame;

/**
 * Defines the requirements to start a game.
 */
public class RoomRequirements {
    /**
     * Maximum amount of parties a game can handle.
     * Must be greater than minParties.
     */
    int maxParties;

    /**
     * Required amount of parties for a game to start.
     * Must be less than maxParties.
     */
    int minParties;

    /**
     * Maximum amount of players per party.
     */
    int maxPlayersPerParty;

    /**
     * Minimum amount of players per party.
     */
    int minPlayersPerParty;

    public RoomRequirements(int minParties, int maxParties, int minPlayersPerParty, int maxPlayersPerParty) {
        this.minParties = minParties;
        this.maxParties = maxParties;
        this.minPlayersPerParty = minPlayersPerParty;
        this.maxPlayersPerParty = maxPlayersPerParty;
    }

    /**
     * @return Default room requirements for at least 1 player.
     */
    public static RoomRequirements zero() {
        return new RoomRequirements(1, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
    }

    /**
     * @return The absolute minimum amount of required players to start a game.
     */
    public int getMinimumAmountOfPlayers() {
        return minPlayersPerParty * minParties;
    }

    /**
     * @return The absolute maximum amount of required players to start a game.
     */
    public long getMaximumAmountOfPlayers() {
        return (long) maxPlayersPerParty * maxParties;
    }
}
