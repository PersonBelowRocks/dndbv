package io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit

import JPlayerHitBoardFigure
import io.github.personbelowrocks.minecraft.dndbv.Items
import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

fun ArmorStand.helmetDisplayName() = (this.equipment.helmet?.itemMeta?.displayName() as? TextComponent)?.content()

class BoardFigureHit(private val boardManager: BoardManager): Listener {
    @EventHandler
    fun onEntityHit(event: EntityDamageByEntityEvent) {
        if (event.entity !is ArmorStand) return
        val player = event.damager as? Player ?: return

        if (player.inventory.itemInMainHand.type != Material.STICK) return

        val pos = IntVec3D.fromBukkitVec(event.entity.location.toVector())
        val board = boardManager.getBoard(pos) ?: return

        val displayName = (event.entity as ArmorStand).helmetDisplayName() ?: return

        if (board.hasFigure(displayName)) {
            event.isCancelled = true
        } else {
            return
        }

        val figure = board.getFigure(displayName)!!

        val boardEvent = JPlayerHitBoardFigure(figure, board, player)
        Bukkit.getPluginManager().callEvent(boardEvent)
//
//        if (player.uniqueId == figure.owner) {
//            if (board.isTurning()) {
//                if (board.activeTurn == player.uniqueId) {
//                    if (board.isHighlighting()) {
//                        board.stopHighlighting()
//                    } else {
//                        board.highlightFromFigure(figure)
//                    }
//                } else {
//                    figure.pointingArmed = !figure.pointingArmed
//                }
//            }
//
//        } else {
//            player.error("That's not your figure.")
//        }
    }
}
