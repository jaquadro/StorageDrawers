package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.BlockModel;
import com.jaquadro.minecraft.chameleon.model.ChamModel;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FramingTableModel extends ChamModel
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

    private TextureAtlasSprite iconParticle;

    public FramingTableModel (IBlockState state) {
        this(state, false);
    }

    protected FramingTableModel (IBlockState state, boolean mergeLayers) {
        super(state, mergeLayers);
    }

    @Override
    protected void renderStart (ChamRender renderer, IBlockState state, Object... args) {
        renderer.state.setRotateTransform(ChamRender.ZPOS, state.getValue(BlockFramingTable.FACING).getIndex());
    }

    @Override
    protected void renderEnd (ChamRender renderer, IBlockState state, Object... args) {
        renderer.state.clearRotateTransform();
    }

    @Override
    protected void renderSolidLayer (ChamRender renderer, IBlockState state, Object... args) {
        TextureAtlasSprite iconBase = Chameleon.instance.iconRegistry.getIcon(Register.iconBaseOak);
        TextureAtlasSprite iconTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconTrimOak);

        renderSolidLayer(state, new CommonFramingRenderer(renderer), iconBase, iconTrim);
        iconParticle = iconBase;
    }

    @Override
    protected void renderTransLayer (ChamRender renderer, IBlockState state, Object... args) {
        TextureAtlasSprite iconOverlayLeft = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayLeft);
        TextureAtlasSprite iconOverlayRight = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayRight);

        renderTransLayer(state, new CommonFramingRenderer(renderer), iconOverlayLeft, iconOverlayRight);
    }

    protected void renderSolidLayer (IBlockState state, CommonFramingRenderer common, TextureAtlasSprite iconBase, TextureAtlasSprite iconTrim) {
        if (state.getValue(BlockFramingTable.RIGHT_SIDE))
            common.renderRight(null, state, BlockPos.ORIGIN, iconBase, iconTrim);
        else
            common.renderLeft(null, state, BlockPos.ORIGIN, iconBase, iconTrim);
    }

    protected void renderTransLayer (IBlockState state, CommonFramingRenderer common, TextureAtlasSprite iconLeft, TextureAtlasSprite iconRight) {
        if (state.getValue(BlockFramingTable.RIGHT_SIDE))
            common.renderOverlayRight(null, state, BlockPos.ORIGIN, iconRight);
        else
            common.renderOverlayLeft(null, state, BlockPos.ORIGIN, iconLeft);
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return iconParticle;
    }

    @SuppressWarnings("deprecation")
    public static class ItemModel extends FramingTableModel
    {
        private static final ItemTransformVec3f transformDefault = new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(-.15f, 0, 0), new Vector3f(.65f, .65f, .65f));
        private static final ItemTransformVec3f transformThirdPerson = new ItemTransformVec3f(new Vector3f(10, 0, 180), new Vector3f(.2f, .1f, -.15f), new Vector3f(.3f, .3f, .3f));
        private static final ItemCameraTransforms transform = new ItemCameraTransforms(transformThirdPerson,
            transformDefault, transformDefault, transformDefault, transformDefault, transformDefault);

        public ItemModel (ItemStack stack) {
            super(ModBlocks.framingTable.getStateFromMeta(stack.getMetadata()), true);
        }

        @Override
        protected void renderSolidLayer (IBlockState state, CommonFramingRenderer common, TextureAtlasSprite iconBase, TextureAtlasSprite iconTrim) {
            common.renderRight(null, state, BlockPos.ORIGIN, iconBase, iconTrim);
            common.renderLeft(null, state, BlockPos.ORIGIN.east(), iconBase, iconTrim);
        }

        @Override
        protected void renderTransLayer (IBlockState state, CommonFramingRenderer common, TextureAtlasSprite iconLeft, TextureAtlasSprite iconRight) {
            common.renderOverlayRight(null, state, BlockPos.ORIGIN, iconRight);
            common.renderOverlayLeft(null, state, BlockPos.ORIGIN.east(), iconLeft);
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms () {
            return transform;
        }
    }
}
