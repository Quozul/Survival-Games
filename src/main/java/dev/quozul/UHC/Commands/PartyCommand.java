package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.quozul.minigame.Party;
import dev.quozul.minigame.exceptions.PartyIsPrivate;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("party")
public class PartyCommand extends BaseCommand {
    @Default
    void onDefault(Player player, PlayerParty party) {
        Component members = party.party()
                .getMembers()
                .stream()
                .map(Player::displayName)
                .reduce(Component.text("Membres de l'équipe:", NamedTextColor.GRAY), (acc, cur) -> acc.appendSpace().append(cur));
        player.sendMessage(members);
    }

    @CommandCompletion("@parties")
    @Subcommand("join")
    void onJoin(Player player, PlayerParty currentParty, Party party) {
        if (currentParty != null) {
            throw new ConditionFailedException("Tu es déjà dans une autre équipe, quitte-la avant de changer.");
        }

        try {
            party.join(player);
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

    @CommandCompletion("@parties")
    @Subcommand("leave")
    @Description("Quitte son équipe.")
    void onLeave(Player player, PlayerParty party) {
        try {
            party.party().leave(player);
        } catch (RoomInGameException e) {
            throw new ConditionFailedException("Tu ne peux pas quitter l'équipe tant qu'une partie est en cours.");
        }

        Component component = Component.text("Tu as quitté l'équipe", NamedTextColor.GRAY)
                .appendSpace()
                .append(party.party().displayName().color(NamedTextColor.GOLD));
        player.sendMessage(component);
    }

    @Subcommand("invite")
    @Description("Invite un joueur dans votre équipe.")
    void onInvite(Player player, PlayerParty party, OnlinePlayer otherPlayer) {
        if (party.party().getOwner() != player) {
            throw new ConditionFailedException("Tu ne peux pas inviter un joueur si tu n'es pas le propriétaire de l'équipe.");
        }

        try {
            party.party().invitePlayer(otherPlayer.getPlayer());
        } catch (RoomInGameException e) {
            throw new ConditionFailedException("Tu ne peux pas inviter un joueur tant qu'une partie est en cours.");
        }

        otherPlayer.getPlayer().sendMessage(
                Component.text("Tu as été invité à rejoindre l'équipe", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(party.party().displayName().color(NamedTextColor.GOLD))
                        .append(Component.text(".", NamedTextColor.GRAY))
                        .appendNewline()
                        .append(
                                Component.text("Clique sur ce message pour accepter.", NamedTextColor.GOLD)
                                        .clickEvent(ClickEvent.runCommand(String.format("/party join %s", party.party().getName())))
                        )
        );

        player.sendMessage(
                otherPlayer.getPlayer().displayName().color(NamedTextColor.GOLD)
                        .appendSpace()
                        .append(Component.text("à été invité à rejoindre ton équipe.", NamedTextColor.GRAY))
        );
    }

    @Subcommand("new")
    @Description("Créé une nouvelle équipe")
    void onNew(Player player, @Default("false") Boolean isPublic) {
        Party party = Party.getParty(player);
        if (party != null) {
            throw new ConditionFailedException("Tu es déjà dans une équipe, quitte ton équipe avant d'en créer une nouvelle.");
        }

        Party createdParty = new Party(player, isPublic);

        Component component = Component.text("L'équipe", NamedTextColor.GRAY)
                .appendSpace()
                .append(createdParty.displayName().color(NamedTextColor.GOLD))
                .appendSpace()
                .append(Component.text("a été créée.", NamedTextColor.GRAY));

        player.sendMessage(component);
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
    void onPublic(Player player, PlayerParty party, @Optional Boolean isPublic) {
        if (isPublic == null) {
            Component component = Component.text("L'état de l'équipe est actuellement", NamedTextColor.GRAY)
                    .appendSpace()
                    .append(getPublicComponent(party.party().isPublic()));

            player.sendMessage(component);
        } else {
            if (party.party().getOwner() != player) {
                throw new ConditionFailedException("Tu ne peux pas inviter un joueur si tu n'es pas le propriétaire de l'équipe.");
            }

            party.party().setPublic(isPublic);

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
