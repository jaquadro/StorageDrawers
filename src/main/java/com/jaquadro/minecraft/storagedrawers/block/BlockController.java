package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Random;

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
    @NotNull
    public InteractionResult use (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        Direction blockDir = state.getValue(FACING);
        TileEntityController tileEntityController = WorldUtils.getBlockEntity(level, pos, TileEntityController.class);
        if (tileEntityController == null)
            return InteractionResult.FAIL;

        ItemStack item = player.getInventory().getSelected();
        if (!item.isEmpty() && toggle(level, pos, player, item.getItem()))
            return InteractionResult.SUCCESS;

        if (blockDir != hit.getDirection())
            return InteractionResult.CONSUME;

        if (!level.isClientSide) {
            if (CommonConfig.GENERAL.debugTrace.get() && item.isEmpty())
                tileEntityController.printDebugInfo();

            tileEntityController.interactPutItemsIntoInventory(player);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean toggle (Level world, BlockPos pos, Player player, Item item) {
        if (world.isClientSide || item == null)
            return false;

        if (item == ModItems.DRAWER_KEY.get())
            toggle(world, pos, player, EnumKeyType.DRAWER);
        else if (item == ModItems.SHROUD_KEY.get())
            toggle(world, pos, player, EnumKeyType.CONCEALMENT);
        else if (item == ModItems.QUANTIFY_KEY.get())
            toggle(world, pos, player, EnumKeyType.QUANTIFY);
        //else if (item == ModItems.personalKey)
        //    toggle(world, pos, player, EnumKeyType.PERSONAL);
        else
            return false;

        return true;
    }

    public void toggle (@NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull EnumKeyType keyType) {
        if (level.isClientSide)
            return;

        TileEntityController tileEntityController = WorldUtils.getBlockEntity(level, pos, TileEntityController.class);
        if (tileEntityController == null)
            return;

        switch (keyType) {
            case DRAWER -> tileEntityController.toggleLock(EnumSet.allOf(LockAttribute.class), LockAttribute.LOCK_POPULATED, player.getGameProfile());
            case CONCEALMENT -> tileEntityController.toggleShroud(player.getGameProfile());
            case QUANTIFY -> tileEntityController.toggleQuantified(player.getGameProfile());

            //case PERSONAL:
            //    String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
            //    ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

            //    te.toggleProtection(player.getGameProfile(), provider);
            //    break;
        }
    }

    @Override
    public void tick (@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (world.isClientSide)
            return;

        TileEntityController tileEntityController = WorldUtils.getBlockEntity(world, pos, TileEntityController.class);
        if (tileEntityController == null)
            return;

        tileEntityController.updateCache();

        world.scheduleTick(pos, this, 100);
    }

    @Override
    public TileEntityController newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityController(pos, state);
    }
}
