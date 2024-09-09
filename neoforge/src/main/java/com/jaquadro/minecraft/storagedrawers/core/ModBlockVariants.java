package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockVariants
{
    public static class VariantData {
        ResourceLocation material;

        public VariantData(ResourceLocation material) {
            this.material = material;
        }

        public DeferredBlock<BlockStandardDrawers> blockFull1;
        public DeferredBlock<BlockStandardDrawers> blockFull2;
        public DeferredBlock<BlockStandardDrawers> blockFull4;
        public DeferredBlock<BlockStandardDrawers> blockHalf1;
        public DeferredBlock<BlockStandardDrawers> blockHalf2;
        public DeferredBlock<BlockStandardDrawers> blockHalf4;
        public DeferredBlock<BlockTrim> blockTrim;
    }

    public static void registerVariant(DeferredRegister.Blocks register, VariantData data) {
        data.blockFull1 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 1, false);
        data.blockFull2 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 2, false);
        data.blockFull4 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 4, false);
        data.blockHalf1 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 1, true);
        data.blockHalf2 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 2, true);
        data.blockHalf4 = ModBlocks.registerWoodenDrawerBlock(register, data.material, 4, true);
        data.blockTrim = ModBlocks.registerTrimBlock(register, data.material);
    }

    public static void registerVariantItem(DeferredRegister<Item> register, VariantData data) {
        ModItems.registerBlock(register, data.blockFull1);
        ModItems.registerBlock(register, data.blockFull2);
        ModItems.registerBlock(register, data.blockFull4);
        ModItems.registerBlock(register, data.blockHalf1);
        ModItems.registerBlock(register, data.blockHalf2);
        ModItems.registerBlock(register, data.blockHalf4);
        ModItems.registerBlock(register, data.blockTrim);
    }
}
