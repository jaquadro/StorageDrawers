package com.jaquadro.minecraft.storagedrawers.client.model.context;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class DrawerModelContext extends FramedModelContext
{
    private IDrawerAttributes attr;
    private IDrawerGroup group;
    private IProtectable protectable;

    public DrawerModelContext (BlockState state) {
        super(state);
    }

    public DrawerModelContext (BlockState state, Direction direction, RandomSource randomSource, RenderType renderType) {
        super(state, direction, randomSource, renderType);
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

    public DrawerModelContext protectable (IProtectable protectable) {
        this.protectable = protectable;
        return this;
    }

    public DrawerModelContext group (IDrawerGroup group) {
        this.group = group;
        return this;
    }

    public DrawerModelContext attr (IDrawerAttributes attr) {
        this.attr = attr;
        return this;
    }

    public DrawerModelContext materialData (MaterialData materialData) {
        super.materialData(materialData);
        return this;
    }
}
