package io.github.personbelowrocks.minecraft.dndbv.events.listeners

import JPlayerHitBoardGround
import io.github.personbelowrocks.minecraft.dndbv.boards.ObstacleType
import io.github.personbelowrocks.minecraft.dndbv.shapes.BoardCircle
import io.github.personbelowrocks.minecraft.dndbv.shapes.BoardCircleRegistry
import io.github.personbelowrocks.minecraft.dndbv.shapes.ParticleFXPainter
import io.github.personbelowrocks.minecraft.dndbv.shapes.ShapeVertex
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.Vec3D
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class BoardGroundPunchedListener(private val circleRegistry: BoardCircleRegistry): Listener {
    private val circles = mutableMapOf<UUID, UUID>()

    @EventHandler
    fun onBoardGroundPunched(event: JPlayerHitBoardGround) {
        if (event.board.activeTurn == null) return
        if (!event.board.hasPlayer(event.player)) return
        event.isCancelled = true

        if (event.player.inventory.itemInMainHand.type != Material.LILY_PAD) return

        val radius = this.circleRegistry.getRadius(event.player).toDouble()

        val shape = BoardCircle(
            event.player,
            IntVec3D.fromBukkitVec(event.block.location.toVector()),
            radius, // IntVec3D.fromBukkitVec(event.block.location.toVector()),
            event.board
        )

        val existingCircle = circles[event.player.uniqueId]
        if (existingCircle != null) {
            ParticleFXPainter.stopDrawing(existingCircle)
        }

        val id = ParticleFXPainter.beginDrawing(shape)
        circles[event.player.uniqueId] = id
        event.player.notice("Started drawing circle with UUID '$id'")
    }
}