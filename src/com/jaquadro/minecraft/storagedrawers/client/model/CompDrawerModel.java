package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerSealedModel;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public final class CompDrawerModel
{
    public static class Register extends DefaultRegister
    {
        public Register () {
            super(ModBlocks.compDrawers);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumCompDrawer drawer : EnumCompDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                    states.add(ModBlocks.compDrawers.getDefaultState()
                        .withProperty(BlockCompDrawers.SLOTS, drawer)
                        .withProperty(BlockCompDrawers.FACING, dir));
                }
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return existingModel; // new ModelHandler(existingModel);
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return existingModel; // new ModelHandler(existingModel);
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
            EnumCompDrawer drawer = (EnumCompDrawer)state.getValue(BlockCompDrawers.SLOTS);
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
