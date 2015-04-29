package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.integration.RefinedRelocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSortingCompDrawers extends BlockCompDrawers
{
    @SideOnly(Side.CLIENT)
    IIcon iconSort;

    public BlockSortingCompDrawers (String blockName) {
        super(blockName);

        setCreativeTab(RefinedRelocation.tabStorageDrawers);
    }

    public static boolean upgradeToSorting (World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityDrawersComp))
            return false;

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        TileEntityDrawersComp oldDrawer = (TileEntityDrawersComp)tile;
        TileSortingDrawersComp newDrawer = new TileSortingDrawersComp();

        NBTTagCompound tag = new NBTTagCompound();
        oldDrawer.writeToNBT(tag);
        newDrawer.readFromNBT(tag);

        world.removeTileEntity(x, y, z);
        world.setBlockToAir(x, y, z);

        world.setBlock(x, y, z, RefinedRelocation.compDrawers, meta, 3);

        world.setTileEntity(x, y, z, newDrawer);

        return true;
    }

    @Override
    public TileSortingDrawersComp createNewTileEntity (World world, int meta) {
        return new TileSortingDrawersComp();
    }

    @Override
    public IIcon getIcon (int side, int meta) {
        if (side == 1)
            return iconSort;

        return super.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (side == 1)
            return iconSort;

        return super.getIcon(blockAccess, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        iconSort = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_sort");
    }
}