package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.error
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

class CmdView(private val boardManager: BoardManager): CommandNode() {
    override val name = "view"

    override fun call(caller: Player, args: Array<String>): String? {
        val board = this.boardManager.getBoard(caller) ?: return "No board found!"

        caller.notice("$board")
        val figures = caller.world.getNearbyEntities(board.boundingBox()).filter {entity ->
            if (entity !is ArmorStand) {
                return@filter false
            }
            val displayComponent = entity.equipment.helmet?.itemMeta?.displayName() ?: return@filter false

            if (displayComponent !is TextComponent) {
                return@filter false
            }

            return@filter displayComponent.content() == "guy"
        }.map {entity ->
            entity as ArmorStand
        }

        figures.forEach { figure ->
            val displayComponent = figure.equipment.helmet?.itemMeta?.displayName() as TextComponent
            caller.notice("Found figure: ${figure.location}, head: ${displayComponent.content()}")
        }

        return null
    }
}