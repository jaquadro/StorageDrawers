package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface INetworked
{
    default boolean supportsDirectControllerLink () {
        return false;
    }

    default IControlGroup getBoundControlGroup () {
        return null;
    }

    default BlockPos getBlockPos () {
        if (this instanceof BlockEntity entity)
            return entity.getBlockPos();
        return null;
    }

    default boolean canRecurseSearch () {
        return true;
    }

    default void unbindControlGroup () { }
}
