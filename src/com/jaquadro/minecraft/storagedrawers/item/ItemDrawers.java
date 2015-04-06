package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
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

public class ItemDrawers extends ItemMultiTexture
{
    public ItemDrawers (Block block) {
        super(block, block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                return BlockPlanks.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
            }
        });
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);
        if (tile != null) {
            if (side != EnumFacing.UP && side != EnumFacing.DOWN)
                tile.setDirection(side.ordinal());

            BlockDrawers block = (BlockDrawers) theBlock;
            if (tile instanceof TileEntityDrawersStandard)
                ((TileEntityDrawersStandard)tile).setDrawerCount(block.drawerCount);

            tile.setDrawerCapacity(getCapacityForBlock(block));
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        list.add(StatCollector.translateToLocalFormatted("storageDrawers.drawers.description", getCapacityForBlock(block)));
    }

    private int getCapacityForBlock (Block block) {
        ConfigManager config = StorageDrawers.config;
        int count = 0;

        if (block == ModBlocks.fullDrawers1)
            count = config.getBlockBaseStorage("fulldrawers1");
        else if (block == ModBlocks.fullDrawers2)
            count = config.getBlockBaseStorage("fulldrawers2");
        else if (block == ModBlocks.fullDrawers4)
            count = config.getBlockBaseStorage("fulldrawers4");
        else if (block == ModBlocks.halfDrawers2)
            count = config.getBlockBaseStorage("halfdrawers2");
        else if (block == ModBlocks.halfDrawers4)
            count = config.getBlockBaseStorage("halfdrawers4");
        else if (block == ModBlocks.compDrawers)
            count = config.getBlockBaseStorage("compDrawers");

        return count;
    }
}
