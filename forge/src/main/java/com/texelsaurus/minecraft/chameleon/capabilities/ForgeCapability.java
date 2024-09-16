package com.texelsaurus.minecraft.chameleon.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ForgeCapability<T> implements ChameleonCapability<T>
{
    final Capability<T> nativeCapability;
    final Map<BlockEntityType<?>, Function<BlockEntity, T>> handlers = new HashMap<>();

    public ForgeCapability (Capability<T> nativeCapability) {
        this.nativeCapability = nativeCapability;
    }

    @Override
    public T getCapability (Level level, BlockPos pos) {
        return getCapability(level.getBlockEntity(pos));
    }

    public <BE extends BlockEntity> T getCapability(BE blockEntity) {
        if (blockEntity == null)
            return null;
        BlockEntityType<?> type = blockEntity.getType();
        if (!handlers.containsKey(type))
            return null;
        return handlers.get(type).apply(blockEntity);
    }

    public <BE extends BlockEntity> void register(BlockEntityType<BE> entity, Function<BE, T> provider) {
        handlers.put(entity, (Function<BlockEntity, T>)provider);
    }
}
