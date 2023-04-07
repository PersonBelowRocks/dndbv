package io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit

import JPlayerRightClickBoardFigure
import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.error
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerArmorStandManipulateEvent

class BukkitBoardFigureRightClickListener(private val boardManager: BoardManager): Listener {
    @EventHandler
    fun onEntityRightClick(event: PlayerArmorStandManipulateEvent) {
        val pos = IntVec3D.fromBukkitVec(event.rightClicked.location.toVector())
        val board = boardManager.getBoard(pos) ?: return

        val displayName = event.rightClicked.helmetDisplayName() ?: return

        if (board.hasFigure(displayName)) {
            event.isCancelled = true
        } else {
            return
        }

        val figure = board.getFigure(displayName)!!

        val boardEvent = JPlayerRightClickBoardFigure(figure, board, event.player)
        Bukkit.getPluginManager().callEvent(boardEvent)

//        if (event.player.uniqueId == figure.owner) {
//            if (figure.isCircled()) {
//                figure.clearCircle()
//            } else {
//                figure.drawCircle()
//            }
//        } else {
//            event.player.error("That's not your figure.")
//        }
    }
}