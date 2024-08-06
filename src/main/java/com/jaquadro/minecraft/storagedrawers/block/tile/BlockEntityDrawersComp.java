package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.FractionalDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.MessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public abstract class BlockEntityDrawersComp extends BlockEntityDrawers
{
    public BlockEntityDrawersComp(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
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

    private class GroupData extends FractionalDrawerGroup
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
            if (!getWorld().isClientSide && CommonConfig.GENERAL.debugTrace.get())
                StorageDrawers.log.info(message);
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
                PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(
                    getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 500, getWorld().dimension());
                MessageHandler.sendTo(PacketDistributor.NEAR.with(point), new CountUpdateMessage(getBlockPos(), 0, getPooledCount()));

                setChanged();
            }
        }

        public <T> T getCapability(@NotNull BlockCapability<T, Void> capability) {
            if (level == null)
                return null;
            return level.getCapability(capability, getBlockPos(), getBlockState(), BlockEntityDrawersComp.this, null);
        }
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
    @OnlyIn(Dist.CLIENT)
    public void clientUpdateCount (final int slot, final int count) {
        if (getLevel() == null || !getLevel().isClientSide)
            return;

        Minecraft.getInstance().tell(() -> BlockEntityDrawersComp.this.clientUpdateCountAsync(count));
    }

    @OnlyIn(Dist.CLIENT)
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
