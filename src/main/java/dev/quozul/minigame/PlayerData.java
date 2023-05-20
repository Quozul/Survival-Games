package dev.quozul.minigame;

import dev.quozul.UHC.Main;
import dev.quozul.minigame.exceptions.PartyIsPrivate;
import dev.quozul.minigame.exceptions.RoomInGameException;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class PlayerData {
    @NotNull
    private Party party;
    @NotNull
    private final Player player;

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

    private PlayerData(@NotNull Party party, @NotNull Player player) {
        this.party = party;
        this.player = player;
    }

    public @NotNull Party getParty() {
        return party;
    }

    public void setParty(@NotNull Party party) throws RoomInGameException, PartyIsPrivate {
        this.party.leave(player);
        party.join(this.player);
        this.party = party;
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
