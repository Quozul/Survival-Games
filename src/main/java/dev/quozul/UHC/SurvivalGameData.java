package dev.quozul.UHC;

import dev.quozul.minigame.GameData;
import org.jetbrains.annotations.NotNull;

enum SurvivalGameStatus {
    SPAWNING,
    ALIVE,
    DEAD,
}

public class SurvivalGameData implements GameData {
    private @NotNull SurvivalGameStatus status;

    public SurvivalGameData() {
        status = SurvivalGameStatus.SPAWNING;
    }

    public boolean isSpawning() {
        return status == SurvivalGameStatus.SPAWNING;
    }

    public boolean isAlive() {
        return status == SurvivalGameStatus.ALIVE || status == SurvivalGameStatus.SPAWNING;
    }

    public boolean isDead() {
        return status == SurvivalGameStatus.DEAD;
    }

    public void setAlive() {
        status = SurvivalGameStatus.ALIVE;
    }

    public void setDead() {
        status = SurvivalGameStatus.DEAD;
    }
}
