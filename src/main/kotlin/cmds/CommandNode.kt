package io.github.personbelowrocks.minecraft.dndbv.cmds

import org.bukkit.entity.Player
import java.lang.NullPointerException

abstract class CommandNode {
    private val edges = HashMap<String, CommandNode>()
    val names: Set<String>
        get() = this.edges.keys

    abstract val name: String

    companion object {
        val doc: String? = null
    }

    fun addEdge(handler: CommandNode, name: String = handler.name) {
        this.edges[name] = handler
    }

    fun hasEdge(name: String): Boolean {
        return name in this.edges.keys
    }

    fun getEdge(name: String): CommandNode? {
        return this.edges[name]
    }

    fun callFor(name: String, caller: Player, args: Array<String>): String? {
        val handler = this.edges[name] ?: throw NullPointerException("No handler exists for that name")
        return handler.call(caller, args)
    }

    abstract fun call(caller: Player, args: Array<String>): String?
    open fun autocomplete(caller: Player, args: Array<String>): MutableList<String> {
        return this.edges.keys.toMutableList()
    }
}