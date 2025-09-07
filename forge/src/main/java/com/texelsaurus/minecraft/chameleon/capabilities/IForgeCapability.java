package com.texelsaurus.minecraft.chameleon.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface IForgeCapability<T> extends ChameleonCapability<T>
{
    <BE extends BlockEntity> T getCapability(BE blockEntity);

    <BE extends BlockEntity> T getCapability(BE blockEntity, Direction dir);

    <BE extends BlockEntity> void register (BlockEntityType<BE> entity, Function<BE, T> provider);

    <BE extends BlockEntity> void register (BlockEntityType<BE> entity, BiFunction<BE, Direction, T> provider);
}
