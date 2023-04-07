package io.github.personbelowrocks.minecraft.dndbv.boards

import io.github.personbelowrocks.minecraft.dndbv.shapes.ParticleFXPainter
import io.github.personbelowrocks.minecraft.dndbv.shapes.Circle
import io.github.personbelowrocks.minecraft.dndbv.shapes.ShapeVertex
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import net.kyori.adventure.text.TextComponent
import org.bukkit.Color
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import java.util.*
import kotlin.Exception

class InvalidFigureNumberError: Exception()

private fun World.getArmorStandAt(pos: IntVec3D, name: String): ArmorStand? {
    val entities = this.getNearbyEntities(pos.toLocation(this), 0.5, 0.5, 0.5)

    return entities.find { entity ->
        if (entity is ArmorStand) {
            // Find all armour stands with the same name as this figure
            val helmetMetadata = entity.equipment.helmet?.itemMeta ?: return@find false
            if (helmetMetadata.displayName() !is TextComponent) return@find false
            return@find (helmetMetadata.displayName() as TextComponent).content() == name
        }
        return@find false
    } as? ArmorStand
}

data class MovementReport(val remainingFuel: Double, val pathVertices: Array<ShapeVertex>) {
    // Autogenerated by intelliJ
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovementReport

        if (remainingFuel != other.remainingFuel) return false
        if (!pathVertices.contentEquals(other.pathVertices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = remainingFuel.hashCode()
        result = 31 * result + pathVertices.contentHashCode()
        return result
    }
    // end
}

fun World.getNearbyEntities(pos: IntVec3D, r: Double = 1.0): List<Entity> =
    this.getNearbyEntities(
        pos.toLocation(this), r, r, r
    ).toList()

class Figure(private val gear: EntityEquipment,
             val board: Board,
             val owner: UUID, absPosition: IntVec3D,
             private val circleColor: Color,
             private val circleRadius: Double? = 2.0) {

    init {
        this.gear.helmet = this.gear.helmet?.clone()
    }

    var absPosition = absPosition
        set(value) {
            field = value
            this.boardPosition = value - this.board.min
        }

    var boardPosition = absPosition - this.board.min
        private set

    var pointingArmed = false

    // The helmet determines the name of the figure.
    val name = (this.gear
        .helmet
        ?.itemMeta
        ?.displayName() as? TextComponent)
        ?.content() ?: throw NullPointerException("Figure needs helmet with valid name.")

    private var circleUuid: UUID? = null

    /**
     * Get this figure's associated armor stand. Returns null if no stand was found. Throws InvalidFigureNumberError
     * if there is more than 1 stand with this figure's name.
     */
    fun armorStand(): ArmorStand? {
        val candidates = this.board.world.getNearbyEntities(absPosition).filter { entity ->
            if (entity is ArmorStand) {
                // Find all armour stands with the same name as this figure
                val helmetMetadata = entity.equipment.helmet?.itemMeta ?: return@filter false
                if (helmetMetadata.displayName() !is TextComponent) return@filter false
                return@filter (helmetMetadata.displayName() as TextComponent).content() == this.name
            }
            return@filter false
        }

        // If there isn't an armour stand with our name something's wrong since Figure.remove() shouldn't be called
        // unless the figure has actually been placed. And if there's more than 1 figure something is wrong because
        // we don't want to have 2 figures with the same name.
        if (candidates.size > 1) run { println("$candidates"); throw InvalidFigureNumberError() }
        return candidates.getOrNull(0) as? ArmorStand
    }

    /**
     * Removes this figure and all associated particle effects.
     */
    fun remove() {
        val candidates = this.board.world.getNearbyEntities(absPosition).filter { entity ->
            if (entity is ArmorStand) {
                // Find all armour stands with the same name as this figure
                val helmetMetadata = entity.equipment.helmet?.itemMeta ?: return@filter false
                if (helmetMetadata.displayName() !is TextComponent) return@filter false
                return@filter (helmetMetadata.displayName() as TextComponent).content() == this.name
            }
            return@filter false
        }

        // If there isn't an armour stand with our name something's wrong since Figure.remove() shouldn't be called
        // unless the figure has actually been placed. And if there's more than 1 figure something is wrong because
        // we don't want to have 2 figures with the same name.
        if (candidates.size != 1) run { println("$candidates"); throw InvalidFigureNumberError() }
        val stand = candidates[0] as ArmorStand

        // stand.equipment.helmet = null
        stand.remove()

        this.clearCircle()
    }

    fun setStandVisibility(state: Boolean) {
        val candidates = this.board.world.getNearbyEntities(absPosition).filter { entity ->
            if (entity is ArmorStand) {
                // Find all armour stands with the same name as this figure
                val helmetMetadata = entity.equipment.helmet?.itemMeta ?: return@filter false
                if (helmetMetadata.displayName() !is TextComponent) return@filter false
                return@filter (helmetMetadata.displayName() as TextComponent).content() == this.name
            }
            return@filter false
        }

        // If there isn't an armour stand with our name something's wrong since Figure.remove() shouldn't be called
        // unless the figure has actually been placed. And if there's more than 1 figure something is wrong because
        // we don't want to have 2 figures with the same name.
        if (candidates.size != 1) run { println("$candidates"); throw InvalidFigureNumberError() }
        val stand = candidates[0] as ArmorStand

        stand.isVisible = state
    }

    fun setStandBasePlate(state: Boolean) {
        val candidates = this.board.world.getNearbyEntities(absPosition).filter { entity ->
            if (entity is ArmorStand) {
                // Find all armour stands with the same name as this figure
                val helmetMetadata = entity.equipment.helmet?.itemMeta ?: return@filter false
                if (helmetMetadata.displayName() !is TextComponent) return@filter false
                return@filter (helmetMetadata.displayName() as TextComponent).content() == this.name
            }
            return@filter false
        }

        // If there isn't an armour stand with our name something's wrong since Figure.remove() shouldn't be called
        // unless the figure has actually been placed. And if there's more than 1 figure something is wrong because
        // we don't want to have 2 figures with the same name.
        if (candidates.size != 1) run { println("$candidates"); throw InvalidFigureNumberError() }
        val stand = candidates[0] as ArmorStand

        stand.setBasePlate(state)
    }

    fun place(onPos: IntVec3D, vecRelation: VecRelation = VecRelation.ABSOLUTE) {

        val pos = if (vecRelation == VecRelation.BOARD) onPos + this.board.min else onPos
        // We need to place the figure .5 blocks further in on the block, otherwise it'll be on the corner
        val location = pos.toLocation(this.board.world).toVector().add(Vector(0.5, 0.0, 0.5)).toLocation(this.board.world)
        val newEntity = this.board.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand

        // Update our position
        this.absPosition = pos

        // Populate the new entity's equipment
        newEntity.equipment.setItem(EquipmentSlot.HEAD, this.gear.helmet?.clone())
        newEntity.equipment.setItem(EquipmentSlot.CHEST, this.gear.chestplate?.clone())
        newEntity.equipment.setItem(EquipmentSlot.LEGS, this.gear.leggings?.clone())
        newEntity.equipment.setItem(EquipmentSlot.FEET, this.gear.boots?.clone())
        newEntity.equipment.setItem(EquipmentSlot.HAND, this.gear.itemInMainHand.clone())
        newEntity.equipment.setItem(EquipmentSlot.OFF_HAND, this.gear.itemInOffHand.clone())

        newEntity.customName = this.name
        newEntity.isCustomNameVisible = true

        newEntity.isVisible = false
    }

    fun drawCircle() {
        if (this.circleRadius == null) return

        this.clearCircle()
        this.circleUuid = ParticleFXPainter.beginDrawing(Circle(this.circleColor,
            this.board.world,
            this.absPosition,
            this.circleRadius,
            1.2))
    }

    fun clearCircle() {
        if (this.circleUuid == null || this.circleRadius == null) return

        ParticleFXPainter.stopDrawing(this.circleUuid!!)
        this.circleUuid = null
    }

    fun isCircled() = this.circleUuid != null

    fun moveTo(pos: IntVec3D, vecRelation: VecRelation = VecRelation.BOARD): MovementReport {
//        val obstacle = this.board.obstacleMap.getAt(pos) ?: throw IndexOutOfBoundsException("$pos not in board")

        // This should never throw in any real use case
        val newFuel = this.board.getFuelAtPos(pos, vecRelation) ?:
            throw NullPointerException("$pos is not a position on this board!")

        val from = this.absPosition

        val candidates = this.board.world.getNearbyEntities(absPosition).filter { entity ->
            if (entity is ArmorStand) {
                // Find all armour stands with the same name as this figure
                val helmetMetadata = entity.equipment.helmet?.itemMeta ?: return@filter false
                if (helmetMetadata.displayName() !is TextComponent) return@filter false
                return@filter (helmetMetadata.displayName() as TextComponent).content() == this.name
            }
            return@filter false
        }

        // If there isn't an armour stand with our name something's wrong since Figure.remove() shouldn't be called
        // unless the figure has actually been placed. And if there's more than 1 figure something is wrong because
        // we don't want to have 2 figures with the same name (confusing to distinguish between them).
        if (candidates.size != 1) throw InvalidFigureNumberError()
        val me = candidates[0]

        val positionVector = pos.toVec3D() + this.board.min.toVec3D() + Vec3D(0.5, 0.0, 0.5)
        me.teleport(positionVector.toLocation(this.board.world))
        this.absPosition = pos + this.board.min

        // If we had a circle add that back now
        if (this.isCircled()) {
            this.clearCircle()
            this.drawCircle()
        }

//        val pathVertices: Array<ShapeVertex> = this.board.obstacleMap.getPath(from - this.board.min, this.absPosition - this.board.min).map {
//            ShapeVertex(Color.LIME, it.toVec3D())
//        }.toTypedArray()
        val pathVertices = this.board.getPathVertices(from, this.absPosition)

        val finalLoc = me.location

        val first = (pathVertices.getOrNull(0) ?: pathVertices[0]) as Vec3D
        val second = (pathVertices.getOrNull(1) ?: pathVertices[0]) as Vec3D

        val towards = (first.normalize() - second.normalize()).normalize()
        if (towards.x.isFinite() || towards.y.isFinite() || towards.z.isFinite()) finalLoc.direction = towards.normalize().toBukkitVec()

        me.teleport(finalLoc)
        // println("moved to $finalLoc")

        // Report some data about this move like the current fuel amount and the path we took
        return MovementReport(newFuel, pathVertices)// arrayOf(ShapeVertex(Color.LIME, 1.0, 1.0, 1.0), ShapeVertex(Color.LIME, 1.0, 1.0, 1.0)))
    }

    fun setFacing(direction: Vec3D) {
        val me = this.board.world.getArmorStandAt(this.absPosition, this.name) ?: return
        val finalLoc = me.location
        finalLoc.direction = direction.normalize().toBukkitVec()

        me.teleport(finalLoc)
    }
}