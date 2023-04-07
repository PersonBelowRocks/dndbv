package io.github.personbelowrocks.minecraft.dndbv.shapes

import io.github.personbelowrocks.minecraft.dndbv.boards.Board
import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle

class BoardHighlight(board: Board, override val offsets: Array<ShapeVertex>): Shape {
    override val position = board.min
    override val world = board.world

    override fun particle(color: Color, loc: Location) {
        this.world.spawnParticle(Particle.REDSTONE, loc, 1, Particle.DustOptions(color, 1.0F))
    }

    override fun drawAgain() = true
}