package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer
{
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    public static final PropertyBool CONDITIONAL = PropertyBool.create("conditional");

    public BlockCommandBlock(MapColor color)
    {
        super(Material.IRON, color);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(CONDITIONAL, Boolean.valueOf(false)));
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        TileEntityCommandBlock tileentitycommandblock = new TileEntityCommandBlock();
        tileentitycommandblock.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
        return tileentitycommandblock;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityCommandBlock)
            {
                TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
                boolean flag = worldIn.isBlockPowered(pos);
                boolean flag1 = tileentitycommandblock.isPowered();
                boolean flag2 = tileentitycommandblock.isAuto();

                if (flag && !flag1)
                {
                    tileentitycommandblock.setPowered(true);

                    if (tileentitycommandblock.getMode() != TileEntityCommandBlock.Mode.SEQUENCE && !flag2)
                    {
                        boolean flag3 = !tileentitycommandblock.isConditional() || this.isNextToSuccessfulCommandBlock(worldIn, pos, state);
                        tileentitycommandblock.setConditionMet(flag3);
                        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));

                        if (flag3)
                        {
                            this.propagateUpdate(worldIn, pos);
                        }
                    }
                }
                else if (!flag && flag1)
                {
                    tileentitycommandblock.setPowered(false);
                }
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityCommandBlock)
            {
                TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
                CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();
                boolean flag = !StringUtils.isNullOrEmpty(commandblockbaselogic.getCommand());
                TileEntityCommandBlock.Mode tileentitycommandblock$mode = tileentitycommandblock.getMode();
                boolean flag1 = !tileentitycommandblock.isConditional() || this.isNextToSuccessfulCommandBlock(worldIn, pos, state);
                boolean flag2 = tileentitycommandblock.isConditionMet();
                boolean flag3 = false;

                if (tileentitycommandblock$mode != TileEntityCommandBlock.Mode.SEQUENCE && flag2 && flag)
                {
                    commandblockbaselogic.trigger(worldIn);
                    flag3 = true;
                }

                if (tileentitycommandblock.isPowered() || tileentitycommandblock.isAuto())
                {
                    if (tileentitycommandblock$mode == TileEntityCommandBlock.Mode.SEQUENCE && flag1 && flag)
                    {
                        commandblockbaselogic.trigger(worldIn);
                        flag3 = true;
                    }

                    if (tileentitycommandblock$mode == TileEntityCommandBlock.Mode.AUTO)
                    {
                        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));

                        if (flag1)
                        {
                            this.propagateUpdate(worldIn, pos);
                        }
                    }
                }

                if (!flag3)
                {
                    commandblockbaselogic.setSuccessCount(0);
                }

                tileentitycommandblock.setConditionMet(flag1);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
    }

    /**
     * Checks whether the command block at a given position is adjacent to another one that executed successfully.
     */
    public boolean isNextToSuccessfulCommandBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        TileEntity tileentity = worldIn.getTileEntity(pos.offset(enumfacing.getOpposite()));
        return tileentity instanceof TileEntityCommandBlock && ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() > 0;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 1;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock && playerIn.canUseCommandBlock())
        {
            playerIn.displayGuiCommandBlock((TileEntityCommandBlock)tileentity);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCommandBlock)
        {
            TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
            CommandBlockBaseLogic commandblockbaselogic = tileentitycommandblock.getCommandBlockLogic();

            if (stack.hasDisplayName())
            {
                commandblockbaselogic.setName(stack.getDisplayName());
            }

            if (!worldIn.isRemote)
            {
                NBTTagCompound nbttagcompound = stack.getTagCompound();

                if (nbttagcompound == null || !nbttagcompound.hasKey("BlockEntityTag", 10))
                {
                    commandblockbaselogic.setTrackOutput(worldIn.getGameRules().getBoolean("sendCommandFeedback"));
                    tileentitycommandblock.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
                }

                if (tileentitycommandblock.getMode() == TileEntityCommandBlock.Mode.SEQUENCE)
                {
                    boolean flag = worldIn.isBlockPowered(pos);
                    tileentitycommandblock.setPowered(flag);
                }
            }
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(CONDITIONAL, Boolean.valueOf((meta & 8) != 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex() | (((Boolean)state.getValue(CONDITIONAL)).booleanValue() ? 8 : 0);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, CONDITIONAL});
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(pos, placer)).withProperty(CONDITIONAL, Boolean.valueOf(false));
    }

    public void propagateUpdate(World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == Blocks.COMMAND_BLOCK || iblockstate.getBlock() == Blocks.REPEATING_COMMAND_BLOCK)
        {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(pos);
            blockpos$mutableblockpos.move((EnumFacing)iblockstate.getValue(FACING));

            for (TileEntity tileentity = worldIn.getTileEntity(blockpos$mutableblockpos); tileentity instanceof TileEntityCommandBlock; tileentity = worldIn.getTileEntity(blockpos$mutableblockpos))
            {
                TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;

                if (tileentitycommandblock.getMode() != TileEntityCommandBlock.Mode.SEQUENCE)
                {
                    break;
                }

                IBlockState iblockstate1 = worldIn.getBlockState(blockpos$mutableblockpos);
                Block block = iblockstate1.getBlock();

                if (block != Blocks.CHAIN_COMMAND_BLOCK || worldIn.isUpdateScheduled(blockpos$mutableblockpos, block))
                {
                    break;
                }

                worldIn.scheduleUpdate(new BlockPos(blockpos$mutableblockpos), block, this.tickRate(worldIn));
                blockpos$mutableblockpos.move((EnumFacing)iblockstate1.getValue(FACING));
            }
        }
    }
}