package io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit

import JPlayerHitBoardGround
import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BukkitBoardGroundHitListener(val boardManager: BoardManager): Listener {
    @EventHandler
    fun onBoardHit(event: BlockBreakEvent) {
        val board = boardManager.getBoard(IntVec3D.fromBukkitVec(event.block.location.toVector())) ?: return

        val boardEvent = JPlayerHitBoardGround(event.block, board, event.player)
        Bukkit.getPluginManager().callEvent(boardEvent)
        event.isCancelled = boardEvent.isCancelled
    }
}