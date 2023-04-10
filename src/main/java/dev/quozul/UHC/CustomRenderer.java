package dev.quozul.UHC;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

public class CustomRenderer extends MapRenderer {
    private final SurvivalGame game;

    public CustomRenderer(SurvivalGame game) {
        this.game = game;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        MapCursorCollection cursorCollection = new MapCursorCollection();

        MapCursor centerCursor = new MapCursor((byte) 0, (byte) 0, (byte) 0, MapCursor.Type.GREEN_POINTER, true);
        cursorCollection.addCursor(centerCursor);

        cursorCollection.addCursor(makeCursor(player.getLocation(), map, MapCursor.Type.WHITE_POINTER, true));

        for (Location location : game.getChests()) {
            cursorCollection.addCursor(makeCursor(location, map, MapCursor.Type.WHITE_CROSS, true));
        }

        canvas.setCursors(cursorCollection);
    }

    private MapCursor makeCursor(Location location, MapView mapView, @NotNull MapCursor.Type type, boolean visible) {
        int scale = getScaleInBlocks(mapView.getScale()) / 2;

        int dir = (int) Math.floor((location.getYaw() / 360 * 16) + 0.5) & 0xF;
        int x = (int) ((location.getX() - mapView.getCenterX()) / scale * 128);
        int z = (int) ((location.getZ() - mapView.getCenterZ()) / scale * 128);

        return new MapCursor(fix(x), fix(z), (byte) dir, type, visible);
    }

    private int getScaleInBlocks(MapView.Scale scale) {
        switch (scale) {
            case CLOSEST -> {
                return 128;
            }
            case CLOSE -> {
                return 256;
            }
            case NORMAL -> {
                return 512;
            }
            case FAR -> {
                return 1024;
            }
            case FARTHEST -> {
                return 2048;
            }
        }
        return 128;
    }

    private byte fix(int value) {
        if (value > 127) {
            return 127;
        } else if (value < -128) {
            return -128;
        }
        return (byte) value;
    }
}
