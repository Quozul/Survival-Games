package dev.quozul.minigame;

/**
 * Defines the requirements to start a game.
 */
public class RoomRequirements {
    /**
     * Maximum amount of parties a game can handle.
     * Must be greater than minParties.
     */
    final int maxParties;

    /**
     * Required amount of parties for a game to start.
     * Must be less than maxParties.
     */
    private final int minParties;

    /**
     * Maximum amount of players per party.
     */
    final int maxPlayersPerParty;

    /**
     * Minimum amount of players per party.
     */
    final int minPlayersPerParty;

    /**
     * The absolute minimum amount of required players to start a game.
     */
    final int minPlayers;

    /**
     * The absolute maximum amount of required players to start a game.
     */
    final int maxPlayers;

    public RoomRequirements(int minParties, int maxParties, int minPlayersPerParty, int maxPlayersPerParty) {
        this.minParties = minParties;
        this.maxParties = maxParties;
        this.minPlayersPerParty = minPlayersPerParty;
        this.maxPlayersPerParty = maxPlayersPerParty;
        this.minPlayers = minPlayersPerParty * minParties;
        this.maxPlayers = maxPlayersPerParty * maxParties;
    }

    public RoomRequirements(int minPlayers, int maxPlayers) {
        this.minParties = 1;
        this.maxParties = maxPlayers;
        this.minPlayersPerParty = 1;
        this.maxPlayersPerParty = maxPlayers;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    /**
     * @return Default room requirements for at least 1 player.
     */
    public static RoomRequirements zero() {
        return new RoomRequirements(1, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
    }

    public int getMinimumAmountOfPlayers() {
        return minPlayers;
    }

    public long getMaximumAmountOfPlayers() {
        return maxPlayers;
    }
}
