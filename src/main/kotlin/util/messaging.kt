package io.github.personbelowrocks.minecraft.dndbv.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player


class ChatPalette(val c1: TextColor, val c2: TextColor, val c3: TextColor, val c4: TextColor)

object DefaultPalette {
    var palette: ChatPalette? = null
        set(fmt) {
            if (field == null) {
                field = fmt
            }
        }
        get() = field ?: throw UninitializedPropertyAccessException("No default palette was set")
}

fun Player.error(msg: String, palette: ChatPalette? = null) {
    val usingPalette = palette ?: DefaultPalette.palette!!
    this.sendMessage(Component
        .text()
        .content(msg)
        .color(usingPalette.c4)
        .build()
    )
}

fun Player.notice(msg: String, palette: ChatPalette? = null) {
    val usingPalette = palette ?: DefaultPalette.palette!!
    this.sendMessage(Component
        .text()
        .content(msg)
        .color(usingPalette.c1)
        .build())
}