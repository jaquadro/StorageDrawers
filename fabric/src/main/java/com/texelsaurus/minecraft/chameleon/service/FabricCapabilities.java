package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.FabricCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.IFabricCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FabricCapabilities implements ChameleonCapabilities
{
    private static Map<Identifier, IFabricCapability> capabilties = new HashMap<>();

    @Override
    public <T, C> ChameleonCapability<T> create (Identifier location, Class<T> clazz, Class<C> context) {
        return new DeferredCapability<>(location);
    }

    public static <T> void reigsterCapability (FabricCapability<T> capability) {
        capabilties.put(capability.id(), capability);
    }

    private class DeferredCapability<T> implements IFabricCapability<T>
    {
        private Identifier id;
        private IFabricCapability<T> cap;

        public DeferredCapability (Identifier id) {
            this.id = id;
        }

        @Override
        public Identifier id () {
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
        public <BE extends BlockEntity> void register (BlockEntityType<BE> entity, Function<BE, T> provider) {
            if (cap == null)
                cap = (IFabricCapability<T>) capabilties.getOrDefault(id, null);

            if (cap != null)
                cap.register(entity, provider);
        }
    }
}
