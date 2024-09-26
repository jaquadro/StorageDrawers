package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.FractionalDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class BlockEntityDrawersComp extends BlockEntityDrawers
{
    public BlockEntityDrawersComp(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public static class Slot2 extends BlockEntityDrawersComp
    {
        private final GroupData groupData = new GroupData(2);

        public Slot2 (BlockPos pos, BlockState state) {
            super(ModBlockEntities.FRACTIONAL_DRAWERS_2.get(), pos, state);
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

    public static class Slot3 extends BlockEntityDrawersComp
    {
        private final GroupData groupData = new GroupData(3);

        public Slot3 (BlockPos pos, BlockState state) {
            super(ModBlockEntities.FRACTIONAL_DRAWERS_3.get(), pos, state);
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

    @Override
    protected boolean emptySlotCanBeCleared (int slot) {
        if (slot != 0)
            return false;

        if (getGroup() instanceof FractionalDrawerGroup fracGroup) {
            return !getGroup().getDrawer(0).isEmpty() && fracGroup.getPooledCount() == 0;
        }

        return false;
    }

    protected class GroupData extends FractionalDrawerGroup
    {
        public GroupData (int slotCount) {
            super(slotCount);
        }

        @Override
        protected Level getWorld () {
            return BlockEntityDrawersComp.this.getLevel();
        }

        @Override
        public boolean isGroupValid () {
            return BlockEntityDrawersComp.this.isGroupValid();
        }

        @Override
        protected void log (String message) {
            if (!getWorld().isClientSide && ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
                ModServices.log.info(message);
        }

        @Override
        protected int getStackCapacity () {
            return upgrades().getStorageMultiplier() * getEffectiveDrawerCapacity();
        }

        @Override
        protected void onItemChanged () {
            if (getWorld() != null && !getWorld().isClientSide) {
                int usedSlots = 0;
                for (int slot : getAccessibleDrawerSlots()) {
                    IDrawer drawer = getDrawer(slot);
                    if (!drawer.isEmpty())
                        usedSlots += 1;
                }
                usedSlots = Math.max(usedSlots, 1);

                EnumCompDrawer open = getBlockState().getValue(BlockCompDrawers.SLOTS);
                if (open.getOpenSlots() != usedSlots) {
                    getWorld().setBlock(worldPosition, getBlockState().setValue(BlockCompDrawers.SLOTS, EnumCompDrawer.byOpenSlots(usedSlots)), 3);
                }

                setChanged();
                markBlockForUpdate();
            }
        }

        @Override
        protected void onAmountChanged () {
            if (getWorld() != null && !getWorld().isClientSide) {
                ChameleonServices.NETWORK.sendToPlayersNear(new CountUpdateMessage(getBlockPos(), 0, getPooledCount()),
                    (ServerLevel) getLevel(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 500);

                setChanged();
            }
        }

        @Override
        public <T> T getCapability (ChameleonCapability<T> capability) {
            if (capability == null || level == null)
                return null;
            return capability.getCapability(level, getBlockPos());
        }

        /*
        public <T> T getCapability(@NotNull BlockCapability<T, Void> capability) {
            if (level == null)
                return null;
            return level.getCapability(capability, getBlockPos(), getBlockState(), BlockEntityDrawersComp.this, null);
        }
        */
    }

    /*@Override
    public int getDrawerCapacity () {
        if (getWorld() == null || getWorld().isRemote)
            return super.getDrawerCapacity();

        if (capacity == 0) {
            ConfigManager config = StorageDrawers.config;
            capacity = config.getBlockBaseStorage("compdrawers");

            if (capacity <= 0)
                capacity = 1;
        }

        return capacity;
    }*/

    @Override
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    @Override
    public void clientUpdateCount (final int slot, final int count) {
        if (getLevel() == null || !getLevel().isClientSide)
            return;

        Minecraft.getInstance().tell(() -> BlockEntityDrawersComp.this.clientUpdateCountAsync(count));
    }

    private void clientUpdateCountAsync (int count) {
        if (getGroup() instanceof FractionalDrawerGroup) {
            ((FractionalDrawerGroup)getGroup()).setPooledCount(count);
        }
    }

    /*@Override
    public String getName () {
        return hasCustomName() ? super.getName() : "storagedrawers.container.compDrawers";
    }*/
}
