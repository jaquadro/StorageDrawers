package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerSealedModel;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;

public final class BasicDrawerModel
{
    public static class Register extends DefaultRegister
    {
        public Register () {
            super(ModBlocks.basicDrawers);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumBasicDrawer drawer : EnumBasicDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                    for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
                        states.add(ModBlocks.basicDrawers.getDefaultState()
                            .withProperty(BlockDrawers.BLOCK, drawer)
                            .withProperty(BlockDrawers.FACING, dir)
                            .withProperty(BlockDrawers.VARIANT, woodType));
                    }
                }
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return new Model(existingModel);
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return existingModel;
            //return new Model(existingModel);
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            List<ResourceLocation> resource = new ArrayList<ResourceLocation>();
            resource.add(DrawerDecoratorModel.iconClaim);
            resource.add(DrawerDecoratorModel.iconClaimLock);
            resource.add(DrawerDecoratorModel.iconLock);
            resource.add(DrawerDecoratorModel.iconShroudCover);
            resource.add(DrawerDecoratorModel.iconVoid);
            resource.add(DrawerSealedModel.iconTapeCover);
            return resource;
        }
    }

    public static class Model implements IBakedModel
    {
        private IBakedModel parent;
        private IBakedModel proxy;
        private IBlockState stateCache;

        public Model (IBakedModel parent) {
            this.parent = parent;
        }

        private IBakedModel buildProxy (IBlockState state) {
            EnumBasicDrawer drawer = (EnumBasicDrawer)state.getValue(BlockDrawers.BLOCK);
            EnumFacing dir = state.getValue(BlockDrawers.FACING);

            if (!(state instanceof IExtendedBlockState))
                return parent;

            IExtendedBlockState xstate = (IExtendedBlockState)state;
            TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

            if (!DrawerDecoratorModel.shouldHandleState(tile))
                return parent;

            return new DrawerDecoratorModel(parent, xstate, drawer, dir, tile);
        }

        private void setProxy (IBlockState state) {
            stateCache = state;
            proxy = buildProxy(state);
        }

        @Override
        public List<BakedQuad> getQuads (IBlockState state, EnumFacing side, long rand) {
            if (proxy == null || stateCache != state)
                setProxy(state);

            return proxy.getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion () {
            return parent.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d () {
            return parent.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer () {
            return parent.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return parent.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms () {
            return parent.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides () {
            return ItemOverrideList.NONE;
        }
    }

    /*public static class ModelHandler extends DefaultHandler
    {
        private IBakedModel parent;

        public ModelHandler (IBakedModel parent) {
            this.parent = parent;
        }

        @Override
        public IBakedModel handleBlockState (IBlockState state) {
            EnumBasicDrawer drawer = (EnumBasicDrawer)state.getValue(BlockDrawers.BLOCK);
            EnumFacing dir = state.getValue(BlockDrawers.FACING);

            if (!(state instanceof IExtendedBlockState))
                return parent;

            IExtendedBlockState xstate = (IExtendedBlockState)state;
            TileEntityDrawers tile = xstate.getValue(BlockDrawers.TILE);

            if (!DrawerDecoratorModel.shouldHandleState(tile))
                return parent;

            return new DrawerDecoratorModel(parent, xstate, drawer, dir, tile);
        }

        @Override
        public IBakedModel handleItemState (ItemStack stack) {
            if (stack == null)
                return parent;

            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("tile", Constants.NBT.TAG_COMPOUND))
                return parent;

            Block block = Block.getBlockFromItem(stack.getItem());
            IBlockState state = block.getStateFromMeta(stack.getMetadata());

            return new DrawerSealedModel(parent, state, true);
        }

        @Override
        public TextureAtlasSprite getParticleTexture () {
            return parent.getParticleTexture();
        }
    }*/
}
