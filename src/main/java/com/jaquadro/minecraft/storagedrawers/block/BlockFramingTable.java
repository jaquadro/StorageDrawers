/*package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedTileEntity;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Random;

public class BlockFramingTable extends BlockContainer
{
    public static final int[][] leftOffset = new int[][] {{0, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    public static final int[][] rightOffset = new int[][] {{0, 0}, {0, 0}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool RIGHT_SIDE = PropertyBool.create("right");

    public static final IUnlistedProperty<TileEntityFramingTable> TILE = UnlistedTileEntity.create(TileEntityFramingTable.class);

    public BlockFramingTable (String registryName, String blockName) {
        super(Material.WOOD);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(2.5f);
        setSoundType(SoundType.WOOD);
        setUnlocalizedName(blockName);
        setRegistryName(registryName);

        setDefaultState(blockState.getBaseState().withProperty(RIGHT_SIDE, true)
            .withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        return new TileEntityFramingTable();
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float vx, float vy, float vz) {
        int priX = pos.getX() + getXOff(state);
        int priZ = pos.getZ() + getZOff(state);

        IBlockState targetState = world.getBlockState(new BlockPos(priX, pos.getY(), priZ));
        if (targetState.getBlock() != this || !isRightBlock(targetState))
            return false;

        player.openGui(StorageDrawers.instance, GuiHandler.framingGuiID, world, priX, pos.getY(), priZ);
        return true;
    }

    private int getXOff (IBlockState state) {
        if (isRightBlock(state))
            return 0;

        return rightOffset[getDirection(state).getIndex()][0];
    }

    private int getZOff (IBlockState state) {
        if (isRightBlock(state))
            return 0;

        return rightOffset[getDirection(state).getIndex()][1];
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
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered (IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType (IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean canRenderInLayer (IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged (IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        EnumFacing side = getDirection(state);
        if (isRightBlock(state)) {
            BlockPos otherPos = pos.add(leftOffset[side.getIndex()][0], 0, leftOffset[side.getIndex()][1]);
            if (world.getBlockState(otherPos).getBlock() != this) {
                world.setBlockToAir(pos);
                if (!world.isRemote)
                    dropBlockAsItem(world, pos, state, 0);
            }
        }
        else {
            BlockPos otherPos = pos.add(rightOffset[side.getIndex()][0], 0, rightOffset[side.getIndex()][1]);
            if (world.getBlockState(otherPos).getBlock() != this)
                world.setBlockToAir(pos);
        }
    }

    @Override
    public Item getItemDropped (IBlockState state, Random rand, int fortune) {
        return isPrimaryBlock(state) ? Item.getItemFromBlock(ModBlocks.framingTable) : Item.getItemById(0);
    }

    @Override
    public void dropBlockAsItemWithChance (World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (isPrimaryBlock(state))
            super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumPushReaction getMobilityFlag (IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    public void onBlockHarvested (World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && !isPrimaryBlock(state)) {
            EnumFacing side = getDirection(state);
            pos = pos.add(rightOffset[side.getIndex()][0], 0, rightOffset[side.getIndex()][1]);

            if (world.getBlockState(pos).getBlock() == this)
                world.setBlockToAir(pos);
        }
    }

    @Override
    public void breakBlock (World world, BlockPos pos, IBlockState state) {
        TileEntityFramingTable tile = (TileEntityFramingTable)world.getTileEntity(pos);
        if (tile != null && isPrimaryBlock(state))
            InventoryHelper.dropInventoryItems(world, pos, tile);

        super.breakBlock(world, pos, state);
    }

    public static EnumFacing getDirection (IBlockState state) {
        return state.getValue(FACING);
    }

    public static boolean isRightBlock (IBlockState state) {
        return state.getValue(RIGHT_SIDE);
    }

    public static boolean isPrimaryBlock (IBlockState state) {
        return isRightBlock(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta (int meta) {
        EnumFacing side = EnumFacing.getFront(meta & 0x7);
        if (side.getAxis() == EnumFacing.Axis.Y)
            side = EnumFacing.NORTH;

        return getDefaultState().withProperty(RIGHT_SIDE, (meta & 0x8) == 0).withProperty(FACING, side);
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return (isRightBlock(state) ? 0x8 : 0) | getDirection(state).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { RIGHT_SIDE, FACING }, new IUnlistedProperty[] { TILE });
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = state.getActualState(world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityFramingTable tile = (TileEntityFramingTable)world.getTileEntity(pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)state).withProperty(TILE, tile);
    }
}
*/