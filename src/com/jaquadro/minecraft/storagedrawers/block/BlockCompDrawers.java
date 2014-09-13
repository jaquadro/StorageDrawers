package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockCompDrawers extends BlockDrawers
{
    @SideOnly(Side.CLIENT)
    private IIcon[] iconFront;

    @SideOnly(Side.CLIENT)
    private IIcon[][] iconFrontInd;

    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconSideEtched;

    public BlockCompDrawers (String blockName) {
        super(Material.rock, blockName, 3, false);

        setStepSound(Block.soundTypeStone);
    }

    @Override
    protected int getDrawerSlot (int side, float hitX, float hitY, float hitZ) {
        if (hitTop(hitY))
            return 0;

        if (hitLeft(side, hitX, hitZ))
            return 1;
        else
            return 2;
    }

    @Override
    public TileEntityDrawersBase createNewTileEntity (World world, int meta) {
        return new TileEntityCompDrawers();
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        switch (side) {
            case 0:
            case 1:
                return iconSide;
            case 4:
                return iconFront[0];
            default:
                return iconSideEtched;
        }
    }

    @Override
    public IIcon getIcon (IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityDrawersBase tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null)
            return iconFront[0];

        if (side == tile.getDirection()) {
            if (tile.getStatusLevel() != 3) {
                if (tile.getItemStackSize(2) > 0)
                    return iconFront[2];
                else if (tile.getItemStackSize(1) > 0)
                    return iconFront[1];
                else
                    return iconFront[0];
            }
            else {
                int plev = 0;
                if (tile.getItemCapacity(0) != 0) {
                    float pfull = (float) tile.getItemCount(0) / tile.getItemCapacity(0);
                    plev = MathHelper.clamp_int((int) (pfull * 6.99), 0, 6);
                }

                if (tile.getItemStackSize(2) > 0)
                    return iconFrontInd[2][plev];
                else if (tile.getItemStackSize(1) > 0)
                    return iconFrontInd[1][plev];
                else
                    return iconFrontInd[0][plev];
            }
        }

        switch (side) {
            case 0:
            case 1:
                return iconSide;
            default:
                return iconSideEtched;
        }
    }

    @Override
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        iconFront = new IIcon[3];
        iconFrontInd = new IIcon[3][7];

        for (int i = 0; i < 3; i++) {
            iconFront[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_front_" + i);
            iconFrontInd[i][0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_front_" + i + "_ind");
            for (int j = 1; j <= 6; j++)
                iconFrontInd[i][j] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_front_" + i + "_ind" + j);
        }

        iconSide = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side");
        iconSideEtched = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side_2");
    }
}
