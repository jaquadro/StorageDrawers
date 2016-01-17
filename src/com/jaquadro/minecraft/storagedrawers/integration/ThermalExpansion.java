package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IExtendedDataResolver;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;

public class ThermalExpansion extends IntegrationModule
{
    private static final String MOD_ID = "ThermalExpansion";

    static Class clSawmillManager;
    static Class clPulverizerManager;
    static Method mtSawmillAddRecipe;
    static Method mtPulverizerAddRecipe;

    static ItemStack itemSawdust;

    static boolean initialized;

    @Override
    public void init () throws Throwable {
        clSawmillManager = Class.forName("cofh.thermalexpansion.util.crafting.SawmillManager");
        clPulverizerManager = Class.forName("cofh.thermalexpansion.util.crafting.PulverizerManager");

        mtSawmillAddRecipe = clSawmillManager.getMethod("addRecipe", int.class, ItemStack.class, ItemStack.class, ItemStack.class, int.class);
        mtPulverizerAddRecipe = clSawmillManager.getMethod("addRecipe", int.class, ItemStack.class, ItemStack.class, ItemStack.class, int.class);

        Item itemMaterial = GameData.getItemRegistry().getObject(MOD_ID + ":material");
        itemSawdust = new ItemStack(itemMaterial, 1, 512);

        for (int i = 0; i < 6; i++) {
            if (ModBlocks.fullDrawers1 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.fullDrawers1, 1, i), new ItemStack(Blocks.planks, 6, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (ModBlocks.fullDrawers2 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.fullDrawers2, 1, i), new ItemStack(Blocks.planks, 7, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (ModBlocks.fullDrawers4 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.fullDrawers4, 1, i), new ItemStack(Blocks.planks, 5, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (ModBlocks.halfDrawers2 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.halfDrawers2, 1, i), new ItemStack(Blocks.planks, 3, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (ModBlocks.halfDrawers4 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.halfDrawers4, 1, i), new ItemStack(Blocks.planks, 2, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (ModBlocks.trim != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(ModBlocks.trim, 1, i), new ItemStack(Blocks.planks, 5, i), itemSawdust, 100);

            if (RefinedRelocation.fullDrawers1 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(RefinedRelocation.fullDrawers1, 1, i), new ItemStack(Blocks.planks, 6, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (RefinedRelocation.fullDrawers2 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(RefinedRelocation.fullDrawers2, 1, i), new ItemStack(Blocks.planks, 7, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (RefinedRelocation.fullDrawers4 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(RefinedRelocation.fullDrawers4, 1, i), new ItemStack(Blocks.planks, 5, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (RefinedRelocation.halfDrawers2 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(RefinedRelocation.halfDrawers2, 1, i), new ItemStack(Blocks.planks, 3, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (RefinedRelocation.halfDrawers4 != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(RefinedRelocation.halfDrawers4, 1, i), new ItemStack(Blocks.planks, 2, i), new ItemStack(Blocks.planks, 8, 0), 100);
            if (RefinedRelocation.trim != null)
                mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(RefinedRelocation.trim, 1, i), new ItemStack(Blocks.planks, 5, i), itemSawdust, 100);
        }

        itemMaterial = GameData.getItemRegistry().getObject("ThermalFoundation:material");

        if (StorageDrawers.config.cache.enableStorageUpgrades) {
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 2), new ItemStack(itemMaterial, 2, 0), itemSawdust, 100);
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 3), new ItemStack(itemMaterial, 2, 1), itemSawdust, 100);
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 4), new ItemStack(itemMaterial, 2, 4), itemSawdust, 100);
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 5), new ItemStack(Items.diamond, 2), itemSawdust, 100);
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgrade, 1, 6), new ItemStack(Items.emerald, 2), itemSawdust, 100);
        }

        if (StorageDrawers.config.cache.enableIndicatorUpgrades) {
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgradeStatus, 1, 1), new ItemStack(Items.redstone, 2), itemSawdust, 100);
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgradeStatus, 1, 2), new ItemStack(Items.quartz, 2), itemSawdust, 100);
        }

        if (StorageDrawers.config.cache.enableVoidUpgrades)
            mtPulverizerAddRecipe.invoke(null, 2400, new ItemStack(ModItems.upgradeVoid, 1, 1), new ItemStack(itemMaterial, 2, 4), itemSawdust, 100);

        initialized = true;
    }

    @Override
    public void postInit () {

    }

    public static void registerPackBlock (IExtendedDataResolver resolver) {
        if (!initialized || resolver == null)
            return;

        Item itemMaterial = GameData.getItemRegistry().getObject(MOD_ID + ":material");
        itemSawdust = new ItemStack(itemMaterial, 1, 512);

        try {
            Block basicFull1 = resolver.getBlock(BlockConfiguration.BasicFull1);
            Block basicFull2 = resolver.getBlock(BlockConfiguration.BasicFull2);
            Block basicFull4 = resolver.getBlock(BlockConfiguration.BasicFull4);
            Block basicHalf2 = resolver.getBlock(BlockConfiguration.BasicHalf2);
            Block basicHalf4 = resolver.getBlock(BlockConfiguration.BasicHalf4);
            Block basicTrim = resolver.getBlock(BlockConfiguration.Trim);

            Block sortingFull1 = resolver.getBlock(BlockConfiguration.SortingFull1);
            Block sortingFull2 = resolver.getBlock(BlockConfiguration.SortingFull2);
            Block sortingFull4 = resolver.getBlock(BlockConfiguration.SortingFull4);
            Block sortingHalf2 = resolver.getBlock(BlockConfiguration.SortingHalf2);
            Block sortingHalf4 = resolver.getBlock(BlockConfiguration.SortingHalf4);
            Block sortingTrim = resolver.getBlock(BlockConfiguration.TrimSorting);

            for (int i = 0; i < 16; i++) {
                if (!resolver.isValidMetaValue(i))
                    continue;

                Block plank = resolver.getPlankBlock(i);
                int plankMeta = resolver.getPlankMeta(i);

                if (plank != null) {
                    if (basicFull1 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(basicFull1, 1, i), new ItemStack(plank, 6, plankMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (basicFull2 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(basicFull2, 1, i), new ItemStack(plank, 7, plankMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (basicFull4 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(basicFull4, 1, i), new ItemStack(plank, 5, plankMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (basicTrim != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(basicTrim, 1, i), new ItemStack(plank, 5, plankMeta), itemSawdust, 100);

                    if (sortingFull1 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(sortingFull1, 1, i), new ItemStack(plank, 6, plankMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (sortingFull2 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(sortingFull2, 1, i), new ItemStack(plank, 7, plankMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (sortingFull4 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(sortingFull4, 1, i), new ItemStack(plank, 5, plankMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (sortingTrim != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(sortingTrim, 1, i), new ItemStack(plank, 5, plankMeta), itemSawdust, 100);
                }

                Block slab = resolver.getSlabBlock(i);
                int slabMeta = resolver.getSlabMeta(i);

                if (slab != null) {
                    if (basicHalf2 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(basicHalf2, 1, i), new ItemStack(slab, 7, slabMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (basicHalf4 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(basicHalf4, 1, i), new ItemStack(slab, 5, slabMeta), new ItemStack(Blocks.planks, 8, 0), 100);

                    if (sortingHalf2 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(sortingHalf2, 1, i), new ItemStack(slab, 7, slabMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                    if (sortingHalf4 != null)
                        mtSawmillAddRecipe.invoke(null, 2400, new ItemStack(sortingHalf4, 1, i), new ItemStack(slab, 5, slabMeta), new ItemStack(Blocks.planks, 8, 0), 100);
                }
            }
        }
        catch (Throwable t) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.WARN, t, "Error registering pack block in module: " + ThermalExpansion.class.getName());
        }
    }
}
