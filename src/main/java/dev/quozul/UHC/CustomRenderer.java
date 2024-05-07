package dev.quozul.UHC;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

record MapCoords(byte x, byte z, byte rotation) {
}

public class CustomRenderer extends MapRenderer {
    private final SurvivalGame game;

    public CustomRenderer(SurvivalGame game) {
        this.game = game;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        // Cursors
        MapCursorCollection cursorCollection = new MapCursorCollection();

        // Player cursor
        cursorCollection.addCursor(makeCursor(player.getLocation(), map, MapCursor.Type.PLAYER, true)); // White pointer?

        // Chest cursors
        for (Location location : game.getChests()) {
            cursorCollection.addCursor(makeCursor(location, map, MapCursor.Type.TARGET_X, true)); // White cross?
        }

        // World border
        WorldBorder worldBorder = game.getWorld().getWorldBorder();
        cursorCollection.addCursor(makeCursor(worldBorder.getCenter(), map, MapCursor.Type.RED_X, true));

        int borderSize = blockSize((int) worldBorder.getSize() / 2, map);
        int centerX = 64;
        int centerZ = 64;

        for (int j = -borderSize; j <= borderSize; j++) {
            canvas.setPixelColor(centerX + j, centerZ - borderSize, Color.RED);
            canvas.setPixelColor(centerX + j, centerZ + borderSize, Color.RED);
            canvas.setPixelColor(centerX - borderSize, centerZ + j, Color.RED);
            canvas.setPixelColor(centerX + borderSize, centerZ + j, Color.RED);
        }

        canvas.setCursors(cursorCollection);
    }

    private int blockSize(int blockSize, MapView mapView) {
        int mapScale = mapView.getScale().getValue();
        return blockSize / (1 << mapScale);
    }

    private MapCursor makeCursor(Location location, MapView mapView, @NotNull MapCursor.Type type, boolean visible) {
        MapCoords mapLocation = getMapCursorLocation(location, mapView);
        return new MapCursor(mapLocation.x(), mapLocation.z(), mapLocation.rotation(), type, visible);
    }

    private MapCoords getMapCursorLocation(Location location, MapView mapView) {
        int scale = getScaleInBlocks(mapView.getScale()) / 2;

        int dir = (int) Math.floor((location.getYaw() / 360 * 16) + 0.5) & 0xF;
        int x = (int) ((location.getX() - mapView.getCenterX()) / scale * 128);
        int z = (int) ((location.getZ() - mapView.getCenterZ()) / scale * 128);

        return new MapCoords(fix(x), fix(z), (byte) dir);
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
