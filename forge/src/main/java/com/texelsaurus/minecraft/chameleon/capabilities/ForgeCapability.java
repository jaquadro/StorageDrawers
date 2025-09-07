package com.texelsaurus.minecraft.chameleon.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ForgeCapability<T> implements IForgeCapability<T>
{
    final ResourceLocation id;
    final Capability<T> nativeCapability;
    final Map<BlockEntityType<?>, Function<BlockEntity, T>> handlers = new HashMap<>();
    final Map<BlockEntityType<?>, BiFunction<BlockEntity, Direction, T>> dirHandlers = new HashMap<>();

    public ForgeCapability (ResourceLocation id, Capability<T> nativeCapability) {
        this.id = id;
        this.nativeCapability = nativeCapability;
    }

    @Override
    public ResourceLocation id () {
        return id;
    }

    @Override
    public T getCapability (Level level, BlockPos pos) {
        return getCapability(level.getBlockEntity(pos));
    }

    @Override
    public <BE extends BlockEntity> T getCapability(BE blockEntity) {
        if (blockEntity == null)
            return null;
        BlockEntityType<?> type = blockEntity.getType();
        if (!handlers.containsKey(type))
            return null;
        return handlers.get(type).apply(blockEntity);
    }

    @Override
    public <BE extends BlockEntity> T getCapability (BE blockEntity, Direction dir) {
        if (dir == null)
            return getCapability(blockEntity);
        if (blockEntity == null)
            return null;
        BlockEntityType<?> type = blockEntity.getType();
        if (!dirHandlers.containsKey(type))
            return getCapability(blockEntity);
        return dirHandlers.get(type).apply(blockEntity, dir);
    }

    @Override
    public <BE extends BlockEntity> void register(BlockEntityType<BE> entity, Function<BE, T> provider) {
        handlers.put(entity, (Function<BlockEntity, T>)provider);
    }

    @Override
    public <BE extends BlockEntity> void register(BlockEntityType<BE> entity, BiFunction<BE, Direction, T> provider) {
        dirHandlers.put(entity, (BiFunction<BlockEntity, Direction, T>)provider);
    }
}
