package io.github.personbelowrocks.minecraft.dndbv.events.listeners

import JPlayerRightClickBoardFigure
import io.github.personbelowrocks.minecraft.dndbv.util.error
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BoardFigureRightClickedListener: Listener {
    @EventHandler
    fun onFigureRightClicked(event: JPlayerRightClickBoardFigure) {
        if (event.player.uniqueId == event.figure.owner) {
            if (event.figure.isCircled()) {
                event.figure.clearCircle()
            } else {
                event.figure.drawCircle()
            }
        } else {
            event.player.error("That's not your figure.")
        }
    }
}