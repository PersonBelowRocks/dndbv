package io.github.personbelowrocks.minecraft.dndbv.util

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

open class Vec3D(val x: Double, val y: Double, val z: Double) {

    operator fun plus(rhs: Vec3D) = Vec3D(this.x + rhs.x, this.y + rhs.y, this.z + rhs.z)
    operator fun minus(rhs: Vec3D) = Vec3D(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z)
    operator fun times(rhs: Double) = Vec3D(this.x * rhs, this.y * rhs, this.z * rhs)

    fun normalize(): Vec3D {
        val magnitude = this.magnitude()

        val newX = this.x/magnitude
        val newY = this.y/magnitude
        val newZ = this.z/magnitude

        return Vec3D(
            if (newX.isFinite()) newX else 0.0,
            if (newY.isFinite()) newY else 0.0,
            if (newZ.isFinite()) newZ else 0.0,
        )
    }

    fun magnitude(): Double = sqrt(this.x.pow(2) + this.y.pow(2) + this.z.pow(2))

    override fun toString(): String {
        return "Vec3D($x, $y, $z)"
    }

    fun toLocation(world: World) = Location(world, this.x, this.y, this.z)
    fun toIntVec() = IntVec3D(floor(this.x).toInt(), floor(this.y).toInt(), floor(this.z).toInt())
    fun toBukkitVec() = Vector(this.x, this.y, this.z)
    fun fromBukkitVec(bukkitVec: Vector) = Vec3D(bukkitVec.x, bukkitVec.y, bukkitVec.z)
}