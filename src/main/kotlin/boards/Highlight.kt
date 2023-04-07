package io.github.personbelowrocks.minecraft.dndbv.boards

import io.github.personbelowrocks.minecraft.dndbv.shapes.ParticleFXPainter
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import org.bukkit.Chunk
import org.bukkit.generator.ChunkGenerator.ChunkData
import org.bukkit.util.BoundingBox
import java.util.*

fun filledCubicArray3D(size: Int): Array<Array<Array<Double>>> {
    val innermost = run {
        val arr = arrayListOf<Double>()
        (0 until size).forEach { _ ->
            arr.add(-1.0)
        }
        arr
    }

    val middle = run {
        val arr = arrayListOf<Array<Double>>()
        (0 until size).forEach { _ ->
            arr.add(innermost.toTypedArray())
        }
        arr
    }

    return run {
        val arr = arrayListOf<Array<Array<Double>>>()
        (0 until size).forEach { _ ->
            arr.add(middle.toTypedArray())
        }
        arr
    }.toTypedArray()
}

const val CHUNK_SIZE: Int = 16

class HighlightChunk {
    private val storage = filledCubicArray3D(CHUNK_SIZE)

    fun get(pos: IntVec3D): Double? {
        val slot = storage.getOrNull(pos.x)?.getOrNull(pos.y)?.getOrNull(pos.z) ?: return null
        return if (slot < 0.0) {
            null
        } else {
            slot
        }
    }
}

class HighlightStorage(val bounds: BoundingBox) {
    private val map = mutableMapOf<IntVec3D, HighlightChunk>()
}

class Highlight(boundingBox: BoundingBox) {
    private val storage = HighlightStorage(boundingBox)
    private var particleFxId: UUID? = null

    fun setFuel(pos: IntVec3D, fuel: Double) {
        val span = storage.bounds
    }

    fun getFuel(pos: IntVec3D): Double? {
        TODO()
    }

    fun render() {
        // Render the highlight with ParticleFXPainter
    }

    fun stopRender() {
        particleFxId?.let { ParticleFXPainter.stopDrawing(it) }
    }
}