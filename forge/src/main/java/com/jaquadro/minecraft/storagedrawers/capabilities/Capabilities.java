package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IDrawerCapability;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;
import java.util.Map;

public class Capabilities
{
    private static Map<Capability, IDrawerCapability> nativeMap = new HashMap<>();

    private static final Capability<IDrawerAttributes> nativeDrawerAttributes = CapabilityManager.get(new CapabilityToken<>(){});
    private static final Capability<IDrawerGroup> nativeDrawerGroup = CapabilityManager.get(new CapabilityToken<>(){});
    private static final Capability<IItemRepository> nativeItemRepository = CapabilityManager.get(new CapabilityToken<>(){});
    private static final Capability<IItemHandler> nativeItemHandler = CapabilityManager.get(new CapabilityToken<>(){});

    public static final IDrawerCapability<IDrawerAttributes> DRAWER_ATTRIBUTES = new Wrapper<>(nativeDrawerAttributes);
    public static final IDrawerCapability<IDrawerGroup> DRAWER_GROUP = new Wrapper<>(nativeDrawerGroup);
    public static final IDrawerCapability<IItemRepository> ITEM_REPOSITORY = new Wrapper<>(nativeItemRepository);
    public static final IDrawerCapability<IItemHandler> ITEM_HANDLER = new Wrapper<>(nativeItemHandler);

    public static <T> IDrawerCapability<T> fromNative(Capability<T> cap) {
        return nativeMap.getOrDefault(cap, null);
    }

    static class Wrapper<T> implements IDrawerCapability<T> {
        final Capability<T> nativeCapability;

        Wrapper(Capability<T> nativeCapability) {
            this.nativeCapability = nativeCapability;
            nativeMap.put(nativeCapability, this);
        }

        @Override
        public T getCapability (Level level, BlockPos pos) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null)
                return blockEntity.getCapability(nativeCapability).orElseGet(null);

            return null;
        }
    }
}
