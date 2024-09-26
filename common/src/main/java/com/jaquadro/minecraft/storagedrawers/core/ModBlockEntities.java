package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.*;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ModBlockEntities {
    public static final ChameleonRegistry<BlockEntityType<?>> BLOCK_ENTITIES = ChameleonServices.REGISTRY.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModConstants.MOD_ID);

    private static final Set<RegistryEntry<? extends BlockEntityType<? extends BlockEntityDrawers>>> BLOCK_ENTITY_TYPES_WITH_RENDERERS = new HashSet<>();
    private static final Set<RegistryEntry<? extends BlockEntityType<? extends BlockEntityDrawers>>> DRAWER_TYPES = new HashSet<>();

    public static final RegistryEntry<BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_1 = registerDrawerBlockEntityType("standard_drawers_1", ModServices.RESOURCE_FACTORY.createBlockEntityDrawersStandard(1), BlockStandardDrawers.class, 1);
    public static final RegistryEntry<BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_2 = registerDrawerBlockEntityType("standard_drawers_2", ModServices.RESOURCE_FACTORY.createBlockEntityDrawersStandard(2), BlockStandardDrawers.class, 2);
    public static final RegistryEntry<BlockEntityType<BlockEntityDrawersStandard>> STANDARD_DRAWERS_4 = registerDrawerBlockEntityType("standard_drawers_4", ModServices.RESOURCE_FACTORY.createBlockEntityDrawersStandard(4), BlockStandardDrawers.class, 4);
    public static final RegistryEntry<BlockEntityType<BlockEntityDrawersComp>> FRACTIONAL_DRAWERS_2 = registerDrawerBlockEntityType("fractional_drawers_2", ModServices.RESOURCE_FACTORY.createBlockEntityDrawersComp(2), BlockCompDrawers.class, 2);
    public static final RegistryEntry<BlockEntityType<BlockEntityDrawersComp>> FRACTIONAL_DRAWERS_3 = registerDrawerBlockEntityType("fractional_drawers_3", ModServices.RESOURCE_FACTORY.createBlockEntityDrawersComp(3), BlockCompDrawers.class, 3);

    public static final RegistryEntry<BlockEntityType<BlockEntityController>> CONTROLLER = BLOCK_ENTITIES.register("controller", () ->
        BlockEntityType.Builder.of(ModServices.RESOURCE_FACTORY.createBlockEntityController(), ModBlocks.CONTROLLER.get()).build(null));

    public static final RegistryEntry<BlockEntityType<BlockEntityControllerIO>> CONTROLLER_IO = BLOCK_ENTITIES.register("controller_io", () ->
        BlockEntityType.Builder.of(ModServices.RESOURCE_FACTORY.createBlockEntityControllerIO(), ModBlocks.CONTROLLER_IO.get()).build(null));

    private ModBlockEntities() {}

    private static <BE extends BlockEntityDrawers, B extends BlockDrawers> RegistryEntry<BlockEntityType<BE>> registerDrawerBlockEntityType(String name, BlockEntityType.BlockEntitySupplier<BE> blockEntitySupplier, Class<B> drawerBlockClass, int size) {
        RegistryEntry<BlockEntityType<BE>> ro = registerBlockEntityType(name, blockEntitySupplier, drawerBlockClass, size);
        BLOCK_ENTITY_TYPES_WITH_RENDERERS.add(ro);
        DRAWER_TYPES.add(ro);
        return ro;
    }

    private static <BE extends BaseBlockEntity, B extends BlockDrawers> RegistryEntry<BlockEntityType<BE>> registerBlockEntityType(String name, BlockEntityType.BlockEntitySupplier<BE> blockEntitySupplier, Class<B> drawerBlockClass, int size) {
        return BLOCK_ENTITIES.register(name, () ->
            BlockEntityType.Builder.of(blockEntitySupplier, ModBlocks.getDrawersOfTypeAndSize(drawerBlockClass, size).toArray(Block[]::new)).build(null));
    }

    public static void init() {
        BLOCK_ENTITIES.init();
    }

    public static Set<RegistryEntry<? extends BlockEntityType<? extends BlockEntityDrawers>>> getBlockEntityTypesWithRenderers() {
        return Collections.unmodifiableSet(BLOCK_ENTITY_TYPES_WITH_RENDERERS);
    }

    public static Stream<BlockEntityType<? extends BlockEntityDrawers>> getDrawerTypes() {
        return DRAWER_TYPES.stream().map(RegistryEntry::get);
    }
}
