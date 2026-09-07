package com.jaquadro.minecraft.storagedrawers.client.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParentModel implements BlockStateModel
{
    @NotNull
    protected final BlockStateModel parent;

    public ParentModel (@NotNull BlockStateModel parent) {
        this.parent = parent;
    }

    @Override
    public void collectParts (RandomSource randomSource, List<BlockStateModelPart> list) {
        parent.collectParts(randomSource, list);
    }

    @Override
    public Material.Baked particleMaterial () {
        return parent.particleMaterial();
    }

    @Override
    public int materialFlags () {
        return parent.materialFlags();
    }
}
