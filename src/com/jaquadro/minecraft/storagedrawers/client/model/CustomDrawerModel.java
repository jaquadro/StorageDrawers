package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.BlockModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonDrawerRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomDrawerModel extends BlockModel
{
    public static class Register extends DefaultRegister
    {
        public static final ResourceLocation iconDefaultSide = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_side");

        public static final ResourceLocation[] iconDefaultFront = new ResourceLocation[] {
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_font_1"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_font_2"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_font_4"),
        };
        public static final ResourceLocation[] iconOverlayTrim = new ResourceLocation[] {
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_trim_1"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_trim_2"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_trim_4"),
        };
        public static final ResourceLocation[] iconOverlayBoldTrim = new ResourceLocation[] {
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_boldtrim_1"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_boldtrim_2"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_boldtrim_4"),
        };
        public static final ResourceLocation[] iconOverlayFace = new ResourceLocation[] {
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_face_1"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_face_2"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_face_4"),
        };
        public static final ResourceLocation[] iconOverlayHandle = new ResourceLocation[]{
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/handle_1"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/handle_2"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/handle_4"),
        };

        public Register () {
            super(ModBlocks.basicDrawers);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumBasicDrawer drawer : EnumBasicDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS)
                    states.add(ModBlocks.basicDrawers.getDefaultState().withProperty(BlockDrawers.BLOCK, drawer).withProperty(BlockDrawers.FACING, dir));
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state) {
            return new CustomDrawerModel(state);
        }

        @Override
        public IBakedModel getModel (ItemStack stack) {
            return new CustomDrawerModel(ModBlocks.basicDrawers.getStateFromMeta(stack.getMetadata()));
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            List<ResourceLocation> resource = new ArrayList<ResourceLocation>();
            resource.add(iconDefaultSide);
            resource.addAll(Arrays.asList(iconDefaultFront));
            resource.addAll(Arrays.asList(iconOverlayTrim));
            resource.addAll(Arrays.asList(iconOverlayBoldTrim));
            resource.addAll(Arrays.asList(iconOverlayFace));
            resource.addAll(Arrays.asList(iconOverlayHandle));
            return resource;
        }
    }

    private static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>(0);

    private static final int[] iconIndex = new int[] { 0, 0, 1, 0, 2 };

    private CommonDrawerRenderer renderer;
    private IBlockState blockState;

    private TextureAtlasSprite iconSide;
    private TextureAtlasSprite iconTrim;
    private TextureAtlasSprite iconFront;

    private TextureAtlasSprite iconOverlayTrim;
    private TextureAtlasSprite iconOverlayHandle;
    private TextureAtlasSprite iconOverlayFace;

    @SuppressWarnings("unchecked")
    protected final List<BakedQuad>[] solidCache = (List[]) Array.newInstance(ArrayList.class, 7);
    @SuppressWarnings("unchecked")
    protected final List<BakedQuad>[] transCache = (List[]) Array.newInstance(ArrayList.class, 7);

    public CustomDrawerModel (IBlockState state) {
        renderer = new CommonDrawerRenderer(ChamRender.instance);
        blockState = state;

        EnumBasicDrawer info = (EnumBasicDrawer) state.getValue(BlockDrawers.BLOCK);
        int index = iconIndex[info.getDrawerCount()];

        iconOverlayFace = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayFace[index]);
        iconOverlayHandle = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayHandle[index]);
        iconOverlayTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayTrim[index]);

        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState xstate = (IExtendedBlockState) state;
            TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

            iconFront = getIconFromStack(tile.getEffectiveMaterialFront());
            iconSide = getIconFromStack(tile.getEffectiveMaterialSide());
            iconTrim = getIconFromStack(tile.getEffectiveMaterialTrim());
        }

        if (iconFront == null)
            iconFront = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultFront[index]);
        if (iconSide == null)
            iconSide = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        if (iconTrim == null)
            iconTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
    }

    private TextureAtlasSprite getIconFromStack (ItemStack stack) {
        if (stack == null)
            return null;

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null)
            return null;

        IBlockState matState = block.getStateFromMeta(stack.getMetadata());
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getBlockModelShapes().getModelForState(matState);

        if (model instanceof ISmartBlockModel)
            model = ((ISmartBlockModel) model).handleBlockState(matState);

        return model.getParticleTexture();
    }

    private void buildModelCache () {
        ChamRender.instance.state.setRotateTransform(ChamRender.ZNEG, blockState.getValue(BlockDrawers.FACING).getIndex());
        ChamRender.instance.startBaking(getFormat());
        renderer.renderBasePass(null, blockState, BlockPos.ORIGIN, ChamRender.FACE_ZNEG, iconSide, iconTrim, iconFront);
        ChamRender.instance.stopBaking();

        solidCache[6] = ChamRender.instance.takeBakedQuads(null);
        for (EnumFacing facing : EnumFacing.VALUES)
            solidCache[facing.getIndex()] = ChamRender.instance.takeBakedQuads(facing);

        ChamRender.instance.startBaking(getFormat());
        renderer.renderOverlayPass(null, blockState, BlockPos.ORIGIN, ChamRender.FACE_ZNEG, iconOverlayTrim, iconOverlayHandle, iconOverlayFace);
        ChamRender.instance.state.clearRotateTransform();
        ChamRender.instance.stopBaking();

        transCache[6] = ChamRender.instance.takeBakedQuads(null);
        for (EnumFacing facing : EnumFacing.VALUES)
            transCache[facing.getIndex()] = ChamRender.instance.takeBakedQuads(facing);
    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        switch (MinecraftForgeClient.getRenderLayer()) {
            case SOLID:
                return solidCache[facing.getIndex()];
            case TRANSLUCENT:
                return transCache[facing.getIndex()];
            default:
                return EMPTY;
        }
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
        switch (MinecraftForgeClient.getRenderLayer()) {
            case SOLID:
                return solidCache[6];
            case TRANSLUCENT:
                return transCache[6];
            default:
                return EMPTY;
        }
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
        return iconSide;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public VertexFormat getFormat () {
        return DefaultVertexFormats.ITEM;
    }
}
