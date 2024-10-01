package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp3;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockFramingTable extends HorizontalDirectionalBlock implements EntityBlock
{
    public static final EnumProperty<EnumFramingTablePart> PART = EnumProperty.create("part", EnumFramingTablePart.class);

    protected static final VoxelShape TABLE_TOP = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape TABLE_BOTTOM = Block.box(1.0D, 0.0D, 1.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape TABLE_SHAPE = Shapes.or(TABLE_TOP, TABLE_BOTTOM);

    public BlockFramingTable (BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(getStateDefinition().any().setValue(PART, EnumFramingTablePart.RIGHT));
    }

    public static Direction getTableDirection (BlockGetter getter, BlockPos pos) {
        BlockState state = getter.getBlockState(pos);
        return state.getBlock() instanceof BlockFramingTable ? state.getValue(FACING) : null;
    }

    private static Direction getNeighborDirection (EnumFramingTablePart part, Direction direction) {
        return part == EnumFramingTablePart.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
    }

    @Override
    public VoxelShape getShape (BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return TABLE_SHAPE;
    }

    @Override
    public void playerWillDestroy (Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            EnumFramingTablePart part = state.getValue(PART);
            if (part == EnumFramingTablePart.RIGHT) {
                BlockPos pos2 = pos.relative(getNeighborDirection(part, state.getValue(FACING)));
                BlockState state2 = level.getBlockState(pos2);
                if (state2.is(this) && state2.getValue(PART) == EnumFramingTablePart.LEFT) {
                    level.setBlock(pos2, Blocks.AIR.defaultBlockState(), 35);
                }
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        Direction dirLeft = direction.getCounterClockWise();
        BlockPos pos = context.getClickedPos();
        BlockPos pos2 = pos.relative(dirLeft);

        return context.getLevel().getBlockState(pos2).canBeReplaced(context)
            && context.getLevel().getWorldBorder().isWithinBounds(pos2)
            ? defaultBlockState().setValue(FACING, direction) : null;
    }

    public static Direction getConnectedDirection (BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(PART) == EnumFramingTablePart.RIGHT ? direction.getOpposite() : direction;
    }

    public static DoubleBlockCombiner.BlockType getBlockType (BlockState state) {
        EnumFramingTablePart part = state.getValue(PART);
        return part == EnumFramingTablePart.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity (BlockPos pos, BlockState state) {
        return new BlockEntityFramingTable(ModBlockEntities.FRAMING_TABLE.get(), pos, state);
    }

    @Override
    public void setPlacedBy (Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (!level.isClientSide) {
            Direction dirLeft = state.getValue(FACING).getCounterClockWise();
            BlockPos pos2 = pos.relative(dirLeft);
            level.setBlock(pos2, state.setValue(PART, EnumFramingTablePart.LEFT), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public InteractionResult use (@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        if (state.getValue(PART) != EnumFramingTablePart.RIGHT)
            pos = pos.relative(getNeighborDirection(state.getValue(PART), state.getValue(FACING)));

        openUI(level, pos, player);
        return InteractionResult.CONSUME;
    }

    private void openUI(Level level, BlockPos pos, Player player) {
        BlockEntityFramingTable blockEntity = WorldUtils.getBlockEntity(level, pos, BlockEntityFramingTable.class);
        NetworkHooks.openScreen((ServerPlayer) player, blockEntity, extraData -> extraData.writeBlockPos(pos));
    }

    /*public static final int[][] leftOffset = new int[][] {{0, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
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
    }*/
}
