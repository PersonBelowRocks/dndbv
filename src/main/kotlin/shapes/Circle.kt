package io.github.personbelowrocks.minecraft.dndbv.shapes

import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Circle(val color: Color, world: World, pos: IntVec3D, radius: Double, y: Double): Shape {
    override val world = world
    override val position = pos

    override val offsets: Array<ShapeVertex> = run {
        val c = 2 * PI * radius
        val inc = (PI*0.25) / c

        val points = mutableListOf<ShapeVertex>()
        var angle = 0.0  // in radians
        while (angle <= 2*PI) {
            angle += inc

            val vec = (Vec3D(sin(angle), 0.0, cos(angle)) * radius) + Vec3D(0.0, y, 0.0)
            // val vec = lateral + Vec3D(0.0, y, 0.0)
            points.add(ShapeVertex(this.color, vec))
        }

        return@run points.toTypedArray()
    }


    override fun particle(color: Color, loc: Location) {
        this.world.spawnParticle(Particle.REDSTONE, loc, 1, Particle.DustOptions(color, 1.0F))
    }

    override fun drawAgain() = true
}