package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlockCompDrawers extends BlockDrawers implements INetworked
{
    public static final EnumProperty<EnumCompDrawer> SLOTS = EnumProperty.create("slots", EnumCompDrawer.class);

    public BlockCompDrawers (int drawerCount, boolean halfDepth, int storageUnits, BlockBehaviour.Properties properties) {
        super(drawerCount, halfDepth, storageUnits, properties);
    }

    public BlockCompDrawers (int drawerCount, boolean halfDepth, BlockBehaviour.Properties properties) {
        super(drawerCount, halfDepth, calcUnits(drawerCount, halfDepth), properties);
    }

    private static int calcUnits (int drawerCount, boolean halfDepth) {
        return halfDepth ? 16 : 32;
    }

    public BlockCompDrawers (int storageUnits, BlockBehaviour.Properties properties) {
        super(3, false, storageUnits, properties);
        this.registerDefaultState(defaultBlockState()
            .setValue(SLOTS, EnumCompDrawer.OPEN1));
    }

    public BlockCompDrawers (BlockBehaviour.Properties properties) {
        this(3, false, properties);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SLOTS);
    }

    @Override
    protected int getFaceSlot (Direction correctSide, @NotNull Vec3 normalizedHit) {
        if (!hitWithinArea(correctSide, normalizedHit, .0625f, .9375f))
            return super.getFaceSlot(correctSide, normalizedHit);

        if (hitWithinY(normalizedHit, .5f, 1f))
            return 0;

        if (getDrawerCount() == 2)
            return 1;

        if (getDrawerCount() == 3) {
            if (hitWithinX(correctSide, normalizedHit, 0, .5f))
                return 1;
            else
                return 2;
        }

        return super.getFaceSlot(correctSide, normalizedHit);
    }

    @Override
    public void setPlacedBy (@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);

        BlockEntityDrawersComp blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntityDrawersComp.class);
        if (blockEntity != null) {
            IDrawerGroup group = blockEntity.getGroup();
            for (int i = group.getDrawerCount() - 1; i >= 0; i--) {
                if (!group.getDrawer(i).isEmpty()) {
                    world.setBlock(pos, state.setValue(SLOTS, EnumCompDrawer.byOpenSlots(i + 1)), 3);
                    break;
                }
            }
        }
    }

    @Override
    public BlockEntityDrawers newBlockEntity (@NotNull BlockPos pos, @NotNull BlockState state) {
        return BlockEntityDrawersComp.createEntity(getDrawerCount(), pos, state);
    }
}
