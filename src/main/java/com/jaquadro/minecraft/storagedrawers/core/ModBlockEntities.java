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

import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ModBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, StorageDrawers.MOD_ID);

    public static final RegistryObject<BlockEntityType<TileEntityDrawersStandard>> STANDARD_DRAWERS_1 = BLOCK_ENTITY_REGISTER.register("standard_drawers_1",
            drawerBlockEntitySupplier(TileEntityDrawersStandard.Slot1::new, BlockStandardDrawers.class, 1));
    public static final RegistryObject<BlockEntityType<TileEntityDrawersStandard>> STANDARD_DRAWERS_2 = BLOCK_ENTITY_REGISTER.register("standard_drawers_2",
            drawerBlockEntitySupplier(TileEntityDrawersStandard.Slot2::new, BlockStandardDrawers.class, 2));
    public static final RegistryObject<BlockEntityType<TileEntityDrawersStandard>> STANDARD_DRAWERS_4 = BLOCK_ENTITY_REGISTER.register("standard_drawers_4",
            drawerBlockEntitySupplier(TileEntityDrawersStandard.Slot4::new, BlockStandardDrawers.class, 4));
    public static final RegistryObject<BlockEntityType<TileEntityDrawersComp>> FRACTIONAL_DRAWERS_3 = BLOCK_ENTITY_REGISTER.register("fractional_drawers_3",
            drawerBlockEntitySupplier(TileEntityDrawersComp.Slot3::new, BlockCompDrawers.class, 3));
    public static final RegistryObject<BlockEntityType<TileEntityController>> CONTROLLER = BLOCK_ENTITY_REGISTER.register("controller",
            blockEntitySupplier(TileEntityController::new, BlockController.class));
    public static final RegistryObject<BlockEntityType<TileEntitySlave>> CONTROLLER_SLAVE = BLOCK_ENTITY_REGISTER.register("controller_slave",
            blockEntitySupplier(TileEntitySlave::new, BlockSlave.class));

    private ModBlockEntities() {}

    private static <BE extends TileEntityDrawers, B extends BlockDrawers> Supplier<BlockEntityType<BE>> drawerBlockEntitySupplier(BlockEntitySupplier<BE> blockEntitySupplier, Class<B> drawerBlockClass, int size) {
        return constructSupplier(blockEntitySupplier, ModBlocks.getDrawersOfTypeAndSize(drawerBlockClass, size));
    }

    private static <BE extends ChamTileEntity, B extends Block> Supplier<BlockEntityType<BE>> blockEntitySupplier(BlockEntitySupplier<BE> blockEntitySupplier, Class<B> blockClass) {
        return constructSupplier(blockEntitySupplier, ModBlocks.getBlocksOfType(blockClass));
    }

    private static <BE extends ChamTileEntity, B extends Block> Supplier<BlockEntityType<BE>> constructSupplier(BlockEntitySupplier<BE> blockEntitySupplier, Stream<B> blockStream) {
        return () -> BlockEntityType.Builder.of(blockEntitySupplier, blockStream.toArray(Block[]::new)).build(null);
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTER.register(bus);
    }
}
