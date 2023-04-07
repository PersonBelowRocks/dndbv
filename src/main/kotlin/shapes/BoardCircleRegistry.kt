package io.github.personbelowrocks.minecraft.dndbv.shapes

import org.bukkit.entity.Player
import java.util.*

const val DEFAULT_RADIUS = 10

class BoardCircleRegistry {
    private val radiusMap = mutableMapOf<UUID, Int>()

    fun setRadius(player: Player, radius: Int) {
        this.radiusMap[player.uniqueId] = radius
    }

    fun getRadius(player: Player): Int {
        return this.radiusMap[player.uniqueId] ?: DEFAULT_RADIUS
    }
}