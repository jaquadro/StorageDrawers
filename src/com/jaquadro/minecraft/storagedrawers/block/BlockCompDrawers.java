package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class BlockCompDrawers extends BlockDrawers
{
    /*@SideOnly(Side.CLIENT)
    private IIcon[] iconFront;

    @SideOnly(Side.CLIENT)
    private IIcon[][] iconFrontInd;

    @SideOnly(Side.CLIENT)
    private IIcon iconTrim;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconSideEtched;*/

    public BlockCompDrawers (String blockName) {
        super(Material.rock, blockName);

        setStepSound(Block.soundTypeStone);
    }

    @Override
    protected void initDefaultState () {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected int getDrawerCount (IBlockState state) {
        return 3;
    }

    @Override
    protected boolean isHalfDepth (IBlockState state) {
        return false;
    }

    @Override
    protected int getDrawerSlot (int drawerCount, int side, float hitX, float hitY, float hitZ) {
        if (hitTop(hitY))
            return 0;

        if (hitLeft(side, hitX, hitZ))
            return 1;
        else
            return 2;
    }

    @Override
    public TileEntityDrawers createNewTileEntity (World world, int meta) {
        return new TileEntityDrawersComp();
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return 0;
    }

    @Override
    protected BlockState createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { FACING }, new IUnlistedProperty[0]);
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            EnumFacing facing = EnumFacing.getFront(tile.getDirection());
            if (facing.getAxis() == EnumFacing.Axis.Y)
                facing = EnumFacing.NORTH;

            return ((IExtendedBlockState) state).withProperty(FACING, facing);
        }
        return state;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconTrim (int meta) {
        return iconTrim;
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
        TileEntityDrawers tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null)
            return iconFront[0];

        if (side == tile.getDirection()) {
            if (tile.getStatusLevel() == 0) {
                if (tile.isDrawerEnabled(2) && tile.getDrawer(2).getStoredItemStackSize() > 0)
                    return iconFront[2];
                else if (tile.isDrawerEnabled(1) && tile.getDrawer(1).getStoredItemStackSize() > 0)
                    return iconFront[1];
                else
                    return iconFront[0];
            }
            else {
                IDrawer main = tile.getDrawer(0);
                int plev = 0;

                if (tile.getStatusLevel() == 1)
                    plev = (main.getMaxCapacity() > 0 && main.getRemainingCapacity() == 0) ? 6 : 0;
                else if (main.getMaxCapacity() > 0) {
                    float pfull = (float) main.getStoredItemCount() / main.getMaxCapacity();
                    plev = MathHelper.clamp_int((int) (pfull * 6.99), 0, 6);
                }

                if (tile.isDrawerEnabled(2) && tile.getDrawer(2).getStoredItemStackSize() > 0)
                    return iconFrontInd[2][plev];
                else if (tile.isDrawerEnabled(1) && tile.getDrawer(1).getStoredItemStackSize() > 0)
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

        iconTrim = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_trim");
        iconSide = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side");
        iconSideEtched = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side_2");
    }*/
}
