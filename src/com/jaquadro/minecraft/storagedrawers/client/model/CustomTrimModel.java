package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.ChamModel;
import com.jaquadro.minecraft.chameleon.model.ProxyBuilderModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.IconUtil;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrimCustom;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.MaterialModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonTrimRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CustomTrimModel extends ChamModel
{
    public static class Register extends DefaultRegister
    {
        public static final ResourceLocation iconDefaultSide = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_raw_side");

        public Register () {
            super(ModBlocks.customTrim);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();
            states.add(ModBlocks.customTrim.getDefaultState());
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
            List<ResourceLocation> resource = new ArrayList<ResourceLocation>();
            resource.add(iconDefaultSide);
            return resource;
        }
    }

    private TextureAtlasSprite iconParticle;

    public static CustomTrimModel fromBlock (IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState xstate = (IExtendedBlockState) state;
            MaterialModelData matModel = xstate.getValue(BlockTrimCustom.MAT_MODEL);

            if (matModel != null) {
                ItemStack matSide = matModel.getEffectiveMaterialSide();
                ItemStack matTrim = matModel.getEffectiveMaterialTrim();

                return new CustomTrimModel(state, matSide, matTrim, false);
            }
        }

        return new CustomTrimModel(state, false);
    }

    public static CustomTrimModel fromItem (ItemStack stack) {
        IBlockState state = ModBlocks.customTrim.getStateFromMeta(stack.getMetadata());
        if (!stack.hasTagCompound())
            return new CustomTrimModel(state, true);

        NBTTagCompound tag = stack.getTagCompound();
        ItemStack matSide = null;
        ItemStack matTrim = null;

        if (tag.hasKey("MatS", Constants.NBT.TAG_COMPOUND))
            matSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));
        if (tag.hasKey("MatT", Constants.NBT.TAG_COMPOUND))
            matTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));

        return new CustomTrimModel(state, matSide, matTrim, true);
    }

    private CustomTrimModel (IBlockState state, boolean mergeLayers) {
        this(state, null, null, mergeLayers);
    }

    private CustomTrimModel (IBlockState state, ItemStack matSide, ItemStack matTrim, boolean mergeLayers) {
        super(state, mergeLayers, matSide, matTrim);
    }

    @Override
    protected void renderMippedLayer (ChamRender renderer, IBlockState state, Object... args) {
        TextureAtlasSprite iconSide = IconUtil.getIconFromStack((ItemStack)args[0]);
        TextureAtlasSprite iconTrim = IconUtil.getIconFromStack((ItemStack)args[1]);

        if (iconTrim == null)
            iconTrim = iconSide;

        if (iconSide == null)
            iconSide = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);
        if (iconTrim == null)
            iconTrim = Chameleon.instance.iconRegistry.getIcon(Register.iconDefaultSide);

        iconParticle = iconSide;

        CommonTrimRenderer drawerRenderer = new CommonTrimRenderer(renderer);
        drawerRenderer.render(null, state, BlockPos.ORIGIN, iconSide, iconTrim);
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
                return CustomTrimModel.fromBlock(state);
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
        public IBakedModel handleItemState (IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            return fromItem(stack);
        }
    }

    private static final ItemHandler itemHandler = new ItemHandler();
}
