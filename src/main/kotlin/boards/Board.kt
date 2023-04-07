package io.github.personbelowrocks.minecraft.dndbv.boards

import io.github.personbelowrocks.minecraft.dndbv.shapes.BoardHighlight
import io.github.personbelowrocks.minecraft.dndbv.shapes.ParticleFXPainter
import io.github.personbelowrocks.minecraft.dndbv.shapes.ShapeVertex
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import io.github.personbelowrocks.minecraft.dndbv.util.contains
import net.kyori.adventure.text.TextComponent
import org.bukkit.Color
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.*
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.max

const val MAX_OPS: Int = 10_000_000
const val MAX_PATHFINDING_STEPS: Int = 1024

enum class VecRelation {
    BOARD,
    ABSOLUTE
}

fun MutableList<Figure>.containsFigureWithName(figureName: String) = this.find { figure -> figure.name == figureName } != null

class Turn(private val board: Board, val player: Player, fuel: Double) {
    var highlightedFigure: Figure? = null

    var fuel: Double = fuel
        private set
}

class Board(val dm: UUID, val world: World, pos1: IntVec3D, pos2: IntVec3D) {
    val players = HashMap<UUID, MutableList<Figure>>()

    private var highlightDrawId: UUID? = null

    var fuel: Double = 0.0
    var activeTurn: UUID? = null
        set(value) {
            if (this.highlightDrawId != null) {
                ParticleFXPainter.stopDrawing(this.highlightDrawId!!)
                this.highlightDrawId = null
            }
            field = value
        }

    var highlightedFigure: Figure? = null

    /**
     * [min] is the corner of the board closest to X=0, Y=0, Z=0
     */
    val min: IntVec3D = IntVec3D(
        min(pos1.x, pos2.x),
        min(pos1.y, pos2.y),
        min(pos1.z, pos2.z),
    )

    /**
     * [max] is the corner of the board furthest from X=0, Y=0, Z=0
     */
    val max: IntVec3D = IntVec3D(
        max(pos1.x, pos2.x),
        max(pos1.y, pos2.y),
        max(pos1.z, pos2.z)
    )

    var obstacleMap = ObstacleMap(this)
        private set

    /**
     * Gets the fuel value at [pos], returns null if [pos] is not part of this board.
     * [vecRelation] denotes if [pos] is in absolute coordinates or not.
     */
    fun getFuelAtPos(pos: IntVec3D, vecRelation: VecRelation = VecRelation.ABSOLUTE): Double? {
        val getAtPos = if (vecRelation == VecRelation.ABSOLUTE) pos - this.min
        else pos

        return this.obstacleMap.getAt(getAtPos)?.d
    }

    fun getPathVertices(from: IntVec3D, to: IntVec3D, vecRelation: VecRelation = VecRelation.ABSOLUTE, color: Color = Color.LIME): Array<ShapeVertex> {
        val fromPos = if (vecRelation == VecRelation.ABSOLUTE) from - this.min else from
        val toPos = if (vecRelation == VecRelation.ABSOLUTE) to - this.min else to

        return this.obstacleMap.getPath(fromPos, toPos).map {
            ShapeVertex(color, it.toVec3D())
        }.toTypedArray()
    }

    /**
     * Does this board contain the [pos]? [vecRelation] denotes if [pos] is in absolute coordinates or
     * board coordinates.
     */
    fun contains(pos: IntVec3D, vecRelation: VecRelation = VecRelation.ABSOLUTE): Boolean {
        return this.boundingBox(vecRelation).contains(pos)
    }

    fun hasPlayer(player: Player) = this.players.contains(player.uniqueId)
    fun isOwner(player: Player) = this.dm == player.uniqueId

    override fun toString(): String {
        return "Board(min=${this.min}, max=${this.max})"
    }

    fun boundingBox(relation: VecRelation = VecRelation.ABSOLUTE): BoundingBox {
        if (relation == VecRelation.ABSOLUTE) {
            return BoundingBox(
                this.min.x.toDouble(),
                this.min.y.toDouble(),
                this.min.z.toDouble(),

                this.max.x.toDouble() + 1.0,
                this.max.y.toDouble() + 1.0,
                this.max.z.toDouble() + 1.0
            )
        } else {
            val normed = (this.max - this.min).toVec3D()
            return BoundingBox(
                0.0,
                0.0,
                0.0,

                normed.x + 1.0,
                normed.y + 1.0,
                normed.z + 1.0,
            )
        }

    }

    /**
     * Add new figure with name [figureName] belonging to [owner] to this board and return it.
     * Return null and do nothing if figure already exists by that name.
     */
    fun newFigure(owner: Player, figureName: String, circleColor: Color = Color.RED, radius: Int? = null): Figure? {
        val candidates = this.world.getNearbyEntities(this.boundingBox()).filter { entity ->
            if (entity is ArmorStand) {
                // Find all armour stands with the same name as this figure
                val helmetMetadata = entity.equipment?.helmet?.itemMeta ?: return@filter false
                if (helmetMetadata.displayName() !is TextComponent) return@filter false
                return@filter (helmetMetadata.displayName() as TextComponent).content() == figureName
            }
            return@filter false
        }

        if (candidates.size != 1) throw InvalidFigureNumberError()
        val stand = candidates[0] as ArmorStand

        val newFigure = Figure(
            stand.equipment,
            this, owner.uniqueId,
            IntVec3D.fromBukkitVec(stand.location.toVector()),
            circleColor,
            radius?.toDouble()
            )

        if (this.hasFigure(newFigure.name)) return null
        this.players[owner.uniqueId]?.add(newFigure) ?: run {this.players.put(owner.uniqueId, mutableListOf(newFigure))}

        // This places the figure cleanly in a block.
        stand.remove()
        newFigure.place(newFigure.absPosition)

        return newFigure
    }

    /**
     * Does this board contain a figure with the name [figureName]
     */
    fun hasFigure(figureName: String) =
        this.players.values.flatten().find { figure ->
            figure.name == figureName
        } != null

    fun getFigures(player: Player): MutableList<Figure>? = this.players[player.uniqueId]
    fun getFigures(ownerUuid: UUID): MutableList<Figure>? = this.players[ownerUuid]

    fun removeFigure(figureName: String): Figure? {
        val figure = this.getFigure(figureName) ?: return null
        this.getFigures(figure.owner)?.remove(figure) ?: return null

        // Clean up any stuff this figure is responsible for
        // figure.remove()  avoid doing this for the time being, removing a figure is useful when it's weird and buggy
        // and we wouldn't want to ruin a game by clearing a figure's equipment or something

        figure.clearCircle() // in case we're drawing a circle
        figure.setStandVisibility(true)

        return figure
    }

    fun getFigure(figureName: String): Figure? {
        val holderList = this.players.toList().find { (_, list) ->
            list.containsFigureWithName(figureName)
        }?.second ?: return null

        val figure = holderList.find {
            it.name == figureName
        }
        return figure
    }

    /**
     * Get first figure [r] distance away from [figurePos] on this board.
     * [vecRelation] denotes whether [figurePos] is in absolute coordinates or in board coordinates
     */
    fun getFigure(figurePos: IntVec3D, r: Double = 0.90, vecRelation: VecRelation = VecRelation.ABSOLUTE): Figure? {

        val pos = if (vecRelation == VecRelation.BOARD) figurePos - this.min else figurePos

        return this.players.values.flatten().find { candidate ->
            val candidatePos = if (vecRelation == VecRelation.BOARD) candidate.boardPosition else candidate.absPosition
            return@find (candidatePos.toVec3D() - pos.toVec3D()).magnitude() <= r
        }
    }

    /**
     * Submit the current obstacle map for drawing to the ParticleFXPainter singleton.
     */
    private fun drawDistanceHighlight() {
        if (this.highlightDrawId != null) ParticleFXPainter.stopDrawing(this.highlightDrawId!!)

        val vertices = arrayListOf<ShapeVertex>()
        for (obst in this.obstacleMap.iter()) {
            if (obst.d > 0.0) {
                val pos = (obst.pos - this.min).toVec3D() + Vec3D(0.0, 0.1, 0.0)

                val color = Color.fromRGB(
                    255,
                    floor(((obst.d / this.fuel)*255)).toInt(),
                    floor(((obst.d / this.fuel)*255)).toInt()
                )

                vertices.add(ShapeVertex(color, pos))
            }
        }
        this.highlightDrawId = ParticleFXPainter.beginDrawing(BoardHighlight(this, vertices.toTypedArray()))
    }

    /**
     * Remove the distance highlight from the ParticleFXPainter
     */
    fun stopHighlighting() {
        if (this.highlightDrawId != null) ParticleFXPainter.stopDrawing(this.highlightDrawId!!)

        this.highlightDrawId = null
        this.highlightedFigure = null

        this.obstacleMap.resetHighlight()
    }

    /**
     * Update the obstacle map by reading all the board's block data again. This can be a very expensive function
     * to call as it may process several million blocks and allocate a lot of memory.
     *
     * This function is not related to drawing and/or measuring distance on the board. It only exists to generate the
     * map of obstacles required to do so.
     */
    fun updateObstacleMap() {
        this.obstacleMap = ObstacleMap(this)
    }

    /**
     * Generate highlight from [figure] and submit the highlight for drawing
     */
    fun highlightFromFigure(figure: Figure) {
        if (this.fuel <= 1.0) return
        this.obstacleMap.highlightFrom(figure.absPosition, this.fuel)
        this.highlightedFigure = figure

        this.drawDistanceHighlight()
    }

    fun isTurning() = this.activeTurn != null
    fun isHighlighting() = this.highlightDrawId != null

    /**
     * Is the [figurePos] part of the current distance highlight?
     * [vecRelation] denotes if [figurePos] is relative or absolute
     */
    fun isHighlighted(figurePos: IntVec3D, vecRelation: VecRelation = VecRelation.ABSOLUTE): Boolean {
        if (!isHighlighting()) return false
        val obst = if (vecRelation == VecRelation.ABSOLUTE) {
            this.obstacleMap.getAt(figurePos - this.min)
        } else {
            this.obstacleMap.getAt(figurePos)
        } ?: return false

        return obst.d > 0.0
    }
}
