package dev.quozul.minigame.exceptions;

public class PartyIncompatibleException extends Throwable {
    private final PartyIncompatibleReason reason;

    public PartyIncompatibleException(PartyIncompatibleReason reason) {
        this.reason = reason;
    }

    public PartyIncompatibleReason getReason() {
        return reason;
    }
}
