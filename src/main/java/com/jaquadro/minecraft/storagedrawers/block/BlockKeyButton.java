package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityKeyButton;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockKeyButton extends Block implements ITileEntityProvider
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyEnum<EnumKeyType> VARIANT = PropertyEnum.create("variant", EnumKeyType.class);

    private static final double U3 = 0.1875;
    private static final double U13 = 0.8125;

    protected static final AxisAlignedBB AABB_DOWN_OFF = new AxisAlignedBB(U3, 0.875D, U3, U13, 1.0D, U13);
    protected static final AxisAlignedBB AABB_UP_OFF = new AxisAlignedBB(U3, 0.0D, U3, U13, 0.125D, U13);
    protected static final AxisAlignedBB AABB_NORTH_OFF = new AxisAlignedBB(U3, U3, 0.875D, U13, U13, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH_OFF = new AxisAlignedBB(U3, U3, 0.0D, U13, U13, 0.125D);
    protected static final AxisAlignedBB AABB_WEST_OFF = new AxisAlignedBB(0.875D, U3, U3, 1.0D, U13, U13);
    protected static final AxisAlignedBB AABB_EAST_OFF = new AxisAlignedBB(0.0D, U3, U3, 0.125D, U13, U13);
    protected static final AxisAlignedBB AABB_DOWN_ON = new AxisAlignedBB(U3, 0.937D, U3, U13, 1.0D, U13);
    protected static final AxisAlignedBB AABB_UP_ON = new AxisAlignedBB(U3, 0.0D, U3, U13, 0.0625D, U13);
    protected static final AxisAlignedBB AABB_NORTH_ON = new AxisAlignedBB(U3, U3, 0.937D, U13, U13, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH_ON = new AxisAlignedBB(U3, U3, 0.0D, U13, U13, 0.0625D);
    protected static final AxisAlignedBB AABB_WEST_ON = new AxisAlignedBB(0.937D, U3, U3, 1.0D, U13, U13);
    protected static final AxisAlignedBB AABB_EAST_ON = new AxisAlignedBB(0.0D, U3, U3, 0.0625D, U13, U13);

    public BlockKeyButton (String registryName, String blockName) {
        super(Material.CIRCUITS);

        setHardness(5);
        setUnlocalizedName(blockName);
        setRegistryName(registryName);
        setSoundType(SoundType.STONE);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setTickRandomly(true);

        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(POWERED, false)
            .withProperty(VARIANT, EnumKeyType.DRAWER));
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox (IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public int tickRate (World worldIn) {
        return 5;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube (IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube (IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer () {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean canPlaceBlockOnSide (World worldIn, BlockPos pos, EnumFacing side) {
        return canPlaceBlock(worldIn, pos, side.getOpposite());
    }

    @Override
    public boolean canPlaceBlockAt (World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canPlaceBlock(worldIn, pos, facing))
                return true;
        }

        return false;
    }

    protected static boolean canPlaceBlock (World world, BlockPos pos, EnumFacing facing) {
        BlockPos blockPos = pos.offset(facing);
        return world.getBlockState(blockPos).isSideSolid(world, blockPos, facing.getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (canPlaceBlock(world, pos, facing.getOpposite()))
            return getStateFromMeta(meta).withProperty(FACING, facing).withProperty(POWERED, false);

        return getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN).withProperty(POWERED, false);
    }

    @Override
    public void onBlockPlacedBy (World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
        TileEntityKeyButton tile = getTileEntity(worldIn, pos);
        if (tile != null)
            tile.setDirection(state.getValue(FACING));

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        state = getActualState(state, worldIn, pos);
        if (checkForDrop(worldIn, pos, state) && !canPlaceBlock(worldIn, pos, state.getValue(FACING).getOpposite())) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean checkForDrop (World world, BlockPos pos, IBlockState state) {
        if (canPlaceBlockAt(world, pos))
            return true;

        dropBlockAsItem(world, pos, state, 0);
        world.setBlockToAir(pos);
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {
        state = getActualState(state, source, pos);
        EnumFacing facing = state.getValue(FACING);
        boolean powered = state.getValue(POWERED);

        switch (facing) {
            case EAST:
                return powered ? AABB_EAST_ON : AABB_EAST_OFF;
            case WEST:
                return powered ? AABB_WEST_ON : AABB_WEST_OFF;
            case SOUTH:
                return powered ? AABB_SOUTH_ON : AABB_SOUTH_OFF;
            case NORTH:
                return powered ? AABB_NORTH_ON : AABB_NORTH_OFF;
            case UP:
                return powered ? AABB_UP_ON : AABB_UP_OFF;
            case DOWN:
                return powered ? AABB_DOWN_ON : AABB_DOWN_OFF;
        }

        return NULL_AABB;
    }

    @Override
    public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        state = getActualState(state, worldIn, pos);
        if (state.getValue(POWERED))
            return true;

        TileEntityKeyButton tile = getTileEntity(worldIn, pos);
        if (tile != null)
            tile.setPowered(true);

        worldIn.setBlockState(pos, state.withProperty(POWERED, true), 3);
        worldIn.markBlockRangeForRenderUpdate(pos, pos);
        worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        notifyNeighbors(worldIn, pos, state.getValue(FACING));
        worldIn.scheduleUpdate(pos, this, tickRate(worldIn));

        BlockPos targetPos = pos.offset(state.getValue(FACING).getOpposite());
        Block target = worldIn.getBlockState(targetPos).getBlock();
        if (target instanceof BlockController) {
            BlockController controller = (BlockController)target;
            controller.toggle(worldIn, targetPos, playerIn, state.getValue(VARIANT));
        }
        else if (target instanceof BlockSlave) {
            BlockSlave slave = (BlockSlave)target;
            slave.toggle(worldIn, targetPos, playerIn, state.getValue(VARIANT));
        }

        return true;
    }

    @Override
    public void breakBlock (World worldIn, BlockPos pos, IBlockState state) {
        state = getActualState(state, worldIn, pos);
        if (state.getValue(POWERED))
            notifyNeighbors(worldIn, pos, state.getValue(FACING));

        worldIn.removeTileEntity(pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean eventReceived (IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, param);
    }

    @Override
    @Nonnull
    public TileEntity createNewTileEntity (World worldIn, int meta) {
        return new TileEntityKeyButton();
    }

    public TileEntityKeyButton getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntityKeyButton) ? (TileEntityKeyButton) tile : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower (IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        blockState = getActualState(blockState, blockAccess, pos);
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower (IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        blockState = getActualState(blockState, blockAccess, pos);
        return !blockState.getValue(POWERED) ? 0 : (blockState.getValue(FACING) == side ? 15 : 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower (IBlockState state) {
        return true;
    }

    @Override
    public void randomTick (World worldIn, BlockPos pos, IBlockState state, Random random) { }

    @Override
    public void updateTick (World worldIn, BlockPos pos, IBlockState state, Random rand) {
        state = getActualState(state, worldIn, pos);
        if (worldIn.isRemote || !state.getValue(POWERED))
            return;

        TileEntityKeyButton tile = getTileEntity(worldIn, pos);
        if (tile != null)
            tile.setPowered(false);

        worldIn.setBlockState(pos, state.withProperty(POWERED, false));
        notifyNeighbors(worldIn, pos, state.getValue(FACING));
        worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
        worldIn.markBlockRangeForRenderUpdate(pos, pos);
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this, false);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withRotation (IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withMirror (IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new BlockStateContainer(this, FACING, POWERED, VARIANT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState (IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityKeyButton tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return state;

        return state.withProperty(FACING, tile.getDirection()).withProperty(POWERED, tile.isPowered());
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta (int meta) {
        return getDefaultState().withProperty(VARIANT, EnumKeyType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public int damageDropped (IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks (CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumKeyType type : EnumKeyType.values()) {
            list.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }
}
