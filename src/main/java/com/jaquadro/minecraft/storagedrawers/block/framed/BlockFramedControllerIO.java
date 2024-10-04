package com.jaquadro.minecraft.storagedrawers.block.framed;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityTrim;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockFramedControllerIO extends BlockSlave implements IFramedBlock
{
    public BlockFramedControllerIO (Properties properties) {
        super(properties);
    }

    public void setPlacedBy (@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);

        BlockEntitySlave blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntitySlave.class);
        if (blockEntity == null)
            return;

        CompoundTag tag = stack.getOrCreateTag();
        blockEntity.material().read(tag);
        blockEntity.setChanged();
    }

    @Override
    @NotNull
    public List<ItemStack> getDrops (@NotNull BlockState state, LootParams.Builder builder) {
        List<ItemStack> items = new ArrayList<>();
        items.add(getMainDrop(state, (BlockEntitySlave)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)));
        return items;
    }

    protected ItemStack getMainDrop (BlockState state, BlockEntitySlave tile) {
        ItemStack drop = new ItemStack(this);
        if (tile == null)
            return drop;

        CompoundTag data = drop.getOrCreateTag();
        tile.material().write(data);
        drop.setTag(data);

        return drop;
    }

    @Override
    public IFramedBlockEntity getFramedBlockEntity (@NotNull Level world, @NotNull BlockPos pos) {
        return WorldUtils.getBlockEntity(world, pos, BlockEntitySlave.class);
    }

    @Override
    public boolean supportsFrameMaterial (FrameMaterial material) {
        return switch (material) {
            case SIDE, TRIM -> true;
            case FRONT -> false;
        };
    }
}
