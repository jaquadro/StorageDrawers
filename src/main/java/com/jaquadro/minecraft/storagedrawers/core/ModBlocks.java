package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, StorageDrawers.MOD_ID);

    public static final RegistryObject<BlockStandardDrawers>
        OAK_FULL_DRAWERS_1 = registerDrawerBlock("oak_full_drawers_1", 1, false),
        OAK_FULL_DRAWERS_2 = registerDrawerBlock("oak_full_drawers_2", 2, false),
        OAK_FULL_DRAWERS_4 = registerDrawerBlock("oak_full_drawers_4", 4, false),
        OAK_HALF_DRAWERS_1 = registerDrawerBlock("oak_half_drawers_1", 1, true),
        OAK_HALF_DRAWERS_2 = registerDrawerBlock("oak_half_drawers_2", 2, true),
        OAK_HALF_DRAWERS_4 = registerDrawerBlock("oak_half_drawers_4", 4, true),
        SPRUCE_FULL_DRAWERS_1 = registerDrawerBlock("spruce_full_drawers_1", 1, false),
        SPRUCE_FULL_DRAWERS_2 = registerDrawerBlock("spruce_full_drawers_2", 2, false),
        SPRUCE_FULL_DRAWERS_4 = registerDrawerBlock("spruce_full_drawers_4", 4, false),
        SPRUCE_HALF_DRAWERS_1 = registerDrawerBlock("spruce_half_drawers_1", 1, true),
        SPRUCE_HALF_DRAWERS_2 = registerDrawerBlock("spruce_half_drawers_2", 2, true),
        SPRUCE_HALF_DRAWERS_4 = registerDrawerBlock("spruce_half_drawers_4", 4, true),
        BIRCH_FULL_DRAWERS_1 = registerDrawerBlock("birch_full_drawers_1", 1, false),
        BIRCH_FULL_DRAWERS_2 = registerDrawerBlock("birch_full_drawers_2", 2, false),
        BIRCH_FULL_DRAWERS_4 = registerDrawerBlock("birch_full_drawers_4", 4, false),
        BIRCH_HALF_DRAWERS_1 = registerDrawerBlock("birch_half_drawers_1", 1, true),
        BIRCH_HALF_DRAWERS_2 = registerDrawerBlock("birch_half_drawers_2", 2, true),
        BIRCH_HALF_DRAWERS_4 = registerDrawerBlock("birch_half_drawers_4", 4, true),
        JUNGLE_FULL_DRAWERS_1 = registerDrawerBlock("jungle_full_drawers_1", 1, false),
        JUNGLE_FULL_DRAWERS_2 = registerDrawerBlock("jungle_full_drawers_2", 2, false),
        JUNGLE_FULL_DRAWERS_4 = registerDrawerBlock("jungle_full_drawers_4", 4, false),
        JUNGLE_HALF_DRAWERS_1 = registerDrawerBlock("jungle_half_drawers_1", 1, true),
        JUNGLE_HALF_DRAWERS_2 = registerDrawerBlock("jungle_half_drawers_2", 2, true),
        JUNGLE_HALF_DRAWERS_4 = registerDrawerBlock("jungle_half_drawers_4", 4, true),
        ACACIA_FULL_DRAWERS_1 = registerDrawerBlock("acacia_full_drawers_1", 1, false),
        ACACIA_FULL_DRAWERS_2 = registerDrawerBlock("acacia_full_drawers_2", 2, false),
        ACACIA_FULL_DRAWERS_4 = registerDrawerBlock("acacia_full_drawers_4", 4, false),
        ACACIA_HALF_DRAWERS_1 = registerDrawerBlock("acacia_half_drawers_1", 1, true),
        ACACIA_HALF_DRAWERS_2 = registerDrawerBlock("acacia_half_drawers_2", 2, true),
        ACACIA_HALF_DRAWERS_4 = registerDrawerBlock("acacia_half_drawers_4", 4, true),
        DARK_OAK_FULL_DRAWERS_1 = registerDrawerBlock("dark_oak_full_drawers_1", 1, false),
        DARK_OAK_FULL_DRAWERS_2 = registerDrawerBlock("dark_oak_full_drawers_2", 2, false),
        DARK_OAK_FULL_DRAWERS_4 = registerDrawerBlock("dark_oak_full_drawers_4", 4, false),
        DARK_OAK_HALF_DRAWERS_1 = registerDrawerBlock("dark_oak_half_drawers_1", 1, true),
        DARK_OAK_HALF_DRAWERS_2 = registerDrawerBlock("dark_oak_half_drawers_2", 2, true),
        DARK_OAK_HALF_DRAWERS_4 = registerDrawerBlock("dark_oak_half_drawers_4", 4, true),
        CRIMSON_FULL_DRAWERS_1 = registerDrawerBlock("crimson_full_drawers_1", 1, false),
        CRIMSON_FULL_DRAWERS_2 = registerDrawerBlock("crimson_full_drawers_2", 2, false),
        CRIMSON_FULL_DRAWERS_4 = registerDrawerBlock("crimson_full_drawers_4", 4, false),
        CRIMSON_HALF_DRAWERS_1 = registerDrawerBlock("crimson_half_drawers_1", 1, true),
        CRIMSON_HALF_DRAWERS_2 = registerDrawerBlock("crimson_half_drawers_2", 2, true),
        CRIMSON_HALF_DRAWERS_4 = registerDrawerBlock("crimson_half_drawers_4", 4, true),
        WARPED_FULL_DRAWERS_1 = registerDrawerBlock("warped_full_drawers_1", 1, false),
        WARPED_FULL_DRAWERS_2 = registerDrawerBlock("warped_full_drawers_2", 2, false),
        WARPED_FULL_DRAWERS_4 = registerDrawerBlock("warped_full_drawers_4", 4, false),
        WARPED_HALF_DRAWERS_1 = registerDrawerBlock("warped_half_drawers_1", 1, true),
        WARPED_HALF_DRAWERS_2 = registerDrawerBlock("warped_half_drawers_2", 2, true),
        WARPED_HALF_DRAWERS_4 = registerDrawerBlock("warped_half_drawers_4", 4, true);

    public static final RegistryObject<BlockCompDrawers> COMPACTING_DRAWERS_3 = registerCompactingDrawerBlock("compacting_drawers_3");

    public static final RegistryObject<BlockTrim>
        OAK_TRIM = registerTrimBlock("oak_trim"),
        SPRUCE_TRIM = registerTrimBlock("spruce_trim"),
        BIRCH_TRIM = registerTrimBlock("birch_trim"),
        JUNGLE_TRIM = registerTrimBlock("jungle_trim"),
        ACACIA_TRIM = registerTrimBlock("acacia_trim"),
        DARK_OAK_TRIM = registerTrimBlock("dark_oak_trim"),
        CRIMSON_TRIM = registerTrimBlock("crimson_trim"),
        WARPED_TRIM = registerTrimBlock("warped_trim");

    public static final RegistryObject<BlockController> CONTROLLER = registerControllerBlock("controller");
    public static final RegistryObject<BlockSlave> CONTROLLER_SLAVE = registerControllerSlaveBlock("controller_slave");

    private ModBlocks() {}

    private static RegistryObject<BlockStandardDrawers> registerDrawerBlock(String name, int drawerCount, boolean halfDepth) {
        return BLOCK_REGISTER.register(name, () -> new BlockStandardDrawers(drawerCount, halfDepth, BlockBehaviour.Properties.of(Material.WOOD)
                .strength(3.0F, 5.0F)
                .sound(SoundType.WOOD)
                .isSuffocating(ModBlocks::predFalse)
                .isRedstoneConductor(ModBlocks::predFalse)));
    }

    private static RegistryObject<BlockCompDrawers> registerCompactingDrawerBlock(String name) {
        return  BLOCK_REGISTER.register(name, () -> new BlockCompDrawers(BlockBehaviour.Properties.of(Material.STONE)
                .sound(SoundType.STONE).strength(10f)
                .isSuffocating(ModBlocks::predFalse)
                .isRedstoneConductor(ModBlocks::predFalse)));
    }

    private static RegistryObject<BlockTrim> registerTrimBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockTrim(BlockBehaviour.Properties.of(Material.WOOD)
                .sound(SoundType.WOOD).strength(5f)));
    }

    private static RegistryObject<BlockController> registerControllerBlock(String name) {
        return  BLOCK_REGISTER.register(name, () -> new BlockController(BlockBehaviour.Properties.of(Material.WOOD)
                .sound(SoundType.STONE).strength(5f)));
    }

    private static RegistryObject<BlockSlave> registerControllerSlaveBlock(String name) {
        return  BLOCK_REGISTER.register(name, () -> new BlockSlave(BlockBehaviour.Properties.of(Material.WOOD)
                .sound(SoundType.STONE).strength(5f)));
    }

    public static void register(IEventBus bus) {
        BLOCK_REGISTER.register(bus);
    }

    public static <B extends Block> Stream<B> getBlocksOfType(Class<B> blockClass) {
        return BLOCK_REGISTER.getEntries()
                .stream()
                .filter(RegistryObject::isPresent)
                .map(RegistryObject::get)
                .filter(blockClass::isInstance)
                .map(blockClass::cast);
    }

    public static Stream<BlockDrawers> getDrawers() {
        return getBlocksOfType(BlockDrawers.class);
    }

    public static <B extends BlockDrawers> Stream<B> getDrawersOfTypeAndSize(Class<B> drawerClass, int size) {
        return getBlocksOfType(drawerClass)
                .filter(blockDrawers -> blockDrawers.getDrawerCount() == size);
    }

    public static <B extends BlockDrawers> Stream<B> getDrawersOfTypeAndSizeAndDepth(Class<B> drawerClass, int size, boolean halfDepth) {
        return getDrawersOfTypeAndSize(drawerClass, size)
                .filter(blockDrawers -> blockDrawers.isHalfDepth() == halfDepth);
    }

    private static boolean predFalse (BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
