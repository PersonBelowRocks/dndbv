package io.github.personbelowrocks.minecraft.dndbv.shapes

import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.ArrayList

object ParticleFXPainter : BukkitRunnable() {
    val shapes: MutableMap<UUID, Shape> = HashMap()

    override fun run() {
        val toRemove = ArrayList<UUID>()

        for ((id, shape) in shapes.entries) {
            // Spawn particle for each vertex (aka. draw the shape)
            for (vertex in shape.offsets) {
                // Centers the particle in a block
                val loc = shape.position.toVec3D() + vertex + Vec3D(0.5, 0.0, 0.5)
                shape.particle(vertex.color, loc.toLocation(shape.world))
            }
            // Ask the shape if it wants to be drawn again, remove if not
            if (!shape.drawAgain()) toRemove.add(id)
        }

        toRemove.forEach {
            this.stopDrawing(it)
        }
    }

    fun beginDrawing(shape: Shape): UUID {
        val id = UUID.randomUUID()
        this.shapes[id] = shape

        return id
    }

    fun stopDrawing(id: UUID): Shape? = this.shapes.remove(id)
}