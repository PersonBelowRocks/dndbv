package io.github.personbelowrocks.minecraft.dndbv.events.custom

import io.github.personbelowrocks.minecraft.dndbv.boards.Board
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerRightClickBoardGround(val clickedFace: IntVec3D, val board: Board, val player: Player): Event() {
    companion object {
        val HANDLERS = HandlerList()
        fun getHandlerList() = HANDLERS
    }

    override fun getHandlers(): HandlerList = HANDLERS

    fun clickedFaceBoardPos(): IntVec3D = this.clickedFace - this.board.min
}