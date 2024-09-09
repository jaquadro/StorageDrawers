package com.jaquadro.minecraft.storagedrawers.api.render;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IRenderLabel
{
    void render (BlockEntity blockEntity, IDrawerGroup drawerGroup, int slot, float brightness, float partialTickTime);
}
