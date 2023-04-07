package io.github.personbelowrocks.minecraft.dndbv.events.custom

import io.github.personbelowrocks.minecraft.dndbv.boards.Board
import io.github.personbelowrocks.minecraft.dndbv.boards.Figure
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerHitBoardFigure(val figure: Figure, val board: Board, val player: Player): Event() {
    companion object {
        val HANDLERS = HandlerList()
        fun getHandlerList() = HANDLERS
    }
    override fun getHandlers(): HandlerList = HANDLERS
}