package com.jaquadro.minecraft.storagedrawers.block.material;

import net.minecraft.block.material.Material;

public class MaterialDrawer extends Material
{
    public static final Material material = new MaterialDrawer();

    public MaterialDrawer () {
        super(Material.wood.getMaterialMapColor());
        setBurning();
        setAdventureModeExempt();
    }
}
