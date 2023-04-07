package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.Items
import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.boards.InvalidFigureNumberError
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.Bukkit.getServer
import org.bukkit.Color
import org.bukkit.entity.Player
import java.lang.Enum.valueOf


val COLOR_MAP = mapOf(
    "red" to Color.fromRGB(0xf23427),
    "yellow" to Color.fromRGB(0xf2f227),
    "lime" to Color.fromRGB(0x3ede21),
    "blue" to Color.fromRGB(0x2137de),
    "pink" to Color.fromRGB(0xde21d8),
    "FUCHSIA" to Color.FUCHSIA,
    "orange" to Color.fromRGB(0xde7321),
    "black" to Color.fromRGB(0x000000),
    "white" to Color.fromRGB(0xffffff),

    "semen" to Color.fromRGB(0xcccabe),
    "diarrhea" to Color.fromRGB(0x594028),
    "dehydrated_piss" to Color.fromRGB(0x95992c)
)


class CmdAdd(private val boardManager: BoardManager): CommandNode() {
    override val name = "add"

    override fun call(caller: Player, args: Array<String>): String? {
        val board = boardManager.getBoard(caller) ?: return "You don't have a board!"
        val figureName = args.getOrNull(1) ?: return "You need to provide a name for the figure!"
        val ownerName = args.getOrNull(2) ?: return "You need to provide a name for the figure owner!"

        val circleRadiusStr = args.getOrNull(3)
        val circleColorName = args.getOrNull(4) ?: "red"

        // Get circle radius, must be integer above 1 if provided and if not provided the figure has no circle
        val circleRadius = if (circleRadiusStr != null) circleRadiusStr.toIntOrNull() ?: return "Invalid circle radius"
        else null
        if (circleRadius != null && circleRadius < 1) return "Circle radius must be more than 1"

        if (circleColorName !in COLOR_MAP.keys) return "Invalid color name"
        val circleColor = COLOR_MAP[circleColorName]!!

        // Get this figure's specified owner
        val owner = getServer().getPlayer(ownerName) ?: return "Couldn't find a player with the name '$ownerName'"
        if (board.hasFigure(figureName)) {
            return "Figure already exists by that name!"
        }

        val figure = try {
            board.newFigure(owner, figureName, circleColor, circleRadius)
        } catch (e: InvalidFigureNumberError) {
            return "You need to place exactly 1 figure with the name '$figureName'. (name the figure by naming the helmet piece)"
        }

        caller.inventory.addItem(Items.movementWand)

        caller.notice("Linked figure '$figureName' to player '$ownerName'. Figure at position ${figure!!.absPosition}")

        return null
    }

    override fun autocomplete(caller: Player, args: Array<String>): MutableList<String> {
        return if (args.size == 5) COLOR_MAP.keys.toMutableList()
        else mutableListOf()
    }
}