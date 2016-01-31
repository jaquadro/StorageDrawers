package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemTrim extends ItemMultiTexture
{
    //@SideOnly(Side.CLIENT)
    //private MeshDefinition meshResolver;

    public ItemTrim (Block block) {
        super(block, block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                return BlockPlanks.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
            }
        });
    }

    protected ItemTrim (Block block, Function function) {
        super(block, block, function);
    }

    @Override
    public boolean doesSneakBypassUse (World world, BlockPos pos, EntityPlayer player) {
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block instanceof BlockDrawers && ((BlockDrawers) block).retrimType() != null)
            return true;

        return false;
    }

    /*@SideOnly(Side.CLIENT)
    public ItemMeshDefinition getMeshResolver () {
        if (meshResolver == null)
            meshResolver = new MeshDefinition();
        return meshResolver;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel (ItemStack stack, EntityPlayer player, int useRemaining) {
        return meshResolver.getModelLocation(stack);
    }

    @SideOnly(Side.CLIENT)
    private class MeshDefinition implements ItemMeshDefinition
    {
        @Override
        public ModelResourceLocation getModelLocation (ItemStack stack) {
            if (stack == null)
                return null;

            String material = "oak";
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("material"))
                material = stack.getTagCompound().getString("material");

            String key = StorageDrawers.MOD_ID + ":trim_" + material;
            return new ModelResourceLocation(key, "inventory");
        }
    }*/
}
