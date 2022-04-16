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
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, StorageDrawers.MOD_ID);

    private static final Set<RegistryObject<? extends BlockEntityType<? extends TileEntityDrawers>>> BLOCK_ENTITY_TYPES_WITH_RENDERERS = new HashSet<>();

    public static final RegistryObject<BlockEntityType<TileEntityDrawersStandard>> STANDARD_DRAWERS_1 = registerDrawerBlockEntityType("standard_drawers_1", TileEntityDrawersStandard.Slot1::new, BlockStandardDrawers.class, 1);
    public static final RegistryObject<BlockEntityType<TileEntityDrawersStandard>> STANDARD_DRAWERS_2 = registerDrawerBlockEntityType("standard_drawers_2", TileEntityDrawersStandard.Slot2::new, BlockStandardDrawers.class, 2);
    public static final RegistryObject<BlockEntityType<TileEntityDrawersStandard>> STANDARD_DRAWERS_4 = registerDrawerBlockEntityType("standard_drawers_4", TileEntityDrawersStandard.Slot4::new, BlockStandardDrawers.class, 4);
    public static final RegistryObject<BlockEntityType<TileEntityDrawersComp>> FRACTIONAL_DRAWERS_3 = registerDrawerBlockEntityType("fractional_drawers_3", TileEntityDrawersComp.Slot3::new, BlockCompDrawers.class, 3);
    public static final RegistryObject<BlockEntityType<TileEntityController>> CONTROLLER = registerBlockEntityType("controller", TileEntityController::new, BlockController.class);
    public static final RegistryObject<BlockEntityType<TileEntitySlave>> CONTROLLER_SLAVE = registerBlockEntityType("controller_slave", TileEntitySlave::new, BlockSlave.class);

    private ModBlockEntities() {}

    private static <BE extends TileEntityDrawers, B extends BlockDrawers> RegistryObject<BlockEntityType<BE>> registerDrawerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, Class<B> drawerBlockClass, int size) {
        RegistryObject<BlockEntityType<BE>> ro = registerBlockEntityType(name, blockEntitySupplier, ModBlocks.getDrawersOfTypeAndSize(drawerBlockClass, size));
        BLOCK_ENTITY_TYPES_WITH_RENDERERS.add(ro);
        return ro;
    }

    private static <BE extends ChamTileEntity, B extends Block> RegistryObject<BlockEntityType<BE>> registerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, Class<B> blockClass) {
        return registerBlockEntityType(name, blockEntitySupplier, ModBlocks.getBlocksOfType(blockClass));
    }

    private static <BE extends ChamTileEntity, B extends Block> RegistryObject<BlockEntityType<BE>> registerBlockEntityType(String name, BlockEntitySupplier<BE> blockEntitySupplier, Stream<B> blockStream) {
        return BLOCK_ENTITY_REGISTER.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, blockStream.toArray(Block[]::new)).build(null));
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTER.register(bus);
    }

    public static Set<RegistryObject<? extends BlockEntityType<? extends TileEntityDrawers>>> getBlockEntityTypesWithRenderers() {
        return Collections.unmodifiableSet(BLOCK_ENTITY_TYPES_WITH_RENDERERS);
    }
}
