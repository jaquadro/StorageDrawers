package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemHandler;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityControllerIO;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.ForgeCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.IForgeCapability;
import com.texelsaurus.minecraft.chameleon.service.ForgeCapabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import java.util.HashMap;
import java.util.Map;

public class PlatformCapabilities
{
    static final Capability<net.minecraftforge.items.IItemHandler> NATIVE_FORGE_ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

    static final Capability<IDrawerAttributes> NATIVE_DRAWER_ATTRIBUTES = CapabilityManager.get(new CapabilityToken<>(){});
    static final Capability<IDrawerGroup> NATIVE_DRAWER_GROUP = CapabilityManager.get(new CapabilityToken<>(){});
    static final Capability<IItemRepository> NATIVE_ITEM_REPOSITORY = CapabilityManager.get(new CapabilityToken<>(){});
    static final Capability<IItemHandler> NATIVE_ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

    public final static ForgeCapability<IDrawerAttributes> DRAWER_ATTRIBUTES = new ForgeCapability<>(Capabilities.DRAWER_ATTRIBUTES.id(), NATIVE_DRAWER_ATTRIBUTES);
    public final static ForgeCapability<IDrawerGroup> DRAWER_GROUP = new ForgeCapability<>(Capabilities.DRAWER_GROUP.id(), NATIVE_DRAWER_GROUP);
    public final static ForgeCapability<IItemRepository> ITEM_REPOSITORY = new ForgeCapability<>(Capabilities.ITEM_REPOSITORY.id(), NATIVE_ITEM_REPOSITORY);
    public final static ForgeCapability<IItemHandler> ITEM_HANDLER = new ForgeCapability<>(Capabilities.ITEM_HANDLER.id(), NATIVE_ITEM_HANDLER);

    private static Map<Capability, ForgeCapability> nativeMap = new HashMap<>();

    static <T> IForgeCapability<T> cast(ChameleonCapability<T> cap) {
        return (IForgeCapability<T>) cap;
    }

    public static <T> boolean hasCapability(Capability<T> capability) {
        return nativeMap.containsKey(capability);
    }

    public static <T> T getCapability(Capability<T> capability, BlockEntity blockEntity) {
        if (!nativeMap.containsKey(capability))
            return null;

        ForgeCapability<T> cap = nativeMap.get(capability);
        return cap.getCapability(blockEntity);
    }

    public static void register (RegisterCapabilitiesEvent event) {
        event.register(IDrawerAttributes.class);
        event.register(IDrawerGroup.class);
        event.register(IItemRepository.class);
        event.register(IItemHandler.class);

        nativeMap.put(NATIVE_DRAWER_ATTRIBUTES, DRAWER_ATTRIBUTES);
        nativeMap.put(NATIVE_DRAWER_GROUP, DRAWER_GROUP);
        nativeMap.put(NATIVE_ITEM_REPOSITORY, ITEM_REPOSITORY);
        nativeMap.put(NATIVE_ITEM_HANDLER, ITEM_HANDLER);
        nativeMap.put(NATIVE_FORGE_ITEM_HANDLER, ITEM_HANDLER);
    }

    public static void initHandlers() {
        ForgeCapabilities.reigsterCapability(DRAWER_ATTRIBUTES);
        ForgeCapabilities.reigsterCapability(DRAWER_GROUP);
        ForgeCapabilities.reigsterCapability(ITEM_REPOSITORY);
        ForgeCapabilities.reigsterCapability(ITEM_HANDLER);

        ModBlockEntities.getDrawerTypes().forEach((entity) -> {
            cast(Capabilities.DRAWER_ATTRIBUTES).register(entity, e -> BlockEntityDrawers.getDrawerAttributes(e));
            cast(Capabilities.DRAWER_GROUP).register(entity, e -> BlockEntityDrawers.getGroup(e));
            cast(Capabilities.ITEM_REPOSITORY).register(entity, DrawerItemRepository::new);
            cast(Capabilities.ITEM_HANDLER).register(entity, DrawerItemHandler::new);

            cast(ITEM_HANDLER).register(entity, PlatformDrawerItemHandler::new);
        });

        cast(Capabilities.DRAWER_GROUP).register(ModBlockEntities.CONTROLLER.get(), e -> e);
        cast(Capabilities.ITEM_REPOSITORY).register(ModBlockEntities.CONTROLLER.get(), BlockEntityController::getItemRepository);
        cast(Capabilities.ITEM_HANDLER).register(ModBlockEntities.CONTROLLER.get(), DrawerItemHandler::new);
        cast(ITEM_HANDLER).register(ModBlockEntities.CONTROLLER.get(), PlatformDrawerItemHandler::new);

        cast(Capabilities.DRAWER_GROUP).register(ModBlockEntities.CONTROLLER_IO.get(), e -> e);
        cast(Capabilities.ITEM_REPOSITORY).register(ModBlockEntities.CONTROLLER_IO.get(), BlockEntityControllerIO::getItemRepository);
        cast(Capabilities.ITEM_HANDLER).register(ModBlockEntities.CONTROLLER_IO.get(), DrawerItemHandler::new);
        cast(ITEM_HANDLER).register(ModBlockEntities.CONTROLLER_IO.get(), PlatformDrawerItemHandler::new);
    }
}
