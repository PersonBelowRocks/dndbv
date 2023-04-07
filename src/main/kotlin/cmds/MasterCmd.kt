package io.github.personbelowrocks.minecraft.dndbv.cmds

import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.shapes.BoardCircleRegistry
import io.github.personbelowrocks.minecraft.dndbv.util.error
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class MasterCmd(private val plugin: JavaPlugin,
                private val boardManager: BoardManager,
                private val circleRegistry: BoardCircleRegistry): CommandNode(), CommandExecutor, TabCompleter {

    override val name = "dndbv"

    init {
        this.addEdge(CmdSet(this.boardManager))
        this.addEdge(CmdView(this.boardManager))
        this.addEdge(CmdClear(this.boardManager))
        this.addEdge(CmdAdd(this.boardManager))
        this.addEdge(CmdRemove(this.boardManager))
        this.addEdge(CmdTurn(this.boardManager))
        this.addEdge(CmdRefresh(this.boardManager))
        this.addEdge(CmdCirc(this.circleRegistry))
    }

    override fun call(caller: Player, args: Array<String>): String? {
        val subcommand = args.getOrNull(0) ?: return "Please provide a subcommand"
        if (!this.hasEdge(subcommand)) {
            return "Couldn't find subcommand '$subcommand'"
        }

        return this.callFor(subcommand, caller, args)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return true
        }

        val error = this.call(sender, args)
        if (error != null) {
            sender.error(error)
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): MutableList<String>? {
        if (sender !is Player) {
            return null
        }

        if (args.isNotEmpty() && this.hasEdge(args[0])) {
            return this.getEdge(args[0])!!.autocomplete(sender, args)
        }
        return this.autocomplete(sender, args)
    }
}