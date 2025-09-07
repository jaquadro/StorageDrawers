package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.FabricCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.IFabricCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FabricCapabilities implements ChameleonCapabilities
{
    private static Map<ResourceLocation, IFabricCapability> capabilties = new HashMap<>();

    @Override
    public <T, C> ChameleonCapability<T> create (ResourceLocation location, Class<T> clazz, Class<C> context) {
        return new DeferredCapability<>(location);
    }

    public static <T> void reigsterCapability (FabricCapability<T> capability) {
        capabilties.put(capability.id(), capability);
    }

    private class DeferredCapability<T> implements IFabricCapability<T>
    {
        private ResourceLocation id;
        private IFabricCapability<T> cap;

        public DeferredCapability (ResourceLocation id) {
            this.id = id;
        }

        @Override
        public ResourceLocation id () {
            return id;
        }

        @Override
        public T getCapability (Level level, BlockPos pos) {
            if (cap != null)
                return cap.getCapability(level, pos);

            cap = (IFabricCapability<T>) capabilties.getOrDefault(id, null);
            if (cap != null)
                return cap.getCapability(level, pos);

            return null;
        }

        @Override
        public <BE extends BlockEntity> T getCapability (BE blockEntity) {
            if (cap != null)
                return cap.getCapability(blockEntity);

            cap = (IFabricCapability<T>) capabilties.getOrDefault(id, null);
            if (cap != null)
                return cap.getCapability(blockEntity);

            return null;
        }

        @Override
        public <BE extends BlockEntity> T getCapability (BE blockEntity, Direction dir) {
            if (cap != null)
                return cap.getCapability(blockEntity, dir);

            cap = (IFabricCapability<T>) capabilties.getOrDefault(id, null);
            if (cap != null)
                return cap.getCapability(blockEntity, dir);

            return null;
        }

        @Override
        public <BE extends BlockEntity> void register (BlockEntityType<BE> entity, Function<BE, T> provider) {
            if (cap == null)
                cap = (IFabricCapability<T>) capabilties.getOrDefault(id, null);

            if (cap != null)
                cap.register(entity, provider);
        }

        @Override
        public <BE extends BlockEntity> void register (BlockEntityType<BE> entity, BiFunction<BE, Direction, T> provider) {
            if (cap == null)
                cap = (IFabricCapability<T>) capabilties.getOrDefault(id, null);

            if (cap != null)
                cap.register(entity, provider);
        }
    }
}
