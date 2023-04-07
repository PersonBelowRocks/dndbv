package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player

class CmdTurn(private val boardManager: BoardManager): CommandNode() {
    override val name = "turn"

    override fun call(caller: Player, args: Array<String>): String? {
        val board = boardManager.getBoard(caller) ?: return "You don't have a board!"
        val targetName = args.getOrNull(1) ?: return "You need to specify a target player!"

        if (targetName == "null") {
            board.activeTurn = null
            board.stopHighlighting()
            board.fuel = 0.0
            caller.notice("Entered null turn, you can now mutate the board")
            return null
        }

        val target = getServer().getPlayer(targetName) ?: return "No player found with name '$targetName'"
        if (!board.hasPlayer(target)) return "Player ${target.name} is not part of the board!"

        val fuelString = args.getOrNull(2) ?: return "You need to specify fuel amount!"
        val fuel = fuelString.toIntOrNull() ?: return "That's not a valid fuel number! Fuel must be a positive integer"

        if (fuel < 0) return "Fuel must be a positive integer"

        board.stopHighlighting()

        board.activeTurn = target.uniqueId
        board.fuel = fuel.toDouble()

        caller.notice("$targetName is now making their turn.")

        return null
    }
}
