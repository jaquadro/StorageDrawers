package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.chameleon.resources.IItemMeshResolver;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemBasicDrawers extends ItemDrawers implements IItemMeshResolver
{
    @SideOnly(Side.CLIENT)
    private MeshDefinition meshResolver;

    private Function nameFunction;

    public ItemBasicDrawers (Block block) {
        this(block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                return EnumBasicDrawer.byMetadata(stack.getMetadata()).getUnlocalizedName();
            }
        });
    }

    protected ItemBasicDrawers (Block block, Function function) {
        super(block);
        setHasSubtypes(true);
        nameFunction = function;
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName (ItemStack stack) {
        return super.getUnlocalizedName() + "." + nameFunction.apply(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemMeshDefinition getMeshResolver () {
        if (meshResolver == null)
            meshResolver = new MeshDefinition();
        return meshResolver;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel (ItemStack stack, EntityPlayer player, int useRemaining) {
        return meshResolver.getModelLocation(stack);
    }*/

    @SideOnly(Side.CLIENT)
    private class MeshDefinition implements ItemMeshDefinition {
        @Override
        public ModelResourceLocation getModelLocation (ItemStack stack) {
            if (stack == null)
                return null;

            EnumBasicDrawer drawer = EnumBasicDrawer.byMetadata(stack.getMetadata());

            String material = "oak";
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("material"))
                material = stack.getTagCompound().getString("material");

            String key = StorageDrawers.MOD_ID + ":basicDrawers_" + drawer + "_" + material;
            return new ModelResourceLocation(key, "inventory");
        }
    }
}
