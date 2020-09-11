package dev.quozul.UHC.Events;

import dev.quozul.UHC.SurvivalGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SurvivalGameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private SurvivalGame game;

    public SurvivalGameStartEvent(SurvivalGame g) {
        game = g;
    }

    public SurvivalGame getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
