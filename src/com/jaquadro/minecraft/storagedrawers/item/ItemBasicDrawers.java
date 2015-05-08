package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBasicDrawers extends ItemMultiTexture
{
    @SideOnly(Side.CLIENT)
    private MeshDefinition meshResolver;

    public ItemBasicDrawers (Block block) {
        super(block, block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
            ItemStack stack = (ItemStack)input;
            return EnumBasicDrawer.byMetadata(stack.getMetadata()).getUnlocalizedName();
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public ItemMeshDefinition getMeshResolver () {
        if (meshResolver == null)
            meshResolver = new MeshDefinition();
        return meshResolver;
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);
        if (tile != null) {
            if (side != EnumFacing.UP && side != EnumFacing.DOWN)
                tile.setDirection(side.ordinal());

            if (tile instanceof TileEntityDrawersStandard) {
                EnumBasicDrawer info = EnumBasicDrawer.byMetadata(stack.getMetadata());
                ((TileEntityDrawersStandard) tile).setDrawerCount(info.getDrawerCount());

                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("material"))
                    tile.setMaterial(stack.getTagCompound().getString("material"));
            }

            tile.setDrawerCapacity(getCapacityForBlock(stack));
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("material")) {
            String key = itemStack.getTagCompound().getString("material");
            list.add(StatCollector.translateToLocalFormatted("storageDrawers.material", StatCollector.translateToLocalFormatted("storageDrawers.material." + key)));
        }
        list.add(StatCollector.translateToLocalFormatted("storageDrawers.drawers.description", getCapacityForBlock(itemStack)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel (ItemStack stack, EntityPlayer player, int useRemaining) {
        return meshResolver.getModelLocation(stack);
    }

    private int getCapacityForBlock (ItemStack itemStack) {
        ConfigManager config = StorageDrawers.config;
        int count = 0;

        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block == ModBlocks.basicDrawers) {
            EnumBasicDrawer info = EnumBasicDrawer.byMetadata(itemStack.getMetadata());
            switch (info) {
                case FULL1:
                    return config.getBlockBaseStorage("fulldrawers1");
                case FULL2:
                    return config.getBlockBaseStorage("fulldrawers2");
                case FULL4:
                    return config.getBlockBaseStorage("fulldrawers4");
                case HALF2:
                    return config.getBlockBaseStorage("halfdrawers2");
                case HALF4:
                    return config.getBlockBaseStorage("halfdrawers4");
                default:
                    return 0;
            }
        }
        else if (block == ModBlocks.compDrawers) {
            return config.getBlockBaseStorage("compDrawers");
        }

        return 0;
    }

    @SideOnly(Side.CLIENT)
    private class MeshDefinition implements ItemMeshDefinition {
        @Override
        public ModelResourceLocation getModelLocation (ItemStack stack) {
            if (stack == null)
                return null;

            EnumBasicDrawer drawer = EnumBasicDrawer.byMetadata(stack.getMetadata());
            EnumFacing facing = EnumFacing.NORTH;

            String material = "oak";
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("material"))
                material = stack.getTagCompound().getString("material");

            String key = StorageDrawers.MOD_ID + ":basicDrawers_" + drawer + "_" + material;
            return new ModelResourceLocation(key, "inventory");
        }
    }
}
