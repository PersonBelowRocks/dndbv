package io.github.personbelowrocks.minecraft.dndbv.events.listeners

import JPlayerRightClickBoardGround
import io.github.personbelowrocks.minecraft.dndbv.shapes.ParticleFXPainter
import io.github.personbelowrocks.minecraft.dndbv.shapes.MovePath
import io.github.personbelowrocks.minecraft.dndbv.util.error
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BoardGroundRightClickedListener: Listener {
    @EventHandler
    fun onGroundRightClicked(event: JPlayerRightClickBoardGround) {
        if (event.board.fuel < 1.0) {
            val figures = event.board.getFigures(event.player)?.filter { figure ->
                figure.pointingArmed
            }

            figures?.forEach { figure ->
                val direction = event.clickedFace.toVec3D() - figure.absPosition.toVec3D()
                figure.setFacing(direction)
                figure.pointingArmed = false
            }
            return
        }

        if (!event.board.isHighlighting()) return
        if (!event.board.isHighlighted(event.clickedFace)) return

        if (event.board.getFigure(event.clickedFace) != null) {
            event.player.error("You can't move there!")
            return
        }

        if (event.board.activeTurn!! != event.player.uniqueId) {
            event.player.error("It's not your turn!")
            return
        }

        val figure = event.board.highlightedFigure!!

        val report = figure.moveTo(event.clickedFace - event.board.min)

        event.board.fuel = report.remainingFuel
        event.board.stopHighlighting()

        ParticleFXPainter.beginDrawing(MovePath(event.board, report.pathVertices))
    }
}