package io.github.personbelowrocks.minecraft.dndbv.shapes

import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import org.bukkit.Color

class ShapeVertex(val color: Color, x: Double, y: Double, z: Double): Vec3D(x, y, z) {
    constructor(color: Color, vec: Vec3D) : this(color, vec.x, vec.y, vec.z)
}