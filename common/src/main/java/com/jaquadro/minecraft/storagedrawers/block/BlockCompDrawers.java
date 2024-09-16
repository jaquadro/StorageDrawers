package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlockCompDrawers extends BlockDrawers implements INetworked
{
    public static final MapCodec<BlockCompDrawers> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.fieldOf("storageUnits").forGetter(BlockDrawers::getStorageUnits),
            propertiesCodec()
        ).apply(instance, BlockCompDrawers::new)
    );

    public static final EnumProperty<EnumCompDrawer> SLOTS = EnumProperty.create("slots", EnumCompDrawer.class);

    public BlockCompDrawers (int storageUnits, Properties properties) {
        super(3, false, storageUnits, properties);
        this.registerDefaultState(defaultBlockState()
            .setValue(SLOTS, EnumCompDrawer.OPEN1));
    }

    public BlockCompDrawers (Properties properties) {
        this(32, properties);
    }

    @Override
    public MapCodec<BlockCompDrawers> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SLOTS);
    }

    @Override
    protected int getDrawerSlot (Direction correctSide, @NotNull Vec3 normalizedHit) {
        if (!hitAny(correctSide, normalizedHit))
            return super.getDrawerSlot(correctSide, normalizedHit);

        if (hitTop(normalizedHit))
            return 0;

        if (hitLeft(correctSide, normalizedHit))
            return 1;
        else
            return 2;
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
        return ModServices.RESOURCE_FACTORY.createBlockEntityDrawersComp(getDrawerCount()).create(pos, state);
    }
}
