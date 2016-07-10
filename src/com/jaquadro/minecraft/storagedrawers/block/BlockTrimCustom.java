package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedTileEntity;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomTrim;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class BlockTrimCustom extends BlockTrim implements ITileEntityProvider
{
    public static final IUnlistedProperty<TileEntityTrim> TILE = UnlistedTileEntity.create(TileEntityTrim.class);

    public BlockTrimCustom (String name) {
        super(name);
        isBlockContainer = true;
    }

    @Override
    protected void setDefaultState () {
        setDefaultState(blockState.getBaseState());
    }

    @Override
    public boolean canRenderInLayer (EnumWorldBlockLayer layer) {
        return layer == EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntityTrim tile = getTileEntity(world, pos);
        if (tile == null)
            return ItemCustomTrim.makeItemStack(this, 1, null, null);

        return ItemCustomTrim.makeItemStack(this, 1, tile.getMaterialSide(), tile.getMaterialTrim());
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        if (StorageDrawers.config.cache.addonShowVanilla)
            list.add(new ItemStack(item));
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
    public boolean onBlockEventReceived (World world, BlockPos pos, IBlockState state, int eventNum, int eventArg) {
        super.onBlockEventReceived(world, pos, state, eventNum, eventArg);
        TileEntity tile = world.getTileEntity(pos);
        return (tile != null) && tile.receiveClientEvent(eventNum, eventArg);
    }

    @Override
    protected BlockState createBlockState () {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] { TILE });
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityTrim tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)state).withProperty(TILE, tile);
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
