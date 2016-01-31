package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

public class BlockController extends BlockContainer implements INetworked
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockController (String name) {
        super(Material.rock);

        setUnlocalizedName(name);
        this.useNeighborBrightness = true;

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setStepSound(Block.soundTypeStone);
        setLightOpacity(255);
        setBlockBounds(0, 0, 0, 1, 1, 1);
        setTickRandomly(true);

        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    public int getRenderType () {
        return 3;
    }

    @Override
    public int tickRate (World world) {
        return 100;
    }

    @Override
    public Item getItemDropped (IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void onBlockAdded (World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            Block blockNorth = world.getBlockState(pos.north()).getBlock();
            Block blockSouth = world.getBlockState(pos.south()).getBlock();
            Block blockWest = world.getBlockState(pos.west()).getBlock();
            Block blockEast = world.getBlockState(pos.east()).getBlock();

            EnumFacing facing = state.getValue(FACING);

            if (facing == EnumFacing.NORTH && blockNorth.isFullBlock() && !blockSouth.isFullBlock())
                facing = EnumFacing.SOUTH;
            if (facing == EnumFacing.SOUTH && blockSouth.isFullBlock() && !blockNorth.isFullBlock())
                facing = EnumFacing.NORTH;
            if (facing == EnumFacing.WEST && blockWest.isFullBlock() && !blockEast.isFullBlock())
                facing = EnumFacing.EAST;
            if (facing == EnumFacing.EAST && blockEast.isFullBlock() && !blockWest.isFullBlock())
                facing = EnumFacing.WEST;

            world.setBlockState(pos, state.withProperty(FACING, facing), 2);
        }
    }

    @Override
    public IBlockState onBlockPlaced (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        world.setBlockState(pos, state.withProperty(FACING, entity.getHorizontalFacing().getOpposite()), 2);

        if (itemStack.hasDisplayName()) {
            TileEntityController tile = getTileEntity(world, pos);
            if (tile != null)
                tile.setInventoryName(itemStack.getDisplayName());
        }
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        EnumFacing blockDir = state.getValue(FACING);
        TileEntityController te = getTileEntitySafe(world, pos);

        ItemStack item = player.inventory.getCurrentItem();
        if (item != null && item.getItem() != null) {
            if (item.getItem() == ModItems.shroudKey) {
                if (!world.isRemote)
                    te.toggleShroud();
                return true;
            }
            else if (item.getItem() == ModItems.drawerKey) {
                if (!world.isRemote)
                    te.toggleLock(EnumSet.allOf(LockAttribute.class), LockAttribute.LOCK_POPULATED);
                return true;
            }
        }

        if (blockDir != side)
            return false;

        if (!world.isRemote)
            te.interactPutItemsIntoInventory(player);

        return true;
    }

    @Override
    public boolean isSideSolid (IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        if (state == null)
            return true;

        EnumFacing facing = state.getValue(FACING);
        return side != facing;
    }

    @Override
    public void updateTick (World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote)
            return;

        TileEntityController te = getTileEntity(world, pos);
        if (te == null)
            return;

        te.updateCache();

        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    @Override
    public IBlockState getStateForEntityRender (IBlockState state) {
        return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {
        EnumFacing facing = EnumFacing.getFront(meta);
        if (facing.getAxis() == EnumFacing.Axis.Y)
            facing = EnumFacing.NORTH;

        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return (state.getValue(FACING)).getIndex();
    }

    @Override
    protected BlockState createBlockState () {
        return new BlockState(this, FACING);
    }

    @Override
    public TileEntityController createNewTileEntity (World world, int meta) {
        return new TileEntityController();
    }

    public TileEntityController getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntityController) ? (TileEntityController) tile : null;
    }

    public TileEntityController getTileEntitySafe (World world, BlockPos pos) {
        TileEntityController tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createNewTileEntity(world, 0);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }
}
