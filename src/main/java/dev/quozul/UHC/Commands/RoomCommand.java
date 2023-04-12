package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import dev.quozul.UHC.Main;
import dev.quozul.UHC.SurvivalGame;
import dev.quozul.minigame.Party;
import dev.quozul.minigame.Room;
import dev.quozul.minigame.Session;
import dev.quozul.minigame.exceptions.PartyIncompatibleException;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

record PlayerParty(@NotNull Party party) {
}

@CommandAlias("room")
public class RoomCommand extends BaseCommand {
    private final Room room = new Room(new Session(new SurvivalGame(Main.plugin.getConfig().getInt("game-duration"), Main.plugin.getConfig().getInt("border-radius"))));

    public RoomCommand(PaperCommandManager manager) {
        manager.getCommandContexts().registerIssuerOnlyContext(PlayerParty.class, supplier -> {
            Party party = Party.getParty(supplier.getPlayer());
            if (party == null) {
                throw new ConditionFailedException("Tu n'es pas dans une équipe.");
            }
            return new PlayerParty(party);
        });

        manager.getCommandContexts().registerContext(Party.class, supplier -> {
            Party party = Party.getParty(supplier.getFirstArg());
            if (party == null) {
                throw new ConditionFailedException("Cette équipe n'existe pas.");
            }
            return party;
        });

        manager.getCommandCompletions().registerCompletion("parties", handler -> Party
                .getPublicParties()
                .stream()
                .map(Party::getName)
                .collect(Collectors.toSet())
        );
    }

    @CommandAlias("join")
    @Description("Rejoint une salle d'attente avec votre équipe actuelle.")
    void onJoin(Player player, PlayerParty party) {
        // TODO: Get room from argument
        if (party.party().getOwner() == player) {
            if (party.party().getRoom() != null) {
                throw new ConditionFailedException("Votre équipe est déjà dans une salle d'attente.");
            }

            try {
                room.addParty(party.party());
            } catch (PartyIncompatibleException e) {
                switch (e.getReason()) {
                    case NOT_ENOUGH_PLAYERS_IN_TEAM -> {
                        throw new ConditionFailedException("Il n'y a pas assez de joueurs dans cette équipe pour rejoindre la salle d'attente.");
                    }
                    case TOO_MANY_PLAYERS_IN_TEAM -> {
                        throw new ConditionFailedException("Il y a trop de joueurs dans cette équipe pour rejoindre la salle d'attente.");
                    }
                    case TOO_MANY_TEAMS_IN_ROOM -> {
                        throw new ConditionFailedException("La salle d'attente est complète.");
                    }
                }
            } catch (RoomInGameException e) {
                throw new ConditionFailedException("Une partie est déjà en cours dans cette salle.");
            }

            party.party().sendMessage(Component.text("Salle d'attente rejointe.", NamedTextColor.GRAY));
        } else {
            throw new ConditionFailedException("Tu n'es pas le propriétaire de l'équipe.");
        }
    }

    @CommandAlias("leave")
    @Description("Quitte la salle d'attente actuelle.")
    void onLeave(PlayerParty party) {
        try {
            if (party.party().getRoom() != null) {
                room.removeParty(party.party());
            } else {
                throw new ConditionFailedException("Ton équipe n'est pas dans une salle d'attente.");
            }
        } catch (RoomInGameException e) {
            throw new ConditionFailedException("L'équipe est dans une partie en cours.");
        }

        party.party().sendMessage(Component.text("Salle d'attente quittée.", NamedTextColor.GRAY));
    }
}
