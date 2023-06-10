package dev.quozul.minigame;

import dev.quozul.UHC.Main;
import dev.quozul.minigame.exceptions.PartyIsPrivate;
import dev.quozul.minigame.exceptions.RoomInGameException;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlayerData {
    @NotNull
    private Party party;
    @NotNull
    private final Player player;
    @Nullable
    private GameData gameData;

    private static final @NotNull NamespacedKey namespace = new NamespacedKey(Main.plugin, "player");

    public static @NotNull PlayerData from(@NotNull Player player) {
        for (MetadataValue value : player.getMetadata(namespace.getKey())) {
            if (value instanceof PlayerMetadata) {
                return ((PlayerMetadata) value).value();
            }
        }

        Party party = new Party(player, false);
        PlayerData playerData = new PlayerData(party, player);
        PlayerMetadata playerMetadata = new PlayerMetadata(playerData, Main.plugin);
        player.setMetadata(namespace.getKey(), playerMetadata);

        return playerData;
    }

    public static void remove(@NotNull Player player) {
        // Remove party
        Party party = from(player).getParty();
        party.forceRemovePlayer(player);

        // Remove metadata
        player.removeMetadata(namespace.getKey(), Main.plugin);
    }

    /**
     * Whether the given Metadatable is a Player and is not in a game, therefore it should be invincible.
     */
    public static boolean shouldBeInvisible(@NotNull Metadatable metadatable) {
        if (metadatable instanceof Player player) {
            Room room = PlayerData.from(player).getParty().getRoom();
            return room == null || room.getSession().isOpen();
        }
        return false;
    }

    private PlayerData(@NotNull Party party, @NotNull Player player) {
        this.party = party;
        this.player = player;
    }

    public @NotNull Party getParty() {
        return party;
    }

    /**
     * Leave previous Party and joins a new one.
     */
    public void setParty(@NotNull Party party) throws RoomInGameException, PartyIsPrivate {
        this.party.removePlayer(player);
        party.addPlayer(this.player);
        this.party = party;
    }

    public @Nullable GameData getGameData() {
        return gameData;
    }

    public void setGameData(@Nullable GameData gameData) {
        this.gameData = gameData;
    }
}

final class PlayerMetadata implements MetadataValue {
    @NotNull
    private final JavaPlugin plugin;
    @NotNull
    private final PlayerData value;

    public PlayerMetadata(@NotNull PlayerData value, @NotNull JavaPlugin plugin) {
        this.value = value;
        this.plugin = plugin;
    }

    @Override
    public @NotNull PlayerData value() {
        return value;
    }

    @Override
    public int asInt() {
        throw new NullPointerException();
    }

    @Override
    public float asFloat() {
        throw new NullPointerException();
    }

    @Override
    public double asDouble() {
        throw new NullPointerException();
    }

    @Override
    public long asLong() {
        throw new NullPointerException();
    }

    @Override
    public short asShort() {
        throw new NullPointerException();
    }

    @Override
    public byte asByte() {
        throw new NullPointerException();
    }

    @Override
    public boolean asBoolean() {
        throw new NullPointerException();
    }

    @Override
    public @NotNull String asString() {
        throw new NullPointerException();
    }

    @Override
    public @NotNull Plugin getOwningPlugin() {
        return plugin;
    }

    @Override
    public void invalidate() {
        throw new NullPointerException();
    }
}
