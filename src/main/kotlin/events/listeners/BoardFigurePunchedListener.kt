package io.github.personbelowrocks.minecraft.dndbv.events.listeners

import JPlayerHitBoardFigure
import io.github.personbelowrocks.minecraft.dndbv.util.error
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BoardFigurePunchedListener: Listener {
    @EventHandler
    fun onFigurePunched(event: JPlayerHitBoardFigure) {
        if (event.player.uniqueId != event.figure.owner) {
            event.player.error("That's not your figure")
            return
        }
        if (event.board.isTurning()) {
            if (event.board.activeTurn == event.player.uniqueId && event.board.fuel >= 1.0) {
                if (event.board.isHighlighting()) {
                    event.board.stopHighlighting()
                } else {
                    event.board.highlightFromFigure(event.figure)
                }
            } else {
                event.figure.pointingArmed = !event.figure.pointingArmed
            }
        }
    }
}