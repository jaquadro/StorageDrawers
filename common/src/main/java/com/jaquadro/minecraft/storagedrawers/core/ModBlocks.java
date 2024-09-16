package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMeta;
import com.jaquadro.minecraft.storagedrawers.block.meta.BlockMetaSized;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ModBlocks
{
    public static final ChameleonRegistry<Block> BLOCKS = ChameleonServices.REGISTRY.create(BuiltInRegistries.BLOCK, ModConstants.MOD_ID);
    //public static final DeferredRegister.Blocks BLOCK_REGISTER = DeferredRegister.createBlocks(StorageDrawers.MOD_ID);

    public static final List<String> EXCLUDE_ITEMS = new ArrayList<>();

    public static final RegistryEntry<BlockStandardDrawers>
        OAK_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("oak"), 1, false),
        OAK_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("oak"), 2, false),
        OAK_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("oak"), 4, false),
        OAK_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("oak"), 1, true),
        OAK_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("oak"), 2, true),
        OAK_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("oak"), 4, true),
        SPRUCE_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("spruce"), 1, false),
        SPRUCE_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("spruce"), 2, false),
        SPRUCE_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("spruce"), 4, false),
        SPRUCE_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("spruce"), 1, true),
        SPRUCE_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("spruce"), 2, true),
        SPRUCE_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("spruce"), 4, true),
        BIRCH_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("birch"), 1, false),
        BIRCH_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("birch"), 2, false),
        BIRCH_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("birch"), 4, false),
        BIRCH_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("birch"), 1, true),
        BIRCH_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("birch"), 2, true),
        BIRCH_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("birch"), 4, true),
        JUNGLE_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("jungle"), 1, false),
        JUNGLE_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("jungle"), 2, false),
        JUNGLE_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("jungle"), 4, false),
        JUNGLE_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("jungle"), 1, true),
        JUNGLE_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("jungle"), 2, true),
        JUNGLE_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("jungle"), 4, true),
        ACACIA_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("acacia"), 1, false),
        ACACIA_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("acacia"), 2, false),
        ACACIA_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("acacia"), 4, false),
        ACACIA_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("acacia"), 1, true),
        ACACIA_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("acacia"), 2, true),
        ACACIA_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("acacia"), 4, true),
        DARK_OAK_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("dark_oak"), 1, false),
        DARK_OAK_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("dark_oak"), 2, false),
        DARK_OAK_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("dark_oak"), 4, false),
        DARK_OAK_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("dark_oak"), 1, true),
        DARK_OAK_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("dark_oak"), 2, true),
        DARK_OAK_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("dark_oak"), 4, true),
        MANGROVE_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("mangrove"), 1, false),
        MANGROVE_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("mangrove"), 2, false),
        MANGROVE_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("mangrove"), 4, false),
        MANGROVE_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("mangrove"), 1, true),
        MANGROVE_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("mangrove"), 2, true),
        MANGROVE_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("mangrove"), 4, true),
        CHERRY_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("cherry"), 1, false),
        CHERRY_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("cherry"), 2, false),
        CHERRY_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("cherry"), 4, false),
        CHERRY_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("cherry"), 1, true),
        CHERRY_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("cherry"), 2, true),
        CHERRY_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("cherry"), 4, true),
        BAMBOO_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("bamboo"), 1, false),
        BAMBOO_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("bamboo"), 2, false),
        BAMBOO_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("bamboo"), 4, false),
        BAMBOO_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("bamboo"), 1, true),
        BAMBOO_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("bamboo"), 2, true),
        BAMBOO_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("bamboo"), 4, true),
        CRIMSON_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("crimson"), 1, false),
        CRIMSON_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("crimson"), 2, false),
        CRIMSON_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("crimson"), 4, false),
        CRIMSON_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("crimson"), 1, true),
        CRIMSON_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("crimson"), 2, true),
        CRIMSON_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("crimson"), 4, true),
        WARPED_FULL_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("warped"), 1, false),
        WARPED_FULL_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("warped"), 2, false),
        WARPED_FULL_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("warped"), 4, false),
        WARPED_HALF_DRAWERS_1 = registerWoodenDrawerBlock(modLoc("warped"), 1, true),
        WARPED_HALF_DRAWERS_2 = registerWoodenDrawerBlock(modLoc("warped"), 2, true),
        WARPED_HALF_DRAWERS_4 = registerWoodenDrawerBlock(modLoc("warped"), 4, true);

    public static final RegistryEntry<BlockCompDrawers> COMPACTING_DRAWERS_3 = registerCompactingDrawerBlock("compacting_drawers_3");

    public static final RegistryEntry<BlockTrim>
        OAK_TRIM = registerTrimBlock(modLoc("oak")),
        SPRUCE_TRIM = registerTrimBlock(modLoc("spruce")),
        BIRCH_TRIM = registerTrimBlock(modLoc("birch")),
        JUNGLE_TRIM = registerTrimBlock(modLoc("jungle")),
        ACACIA_TRIM = registerTrimBlock(modLoc("acacia")),
        DARK_OAK_TRIM = registerTrimBlock(modLoc("dark_oak")),
        MANGROVE_TRIM = registerTrimBlock(modLoc("mangrove")),
        CHERRY_TRIM = registerTrimBlock(modLoc("cherry")),
        BAMBOO_TRIM = registerTrimBlock(modLoc("bamboo")),
        CRIMSON_TRIM = registerTrimBlock(modLoc("crimson")),
        WARPED_TRIM = registerTrimBlock(modLoc("warped"));

    public static final RegistryEntry<BlockController>
        CONTROLLER = registerControllerBlock("controller");

    public static final RegistryEntry<BlockControllerIO>
        CONTROLLER_IO = registerControllerIOBlock("controller_io");

    public static final RegistryEntry<BlockMeta>
        META_LOCKED = registerMetaBlock("meta_locked"),
        META_VOID = registerMetaBlock("meta_void"),
        META_SHROUD = registerMetaBlock("meta_shroud"),
        META_INDICATOR = registerSizedMetaBlock("meta_indicator"),
        META_COMP_INDICATOR = registerMetaBlock("meta_comp_indicator");

    public static final RegistryEntry<BlockKeyButton>
        KEYBUTTON_DRAWER = BLOCKS.register("keybutton_drawer",
        () -> new BlockKeyButton(KeyType.DRAWER, Properties.of().sound(SoundType.STONE))),
        KEYBUTTON_QUANTIFY = BLOCKS.register("keybutton_quantify",
            () -> new BlockKeyButton(KeyType.QUANTIFY, Properties.of().sound(SoundType.STONE))),
        KEYBUTTON_CONCEALMENT = BLOCKS.register("keybutton_concealment",
            () -> new BlockKeyButton(KeyType.CONCEALMENT, Properties.of().sound(SoundType.STONE)));

    private ModBlocks() {}

    static ResourceLocation modLoc (String name) {
        return ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name);
    }

    static RegistryEntry<BlockStandardDrawers> registerWoodenDrawerBlock(ResourceLocation name, int drawerCount, boolean halfDepth) {
        return registerWoodenDrawerBlock(BLOCKS, name, drawerCount, halfDepth);
    }

    static RegistryEntry<BlockStandardDrawers> registerWoodenDrawerBlock(ChameleonRegistry<Block> register, String name, int drawerCount, boolean halfDepth) {
        return register.register(name, () -> new BlockStandardDrawers(drawerCount, halfDepth, getWoodenDrawerBlockProperties()));
    }

    static RegistryEntry<BlockStandardDrawers> registerWoodenDrawerBlock(ChameleonRegistry<Block> register, ResourceLocation material, int drawerCount, boolean halfDepth) {
        String name = material.getPath() + (halfDepth ? "_half_drawers_" : "_full_drawers_") + drawerCount;
        return register.register(name, () -> new BlockStandardDrawers(drawerCount, halfDepth, getWoodenDrawerBlockProperties()).setMatKey(material));
    }

    static RegistryEntry<BlockCompDrawers> registerCompactingDrawerBlock(String name) {
        return BLOCKS.register(name, () -> new BlockCompDrawers(getStoneDrawerBlockProperties()));
    }

    static RegistryEntry<BlockTrim> registerTrimBlock(ResourceLocation name) {
        return registerTrimBlock(BLOCKS, name);
    }

    static RegistryEntry<BlockTrim> registerTrimBlock(ChameleonRegistry<Block> register, String name) {
        return register.register(name, () -> new BlockTrim(getWoodenBlockProperties()));
    }

    static RegistryEntry<BlockTrim> registerTrimBlock(ChameleonRegistry<Block> register, ResourceLocation material) {
        String name = material.getPath() + "_trim";
        return register.register(name, () -> new BlockTrim(getWoodenBlockProperties()).setMatKey(material));
    }

    static RegistryEntry<BlockController> registerControllerBlock(String name) {
        return BLOCKS.register(name, () -> new BlockController(getStoneBlockProperties()));
    }

    static RegistryEntry<BlockControllerIO> registerControllerIOBlock (String name) {
        return BLOCKS.register(name, () -> new BlockControllerIO(getStoneBlockProperties()));
    }

    static RegistryEntry<BlockMeta> registerMetaBlock(String name) {
        EXCLUDE_ITEMS.add(name);
        return BLOCKS.register(name, () -> new BlockMeta(Properties.of().air()));
    }

    static RegistryEntry<BlockMeta> registerSizedMetaBlock(String name) {
        EXCLUDE_ITEMS.add(name);
        return BLOCKS.register(name, () -> new BlockMetaSized(Properties.of().air()));
    }

    static Properties getWoodenBlockProperties() {
        return Properties.of().sound(SoundType.WOOD).strength(3f, 4f);
    }

    static Properties getWoodenDrawerBlockProperties() {
        return getWoodenBlockProperties().isSuffocating(ModBlocks::predFalse).isRedstoneConductor(ModBlocks::predFalse);
    }

    static Properties getStoneBlockProperties() {
        return Properties.of().sound(SoundType.STONE).strength(4f, 5f);
    }

    static Properties getStoneDrawerBlockProperties() {
        return getStoneBlockProperties().isSuffocating(ModBlocks::predFalse).isRedstoneConductor(ModBlocks::predFalse);
    }

    public static void init() {
        BLOCKS.init();
    }

    private static <B extends Block> Stream<B> getBlocksOfType(Class<B> blockClass) {
        return BLOCKS.getEntries().stream().map(RegistryEntry::get).filter(blockClass::isInstance).map(blockClass::cast);
    }

    public static Stream<BlockDrawers> getDrawers() {
        return getBlocksOfType(BlockDrawers.class);
    }

    public static Stream<BlockController> getControllers() {
        return getBlocksOfType(BlockController.class);
    }

    public static Stream<BlockControllerIO> getControllerIOs () {
        return getBlocksOfType(BlockControllerIO.class);
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
