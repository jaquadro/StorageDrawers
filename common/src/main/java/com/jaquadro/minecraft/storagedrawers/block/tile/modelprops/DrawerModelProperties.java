package com.jaquadro.minecraft.storagedrawers.block.tile.modelprops;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.model.ModelContextSupplier;
import com.jaquadro.minecraft.storagedrawers.client.model.context.DrawerModelContext;
import com.jaquadro.minecraft.storagedrawers.components.item.FrameData;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DrawerModelProperties implements ModelContextSupplier<DrawerModelContext>
{
    public static final DrawerModelProperties INSTANCE = new DrawerModelProperties();

    public final IDrawerAttributes attributes;
    public final IDrawerGroup group;
    public final IProtectable protectable;
    public final MaterialData material;

    protected DrawerModelProperties () {
        attributes = null;
        group = null;
        protectable = null;
        material = null;
    }

    protected DrawerModelProperties (BlockEntityDrawers blockEntity) {
        attributes = blockEntity.getDrawerAttributes();
        group = blockEntity.getGroup();
        protectable = blockEntity;
        material = blockEntity.material();
    }

    public static DrawerModelProperties getModelData (BlockEntityDrawers blockEntity) {
        return new DrawerModelProperties(blockEntity);
    }

    @Override
    public DrawerModelContext makeContext (@Nullable BlockState state, RandomSource rand, Object renderData) {
        if (renderData instanceof DrawerModelProperties props) {
            return new DrawerModelContext(state, rand).attr(props.attributes)
                .group(props.group)
                .protectable(props.protectable)
                .materialData(props.material);
        }

        return new DrawerModelContext(state, rand);
    }

    @Override
    public DrawerModelContext makeContext (ItemStack stack) {
        MaterialData data = stack.getOrDefault(ModDataComponents.FRAME_DATA.get(), FrameData.EMPTY).asMaterialData();

        Block block = Blocks.AIR;
        if (stack.getItem() instanceof BlockItem blockItem)
            block = blockItem.getBlock();

        return new DrawerModelContext(block.defaultBlockState())
            .materialData(data);
    }
}