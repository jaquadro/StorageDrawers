package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.BlockModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonDrawerRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.util.vector.Vector3f;

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
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_front_1"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_front_2"),
            new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_front_4"),
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

        private static IBakedModel blockModel;
        private static IBakedModel itemModel;

        public Register () {
            super(ModBlocks.customDrawers);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumBasicDrawer drawer : EnumBasicDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS)
                    states.add(ModBlocks.customDrawers.getDefaultState().withProperty(BlockDrawers.BLOCK, drawer).withProperty(BlockDrawers.FACING, dir));
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state) {
            if (blockModel == null)
                blockModel = new ModelHandler();

            return blockModel;
        }

        @Override
        public IBakedModel getModel (ItemStack stack) {
            if (itemModel == null)
                itemModel = new ItemModelHandler();

            return itemModel;
            //return new CustomDrawerModel(ModBlocks.customDrawers.getStateFromMeta(stack.getMetadata()));
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

    public static CustomDrawerModel fromBlock (IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState xstate = (IExtendedBlockState) state;
            TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

            ItemStack matFront = tile.getEffectiveMaterialFront();
            ItemStack matSide = tile.getEffectiveMaterialSide();
            ItemStack matTrim = tile.getEffectiveMaterialTrim();

            return new CustomDrawerModel(state, matFront, matSide, matTrim, false);
        }

        return new CustomDrawerModel(state, false);
    }

    public static CustomDrawerModel fromItem (ItemStack stack) {
        IBlockState state = ModBlocks.customDrawers.getStateFromMeta(stack.getMetadata());
        if (!stack.hasTagCompound())
            return new CustomDrawerModel(state, true);

        NBTTagCompound tag = stack.getTagCompound();
        ItemStack matFront = null;
        ItemStack matSide = null;
        ItemStack matTrim = null;

        if (tag.hasKey("MatF", Constants.NBT.TAG_COMPOUND))
            matFront = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatF"));
        if (tag.hasKey("MatS", Constants.NBT.TAG_COMPOUND))
            matSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));
        if (tag.hasKey("MatT", Constants.NBT.TAG_COMPOUND))
            matTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));

        return new CustomDrawerModel(state, matFront, matSide, matTrim, true);
    }

    private CustomDrawerModel (IBlockState state, boolean mergeLayers) {
        this(state, null, null, null, mergeLayers);
    }

    private CustomDrawerModel (IBlockState state, ItemStack matFront, ItemStack matSide, ItemStack matTrim, boolean mergeLayers) {
        renderer = new CommonDrawerRenderer(ChamRender.instance);
        blockState = state;

        EnumBasicDrawer info = (EnumBasicDrawer) state.getValue(BlockDrawers.BLOCK);
        int index = iconIndex[info.getDrawerCount()];

        iconOverlayFace = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayFace[index]);
        iconOverlayHandle = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayHandle[index]);
        iconOverlayTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayTrim[index]);

        iconFront = getIconFromStack(matFront);
        iconSide = getIconFromStack(matSide);
        iconTrim = getIconFromStack(matTrim);

        if (iconFront == null)
            iconFront = iconSide;
        if (iconTrim == null)
            iconTrim = iconSide;

        if (iconFront == null)
            iconFront = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultFront[index]);
        if (iconSide == null)
            iconSide = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        if (iconTrim == null)
            iconTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);

        buildModelCache(mergeLayers);
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

    private void buildModelCache (boolean mergeLayers) {
        for (int i = 0; i < 7; i++) {
            solidCache[i] = EMPTY;
            transCache[i] = EMPTY;
        }

        EnumFacing dir = blockState.getValue(BlockDrawers.FACING);

        ChamRender.instance.startBaking(getFormat());
        renderer.renderBasePass(null, blockState, BlockPos.ORIGIN, dir, iconSide, iconTrim, iconFront);
        if (mergeLayers)
            renderer.renderOverlayPass(null, blockState, BlockPos.ORIGIN, dir, iconOverlayTrim, iconOverlayHandle, iconOverlayFace);
        ChamRender.instance.stopBaking();

        solidCache[6] = ChamRender.instance.takeBakedQuads(null);
        for (EnumFacing facing : EnumFacing.VALUES)
            solidCache[facing.getIndex()] = ChamRender.instance.takeBakedQuads(facing);

        if (!mergeLayers) {
            ChamRender.instance.startBaking(getFormat());
            renderer.renderOverlayPass(null, blockState, BlockPos.ORIGIN, dir, iconOverlayTrim, iconOverlayHandle, iconOverlayFace);
            ChamRender.instance.stopBaking();

            transCache[6] = ChamRender.instance.takeBakedQuads(null);
            for (EnumFacing facing : EnumFacing.VALUES)
                transCache[facing.getIndex()] = ChamRender.instance.takeBakedQuads(facing);
        }
    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        switch (MinecraftForgeClient.getRenderLayer()) {
            case SOLID:
            case CUTOUT_MIPPED:
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
            case CUTOUT_MIPPED:
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

    private static final ItemTransformVec3f transformThirdPerson = new ItemTransformVec3f(new Vector3f(10, -45, 170), new Vector3f(0, 0.09375f, -0.078125f), new Vector3f(.375f, .375f, .375f));
    private static final ItemCameraTransforms transform = new ItemCameraTransforms(transformThirdPerson,
        ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return transform;
    }

    @Override
    public VertexFormat getFormat () {
        return DefaultVertexFormats.ITEM;
    }

    public static class ModelHandler implements ISmartBlockModel
    {
        private TextureAtlasSprite iconRawSide;

        public ModelHandler () {
            iconRawSide = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        }

        @Override
        public IBakedModel handleBlockState (IBlockState state) {
            IBakedModel mainModel = CustomDrawerModel.fromBlock(state);
            if (!(state instanceof IExtendedBlockState))
                return mainModel;

            IExtendedBlockState xstate = (IExtendedBlockState)state;
            TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

            if (!DrawerDecoratorModel.shouldHandleState(tile))
                return mainModel;

            EnumBasicDrawer drawer = (EnumBasicDrawer)state.getValue(BlockDrawers.BLOCK);
            EnumFacing dir = state.getValue(BlockDrawers.FACING);

            return new DrawerDecoratorModel(mainModel, xstate, drawer, dir, tile);
        }

        @Override
        public List<BakedQuad> getFaceQuads (EnumFacing facing) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<BakedQuad> getGeneralQuads () {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAmbientOcclusion () {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isGui3d () {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isBuiltInRenderer () {
            throw new UnsupportedOperationException();
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return iconRawSide;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms () {
            throw new UnsupportedOperationException();
        }
    }

    public static class ItemModelHandler implements ISmartItemModel
    {
        private TextureAtlasSprite iconRawSide;

        public ItemModelHandler () {
            iconRawSide = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        }

        @Override
        public IBakedModel handleItemState (ItemStack stack) {
            return CustomDrawerModel.fromItem(stack);
        }

        @Override
        public List<BakedQuad> getFaceQuads (EnumFacing facing) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<BakedQuad> getGeneralQuads () {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAmbientOcclusion () {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isGui3d () {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isBuiltInRenderer () {
            throw new UnsupportedOperationException();
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return iconRawSide;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms () {
            throw new UnsupportedOperationException();
        }
    }
}
