package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StorageDrawers.MOD_ID);

    private static final Set<RegistryObject<? extends BlockEntityType<? extends BlockEntityDrawers>>> BLOCK_ENTITY_TYPES_WITH_RENDERERS = new HashSet<>();

    public static final RegistryObject<BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_1 = registerDrawerBlockEntityType("standard_drawers_1", BlockEntityDrawersStandard.Slot1::new, BlockStandardDrawers.class, 1);
    public static final RegistryObject<BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_2 = registerDrawerBlockEntityType("standard_drawers_2", BlockEntityDrawersStandard.Slot2::new, BlockStandardDrawers.class, 2);
    public static final RegistryObject<BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_4 = registerDrawerBlockEntityType("standard_drawers_4", BlockEntityDrawersStandard.Slot4::new, BlockStandardDrawers.class, 4);
    public static final RegistryObject<BlockEntityType<BlockEntityDrawersComp>> FRACTIONAL_DRAWERS_2 = registerDrawerBlockEntityType("fractional_drawers_2", BlockEntityDrawersComp.Slot2::new, BlockCompDrawers.class, 2);
    public static final RegistryObject<BlockEntityType<BlockEntityDrawersComp>> FRACTIONAL_DRAWERS_3 = registerDrawerBlockEntityType("fractional_drawers_3", BlockEntityDrawersComp.Slot3::new, BlockCompDrawers.class, 3);
    public static final RegistryObject<BlockEntityType<BlockEntityController>> CONTROLLER = registerBlockEntityType("controller", BlockEntityController::new, ModBlocks.getControllers());
    public static final RegistryObject<BlockEntityType<BlockEntitySlave>> CONTROLLER_SLAVE = registerBlockEntityType("controller_slave", BlockEntitySlave::new, ModBlocks.getControllerSlaves());
    public static final RegistryObject<BlockEntityType<BlockEntityFramingTable>> FRAMING_TABLE = registerBlockEntityType("framing_table", BlockEntityFramingTable::new, ModBlocks.FRAMING_TABLE);
    public static final RegistryObject<BlockEntityType<BlockEntityTrim>> TRIM = registerTrimBlockEntityType("trim", BlockEntityTrim::new);

    private ModBlockEntities() {}

    private static <BE extends BlockEntityDrawers, B extends BlockDrawers> RegistryObject<BlockEntityType<BE>> registerDrawerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, Class<B> drawerBlockClass, int size) {
        RegistryObject<BlockEntityType<BE>> ro = registerBlockEntityType(name, blockEntitySupplier, ModBlocks.getDrawersOfTypeAndSize(drawerBlockClass, size));
        BLOCK_ENTITY_TYPES_WITH_RENDERERS.add(ro);
        return ro;
    }

    private static <BE extends BlockEntityTrim> RegistryObject<BlockEntityType<BE>> registerTrimBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier) {
        return registerBlockEntityType(name, blockEntitySupplier, ModBlocks.getFramedTrim());
    }

    private static <BE extends BaseBlockEntity, B extends Block> RegistryObject<BlockEntityType<BE>> registerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, Stream<B> blockStream) {
        return BLOCK_ENTITY_REGISTER.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, blockStream.toArray(Block[]::new)).build(null));
    }

    private static <BE extends BaseBlockEntity, B extends Block> RegistryObject<BlockEntityType<BE>> registerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, RegistryObject<B> block) {
        return BLOCK_ENTITY_REGISTER.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, block.get()).build(null));
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTER.register(bus);
    }

    public static Set<RegistryObject<? extends BlockEntityType<? extends BlockEntityDrawers>>> getBlockEntityTypesWithRenderers() {
        return Collections.unmodifiableSet(BLOCK_ENTITY_TYPES_WITH_RENDERERS);
    }
}
