package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

public class BlockController extends HorizontalBlock implements INetworked
{
    public BlockController (Block.Properties properties) {
        super(properties);
    }

    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public int tickRate (IWorldReader world) {
        return 100;
    }

    @Override
    public BlockState getStateForPlacement (BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        Direction blockDir = state.get(HORIZONTAL_FACING);
        TileEntityController te = getTileEntitySafe(world, pos);

        ItemStack item = player.inventory.getCurrentItem();
        if (!item.isEmpty() && toggle(world, pos, player, item.getItem()))
            return true;

        if (blockDir != hit.getFace())
            return false;

        if (!world.isRemote) {
            if (CommonConfig.GENERAL.debugTrace.get() && item.isEmpty())
                te.printDebugInfo();

            te.interactPutItemsIntoInventory(player);
        }

        return true;
    }

    public boolean toggle (World world, BlockPos pos, PlayerEntity player, Item item) {
        if (world.isRemote || item == null)
            return false;

        if (item == ModItems.DRAWER_KEY)
            toggle(world, pos, player, EnumKeyType.DRAWER);
        //else if (item == ModItems.shroudKey)
        //    toggle(world, pos, player, EnumKeyType.CONCEALMENT);
        else if (item == ModItems.QUANTIFY_KEY)
            toggle(world, pos, player, EnumKeyType.QUANTIFY);
        //else if (item == ModItems.personalKey)
        //    toggle(world, pos, player, EnumKeyType.PERSONAL);
        else
            return false;

        return true;
    }

    public void toggle (World world, BlockPos pos, PlayerEntity player, EnumKeyType keyType) {
        if (world.isRemote)
            return;

        TileEntityController te = getTileEntitySafe(world, pos);
        if (te == null)
            return;

        switch (keyType) {
            case DRAWER:
                te.toggleLock(EnumSet.allOf(LockAttribute.class), LockAttribute.LOCK_POPULATED, player.getGameProfile());
                break;
            //case CONCEALMENT:
            //    te.toggleShroud(player.getGameProfile());
            //    break;
            case QUANTIFY:
                te.toggleQuantified(player.getGameProfile());
                break;
            //case PERSONAL:
            //    String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
            //    ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

            //    te.toggleProtection(player.getGameProfile(), provider);
            //    break;
        }
    }

    @Override
    public void tick (BlockState state, World world, BlockPos pos, Random rand) {
        if (world.isRemote)
            return;

        TileEntityController te = getTileEntitySafe(world, pos);
        if (te == null)
            return;

        te.updateCache();

        world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world));
    }

    @Override
    public boolean hasTileEntity (BlockState state) {
        return true;
    }

    @Override
    public TileEntityController createTileEntity (BlockState state, IBlockReader world) {
        return new TileEntityController();
    }

    public TileEntityController getTileEntity (IBlockReader blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntityController) ? (TileEntityController) tile : null;
    }

    public TileEntityController getTileEntitySafe (World world, BlockPos pos) {
        TileEntityController tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createTileEntity(world.getBlockState(pos), world);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }
}
