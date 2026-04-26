package com.xarhabia.item;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItems {

    public static final Item CUSTOM_SWORD = Registry.register(
            Registries.ITEM,
            new Identifier("combatmod", "custom_sword"),
            new SwordItem(ToolMaterials.IRON, 3, -2.4F, new Item.Settings())
    );

    public static void register() {
        System.out.println("Cargando ModItems...");
        System.out.println(CUSTOM_SWORD);
    }
}
