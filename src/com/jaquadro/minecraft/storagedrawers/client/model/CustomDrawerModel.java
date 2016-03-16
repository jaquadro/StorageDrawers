package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonDrawerRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;

import java.util.ArrayList;
import java.util.List;

public class CustomDrawerModel implements IFlexibleBakedModel {
    private static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>(0);

    private CommonDrawerRenderer renderer;
    private IBlockState blockState;

    private TextureAtlasSprite iconSide;
    private TextureAtlasSprite iconTrim;
    private TextureAtlasSprite iconFront;

    private TextureAtlasSprite iconOverlayTrim;
    private TextureAtlasSprite iconOverlayHandle;
    private TextureAtlasSprite iconOverlayFace;

    public CustomDrawerModel (IBlockState state) {
        renderer = new CommonDrawerRenderer(ChamRender.instance);
        blockState = state;


    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        return null;
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
        return null;
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
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return null;
    }

    @Override
    public VertexFormat getFormat () {
        return DefaultVertexFormats.BLOCK;
    }
}
