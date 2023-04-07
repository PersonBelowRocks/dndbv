package io.github.personbelowrocks.minecraft.dndbv.cmds

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.SessionManager
import com.sk89q.worldedit.world.World
import io.github.personbelowrocks.minecraft.dndbv.Items
import io.github.personbelowrocks.minecraft.dndbv.boards.Board
import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D
import io.github.personbelowrocks.minecraft.dndbv.util.error
import io.github.personbelowrocks.minecraft.dndbv.util.notice
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class CmdSet(private val boardManager: BoardManager): CommandNode() {
    override val name = "set"

    private val sessionManager: SessionManager = WorldEdit.getInstance().sessionManager

    override fun call(caller: Player, args: Array<String>): String? {

        val selection: CuboidRegion = try {
            val session = this.sessionManager.findByName(caller.name) ?: throw NullPointerException()
            if (session.selection !is CuboidRegion) {
                throw NullPointerException()
            }
            session.selection as CuboidRegion

        } catch (e: Exception) {
            return "Couldn't find WorldEdit selection."
        }

        val board = Board(caller.uniqueId, caller.world, IntVec3D.fromBlockVec(selection.pos1), IntVec3D.fromBlockVec(selection.pos2))
        caller.inventory.addItem(Items.movementWand)
        boardManager.addBoard(board)

        caller.notice("Set board $board")

        return null
    }
}