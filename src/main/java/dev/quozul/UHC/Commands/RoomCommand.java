package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.quozul.UHC.Listeners.GameStart;
import dev.quozul.UHC.Listeners.SpawnChest;
import dev.quozul.UHC.Main;
import dev.quozul.UHC.SurvivalGame;
import dev.quozul.minigame.Party;
import dev.quozul.minigame.Room;
import dev.quozul.minigame.exceptions.PartyIncompatibleException;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

record PlayerParty(@NotNull Party party) {
}

record OptionalPlayerParty(Party party) {
}

@CommandAlias("room")
public class RoomCommand extends BaseCommand {
    public RoomCommand(PaperCommandManager manager) {
        manager.getCommandContexts().registerContext(Room.class, supplier -> {
            Room room = Room.getRoom(supplier.getFirstArg());
            if (room == null) {
                throw new ConditionFailedException("Cette salle n'existe pas.");
            }
            return room;
        });

        manager.getCommandCompletions().registerCompletion("rooms", handler -> Room.getOpenRooms().stream().map(Room::getIdentifier).collect(Collectors.toSet()));

        Room.fromGame(new SurvivalGame(Main.plugin.getConfig().getInt("game-duration"), Main.plugin.getConfig().getInt("border-radius")));
        Room.fromGame(new SurvivalGame(Main.plugin.getConfig().getInt("game-duration"), Main.plugin.getConfig().getInt("border-radius")));
        Room.fromGame(new SurvivalGame(Main.plugin.getConfig().getInt("game-duration"), Main.plugin.getConfig().getInt("border-radius")));
    }

    @Default
    void onDefault(Player player) {
        Component message = Component.text("Liste des salles d'attente :", NamedTextColor.GRAY);
        List<Component> components = Room.getOpenRooms().stream().map(room -> {
            int current = room.getPlayerCount();

            return Component.text(room.getIdentifier(), NamedTextColor.GOLD)
                    .append(Component.text(" : ", NamedTextColor.GRAY))
                    .append(Component.text(current, NamedTextColor.GRAY))
                    .append(Component.text(" joueurs", NamedTextColor.GRAY))
                    .hoverEvent(HoverEvent.showText(Component.text("Clique pour rejoindre")))
                    .clickEvent(ClickEvent.runCommand(String.format("/room join %s", room.getIdentifier())));
        }).collect(Collectors.toList());

        for (Component component : components) {
            message = message.appendNewline().append(component);
        }

        player.sendMessage(message);
    }

    @CommandAlias("join")
    @Description("Rejoint une salle d'attente avec votre équipe actuelle. Créé une équipe avec 1 joueur si vous n'en avez pas.")
    @CommandCompletion("@rooms")
    void onJoin(Player player, Room room, OptionalPlayerParty playerParty) {
        Party party;

        if (playerParty.party() == null) {
            party = new Party(player, false);
        } else {
            party = playerParty.party();
        }

        if (party.getOwner() == player) {
            if (party.getRoom() != null) {
                throw new ConditionFailedException("Votre équipe est déjà dans une salle d'attente.");
            }

            try {
                room.addParty(party);
            } catch (PartyIncompatibleException e) {
                switch (e.getReason()) {
                    case NOT_ENOUGH_PLAYERS_IN_TEAM ->
                            throw new ConditionFailedException("Il n'y a pas assez de joueurs dans cette équipe pour rejoindre la salle d'attente.");
                    case TOO_MANY_PLAYERS_IN_TEAM ->
                            throw new ConditionFailedException("Il y a trop de joueurs dans cette équipe pour rejoindre la salle d'attente.");
                    case TOO_MANY_TEAMS_IN_ROOM ->
                            throw new ConditionFailedException("La salle d'attente est complète.");
                }
            } catch (RoomInGameException e) {
                throw new ConditionFailedException("Une partie est déjà en cours dans cette salle.");
            }

            party.sendMessage(Component.text("Salle d'attente rejointe.", NamedTextColor.GRAY));
        } else {
            throw new ConditionFailedException("Tu n'es pas le propriétaire de l'équipe.");
        }
    }

    @CommandAlias("leave")
    @Description("Quitte la salle d'attente actuelle.")
    void onLeave(PlayerParty party) {
        try {
            Room room = party.party().getRoom();
            if (room != null) {
                room.removeParty(party.party());
            } else {
                throw new ConditionFailedException("Ton équipe n'est pas dans une salle d'attente.");
            }
        } catch (RoomInGameException e) {
            throw new ConditionFailedException("L'équipe est dans une partie en cours.");
        }

        party.party().sendMessage(Component.text("Salle d'attente quittée.", NamedTextColor.GRAY));
    }

    @CommandAlias("test")
    @Description("Ouvre le menu de sélection de salle d'attente")
    void onTest(Player player) {
        SurvivalGame game = new SurvivalGame(100000, 1024);
        game.setWorld(player.getWorld());
        player.getInventory().addItem(GameStart.getMap(player.getWorld(), game, GameStart.getMapScale(game)));

        SpawnChest.spawnChest(game, player.getLocation());
    }

    @CommandAlias("prepare")
    @Description("Force le démarrage d'une session")
    void onPrepare(PlayerParty party) {
        Room room = party.party().getRoom();
        if (room == null) {
            throw new ConditionFailedException("Ton équipe n'est pas dans une salle d'attente.");
        }
        room.getSession().prepare(room);
    }

    @CommandAlias("unprepare")
    @Description("Force le démarrage d'une session")
    void onUnprepare(PlayerParty party) {
        Room room = party.party().getRoom();
        if (room == null) {
            throw new ConditionFailedException("Ton équipe n'est pas dans une salle d'attente.");
        }
        room.getSession().unprepare(room);
    }
}
