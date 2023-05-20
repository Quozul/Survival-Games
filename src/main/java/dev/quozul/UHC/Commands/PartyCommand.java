package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.quozul.minigame.Party;
import dev.quozul.minigame.PlayerData;
import dev.quozul.minigame.exceptions.PartyIsPrivate;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("party")
public class PartyCommand extends BaseCommand {
    public PartyCommand(PaperCommandManager manager) {
        manager.getCommandContexts().registerContext(Party.class, supplier -> {
            System.out.println(supplier.getFirstArg());
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

    @Default
    void onDefault(Player player) {
        Party party = PlayerData.from(player).getParty();

        player.sendMessage(Component.text("Nom de l'équipe : ").append(party.displayName()));
        player.sendMessage(Component.text("Propriétaire de l'équipe : ").append(party.getOwner().displayName()));
        Component members = party
                .getMembers()
                .stream()
                .map(Player::displayName)
                .reduce(Component.text("Membres de l'équipe :", NamedTextColor.GRAY), (acc, cur) -> acc.appendSpace().append(cur));
        player.sendMessage(members);
    }

    @Subcommand("list")
    void onList(Player player) {
        Component members = Party
                .getPublicParties()
                .stream()
                .map(party -> Component.text(party.getName()))
                .reduce(Component.text("Liste des équipes publiques :", NamedTextColor.GRAY), (acc, cur) -> (TextComponent) acc.appendSpace().append(cur));
        player.sendMessage(members);
    }

    @CommandCompletion("@parties")
    @Subcommand("join")
    void onJoin(Player player, Party party) {
        PlayerData playerData = PlayerData.from(player);

        try {
            playerData.setParty(party);
        } catch (RoomInGameException e) {
            throw new ConditionFailedException("Tu ne peux pas rejoindre une équipe qui est dans une partie.");
        } catch (PartyIsPrivate e) {
            throw new ConditionFailedException("Cette équipe est privée.");
        }

        Component component = Component.text("Tu as rejoint l'équipe", NamedTextColor.GRAY)
                .appendSpace()
                .append(party.displayName().color(NamedTextColor.GOLD));
        player.sendMessage(component);
    }

    @Subcommand("invite")
    @Description("Invite un joueur dans votre équipe.")
    void onInvite(Player player, OnlinePlayer otherPlayer) {
        Party party = PlayerData.from(player).getParty();

        if (party.getOwner() != player) {
            throw new ConditionFailedException("Tu ne peux pas inviter un joueur si tu n'es pas le propriétaire de l'équipe.");
        }

        try {
            party.invitePlayer(otherPlayer.getPlayer());
        } catch (RoomInGameException e) {
            throw new ConditionFailedException("Tu ne peux pas inviter un joueur tant qu'une partie est en cours.");
        }

        otherPlayer.getPlayer().sendMessage(
                Component.text("Tu as été invité à rejoindre l'équipe", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(party.displayName().color(NamedTextColor.GOLD))
                        .append(Component.text(".", NamedTextColor.GRAY))
                        .appendNewline()
                        .append(
                                Component.text("Clique sur ce message pour accepter.", NamedTextColor.GOLD)
                                        .clickEvent(ClickEvent.runCommand(String.format("/party join %s", party.getName())))
                        )
        );

        player.sendMessage(
                otherPlayer.getPlayer().displayName().color(NamedTextColor.GOLD)
                        .appendSpace()
                        .append(Component.text("à été invité à rejoindre ton équipe.", NamedTextColor.GRAY))
        );
    }

    private Component getPublicComponent(boolean isPublic) {
        if (isPublic) {
            return Component.text("public", NamedTextColor.GOLD);
        } else {
            return Component.text("privé", NamedTextColor.GOLD);
        }
    }

    @Subcommand("public")
    @Description("Change l'état public de l'équipe.")
    void onPublic(Player player, @Optional Boolean isPublic) {
        Party party = PlayerData.from(player).getParty();

        if (isPublic == null) {
            Component component = Component.text("L'état de l'équipe est actuellement", NamedTextColor.GRAY)
                    .appendSpace()
                    .append(getPublicComponent(party.isPublic()));

            player.sendMessage(component);
        } else {
            if (party.getOwner() != player) {
                throw new ConditionFailedException("Tu ne peux pas inviter un joueur si tu n'es pas le propriétaire de l'équipe.");
            }

            party.setPublic(isPublic);

            Component component = Component.text("L'état de l'équipe a été changé à", NamedTextColor.GRAY)
                    .appendSpace()
                    .append(getPublicComponent(isPublic));

            player.sendMessage(component);
        }
    }

    @HelpCommand
    void doHelp(CommandHelp help) {
        help.showHelp();
    }
}
