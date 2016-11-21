package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.ChamModel;
import com.jaquadro.minecraft.chameleon.model.ProxyBuilderModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.IconUtil;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.DrawerStateModelData;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.MaterialModelData;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerSealedModel;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonDrawerRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomDrawerModel extends ChamModel
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

        public Register () {
            super(ModBlocks.customDrawers);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<>();

            for (EnumBasicDrawer drawer : EnumBasicDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS)
                    states.add(ModBlocks.customDrawers.getDefaultState().withProperty(BlockDrawers.BLOCK, drawer).withProperty(BlockDrawers.FACING, dir));
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return new Model();
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return new Model();
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            List<ResourceLocation> resource = new ArrayList<>();
            resource.add(iconDefaultSide);
            resource.addAll(Arrays.asList(iconDefaultFront));
            resource.addAll(Arrays.asList(iconOverlayTrim));
            resource.addAll(Arrays.asList(iconOverlayBoldTrim));
            resource.addAll(Arrays.asList(iconOverlayFace));
            resource.addAll(Arrays.asList(iconOverlayHandle));
            return resource;
        }
    }

    private static final int[] iconIndex = new int[] { 0, 0, 1, 0, 2 };

    private TextureAtlasSprite iconParticle;

    public static IBakedModel fromBlock (IBlockState state) {
        if (!(state instanceof IExtendedBlockState))
            return new CustomDrawerModel(state, false);

        IExtendedBlockState xstate = (IExtendedBlockState) state;
        DrawerStateModelData stateModel = xstate.getValue(BlockDrawers.STATE_MODEL);
        MaterialModelData matModel = xstate.getValue(BlockDrawersCustom.MAT_MODEL);
        if (stateModel == null || matModel == null)
            return new CustomDrawerModel(state, false);

        ItemStack effMatFront = matModel.getEffectiveMaterialFront();
        ItemStack effMatSide = matModel.getEffectiveMaterialSide();
        ItemStack effMatTrim = matModel.getEffectiveMaterialTrim();

        ItemStack matFront = matModel.getMaterialFront();
        ItemStack matSide = matModel.getMaterialSide();
        ItemStack matTrim = matModel.getMaterialTrim();

        return new CustomDrawerModel(state, effMatFront, effMatSide, effMatTrim, matFront, matSide, matTrim, false);
    }

    public static IBakedModel fromItem (@Nonnull ItemStack stack) {
        IBlockState state = ModBlocks.customDrawers.getStateFromMeta(stack.getMetadata());
        if (!stack.hasTagCompound())
            return new CustomDrawerModel(state, true);

        NBTTagCompound tag = stack.getTagCompound();
        ItemStack matFront = ItemStack.EMPTY;
        ItemStack matSide = ItemStack.EMPTY;
        ItemStack matTrim = ItemStack.EMPTY;

        if (tag.hasKey("MatF", Constants.NBT.TAG_COMPOUND))
            matFront = new ItemStack(tag.getCompoundTag("MatF"));
        if (tag.hasKey("MatS", Constants.NBT.TAG_COMPOUND))
            matSide = new ItemStack(tag.getCompoundTag("MatS"));
        if (tag.hasKey("MatT", Constants.NBT.TAG_COMPOUND))
            matTrim = new ItemStack(tag.getCompoundTag("MatT"));

        ItemStack effMatFront = !matFront.isEmpty() ? matFront : matSide;
        ItemStack effMatTrim = !matTrim.isEmpty() ? matTrim : matSide;
        ItemStack effMatSide = matSide;

        IBakedModel model = new CustomDrawerModel(state, effMatFront, effMatSide, effMatTrim, matFront, matSide, matTrim, true);
        if (!stack.getTagCompound().hasKey("tile", Constants.NBT.TAG_COMPOUND))
            return model;

        return new DrawerSealedModel(model, state, true);
    }

    private CustomDrawerModel (IBlockState state, boolean mergeLayers) {
        this(state, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, mergeLayers);
    }

    private CustomDrawerModel (IBlockState state, @Nonnull ItemStack effMatFront, @Nonnull ItemStack effMatSide, @Nonnull ItemStack effMatTrim,
                               @Nonnull ItemStack matFront, @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim, boolean mergeLayers) {
        super(state, mergeLayers, effMatFront, effMatSide, effMatTrim, matFront, matSide, matTrim);
    }

    @Override
    protected void renderMippedLayer (ChamRender renderer, IBlockState state, Object... args) {
        EnumBasicDrawer info = state.getValue(BlockDrawers.BLOCK);
        int index = iconIndex[info.getDrawerCount()];

        ItemStack itemFront = (ItemStack)args[0];
        ItemStack itemSide = (ItemStack)args[1];
        ItemStack itemTrim = (ItemStack)args[2];

        TextureAtlasSprite iconFront = !itemFront.isEmpty() ? IconUtil.getIconFromStack(itemFront) : null;
        TextureAtlasSprite iconSide = !itemSide.isEmpty() ? IconUtil.getIconFromStack(itemSide) : null;
        TextureAtlasSprite iconTrim = !itemTrim.isEmpty() ? IconUtil.getIconFromStack(itemTrim) : null;

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

        iconParticle = iconSide;

        CommonDrawerRenderer drawerRenderer = new CommonDrawerRenderer(renderer);
        drawerRenderer.renderBasePass(null, state, BlockPos.ORIGIN, state.getValue(BlockDrawers.FACING), iconSide, iconTrim, iconFront);
    }

    @Override
    protected void renderTransLayer (ChamRender renderer, IBlockState state, Object... args) {
        EnumBasicDrawer info = state.getValue(BlockDrawers.BLOCK);
        int index = iconIndex[info.getDrawerCount()];

        TextureAtlasSprite iconOverlayFace = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayFace[index]);
        TextureAtlasSprite iconOverlayHandle = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayHandle[index]);

        ItemStack itemTrim = (ItemStack)args[5];

        TextureAtlasSprite iconTrim = !itemTrim.isEmpty() ? IconUtil.getIconFromStack(itemTrim) : null;
        TextureAtlasSprite iconOverlayTrim;

        if (iconTrim == null)
            iconOverlayTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayBoldTrim[index]);
        else
            iconOverlayTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconOverlayTrim[index]);

        CommonDrawerRenderer drawerRenderer = new CommonDrawerRenderer(renderer);
        drawerRenderer.renderOverlayPass(null, state, BlockPos.ORIGIN, state.getValue(BlockDrawers.FACING), iconOverlayTrim, iconOverlayHandle, iconOverlayFace);
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return iconParticle;
    }

    public static class Model extends ProxyBuilderModel
    {
        public Model () {
            super(Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide));
        }

        @Override
        protected IBakedModel buildModel (IBlockState state, IBakedModel parent) {
            try {
                IBakedModel mainModel = CustomDrawerModel.fromBlock(state);
                if (!(state instanceof IExtendedBlockState))
                    return mainModel;

                IExtendedBlockState xstate = (IExtendedBlockState) state;
                DrawerStateModelData stateModel = xstate.getValue(BlockDrawers.STATE_MODEL);

                try {
                    if (!DrawerDecoratorModel.shouldHandleState(stateModel))
                        return mainModel;

                    EnumBasicDrawer drawer = state.getValue(BlockDrawers.BLOCK);
                    EnumFacing dir = state.getValue(BlockDrawers.FACING);

                    return new DrawerDecoratorModel(mainModel, xstate, drawer, dir, stateModel);
                }
                catch (Throwable t) {
                    return mainModel;
                }
            }
            catch (Throwable t) {
                return parent;
            }
        }

        @Override
        public ItemOverrideList getOverrides () {
            return itemHandler;
        }
    }

    private static class ItemHandler extends ItemOverrideList
    {
        public ItemHandler () {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState (IBakedModel originalModel, @Nonnull ItemStack stack, World world, EntityLivingBase entity) {
            return fromItem(stack);
        }
    }

    private static final ItemHandler itemHandler = new ItemHandler();
}
