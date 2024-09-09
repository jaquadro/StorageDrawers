package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, StorageDrawers.MOD_ID);

    private static final Set<DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<? extends BlockEntityDrawers>>> BLOCK_ENTITY_TYPES_WITH_RENDERERS = new HashSet<>();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_1 = registerDrawerBlockEntityType("standard_drawers_1", BlockEntityDrawersStandard.Slot1::new, BlockStandardDrawers.class, 1);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_2 = registerDrawerBlockEntityType("standard_drawers_2", BlockEntityDrawersStandard.Slot2::new, BlockStandardDrawers.class, 2);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_4 = registerDrawerBlockEntityType("standard_drawers_4", BlockEntityDrawersStandard.Slot4::new, BlockStandardDrawers.class, 4);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityDrawersComp>> FRACTIONAL_DRAWERS_3 = registerDrawerBlockEntityType("fractional_drawers_3", BlockEntityDrawersComp.Slot3::new, BlockCompDrawers.class, 3);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityController>> CONTROLLER = BLOCK_ENTITY_REGISTER.register("controller", () ->
        BlockEntityType.Builder.of(BlockEntityController::new, ModBlocks.CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityControllerIO>> CONTROLLER_IO = BLOCK_ENTITY_REGISTER.register("controller_io", () ->
        BlockEntityType.Builder.of(BlockEntityControllerIO::new, ModBlocks.CONTROLLER_IO.get()).build(null));

    private ModBlockEntities() {}

    private static <BE extends BlockEntityDrawers, B extends BlockDrawers> DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> registerDrawerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, Class<B> drawerBlockClass, int size) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> ro = registerBlockEntityType(name, blockEntitySupplier, drawerBlockClass, size);
        BLOCK_ENTITY_TYPES_WITH_RENDERERS.add(ro);
        return ro;
    }

    private static <BE extends BaseBlockEntity, B extends BlockDrawers> DeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> registerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, Class<B> drawerBlockClass, int size) {
        return BLOCK_ENTITY_REGISTER.register(name, () ->
            BlockEntityType.Builder.of(blockEntitySupplier, ModBlocks.getDrawersOfTypeAndSize(drawerBlockClass, size).toArray(Block[]::new)).build(null));
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTER.register(bus);
    }

    public static Set<DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<? extends BlockEntityDrawers>>> getBlockEntityTypesWithRenderers() {
        return Collections.unmodifiableSet(BLOCK_ENTITY_TYPES_WITH_RENDERERS);
    }
}
