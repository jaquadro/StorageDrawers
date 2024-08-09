package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilityDrawerAttributes
{
    public static final BlockCapability<IDrawerAttributes, Void> DRAWER_ATTRIBUTES_CAPABILITY =
        BlockCapability.createVoid(ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID, "drawer_attributes"), IDrawerAttributes.class);

    public static void register (RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(DRAWER_ATTRIBUTES_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_1.get(),
            (entity, context) -> BlockEntityDrawers.getDrawerAttributes(entity));
        event.registerBlockEntity(DRAWER_ATTRIBUTES_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_2.get(),
            (entity, context) -> BlockEntityDrawers.getDrawerAttributes(entity));
        event.registerBlockEntity(DRAWER_ATTRIBUTES_CAPABILITY, ModBlockEntities.STANDARD_DRAWERS_4.get(),
            (entity, context) -> BlockEntityDrawers.getDrawerAttributes(entity));
        event.registerBlockEntity(DRAWER_ATTRIBUTES_CAPABILITY, ModBlockEntities.FRACTIONAL_DRAWERS_3.get(),
            (entity, context) -> BlockEntityDrawers.getDrawerAttributes(entity));
    }
}
