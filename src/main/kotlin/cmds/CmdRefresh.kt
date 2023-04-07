package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.entity.Player

class CmdRefresh(private val boardManager: BoardManager): CommandNode() {
    override val name = "refresh"

    override fun call(caller: Player, args: Array<String>): String? {
        val board = this.boardManager.getBoard(caller) ?: return "You don't have a board!"

        board.updateObstacleMap()
        caller.notice("Refreshed obstacle map")

        return null
    }

}