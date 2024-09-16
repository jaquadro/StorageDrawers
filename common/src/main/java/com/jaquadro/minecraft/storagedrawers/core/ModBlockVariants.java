package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModBlockVariants
{
    public static class VariantData {
        ResourceLocation material;

        public VariantData(ResourceLocation material) {
            this.material = material;
        }

        public RegistryEntry<BlockStandardDrawers> blockFull1;
        public RegistryEntry<BlockStandardDrawers> blockFull2;
        public RegistryEntry<BlockStandardDrawers> blockFull4;
        public RegistryEntry<BlockStandardDrawers> blockHalf1;
        public RegistryEntry<BlockStandardDrawers> blockHalf2;
        public RegistryEntry<BlockStandardDrawers> blockHalf4;
        public RegistryEntry<BlockTrim> blockTrim;
    }

    public static void registerVariant(ChameleonRegistry<Block> register, VariantData data) {
        data.blockFull1 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 1, false);
        data.blockFull2 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 2, false);
        data.blockFull4 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 4, false);
        data.blockHalf1 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 1, true);
        data.blockHalf2 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 2, true);
        data.blockHalf4 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 4, true);
        data.blockTrim = ModBlocks.registerTrimBlock(register, data.material);
    }

    public static void registerVariantItem(ChameleonRegistry<Item> register, VariantData data) {
        ModItems.registerBlock(register, data.blockFull1);
        ModItems.registerBlock(register, data.blockFull2);
        ModItems.registerBlock(register, data.blockFull4);
        ModItems.registerBlock(register, data.blockHalf1);
        ModItems.registerBlock(register, data.blockHalf2);
        ModItems.registerBlock(register, data.blockHalf4);
        ModItems.registerBlock(register, data.blockTrim);
    }
}
