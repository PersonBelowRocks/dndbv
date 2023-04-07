package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.error
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.entity.Player

class CmdClear(private val boardManager: BoardManager): CommandNode() {
    override val name = "clear"

    override fun call(caller: Player, args: Array<String>): String? {
        val board = boardManager.getBoard(caller)
        if (board == null) {
            caller.error("You don't have a board!")
        } else {
            boardManager.removeBoard(caller)
            caller.notice("Removed board $board")
        }

        return null
    }
}