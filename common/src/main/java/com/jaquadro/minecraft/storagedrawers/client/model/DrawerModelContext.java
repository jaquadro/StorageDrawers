package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.world.level.block.state.BlockState;

public record DrawerModelContext(BlockState state, IDrawerAttributes attr, IDrawerGroup group)
{
}
