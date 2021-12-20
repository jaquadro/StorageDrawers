package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockController extends HorizontalDirectionalBlock implements INetworked, EntityBlock
{
    public BlockController (BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use (BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Direction blockDir = state.getValue(FACING);
        TileEntityController te = getTileEntitySafe(world, pos);

        ItemStack item = player.getInventory().getSelected();
        if (!item.isEmpty() && toggle(world, pos, player, item.getItem()))
            return InteractionResult.SUCCESS;

        if (blockDir != hit.getDirection())
            return InteractionResult.CONSUME;

        if (!world.isClientSide) {
            if (CommonConfig.GENERAL.debugTrace.get() && item.isEmpty())
                te.printDebugInfo();

            te.interactPutItemsIntoInventory(player);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean toggle (Level world, BlockPos pos, Player player, Item item) {
        if (world.isClientSide || item == null)
            return false;

        if (item == ModItems.DRAWER_KEY)
            toggle(world, pos, player, EnumKeyType.DRAWER);
        else if (item == ModItems.SHROUD_KEY)
            toggle(world, pos, player, EnumKeyType.CONCEALMENT);
        else if (item == ModItems.QUANTIFY_KEY)
            toggle(world, pos, player, EnumKeyType.QUANTIFY);
        //else if (item == ModItems.personalKey)
        //    toggle(world, pos, player, EnumKeyType.PERSONAL);
        else
            return false;

        return true;
    }

    public void toggle (Level world, BlockPos pos, Player player, EnumKeyType keyType) {
        if (world.isClientSide)
            return;

        TileEntityController te = getTileEntitySafe(world, pos);
        if (te == null)
            return;

        switch (keyType) {
            case DRAWER:
                te.toggleLock(EnumSet.allOf(LockAttribute.class), LockAttribute.LOCK_POPULATED, player.getGameProfile());
                break;
            case CONCEALMENT:
                te.toggleShroud(player.getGameProfile());
                break;
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
    public void tick (BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        if (world.isClientSide)
            return;

        TileEntityController te = getTileEntitySafe(world, pos);
        if (te == null)
            return;

        te.updateCache();

        world.getBlockTicks().scheduleTick(pos, this, 100);
    }

    @Override
    public TileEntityController newBlockEntity (BlockPos pos, BlockState state) {
        return new TileEntityController(pos, state);
    }

    public TileEntityController getTileEntity (BlockGetter blockAccess, BlockPos pos) {
        BlockEntity tile = blockAccess.getBlockEntity(pos);
        return (tile instanceof TileEntityController) ? (TileEntityController) tile : null;
    }

    public TileEntityController getTileEntitySafe (Level world, BlockPos pos) {
        TileEntityController tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = newBlockEntity(pos, world.getBlockState(pos));
            world.setBlockEntity(tile);
        }

        return tile;
    }
}
