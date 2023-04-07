package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.shapes.BoardCircleRegistry
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.entity.Player

class CmdCirc(val circRegistry: BoardCircleRegistry): CommandNode() {
    override val name = "circ"

    override fun call(caller: Player, args: Array<String>): String? {
        val radiusString = args.getOrNull(1) ?: return "You must provide a radius!"
        val radius = radiusString.toIntOrNull() ?: return "Invalid number!"

        if (radius < 1) return "Radius must be greater than 1!"

        this.circRegistry.setRadius(caller, radius)
        caller.notice("Set circle radius to $radius")

        return null
    }
}