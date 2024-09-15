package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemKeyring;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BlockController extends HorizontalDirectionalBlock implements INetworked, EntityBlock
{
    public static final MapCodec<BlockController> CODEC = simpleCodec(BlockController::new);

    public BlockController (BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BlockController> codec() {
        return CODEC;
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
    public InteractionResult useWithoutItem (@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        Direction blockDir = state.getValue(FACING);
        BlockEntityController blockEntity = WorldUtils.getBlockEntity(level, pos, BlockEntityController.class);
        if (blockEntity == null)
            return InteractionResult.FAIL;

        ItemStack item = player.getInventory().getSelected();
        if (!item.isEmpty() && toggle(level, pos, player, item.getItem()))
            return InteractionResult.SUCCESS;

        if (blockDir != hit.getDirection())
            return InteractionResult.CONSUME;

        if (!level.isClientSide) {
            if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get() && item.isEmpty())
                blockEntity.printDebugInfo();

            blockEntity.interactPutItemsIntoInventory(player);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean toggle (Level world, BlockPos pos, Player player, Item item) {
        if (world.isClientSide || item == null)
            return false;

        if (item instanceof ItemKeyring keyring)
            item = keyring.getKey().getItem();

        if (item == ModItems.DRAWER_KEY.get())
            toggle(world, pos, player, KeyType.DRAWER);
        else if (item == ModItems.SHROUD_KEY.get())
            toggle(world, pos, player, KeyType.CONCEALMENT);
        else if (item == ModItems.QUANTIFY_KEY.get())
            toggle(world, pos, player, KeyType.QUANTIFY);
        //else if (item == ModItems.personalKey)
        //    toggle(world, pos, player, EnumKeyType.PERSONAL);
        else
            return false;

        return true;
    }

    public void toggle (@NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull KeyType keyType) {
        if (level.isClientSide)
            return;

        BlockEntityController blockEntity = WorldUtils.getBlockEntity(level, pos, BlockEntityController.class);
        if (blockEntity == null)
            return;

        if (keyType == KeyType.DRAWER)
            blockEntity.toggleLock(EnumSet.allOf(LockAttribute.class), LockAttribute.LOCK_POPULATED, player.getGameProfile());
        else if (keyType == KeyType.CONCEALMENT)
            blockEntity.toggleShroud(player.getGameProfile());
        else if (keyType == KeyType.QUANTIFY)
            blockEntity.toggleQuantified(player.getGameProfile());

        //case PERSONAL:
        //    String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
        //    ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

        //    te.toggleProtection(player.getGameProfile(), provider);
        //    break;
    }

    @Override
    public void tick (@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (world.isClientSide)
            return;

        BlockEntityController blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntityController.class);
        if (blockEntity == null)
            return;

        blockEntity.updateCache();

        world.scheduleTick(pos, this, 100);
    }

    @Override
    public BlockEntityController newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BlockEntityController(pos, state);
    }
}
