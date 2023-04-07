package io.github.personbelowrocks.minecraft.dndbv

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Items {
    companion object {
        val movementWand: ItemStack = run {
            val item = ItemStack(Material.STICK)

            item
        }

        val masterWand: ItemStack = run {
            val item = ItemStack(Material.BLAZE_ROD)
//
//            val name = Component.text()
//                .content("DM Wand")
//                .color(TextColor.fromHexString("4faaff"))
//                .decorate(TextDecoration.BOLD)
//                .build()

            // item.itemMeta.displayName(name)
            item
        }
    }
}