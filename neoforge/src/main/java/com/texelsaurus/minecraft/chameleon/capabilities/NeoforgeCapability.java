package com.texelsaurus.minecraft.chameleon.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class NeoforgeCapability<T, C> implements ChameleonCapability<T>
{
    final BlockCapability<T, C> nativeCapability;

    public NeoforgeCapability(ResourceLocation location, Class<T> clazz, Class<C> context) {
        nativeCapability = BlockCapability.create(location, clazz, context);
    }

    public NeoforgeCapability(BlockCapability<T, C> nativeCapability) {
        this.nativeCapability = nativeCapability;
    }

    @Override
    public T getCapability (Level level, BlockPos pos) {
        return level.getCapability(nativeCapability, pos, null, null, null);
    }

    public <BE extends BlockEntity> void register(RegisterCapabilitiesEvent event, BlockEntityType<BE> entity, ICapabilityProvider<? super BE, C, T> provider) {
        event.registerBlockEntity(nativeCapability, entity, provider);
    }
}
