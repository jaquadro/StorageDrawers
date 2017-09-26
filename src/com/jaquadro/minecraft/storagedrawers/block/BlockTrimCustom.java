package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedModelData;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.MaterialModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomTrim;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockTrimCustom extends BlockTrim implements ITileEntityProvider
{
    public static final IUnlistedProperty<MaterialModelData> MAT_MODEL = UnlistedModelData.create(MaterialModelData.class);

    public BlockTrimCustom (String registryName, String name) {
        super(registryName, name);
        hasTileEntity = true;
    }

    @Override
    protected void setDefaultState () {
        setDefaultState(blockState.getBaseState());
    }

    @Override
    public boolean canRenderInLayer (IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube (IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntityTrim tile = getTileEntity(world, pos);
        if (tile == null)
            return ItemCustomTrim.makeItemStack(this, 1, ItemStack.EMPTY, ItemStack.EMPTY);

        return ItemCustomTrim.makeItemStack(this, 1, tile.material().getSide(), tile.material().getTrim());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (CreativeTabs creativeTabs, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this));
    }

    public TileEntityTrim getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntityTrim) ? (TileEntityTrim) tile : null;
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        return new TileEntityTrim();
    }

    @Override
    public void breakBlock (World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean eventReceived (IBlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tile = world.getTileEntity(pos);
        return (tile != null) && tile.receiveClientEvent(id, param);
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] { MAT_MODEL });
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityTrim tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)state).withProperty(MAT_MODEL, new MaterialModelData(tile));
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {
        return getDefaultState();
    }
}
