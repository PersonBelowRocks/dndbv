package io.github.personbelowrocks.minecraft.dndbv

import io.github.personbelowrocks.minecraft.dndbv.boards.BoardManager
import io.github.personbelowrocks.minecraft.dndbv.cmds.MasterCmd
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.BoardFigurePunchedListener
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.BoardFigureRightClickedListener
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.BoardGroundPunchedListener
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.BoardGroundRightClickedListener
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit.BoardFigureHit
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit.BukkitBoardFigureRightClickListener
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit.BukkitBoardGroundHitListener
import io.github.personbelowrocks.minecraft.dndbv.events.listeners.bukkit.BukkitBoardGroundRightClickListener
import io.github.personbelowrocks.minecraft.dndbv.shapes.BoardCircleRegistry
import io.github.personbelowrocks.minecraft.dndbv.shapes.ParticleFXPainter
import io.github.personbelowrocks.minecraft.dndbv.util.ChatPalette
import io.github.personbelowrocks.minecraft.dndbv.util.DefaultPalette
import net.kyori.adventure.text.format.TextColor
import org.bukkit.plugin.java.JavaPlugin

class DndBoardVisualizer: JavaPlugin() {
    override fun onEnable() {
        DefaultPalette.palette = ChatPalette(
            c1 = TextColor.color(0x42b3e3),
            c2 = TextColor.color(0xe342db),
            c3 = TextColor.color(0x5a42e3),
            c4 = TextColor.color(0xd43157),
        )

        val boardManager = BoardManager()
        val circleRegistry = BoardCircleRegistry()

        val masterCmd = MasterCmd(this, boardManager, circleRegistry)

        this.getCommand("dndbv")?.setExecutor(masterCmd) ?: logger.severe("Couldn't get main command")
        this.getCommand("dndbv")?.tabCompleter = masterCmd

        // Bukkit events
        this.server.pluginManager.registerEvents(BoardFigureHit(boardManager), this)
        this.server.pluginManager.registerEvents(BukkitBoardFigureRightClickListener(boardManager), this)
        this.server.pluginManager.registerEvents(BukkitBoardGroundRightClickListener(boardManager), this)
        this.server.pluginManager.registerEvents((BukkitBoardGroundHitListener(boardManager)), this)

        // Custom plugin events
        this.server.pluginManager.registerEvents(BoardFigurePunchedListener(), this)
        this.server.pluginManager.registerEvents(BoardGroundRightClickedListener(), this)
        this.server.pluginManager.registerEvents(BoardFigureRightClickedListener(), this)
        this.server.pluginManager.registerEvents(BoardGroundPunchedListener(circleRegistry), this)

        ParticleFXPainter.runTaskTimer(this, 10, 5)
    }

    override fun onDisable() {

    }
}