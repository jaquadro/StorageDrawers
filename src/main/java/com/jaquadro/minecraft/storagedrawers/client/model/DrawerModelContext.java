package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import net.minecraft.world.level.block.state.BlockState;

public class DrawerModelContext
{
    private final BlockState state;
    private final IDrawerAttributes attr;
    private final IDrawerGroup group;
    private final IProtectable protectable;
    private MaterialData materialData;

    public DrawerModelContext (BlockState state, IDrawerAttributes attr, IDrawerGroup group, IProtectable protectable) {
        this.state = state;
        this.attr = attr;
        this.group = group;
        this.protectable = protectable;
    }

    public BlockState state () {
        return state;
    }

    public IDrawerAttributes attr () {
        return attr;
    }

    public IDrawerGroup group () {
        return group;
    }

    public IProtectable protectable () {
        return protectable;
    }

    public MaterialData materialData () {
        return materialData;
    }

    public DrawerModelContext materialData (MaterialData materialData) {
        this.materialData = materialData;
        return this;
    }
}
