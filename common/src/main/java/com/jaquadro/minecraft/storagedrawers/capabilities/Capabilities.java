package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemHandler;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;

public class Capabilities
{
    public static final ChameleonCapability<IDrawerAttributes> DRAWER_ATTRIBUTES =
        ChameleonServices.CAPABILITY.create(ModConstants.loc("drawer_attributes"), IDrawerAttributes.class, Void.TYPE);
    public static final ChameleonCapability<IDrawerGroup> DRAWER_GROUP =
        ChameleonServices.CAPABILITY.create(ModConstants.loc("drawer_group"), IDrawerGroup.class, Void.TYPE);
    public static final ChameleonCapability<IItemRepository> ITEM_REPOSITORY =
        ChameleonServices.CAPABILITY.create(ModConstants.loc("item_repository"), IItemRepository.class, Void.TYPE);
    public static final ChameleonCapability<IItemHandler> ITEM_HANDLER =
        ChameleonServices.CAPABILITY.create(ModConstants.loc("item_handler"), IItemHandler.class, Void.TYPE);
}
