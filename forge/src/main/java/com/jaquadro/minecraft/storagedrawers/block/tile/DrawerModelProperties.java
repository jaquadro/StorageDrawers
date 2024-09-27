package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class DrawerModelProperties
{
    public static final ModelProperty<IDrawerAttributes> ATTRIBUTES = new ModelProperty<>();
    public static final ModelProperty<IDrawerGroup> DRAWER_GROUP = new ModelProperty<>();
    public static final ModelProperty<IProtectable> PROTECTABLE = new ModelProperty<>();

    public static ModelData getModelData (BlockEntityDrawers blockEntity) {
        return ModelData.builder()
            .with(ATTRIBUTES, blockEntity.getDrawerAttributes())
            .with(DRAWER_GROUP, blockEntity.getGroup())
            .with(PROTECTABLE, blockEntity).build();
    }
}
