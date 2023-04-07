package io.github.personbelowrocks.minecraft.dndbv.shapes

import io.github.personbelowrocks.minecraft.dndbv.boards.Board
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle

class MovePath(board: Board, override val offsets: Array<ShapeVertex>): Shape {
    override val position = board.min
    override val world = board.world

    private var drawTimes = 8

    override fun particle(color: Color, loc: Location) {
        this.world.spawnParticle(Particle.REDSTONE, loc, 1, Particle.DustOptions(color, 2.0F))
    }

    override fun drawAgain() = this.drawTimes-- > 0
}