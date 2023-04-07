package io.github.personbelowrocks.minecraft.dndbv.util

import com.sk89q.worldedit.math.BlockVector3
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import kotlin.math.*

fun BoundingBox.contains(pos: IntVec3D) = this.contains(pos.toVec3D().toBukkitVec())

class IntVec3D(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun fromBukkitVec(vec: Vector) = IntVec3D(floor(vec.x).toInt(), floor(vec.y).toInt(), floor(vec.z).toInt())
        fun fromBlockVec(vec: BlockVector3) = IntVec3D(vec.x, vec.y, vec.z)
    }

    operator fun plus(rhs: IntVec3D) = IntVec3D(this.x + rhs.x, this.y + rhs.y, + this.z + rhs.z)
    operator fun minus(rhs: IntVec3D) = IntVec3D(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z)

    fun magnitude(): Double = sqrt(this.x.toDouble().pow(2) + this.y.toDouble().pow(2) + this.z.toDouble().pow(2))

    override fun toString(): String {
        return "IntVec3D(${this.x}, ${this.y}, ${this.z})"
    }

    fun toLocation(world: World) = Location(world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
    fun toVec3D() = Vec3D(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}