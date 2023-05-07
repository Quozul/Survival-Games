package dev.quozul.minigame

import fr.pickaria.menu.Result
import fr.pickaria.menu.menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

fun roomMenu() = menu("room") {
	title = Component.text("Salles d'attente", NamedTextColor.GOLD, TextDecoration.BOLD)
	rows = 6

	var x = 0
	val openersRoom = Room.getRoom(opener)

	Room.getOpenRooms().forEach { room ->
		item {
			slot = x++
			title = room.session.game.displayName()
			material = Material.ACACIA_BOAT
			lore {
				keyValues {
					"Joueurs" to room.playerCount
					"Statut" to when (room.session.status) {
						SessionStatus.WAITING -> "En attente"
						SessionStatus.IN_GAME -> "En jeu"
					}
				}
				if (openersRoom == null) {
					leftClick = "Clic-gauche pour rejoindre"
				} else if (openersRoom == room) {
					rightClick = "Clic-droit pour quitter"
				}
			}
			leftClick = Result.CLOSE to "/room join ${room.identifier}"
			rightClick = Result.CLOSE to "/room leave ${room.identifier}"
		}
	}
}
