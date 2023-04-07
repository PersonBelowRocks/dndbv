package io.github.personbelowrocks.minecraft.dndbv.shapes

import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World

interface Shape {
    val position: IntVec3D
    val world: World
    val offsets: Array<ShapeVertex>

    fun particle(color: Color, loc: Location)
    fun drawAgain(): Boolean
}