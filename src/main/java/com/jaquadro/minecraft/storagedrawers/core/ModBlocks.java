package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
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
        OAK_FULL_DRAWERS_1 = registerWoodenDrawerBlock("oak_full_drawers_1", 1, false),
        OAK_FULL_DRAWERS_2 = registerWoodenDrawerBlock("oak_full_drawers_2", 2, false),
        OAK_FULL_DRAWERS_4 = registerWoodenDrawerBlock("oak_full_drawers_4", 4, false),
        OAK_HALF_DRAWERS_1 = registerWoodenDrawerBlock("oak_half_drawers_1", 1, true),
        OAK_HALF_DRAWERS_2 = registerWoodenDrawerBlock("oak_half_drawers_2", 2, true),
        OAK_HALF_DRAWERS_4 = registerWoodenDrawerBlock("oak_half_drawers_4", 4, true),
        SPRUCE_FULL_DRAWERS_1 = registerWoodenDrawerBlock("spruce_full_drawers_1", 1, false),
        SPRUCE_FULL_DRAWERS_2 = registerWoodenDrawerBlock("spruce_full_drawers_2", 2, false),
        SPRUCE_FULL_DRAWERS_4 = registerWoodenDrawerBlock("spruce_full_drawers_4", 4, false),
        SPRUCE_HALF_DRAWERS_1 = registerWoodenDrawerBlock("spruce_half_drawers_1", 1, true),
        SPRUCE_HALF_DRAWERS_2 = registerWoodenDrawerBlock("spruce_half_drawers_2", 2, true),
        SPRUCE_HALF_DRAWERS_4 = registerWoodenDrawerBlock("spruce_half_drawers_4", 4, true),
        BIRCH_FULL_DRAWERS_1 = registerWoodenDrawerBlock("birch_full_drawers_1", 1, false),
        BIRCH_FULL_DRAWERS_2 = registerWoodenDrawerBlock("birch_full_drawers_2", 2, false),
        BIRCH_FULL_DRAWERS_4 = registerWoodenDrawerBlock("birch_full_drawers_4", 4, false),
        BIRCH_HALF_DRAWERS_1 = registerWoodenDrawerBlock("birch_half_drawers_1", 1, true),
        BIRCH_HALF_DRAWERS_2 = registerWoodenDrawerBlock("birch_half_drawers_2", 2, true),
        BIRCH_HALF_DRAWERS_4 = registerWoodenDrawerBlock("birch_half_drawers_4", 4, true),
        JUNGLE_FULL_DRAWERS_1 = registerWoodenDrawerBlock("jungle_full_drawers_1", 1, false),
        JUNGLE_FULL_DRAWERS_2 = registerWoodenDrawerBlock("jungle_full_drawers_2", 2, false),
        JUNGLE_FULL_DRAWERS_4 = registerWoodenDrawerBlock("jungle_full_drawers_4", 4, false),
        JUNGLE_HALF_DRAWERS_1 = registerWoodenDrawerBlock("jungle_half_drawers_1", 1, true),
        JUNGLE_HALF_DRAWERS_2 = registerWoodenDrawerBlock("jungle_half_drawers_2", 2, true),
        JUNGLE_HALF_DRAWERS_4 = registerWoodenDrawerBlock("jungle_half_drawers_4", 4, true),
        ACACIA_FULL_DRAWERS_1 = registerWoodenDrawerBlock("acacia_full_drawers_1", 1, false),
        ACACIA_FULL_DRAWERS_2 = registerWoodenDrawerBlock("acacia_full_drawers_2", 2, false),
        ACACIA_FULL_DRAWERS_4 = registerWoodenDrawerBlock("acacia_full_drawers_4", 4, false),
        ACACIA_HALF_DRAWERS_1 = registerWoodenDrawerBlock("acacia_half_drawers_1", 1, true),
        ACACIA_HALF_DRAWERS_2 = registerWoodenDrawerBlock("acacia_half_drawers_2", 2, true),
        ACACIA_HALF_DRAWERS_4 = registerWoodenDrawerBlock("acacia_half_drawers_4", 4, true),
        DARK_OAK_FULL_DRAWERS_1 = registerWoodenDrawerBlock("dark_oak_full_drawers_1", 1, false),
        DARK_OAK_FULL_DRAWERS_2 = registerWoodenDrawerBlock("dark_oak_full_drawers_2", 2, false),
        DARK_OAK_FULL_DRAWERS_4 = registerWoodenDrawerBlock("dark_oak_full_drawers_4", 4, false),
        DARK_OAK_HALF_DRAWERS_1 = registerWoodenDrawerBlock("dark_oak_half_drawers_1", 1, true),
        DARK_OAK_HALF_DRAWERS_2 = registerWoodenDrawerBlock("dark_oak_half_drawers_2", 2, true),
        DARK_OAK_HALF_DRAWERS_4 = registerWoodenDrawerBlock("dark_oak_half_drawers_4", 4, true),
        CRIMSON_FULL_DRAWERS_1 = registerWoodenDrawerBlock("crimson_full_drawers_1", 1, false),
        CRIMSON_FULL_DRAWERS_2 = registerWoodenDrawerBlock("crimson_full_drawers_2", 2, false),
        CRIMSON_FULL_DRAWERS_4 = registerWoodenDrawerBlock("crimson_full_drawers_4", 4, false),
        CRIMSON_HALF_DRAWERS_1 = registerWoodenDrawerBlock("crimson_half_drawers_1", 1, true),
        CRIMSON_HALF_DRAWERS_2 = registerWoodenDrawerBlock("crimson_half_drawers_2", 2, true),
        CRIMSON_HALF_DRAWERS_4 = registerWoodenDrawerBlock("crimson_half_drawers_4", 4, true),
        WARPED_FULL_DRAWERS_1 = registerWoodenDrawerBlock("warped_full_drawers_1", 1, false),
        WARPED_FULL_DRAWERS_2 = registerWoodenDrawerBlock("warped_full_drawers_2", 2, false),
        WARPED_FULL_DRAWERS_4 = registerWoodenDrawerBlock("warped_full_drawers_4", 4, false),
        WARPED_HALF_DRAWERS_1 = registerWoodenDrawerBlock("warped_half_drawers_1", 1, true),
        WARPED_HALF_DRAWERS_2 = registerWoodenDrawerBlock("warped_half_drawers_2", 2, true),
        WARPED_HALF_DRAWERS_4 = registerWoodenDrawerBlock("warped_half_drawers_4", 4, true),
        MANGROVE_FULL_DRAWERS_1 = registerWoodenDrawerBlock("mangrove_full_drawers_1", 1, false),
        MANGROVE_FULL_DRAWERS_2 = registerWoodenDrawerBlock("mangrove_full_drawers_2", 2, false),
        MANGROVE_FULL_DRAWERS_4 = registerWoodenDrawerBlock("mangrove_full_drawers_4", 4, false),
        MANGROVE_HALF_DRAWERS_1 = registerWoodenDrawerBlock("mangrove_half_drawers_1", 1, true),
        MANGROVE_HALF_DRAWERS_2 = registerWoodenDrawerBlock("mangrove_half_drawers_2", 2, true),
        MANGROVE_HALF_DRAWERS_4 = registerWoodenDrawerBlock("mangrove_half_drawers_4", 4, true);

    public static final RegistryObject<BlockCompDrawers> COMPACTING_DRAWERS_3 = registerCompactingDrawerBlock("compacting_drawers_3");

    public static final RegistryObject<BlockTrim>
        OAK_TRIM = registerTrimBlock("oak_trim"),
        SPRUCE_TRIM = registerTrimBlock("spruce_trim"),
        BIRCH_TRIM = registerTrimBlock("birch_trim"),
        JUNGLE_TRIM = registerTrimBlock("jungle_trim"),
        ACACIA_TRIM = registerTrimBlock("acacia_trim"),
        DARK_OAK_TRIM = registerTrimBlock("dark_oak_trim"),
        CRIMSON_TRIM = registerTrimBlock("crimson_trim"),
        WARPED_TRIM = registerTrimBlock("warped_trim"),
        MANGROVE_TRIM = registerTrimBlock("mangrove_trim");

    public static final RegistryObject<BlockController> CONTROLLER = registerControllerBlock("controller");
    public static final RegistryObject<BlockSlave> CONTROLLER_SLAVE = registerControllerSlaveBlock("controller_slave");

    private ModBlocks() {}

    private static RegistryObject<BlockStandardDrawers> registerWoodenDrawerBlock(String name, int drawerCount, boolean halfDepth) {
        return BLOCK_REGISTER.register(name, () -> new BlockStandardDrawers(drawerCount, halfDepth, getWoodenDrawerBlockProperties()));
    }

    private static RegistryObject<BlockCompDrawers> registerCompactingDrawerBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockCompDrawers(getStoneDrawerBlockProperties()));
    }

    private static RegistryObject<BlockTrim> registerTrimBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockTrim(getWoodenBlockProperties()));
    }

    private static RegistryObject<BlockController> registerControllerBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockController(getStoneBlockProperties()));
    }

    private static RegistryObject<BlockSlave> registerControllerSlaveBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockSlave(getStoneBlockProperties()));
    }

    private static Properties getWoodenBlockProperties() {
        return Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(3f, 4f);
    }

    private static Properties getWoodenDrawerBlockProperties() {
        return getWoodenBlockProperties().isSuffocating(ModBlocks::predFalse).isRedstoneConductor(ModBlocks::predFalse);
    }

    private static Properties getStoneBlockProperties() {
        return Properties.of(Material.STONE).sound(SoundType.STONE).strength(4f, 5f);
    }

    private static Properties getStoneDrawerBlockProperties() {
        return getStoneBlockProperties().isSuffocating(ModBlocks::predFalse).isRedstoneConductor(ModBlocks::predFalse);
    }

    public static void register(IEventBus bus) {
        BLOCK_REGISTER.register(bus);
    }

    private static <B extends Block> Stream<B> getBlocksOfType(Class<B> blockClass) {
        return ForgeRegistries.BLOCKS.getValues().stream().filter(blockClass::isInstance).map(blockClass::cast);
    }

    public static Stream<BlockDrawers> getDrawers() {
        return getBlocksOfType(BlockDrawers.class);
    }

    public static Stream<BlockController> getControllers() {
        return getBlocksOfType(BlockController.class);
    }

    public static Stream<BlockSlave> getControllerSlaves() {
        return getBlocksOfType(BlockSlave.class);
    }

    public static <BD extends BlockDrawers> Stream<BD> getDrawersOfType(Class<BD> drawerClass) {
        return getBlocksOfType(drawerClass);
    }

    public static <BD extends BlockDrawers> Stream<BD> getDrawersOfTypeAndSize(Class<BD> drawerClass, int size) {
        return getDrawersOfType(drawerClass).filter(blockDrawers -> blockDrawers.getDrawerCount() == size);
    }

    public static <BD extends BlockDrawers> Stream<BD> getDrawersOfTypeAndSizeAndDepth(Class<BD> drawerClass, int size, boolean halfDepth) {
        return getDrawersOfTypeAndSize(drawerClass, size).filter(blockDrawers -> blockDrawers.isHalfDepth() == halfDepth);
    }

    private static boolean predFalse (BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
