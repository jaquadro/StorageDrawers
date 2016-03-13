package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.EnumQuadGroup;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonFramingRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IFlexibleBakedModel;

import java.util.ArrayList;
import java.util.List;

public class FramingTableModel implements IFlexibleBakedModel {
    private static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>(0);

    private CommonFramingRenderer renderer;
    private IBlockState blockState;

    private TextureAtlasSprite iconBase;
    private TextureAtlasSprite iconTrim;

    public FramingTableModel (IBlockState state) {
        renderer = new CommonFramingRenderer(ChamRender.instance);
        blockState = state;

        iconBase = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconBaseOak);
        iconTrim = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconTrimOak);
    }

    @Override
    public VertexFormat getFormat () {
        return DefaultVertexFormats.BLOCK;
    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        if (MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.SOLID)
            return EMPTY;
        
        ChamRender.instance.startBaking(getFormat());
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, blockState.getValue(BlockFramingTable.FACING).getIndex());
        if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
            renderer.renderRight(null, blockState, iconBase, iconTrim, EnumQuadGroup.FACE);
        else
            renderer.renderLeft(null, blockState, iconBase, iconTrim, EnumQuadGroup.FACE);
        ChamRender.instance.state.clearRotateTransform();
        return ChamRender.instance.stopBaking();
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
        if (MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.SOLID)
            return EMPTY;

        ChamRender.instance.startBaking(getFormat());
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, blockState.getValue(BlockFramingTable.FACING).getIndex());
        if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
            renderer.renderRight(null, blockState, iconBase, iconTrim, EnumQuadGroup.GENERAL);
        else
            renderer.renderLeft(null, blockState, iconBase, iconTrim, EnumQuadGroup.GENERAL);
        ChamRender.instance.state.clearRotateTransform();
        return ChamRender.instance.stopBaking();
    }

    @Override
    public boolean isAmbientOcclusion () {
        return true;
    }

    @Override
    public boolean isGui3d () {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer () {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return iconBase;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return null;
    }
}
