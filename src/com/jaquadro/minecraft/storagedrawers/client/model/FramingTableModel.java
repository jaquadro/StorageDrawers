package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.BlockModel;
import com.jaquadro.minecraft.chameleon.model.EnumQuadGroup;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FramingTableModel extends BlockModel
{
    public static class Register extends DefaultRegister
    {
        public static final ResourceLocation iconBaseOak = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/base/base_oak");
        public static final ResourceLocation iconTrimOak = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/base/trim_oak");
        public static final ResourceLocation iconOverlayLeft = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_worktable_left");
        public static final ResourceLocation iconOverlayRight = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_worktable_right");

        public Register () {
            super(ModBlocks.framingTable);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                for (Boolean side : new Boolean[] { false, true })
                    states.add(ModBlocks.framingTable.getDefaultState().withProperty(BlockFramingTable.FACING, dir).withProperty(BlockFramingTable.RIGHT_SIDE, side));
            }

            return states;
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            return Arrays.asList(iconBaseOak, iconTrimOak, iconOverlayLeft, iconOverlayRight);
        }

        @Override
        public IBakedModel getModel (IBlockState state) {
            return new FramingTableModel(state);
        }

        @Override
        public IBakedModel getModel (ItemStack stack) {
            return new FramingTableModel.ItemModel(stack);
        }
    }

    private static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>(0);

    protected final CommonFramingRenderer renderer;
    protected final IBlockState blockState;

    protected final TextureAtlasSprite iconBase;
    protected final TextureAtlasSprite iconTrim;
    protected final TextureAtlasSprite iconOverlayLeft;
    protected final TextureAtlasSprite iconOverlayRight;

    public FramingTableModel (IBlockState state) {
        renderer = new CommonFramingRenderer(ChamRender.instance);
        blockState = state;

        iconBase = Chameleon.instance.iconRegistry.getIcon(Register.iconBaseOak);
        iconTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconTrimOak);
        iconOverlayLeft = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayLeft);
        iconOverlayRight = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayRight);
    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        if (MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.SOLID && MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.TRANSLUCENT)
            return EMPTY;

        ChamRender.instance.startBaking(getFormat());
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, blockState.getValue(BlockFramingTable.FACING).getIndex());

        renderFaceQuads();

        ChamRender.instance.state.clearRotateTransform();
        return ChamRender.instance.stopBaking();
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
        if (MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.SOLID)
            return EMPTY;

        ChamRender.instance.startBaking(getFormat());
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, blockState.getValue(BlockFramingTable.FACING).getIndex());

        renderGeneralQuads();

        ChamRender.instance.state.clearRotateTransform();
        return ChamRender.instance.stopBaking();
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return iconBase;
    }

    protected void renderFaceQuads () {
        if (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.SOLID) {
            if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
                renderer.renderRight(null, blockState, BlockPos.ORIGIN, iconBase, iconTrim, EnumQuadGroup.FACE);
            else
                renderer.renderLeft(null, blockState, BlockPos.ORIGIN, iconBase, iconTrim, EnumQuadGroup.FACE);
        }
        else if (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.TRANSLUCENT) {
            if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
                renderer.renderOverlayRight(null, blockState, BlockPos.ORIGIN, iconOverlayRight, EnumQuadGroup.FACE);
            else
                renderer.renderOverlayLeft(null, blockState, BlockPos.ORIGIN, iconOverlayLeft, EnumQuadGroup.FACE);
        }
    }

    protected void renderGeneralQuads () {
        if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
            renderer.renderRight(null, blockState, BlockPos.ORIGIN, iconBase, iconTrim, EnumQuadGroup.GENERAL);
        else
            renderer.renderLeft(null, blockState, BlockPos.ORIGIN, iconBase, iconTrim, EnumQuadGroup.GENERAL);
    }

    @SuppressWarnings("deprecation")
    public static class ItemModel extends FramingTableModel
    {
        private static final ItemTransformVec3f transformDefault = new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(-.15f, 0, 0), new Vector3f(.65f, .65f, .65f));
        private static final ItemTransformVec3f transformThirdPerson = new ItemTransformVec3f(new Vector3f(10, 0, 180), new Vector3f(.2f, .1f, -.15f), new Vector3f(.3f, .3f, .3f));
        private static final ItemCameraTransforms transform = new ItemCameraTransforms(transformThirdPerson,
            transformDefault, transformDefault, transformDefault, transformDefault, transformDefault);

        public ItemModel (ItemStack stack) {
            super(ModBlocks.framingTable.getStateFromMeta(stack.getMetadata()));
        }

        @Override
        protected void renderFaceQuads () {
            renderer.renderRight(null, blockState, BlockPos.ORIGIN, iconBase, iconTrim, EnumQuadGroup.FACE);
            renderer.renderLeft(null, blockState, BlockPos.ORIGIN.east(), iconBase, iconTrim, EnumQuadGroup.FACE);

            renderer.renderOverlayRight(null, blockState, BlockPos.ORIGIN, iconOverlayRight, EnumQuadGroup.FACE);
            renderer.renderOverlayLeft(null, blockState, BlockPos.ORIGIN.east(), iconOverlayLeft, EnumQuadGroup.FACE);
        }

        @Override
        protected void renderGeneralQuads () {
            renderer.renderRight(null, blockState, BlockPos.ORIGIN, iconBase, iconTrim, EnumQuadGroup.GENERAL);
            renderer.renderLeft(null, blockState, BlockPos.ORIGIN.east(), iconBase, iconTrim, EnumQuadGroup.GENERAL);
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms () {
            return transform;
        }
    }
}
