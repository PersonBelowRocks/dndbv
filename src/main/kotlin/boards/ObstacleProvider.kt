package io.github.personbelowrocks.minecraft.dndbv.boards

import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D

abstract class ObstacleProvider {
    abstract fun getObstacle(pos: IntVec3D): ObstacleType
}