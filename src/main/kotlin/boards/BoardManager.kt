package io.github.personbelowrocks.minecraft.dndbv.boards

import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import org.bukkit.entity.Player
import java.io.NotActiveException
import java.util.*
import kotlin.collections.HashSet

class BoardManager() {
    private val boards = HashSet<Board>()
    private val boardCircles = mapOf<UUID, UUID>()

    fun addBoard(board: Board) {
        boards.add(board)
    }

    fun removeBoard(owner: Player) {
        val board = this.getBoard(owner) ?: throw NotActiveException()
        board.stopHighlighting()
        this.boards.remove(board)
    }

    fun getBoard(pos: IntVec3D) = this.boards.find { it.contains(pos) }
    fun getBoard(owner: Player) = this.boards.find { it.isOwner(owner) }

    fun drawBoardCircle() {
        TODO()
    }

}