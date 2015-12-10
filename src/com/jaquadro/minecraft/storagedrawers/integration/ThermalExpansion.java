package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlock;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.lang.reflect.Method;

public class ThermalExpansion extends IntegrationModule
{
    private static final String MOD_ID = "ThermalExpansion";

    static Class clSawmillManager;
    static Class clPulverizerManager;
    static Method mtSawmillAddRecipe;
    static Method mtPulverizerAddRecipe;

    static ItemStack itemSawdust;

    @Override
    public void init () throws Throwable {
        clSawmillManager = Class.forName("cofh.thermalexpansion.util.crafting.SawmillManager");
        clPulverizerManager = Class.forName("cofh.thermalexpansion.util.crafting.PulverizerManager");

        mtSawmillAddRecipe = clSawmillManager.getMethod("addRecipe", int.class, ItemStack.class, ItemStack.class, ItemStack.class, int.class);
        mtPulverizerAddRecipe = clSawmillManager.getMethod("addRecipe", int.class, ItemStack.class, ItemStack.class, ItemStack.class, int.class);

        Item itemMaterial = GameData.getItemRegistry().getObject(MOD_ID + ":material");
        itemSawdust = new ItemStack(itemMaterial, 1, 512);

        for (int i = 0; i < 6; i++) {
            mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.fullDrawers1, 1, i), new ItemStack(Blocks.planks, 6, i), itemSawdust, 100);
            mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.fullDrawers2, 1, i), new ItemStack(Blocks.planks, 7, i), itemSawdust, 100);
            mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.fullDrawers4, 1, i), new ItemStack(Blocks.planks, 5, i), itemSawdust, 100);
            mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.halfDrawers2, 1, i), new ItemStack(Blocks.planks, 3, i), itemSawdust, 100);
            mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.halfDrawers4, 1, i), new ItemStack(Blocks.planks, 2, i), itemSawdust, 100);
            mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.trim, 1, i), new ItemStack(Blocks.planks, 5, i), itemSawdust, 100);
        }

        itemMaterial = GameData.getItemRegistry().getObject("ThermalFoundation:material");

        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 2), new ItemStack(itemMaterial, 2, 0), itemSawdust, 100);
        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 3), new ItemStack(itemMaterial, 2, 1), itemSawdust, 100);
        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 4), new ItemStack(itemMaterial, 2, 4), itemSawdust, 100);
        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 5), new ItemStack(Items.diamond, 2), itemSawdust, 100);
        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 6), new ItemStack(Items.emerald, 2), itemSawdust, 100);

        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgradeStatus, 1, 1), new ItemStack(Items.redstone, 2), itemSawdust, 100);
        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgradeStatus, 1, 2), new ItemStack(Items.quartz, 2), itemSawdust, 100);

        mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgradeVoid, 1, 1), new ItemStack(itemMaterial, 2, 4), itemSawdust, 100);
    }

    @Override
    public void postInit () {

    }

    /*public static void registerPackBlock (Block block, IPackBlock packBlock) {
        IPackDataResolver resolver = packBlock.getDataResolver();
        if (resolver == null)
            return;
    }*/
}
