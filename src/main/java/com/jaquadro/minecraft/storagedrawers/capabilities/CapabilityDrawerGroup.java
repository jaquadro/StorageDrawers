package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilityDrawerGroup
{
    public static final BlockCapability<IDrawerGroup, Void> DRAWER_GROUP_CAPABILITY =
        BlockCapability.createVoid(new ResourceLocation(StorageDrawers.MOD_ID, "drawer_group"), IDrawerGroup.class);

    public static void register (RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(DRAWER_GROUP_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_1.get(),
            (entity, context) -> BlockEntityDrawers.getGroup(entity));
        event.registerBlockEntity(DRAWER_GROUP_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_2.get(),
            (entity, context) -> BlockEntityDrawers.getGroup(entity));
        event.registerBlockEntity(DRAWER_GROUP_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_4.get(),
            (entity, context) -> BlockEntityDrawers.getGroup(entity));
        event.registerBlockEntity(DRAWER_GROUP_CAPABILITY, ModBlockEntities.FRACTIONAL_DRAWERS_3.get(),
            (entity, context) -> BlockEntityDrawers.getGroup(entity));

        event.registerBlockEntity(DRAWER_GROUP_CAPABILITY, ModBlockEntities.CONTROLLER.get(),
            (entity, context) -> entity);
        event.registerBlockEntity(DRAWER_GROUP_CAPABILITY, ModBlockEntities.CONTROLLER_IO.get(),
            (entity, context) -> entity);
    }
}
