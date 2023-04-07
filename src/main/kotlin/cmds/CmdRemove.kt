package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player

class CmdRemove(private val boardManager: BoardManager): CommandNode() {
    override val name = "remove"

    override fun call(caller: Player, args: Array<String>): String? {
        val board = boardManager.getBoard(caller) ?: return "You don't have a board!"
        val figureName = args.getOrNull(1) ?: return "You need to provide a figure name!"

        val figure = board.removeFigure(figureName) ?: return "No figure found with the name '$figureName'"
        board.stopHighlighting()

        caller.notice("Unlinked figure with name '${figure.name}' belonging to '${getServer().getPlayer(figure.owner)?.name}' at position ${figure.absPosition}")

        return null
    }

}