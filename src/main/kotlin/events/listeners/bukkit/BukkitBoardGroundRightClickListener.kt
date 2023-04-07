package io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit

import JPlayerRightClickBoardGround
import io.github.personbelowrocks.minecraft.dndbv.Items
import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class BukkitBoardGroundRightClickListener(val boardManager: BoardManager): Listener {
    @EventHandler
    fun onBlockFaceRightClicked(event: PlayerInteractEvent) {
        if (event.player.inventory.itemInMainHand.type != Material.STICK) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val face = event.blockFace
        val block = event.clickedBlock!!

        // val modVec = IntVec3D(face.modX, face.modY, face.modZ)
        val facePos = IntVec3D.fromBukkitVec(block.getRelative(face).location.toVector())

        val board = boardManager.getBoard(facePos) ?: return
        // if (!board.isTurning()) return

        val boardEvent = JPlayerRightClickBoardGround(facePos, board, event.player)
        Bukkit.getPluginManager().callEvent(boardEvent)
        // val player = event.player

//        if (board.activeTurn != player.uniqueId) {
//            val figures = board.getFigures(player)?.filter { figure ->
//                figure.pointingArmed
//            }
//
//            figures?.forEach { figure ->
//                val direction = facePos.toVec3D() - figure.position.toVec3D()
//                figure.setFacing(direction)
//                figure.pointingArmed = false
//            }
//            return
//        }
//
//        if (!board.isHighlighting()) return
//
//        if (!board.isHighlighted(facePos)) return
//
//        val figure = board.highlightedFigure!!
//        val report = figure.moveTo(facePos - board.min) ?: run {
//            player.error("You can't move there!")
//            return
//        }
//
//        board.fuel = report.remainingFuel
//        board.stopHighlighting()
//
//        BoardParticlePainter.beginDrawing(MovePath(board, report.pathfindingVertices))
    }
}