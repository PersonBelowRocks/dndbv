package io.github.personbelowrocks.minecraft.dndbv.shapes

import io.github.personbelowrocks.minecraft.dndbv.boards.Board
import io.github.personbelowrocks.minecraft.dndbv.boards.ObstacleType
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// radius + obstacles
class BoardCircle(val player: Player, targetPos: IntVec3D, radius: Double, board: Board): Shape {

    override val position = board.min
    override val world = board.world

    override val offsets = run {
        val c = 2 * PI * radius
        val inc = (PI * 0.5) / c

        val points = mutableListOf<Vec3D>()
        var angle = 0.0  // in radians
        while (angle <= 2* PI) {
            angle += inc

            val vec = (Vec3D(sin(angle), 0.0, cos(angle)) * radius) // + Vec3D(0.0, event.board.max.y.toDouble(), 0.0)
            // val vec = lateral + Vec3D(0.0, y, 0.0)
            points.add(vec)
        }

        val vertices = mutableListOf<ShapeVertex>()

        // val lateral = (IntVec3D.fromBukkitVec(event.block.location.toVector()) - event.board.min).toVec3D()
        points.forEach { rawPos ->
            val pos = rawPos + (targetPos - board.min).toVec3D()

            var y = board.max.y.toDouble()
            while (y > board.min.y.toDouble()) {

                val horizontalVec = Vec3D(pos.x, (y - board.min.y), pos.z) + Vec3D(0.5, 0.0, 0.5)

                val typeOver = board.obstacleMap.getAt(
                    horizontalVec.toIntVec() + IntVec3D(0, 1, 0)
                )?.type
                val typeAt = board.obstacleMap.getAt(
                    horizontalVec.toIntVec() + IntVec3D(0, 0, 0)
                )?.type
                val typeBelow = board.obstacleMap.getAt(
                    horizontalVec.toIntVec() + IntVec3D(0, -1, 0)
                )?.type

                if (typeAt == ObstacleType.FLACCID && typeBelow != ObstacleType.FLACCID) {
                    val base = Vec3D(0.0, (y - board.min.y.toDouble()), 0.0)
                    val vertex = ShapeVertex(Color.RED, base + Vec3D(pos.x, 0.0, pos.z))
                    vertices.add(vertex)
                }
                if (typeAt == ObstacleType.FLACCID && (typeOver != ObstacleType.FLACCID && typeOver != null)) {
                    val base = Vec3D(0.0, (y - board.min.y.toDouble()), 0.0)
                    val vertex = ShapeVertex(Color.RED, base + Vec3D(pos.x, 0.95, pos.z))
                    vertices.add(vertex)
                }

                y--

            }
        }

        return@run vertices.toTypedArray()
    }

    private var drawFor = 100

    override fun particle(color: Color, loc: Location) {
        world.spawnParticle(Particle.REDSTONE, loc, 1, Particle.DustOptions(color, 1.0F))
    }

    override fun drawAgain() = this.drawFor-- > 0
}