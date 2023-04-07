package io.github.personbelowrocks.minecraft.dndbv.boards

import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import org.bukkit.Material
import java.lang.IndexOutOfBoundsException
import java.util.concurrent.LinkedBlockingDeque

typealias ObstMap = Array<Array<Array<ObstacleSlot?>>>

enum class ObstacleType {
    FLACCID,
    FENCE,
    HAZARD,
    HARD
}

private val obstacles = mapOf(
    Material.ACACIA_FENCE to ObstacleType.FENCE,
    Material.SPRUCE_FENCE to ObstacleType.FENCE,
    Material.OAK_FENCE to ObstacleType.FENCE,
    Material.BIRCH_FENCE to ObstacleType.FENCE,
    Material.DARK_OAK_FENCE to ObstacleType.FENCE,
    Material.JUNGLE_FENCE to ObstacleType.FENCE,
    Material.WARPED_FENCE to ObstacleType.FENCE,
    Material.CRIMSON_FENCE to ObstacleType.FENCE,
    Material.NETHER_BRICK_FENCE to ObstacleType.FENCE,

    Material.AIR to ObstacleType.FLACCID,
    Material.GRASS to ObstacleType.FLACCID,
    Material.TALL_GRASS to ObstacleType.FLACCID,
    Material.POPPY to ObstacleType.FLACCID,
    Material.DANDELION to ObstacleType.FLACCID,
    Material.ORANGE_TULIP to ObstacleType.FLACCID,
    Material.LIGHT to ObstacleType.FLACCID,

    Material.WHEAT to ObstacleType.HARD,

    Material.WATER to ObstacleType.HAZARD,
    Material.LAVA to ObstacleType.HAZARD
)

private fun obstacleTypeFromMaterial(material: Material): ObstacleType {
    return if (material in obstacles.keys) {
        obstacles[material]!!
    } else {
        ObstacleType.HARD
    }
}

data class ObstacleSlot(val pos: IntVec3D, var type: ObstacleType = ObstacleType.HARD, var d: Double = 0.0)

class ObstacleMapIterator(map: ObstacleMap): Iterator<ObstacleSlot> {

    private val flattened = map.obstacleMap.flatten().toTypedArray().flatten()
    private var index = 0

    override fun hasNext() = index < flattened.size

    override fun next(): ObstacleSlot {
        val out = this.flattened[this.index]!!
        this.index += 1
        return out
    }
}

class ObstacleMap(val board: Board) {

    val obstacleMap: ObstMap = run {
        val max = IntVec3D.fromBukkitVec(board.boundingBox().max) - board.min
        val min = IntVec3D.fromBukkitVec(board.boundingBox().min) - board.min

        val out = arrayListOf<ArrayList<ArrayList<ObstacleSlot?>>>()

        for (x in min.x until max.x) {
            out.add(arrayListOf(ArrayList()))
            for (y in min.y until max.y) {
                out[x].add(ArrayList())
                for (z in min.z until max.z) {
                    out[x][y].add(null)

                    val worldPos = IntVec3D(x, y, z) + board.min
                    val material = this.board.world.getBlockAt(worldPos.x, worldPos.y, worldPos.z).type
                    out[x][y][z] = ObstacleSlot(pos = IntVec3D(x, y, z) + board.min, type = obstacleTypeFromMaterial(material))
                }
            }
        }
        val outFinal = out.map { outer ->
            outer.map { inner ->
                inner.toTypedArray()
            }.toTypedArray()
        }.toTypedArray()

        return@run outFinal
    }

    val maxX = this.obstacleMap.size
    val maxY = this.obstacleMap[0].size
    val maxZ = this.obstacleMap[0][0].size

    fun getPath(from: IntVec3D, to: IntVec3D): Array<IntVec3D> {
        val out = ArrayList<IntVec3D>()
        // from.d > to.d
        var curr = this.getAt(to) ?: run {println("tried to move to $to"); throw NullPointerException("tried to move to $to")}
        out.add(curr.pos - this.board.min)

        var count = 0
        while (count < MAX_PATHFINDING_STEPS && ((curr.pos - this.board.min) - from).magnitude() > 1.5) {
            val currPos = curr.pos - this.board.min

            count++

            val neighbors = this.neighbors(currPos).map {
                it + currPos
            }

            var largest = this.getAt(neighbors.find {
                this.getAt(it) != null
            }!!)!!

            neighbors.forEach {
                val neighbor = this.getAt(it) ?: return@forEach
                if (neighbor.d > largest.d) largest = neighbor
            }

            out.add(largest.pos - this.board.min)
            curr = largest
        }

        return out.toTypedArray()
    }

    private fun neighbors(pos: IntVec3D): Array<IntVec3D> {
        val out = arrayListOf<IntVec3D>()
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    out.add(IntVec3D(x, y, z))
                }
            }
        }
        return out.toTypedArray()
    }

    fun getAt(pos: IntVec3D): ObstacleSlot? {
        return try {
            this.obstacleMap[pos.x][pos.y][pos.z]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    fun highlightFrom(position: IntVec3D, fuel: Double) {
        val operationStack = LinkedBlockingDeque<IntVec3D>()
        var opCount = 0

        val pos = position - this.board.min

        val startingSlot = this.getAt(pos)!!
        startingSlot.d = fuel
        operationStack.add(pos)
        do {
            opCount += 1
            if (opCount >= MAX_OPS) break
            val curr = operationStack.removeFirst()
            val currObstacle = this.getAt(curr)!!

            val neighbors = this.neighbors(curr).filter {
                val neighborPos = curr + it
                val neighbor = this.getAt(neighborPos) ?: return@filter false

                val blockUnder = this.getAt(neighborPos + IntVec3D(0, -1, 0)) ?: return@filter false
                val blockOver = this.getAt(neighborPos + IntVec3D(0, 1, 0)) ?: return@filter false

                if (ObstacleType.HAZARD in listOf(neighbor.type, blockUnder.type, blockOver.type)) {
                    return@filter false
                }
                if (neighbor.type == ObstacleType.HARD || blockOver.type == ObstacleType.HARD) {
                    return@filter false
                }
                if (blockUnder.type == ObstacleType.FLACCID && neighbor.type == ObstacleType.FLACCID) {
                    return@filter false
                }

                var neighborDistance = currObstacle.d
                neighborDistance -= if (neighbor.type == ObstacleType.FENCE) {
                    (it.magnitude() * 2)
                } else {
                    (it.magnitude())
                }
                if (neighbor.d < neighborDistance) neighbor.d = neighborDistance
                else { return@filter false }

                return@filter true
            }

            neighbors.forEach {
                if (this.getAt(curr + it)!!.d > 0.0) operationStack.add(curr + it)
            }
        } while (operationStack.isNotEmpty())
    }

    fun resetHighlight() {
        this.obstacleMap.forEach { outer ->
            outer.forEach { mid ->
                mid.forEach { inner ->
                    inner!!.d = 0.0
                }
            }
        }
    }

    fun iter() = ObstacleMapIterator(this)
}