package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class BlockEntityDrawersStandard extends BlockEntityDrawers
{
    public BlockEntityDrawersStandard(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public static class Slot1 extends BlockEntityDrawersStandard
    {
        private final GroupData groupData = new GroupData(1);

        public Slot1 (BlockPos pos, BlockState state) {
            super(ModBlockEntities.STANDARD_DRAWERS_1.get(), pos, state);
            injectPortableData(groupData);
        }

        @Override
        @NotNull
        public IDrawerGroup getGroup () {
            return groupData;
        }

        @Override
        protected void onAttributeChanged () {
            super.onAttributeChanged();
            groupData.syncAttributes();
        }
    }

    public static class Slot2 extends BlockEntityDrawersStandard
    {
        private final GroupData groupData = new GroupData(2);

        public Slot2 (BlockPos pos, BlockState state) {
            super(ModBlockEntities.STANDARD_DRAWERS_2.get(), pos, state);
            injectPortableData(groupData);
        }

        @Override
        @NotNull
        public IDrawerGroup getGroup () {
            return groupData;
        }

        @Override
        protected void onAttributeChanged () {
            super.onAttributeChanged();
            groupData.syncAttributes();
        }
    }

    public static class Slot4 extends BlockEntityDrawersStandard
    {
        private final GroupData groupData = new GroupData(4);

        public Slot4 (BlockPos pos, BlockState state) {
            super(ModBlockEntities.STANDARD_DRAWERS_4.get(), pos, state);
            injectPortableData(groupData);
        }

        @Override
        @NotNull
        public IDrawerGroup getGroup () {
            return groupData;
        }

        @Override
        protected void onAttributeChanged () {
            super.onAttributeChanged();
            groupData.syncAttributes();
        }
    }

    public static BlockEntityDrawersStandard createEntity (int slotCount, BlockPos pos, BlockState state) {
        return switch (slotCount) {
            case 1 -> new Slot1(pos, state);
            case 2 -> new Slot2(pos, state);
            case 4 -> new Slot4(pos, state);
            default -> null;
        };
    }

    private class GroupData extends StandardDrawerGroup
    {
        public GroupData (int slotCount) {
            super(slotCount);
        }

        @NotNull
        @Override
        protected DrawerData createDrawer (int slot) {
            return new StandardDrawerData(this, slot);
        }

        @Override
        public boolean isGroupValid () {
            return BlockEntityDrawersStandard.this.isGroupValid();
        }

        @Override
        public <T> T getCapability (ChameleonCapability<T> capability) {
            if (level == null)
                return null;

            return capability.getCapability(level, getBlockPos());
        }
    }

    private class StandardDrawerData extends StandardDrawerGroup.DrawerData
    {
        private final int slot;

        public StandardDrawerData (StandardDrawerGroup group, int slot) {
            super(group);
            this.slot = slot;
        }

        @Override
        protected int getStackCapacity () {
            return upgrades().getStorageMultiplier() * getEffectiveDrawerCapacity();
        }

        @Override
        protected void onItemChanged () {
            // DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
            // NeoForge.EVENT_BUS.post(event);

            if (getLevel() != null && !getLevel().isClientSide) {
                setChanged();
                markBlockForUpdate();
            }
        }

        @Override
        protected void onAmountChanged () {
            if (getLevel() != null && !getLevel().isClientSide) {
                syncClientCount(slot, getStoredItemCount());
                setChanged();
            }
        }
    }
}
