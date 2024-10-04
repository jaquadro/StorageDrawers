package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.framed.*;
import com.jaquadro.minecraft.storagedrawers.block.meta.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, StorageDrawers.MOD_ID);

    public static final List<String> EXCLUDE_ITEMS = new ArrayList<>();

    public static final RegistryObject<BlockStandardDrawers>
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

    public static final RegistryObject<BlockFramedStandardDrawers>
        FRAMED_FULL_DRAWERS_1 = registerFramedDrawerBlock("framed_full_drawers_1", 1, false),
        FRAMED_FULL_DRAWERS_2 = registerFramedDrawerBlock("framed_full_drawers_2", 2, false),
        FRAMED_FULL_DRAWERS_4 = registerFramedDrawerBlock("framed_full_drawers_4", 4, false),
        FRAMED_HALF_DRAWERS_1 = registerFramedDrawerBlock("framed_half_drawers_1", 1, true),
        FRAMED_HALF_DRAWERS_2 = registerFramedDrawerBlock("framed_half_drawers_2", 2, true),
        FRAMED_HALF_DRAWERS_4 = registerFramedDrawerBlock("framed_half_drawers_4", 4, true);

    public static final RegistryObject<BlockCompDrawers> COMPACTING_DRAWERS_2 = registerCompactingDrawerBlock("compacting_drawers_2", 2, false);
    public static final RegistryObject<BlockCompDrawers> COMPACTING_DRAWERS_3 = registerCompactingDrawerBlock("compacting_drawers_3", 3, false);
    public static final RegistryObject<BlockCompDrawers> COMPACTING_HALF_DRAWERS_2 = registerCompactingDrawerBlock("compacting_half_drawers_2", 2, true);
    public static final RegistryObject<BlockCompDrawers> COMPACTING_HALF_DRAWERS_3 = registerCompactingDrawerBlock("compacting_half_drawers_3", 3, true);

    public static final RegistryObject<BlockFramedCompDrawers> FRAMED_COMPACTING_DRAWERS_2 = registerFramedCompactingDrawerBlock("framed_compacting_drawers_2", 2, false);
    public static final RegistryObject<BlockFramedCompDrawers> FRAMED_COMPACTING_DRAWERS_3 = registerFramedCompactingDrawerBlock("framed_compacting_drawers_3", 3, false);
    public static final RegistryObject<BlockFramedCompDrawers> FRAMED_COMPACTING_HALF_DRAWERS_2 = registerFramedCompactingDrawerBlock("framed_compacting_half_drawers_2", 2, true);
    public static final RegistryObject<BlockFramedCompDrawers> FRAMED_COMPACTING_HALF_DRAWERS_3 = registerFramedCompactingDrawerBlock("framed_compacting_half_drawers_3", 3, true);

    public static final RegistryObject<BlockTrim>
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

    public static final RegistryObject<BlockFramedTrim> FRAMED_TRIM = registerFramedTrimBlock("framed_trim");

    public static final RegistryObject<BlockController> CONTROLLER = registerControllerBlock("controller");
    public static final RegistryObject<BlockSlave> CONTROLLER_SLAVE = registerControllerSlaveBlock("controller_slave");

    public static final RegistryObject<BlockFramedController> FRAMED_CONTROLLER = BLOCK_REGISTER.register("framed_controller",
        () -> new BlockFramedController(getStoneBlockProperties()));
    public static final RegistryObject<BlockFramedControllerIO> FRAMED_CONTROLLER_IO = BLOCK_REGISTER.register("framed_controller_io",
        () -> new BlockFramedControllerIO(getStoneBlockProperties()));

    public static final RegistryObject<BlockFramingTable> FRAMING_TABLE = registerFramingTableBlock("framing_table");

    public static final RegistryObject<BlockMeta>
        META_LOCKED = registerMetaFacingSizedBlock("meta_locked"),
        META_CLAIMED = registerMetaFacingSizedBlock("meta_claimed"),
        META_LOCKED_CLAIMED = registerMetaFacingSizedBlock("meta_locked_claimed"),
        META_VOID = registerMetaFacingSizedBlock("meta_void"),
        META_SHROUD = registerMetaFacingSizedBlock("meta_shroud"),
        META_INDICATOR = registerMetaFacingSizedSlottedBlock("meta_indicator"),
        META_COMP_INDICATOR = registerMetaFacingSizedSlottedBlock("meta_comp_indicator"),
        META_PRIORITY_P1 = registerMetaFacingSizedBlock("meta_priority_p1"),
        META_PRIORITY_P2 = registerMetaFacingSizedBlock("meta_priority_p2"),
        META_PRIORITY_N1 = registerMetaFacingSizedBlock("meta_priority_n1"),
        META_PRIORITY_N2 = registerMetaFacingSizedBlock("meta_priority_n2"),
        META_MISSING_1_1 = registerMetaFacingSizedBlock("meta_missing_slot_1_1"),
        META_MISSING_2_1 = registerMetaFacingSizedBlock("meta_missing_slot_2_1"),
        META_MISSING_2_2 = registerMetaFacingSizedBlock("meta_missing_slot_2_2"),
        META_MISSING_4_1 = registerMetaFacingSizedBlock("meta_missing_slot_4_1"),
        META_MISSING_4_2 = registerMetaFacingSizedBlock("meta_missing_slot_4_2"),
        META_MISSING_4_3 = registerMetaFacingSizedBlock("meta_missing_slot_4_3"),
        META_MISSING_4_4 = registerMetaFacingSizedBlock("meta_missing_slot_4_4"),
        META_FRAMED_DRAWERS_SIDE = registerMetaFacingSizedSlottedBlock("meta_framed_drawers_side"),
        META_FRAMED_DRAWERS_TRIM = registerMetaFacingSizedSlottedBlock("meta_framed_drawers_trim"),
        META_FRAMED_DRAWERS_FRONT = registerMetaFacingSizedSlottedBlock("meta_framed_drawers_front"),
        META_FRAMED_DRAWERS_SHADING = registerMetaFacingSizedSlottedBlock("meta_framed_drawers_shading"),
        META_FRAMED_TRIM_SIDE = registerMetaBlock("meta_framed_trim_side"),
        META_FRAMED_TRIM_TRIM = registerMetaBlock("meta_framed_trim_trim"),
        META_FRAMED_CONTROLLER_SIDE = registerMetaFacingBlock("meta_framed_controller_side"),
        META_FRAMED_CONTROLLER_TRIM = registerMetaFacingBlock("meta_framed_controller_trim"),
        META_FRAMED_CONTROLLER_FRONT = registerMetaFacingBlock("meta_framed_controller_front"),
        META_FRAMED_CONTROLLER_SHADING = registerMetaFacingBlock("meta_framed_controller_shading"),
        META_FRAMED_CONTROLLER_IO_SIDE = registerMetaBlock("meta_framed_controller_io_side"),
        META_FRAMED_CONTROLLER_IO_TRIM = registerMetaBlock("meta_framed_controller_io_trim"),
        META_FRAMED_CONTROLLER_IO_SHADING = registerMetaBlock("meta_framed_controller_io_shading"),
        META_FRAMED_COMPDRAWERS_2_SIDE = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_2_side"),
        META_FRAMED_COMPDRAWERS_2_TRIM = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_2_trim"),
        META_FRAMED_COMPDRAWERS_2_FRONT = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_2_front"),
        META_FRAMED_COMPDRAWERS_2_SHADING = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_2_shading"),
        META_FRAMED_COMPDRAWERS_3_SIDE = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_3_side"),
        META_FRAMED_COMPDRAWERS_3_TRIM = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_3_trim"),
        META_FRAMED_COMPDRAWERS_3_FRONT = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_3_front"),
        META_FRAMED_COMPDRAWERS_3_SHADING = registerMetaFacingSizedOpenBlock("meta_framed_compdrawers_3_shading");

    public static final RegistryObject<BlockKeyButton>
        KEYBUTTON_DRAWER = BLOCK_REGISTER.register("keybutton_drawer",
            () -> new BlockKeyButton(Properties.of().sound(SoundType.STONE), EnumKeyType.DRAWER)),
        KEYBUTTON_QUANTIFY = BLOCK_REGISTER.register("keybutton_quantify",
            () -> new BlockKeyButton(Properties.of().sound(SoundType.STONE), EnumKeyType.QUANTIFY)),
        KEYBUTTON_CONCEALMENT = BLOCK_REGISTER.register("keybutton_concealment",
            () -> new BlockKeyButton(Properties.of().sound(SoundType.STONE), EnumKeyType.CONCEALMENT));

    private ModBlocks() {}

    static ResourceLocation modLoc (String name) {
        return new ResourceLocation(StorageDrawers.MOD_ID, name);
    }

    static RegistryObject<BlockStandardDrawers> registerWoodenDrawerBlock(ResourceLocation name, int drawerCount, boolean halfDepth) {
        return registerWoodenDrawerBlock(BLOCK_REGISTER, name, drawerCount, halfDepth);
    }

    static RegistryObject<BlockStandardDrawers> registerWoodenDrawerBlock(DeferredRegister<Block> register, String name, int drawerCount, boolean halfDepth) {
        return register.register(name, () -> new BlockStandardDrawers(drawerCount, halfDepth, getWoodenDrawerBlockProperties()));
    }

    static RegistryObject<BlockStandardDrawers> registerWoodenDrawerBlock(DeferredRegister<Block> register, ResourceLocation material, int drawerCount, boolean halfDepth) {
        String name = material.getPath() + (halfDepth ? "_half_drawers_" : "_full_drawers_") + drawerCount;
        return register.register(name, () -> new BlockStandardDrawers(drawerCount, halfDepth, getWoodenDrawerBlockProperties()).setMatKey(material));
    }

    static RegistryObject<BlockFramedStandardDrawers> registerFramedDrawerBlock(String name, int drawerCount, boolean halfDepth) {
        return BLOCK_REGISTER.register(name, () -> (BlockFramedStandardDrawers)new BlockFramedStandardDrawers(drawerCount, halfDepth, getWoodenDrawerBlockProperties()).setMatKey("framed"));
    }

    static RegistryObject<BlockCompDrawers> registerCompactingDrawerBlock(String name, int drawerCount, boolean halfDepth) {
        return BLOCK_REGISTER.register(name, () -> new BlockCompDrawers(drawerCount, halfDepth, getStoneDrawerBlockProperties()));
    }

    static RegistryObject<BlockFramedCompDrawers> registerFramedCompactingDrawerBlock(String name, int drawerCount, boolean halfDepth) {
        return BLOCK_REGISTER.register(name, () -> new BlockFramedCompDrawers(drawerCount, halfDepth, getStoneDrawerBlockProperties()));
    }

    static RegistryObject<BlockTrim> registerTrimBlock(ResourceLocation name) {
        return registerTrimBlock(BLOCK_REGISTER, name);
    }

    static RegistryObject<BlockTrim> registerTrimBlock(DeferredRegister<Block> register, String name) {
        return register.register(name, () -> new BlockTrim(getWoodenBlockProperties()));
    }

    static RegistryObject<BlockTrim> registerTrimBlock(DeferredRegister<Block> register, ResourceLocation material) {
        String name = material.getPath() + "_trim";
        return register.register(name, () -> new BlockTrim(getWoodenBlockProperties()).setMatKey(material));
    }

    static RegistryObject<BlockFramedTrim> registerFramedTrimBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> (BlockFramedTrim)new BlockFramedTrim(getWoodenDrawerBlockProperties()).setMatKey("framed"));
    }

    static RegistryObject<BlockController> registerControllerBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockController(getStoneBlockProperties()));
    }

    static RegistryObject<BlockSlave> registerControllerSlaveBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockSlave(getStoneBlockProperties()));
    }

    static RegistryObject<BlockFramingTable> registerFramingTableBlock(String name) {
        return BLOCK_REGISTER.register(name, () -> new BlockFramingTable(getWoodenBlockProperties()));
    }

    static RegistryObject<BlockMeta> registerMetaBlock (String name) {
        EXCLUDE_ITEMS.add(name);
        return BLOCK_REGISTER.register(name, () -> new BlockMeta(Properties.of().air()));
    }

    static RegistryObject<BlockMeta> registerMetaFacingBlock (String name) {
        EXCLUDE_ITEMS.add(name);
        return BLOCK_REGISTER.register(name, () -> new BlockMetaFacing(Properties.of().air()));
    }

    static RegistryObject<BlockMeta> registerMetaFacingSizedBlock (String name) {
        EXCLUDE_ITEMS.add(name);
        return BLOCK_REGISTER.register(name, () -> new BlockMetaFacingSized(Properties.of().air()));
    }

    static RegistryObject<BlockMeta> registerMetaFacingSizedSlottedBlock (String name) {
        EXCLUDE_ITEMS.add(name);
        return BLOCK_REGISTER.register(name, () -> new BlockMetaFacingSizedSlotted(Properties.of().air()));
    }

    static RegistryObject<BlockMeta> registerMetaFacingSizedOpenBlock (String name) {
        EXCLUDE_ITEMS.add(name);
        return BLOCK_REGISTER.register(name, () -> new BlockMetaFacingSizedOpen(Properties.of().air()));
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

    public static void register(IEventBus bus) {
        BLOCK_REGISTER.register(bus);
    }

    private static <B extends Block> Stream<B> getBlocksOfType(Class<B> blockClass) {
        return ForgeRegistries.BLOCKS.getValues().stream().filter(blockClass::isInstance).map(blockClass::cast);
    }

    public static Stream<BlockDrawers> getDrawers() {
        return getBlocksOfType(BlockDrawers.class);
    }

    public static Stream<BlockFramedStandardDrawers> getFramedDrawers() {
        return getBlocksOfType(BlockFramedStandardDrawers.class);
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

    public static Stream<BlockFramedTrim> getFramedTrim() {
        return getBlocksOfType(BlockFramedTrim.class);
    }

    public static Stream<IFramedBlock> getFramedBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream().filter(IFramedBlock.class::isInstance).map(IFramedBlock.class::cast);
    }

    private static boolean predFalse (BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
