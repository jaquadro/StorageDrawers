package com.jaquadro.minecraft.storagedrawers.block.framed;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityTrim;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.components.item.FrameData;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockFramedTrim extends BlockTrim implements EntityBlock, IFramedBlock
{
    public BlockFramedTrim (Properties properties) {
        super(properties);
    }

    public void setPlacedBy (@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);

        BlockEntityTrim blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntityTrim.class);
        if (blockEntity == null)
            return;

        blockEntity.material().read(stack);
        blockEntity.setChanged();
    }

    @Override
    public boolean canUseForRetrim () {
        return false;
    }

    @Override
    @NotNull
    public List<ItemStack> getDrops (@NotNull BlockState state, LootParams.Builder builder) {
        List<ItemStack> items = new ArrayList<>();
        items.add(getMainDrop(state, (BlockEntityTrim)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)));
        return items;
    }

    protected ItemStack getMainDrop (BlockState state, BlockEntityTrim tile) {
        ItemStack drop = new ItemStack(this);
        if (tile == null)
            return drop;

        if (!tile.material().isEmpty())
            drop.set(ModDataComponents.FRAME_DATA.get(), new FrameData(tile.material()));

        return drop;
    }

    @Override
    public ItemStack getCloneItemStack (LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);

        BlockEntityTrim tile = WorldUtils.getBlockEntity(level, pos, BlockEntityTrim.class);
        if (tile != null && !tile.material().isEmpty())
            stack.set(ModDataComponents.FRAME_DATA.get(), new FrameData(tile.material()));

        return stack;
    }

    @Override
    public BlockEntityTrim newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return ModServices.RESOURCE_FACTORY.createBlockEntityTrim().create(pos, state);
    }

    @Override
    public IFramedBlockEntity getFramedBlockEntity (@NotNull Level world, @NotNull BlockPos pos) {
        return WorldUtils.getBlockEntity(world, pos, BlockEntityTrim.class);
    }

    @Override
    public boolean supportsFrameMaterial (FrameMaterial material) {
        return switch (material) {
            case SIDE, TRIM -> true;
            case FRONT -> false;
        };
    }

    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        BlockEntityTrim tile = WorldUtils.getBlockEntity(level, pos, BlockEntityTrim.class);
        if (tile == null)
            return 1f;

        MaterialData data = tile.material();
        if (data == null)
            return 1f;

        return data.allMatOpaque() ? 0.8f : 1f;
    }
}