package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public boolean doesSneakBypassUse (World world, int x, int y, int z, EntityPlayer player) {
        Block block = world.getBlock(x, y, z);
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
