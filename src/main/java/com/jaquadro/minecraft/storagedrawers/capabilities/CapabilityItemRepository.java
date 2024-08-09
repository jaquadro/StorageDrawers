package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilityItemRepository
{
    public static final BlockCapability<IItemRepository, Void> ITEM_REPOSITORY_CAPABILITY =
        BlockCapability.createVoid(ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID, "item_repository"), IItemRepository.class);

    public static void register (RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(ITEM_REPOSITORY_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_1.get(),
            (entity, context) -> new DrawerItemRepository(entity));
        event.registerBlockEntity(ITEM_REPOSITORY_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_2.get(),
            (entity, context) -> new DrawerItemRepository(entity));
        event.registerBlockEntity(ITEM_REPOSITORY_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_4.get(),
            (entity, context) -> new DrawerItemRepository(entity));
        event.registerBlockEntity(ITEM_REPOSITORY_CAPABILITY, ModBlockEntities.FRACTIONAL_DRAWERS_3.get(),
            (entity, context) -> new DrawerItemRepository(entity));

        event.registerBlockEntity(ITEM_REPOSITORY_CAPABILITY, ModBlockEntities.CONTROLLER.get(),
            (entity, context) -> entity.getItemRepository());
        event.registerBlockEntity(ITEM_REPOSITORY_CAPABILITY, ModBlockEntities.CONTROLLER_IO.get(),
            (entity, context) -> entity.getItemRepository());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.STANDARD_DRAWERS_1.get(),
            (entity, context) -> new DrawerItemHandler(entity));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.STANDARD_DRAWERS_2.get(),
            (entity, context) -> new DrawerItemHandler(entity));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.STANDARD_DRAWERS_4.get(),
            (entity, context) -> new DrawerItemHandler(entity));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.FRACTIONAL_DRAWERS_3.get(),
            (entity, context) -> new DrawerItemHandler(entity));

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CONTROLLER.get(),
            (entity, context) -> new DrawerItemHandler(entity));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CONTROLLER_IO.get(),
            (entity, context) -> new DrawerItemHandler(entity));
    }
}
