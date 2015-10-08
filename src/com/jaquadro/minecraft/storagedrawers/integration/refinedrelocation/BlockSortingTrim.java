package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.integration.RefinedRelocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSortingTrim extends BlockTrim implements ITileEntityProvider
{
    @SideOnly(Side.CLIENT)
    protected IIcon[] iconSort;

    public BlockSortingTrim (String name) {
        super(name);
        setCreativeTab(RefinedRelocation.tabStorageDrawers);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileSortingTrim();
    }

    @Override
    public IIcon getIcon (int side, int meta) {
        if (side == 1)
            return iconSort[meta];

        return super.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        String[] subtex = BlockWood.field_150096_a;

        iconSort = new IIcon[subtex.length];

        for (int i = 0; i < subtex.length; i++) {
            iconSort[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_sort");
        }
    }

    public static boolean upgradeToSorting(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Block sortingBlock = SortingBlockRegistry.resolveSortingBlock(block);
        if (sortingBlock != null) {
            world.setBlock(x, y, z, sortingBlock, meta, 3);
        }
        return true;
    }
}
