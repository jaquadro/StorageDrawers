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
import net.minecraft.block.BlockWood;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockCompDrawers extends BlockDrawers
{
    @SideOnly(Side.CLIENT)
    private IIcon[][] iconFront;

    public BlockCompDrawers (String blockName) {
        super(blockName, 3, false);
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
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        if (side == 4) {
            meta %= BlockWood.field_150096_a.length;
            return iconFront[0][meta];
        }

        return super.getIcon(side, meta);
    }

    @Override
    public IIcon getIcon (IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityDrawersBase tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null || side == tile.getDirection()) {
            int meta = blockAccess.getBlockMetadata(x, y, z) % BlockWood.field_150096_a.length;

            if (tile == null)
                return iconFront[0][meta];
            else if (tile.getItemStackSize(2) > 0)
                return iconFront[2][meta];
            else if (tile.getItemStackSize(1) > 0)
                return iconFront[1][meta];
            else
                return iconFront[0][meta];
        }

        return super.getIcon(blockAccess, x, y, z, side);
    }

    @Override
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);
        String[] subtex = BlockWood.field_150096_a;

        iconFront = new IIcon[3][subtex.length];

        for (int i = 0; i < subtex.length; i++) {
            iconFront[0][i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_comp_0");
            iconFront[1][i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_comp_1");
            iconFront[2][i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_comp_2");
        }
    }
}
