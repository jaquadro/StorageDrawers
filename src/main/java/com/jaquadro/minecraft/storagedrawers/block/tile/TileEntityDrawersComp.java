package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.FractionalDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.MessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDrawersComp extends TileEntityDrawers
{
    @CapabilityInject(IDrawerAttributes.class)
    static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    private GroupData groupData;

    private int capacity = 0;

    public TileEntityDrawersComp (TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        groupData = new GroupData(3);
        groupData.setCapabilityProvider(this);

        injectPortableData(groupData);
    }

    public TileEntityDrawersComp () {
        this(ModBlocks.FRACTIONAL_DRAWERS_3);
    }

    public static TileEntityDrawersComp createEntity () {
        return new TileEntityDrawersComp(ModBlocks.FRACTIONAL_DRAWERS_3);
    }

    @Override
    public IDrawerGroup getGroup () {
        return groupData;
    }

    @Override
    protected void onAttributeChanged () {
        groupData.syncAttributes();
    }

    private class GroupData extends FractionalDrawerGroup
    {
        public GroupData (int slotCount) {
            super(slotCount);
        }

        @Override
        protected World getWorld () {
            return TileEntityDrawersComp.this.getWorld();
        }

        @Override
        protected void log (String message) {
            if (!getWorld().isRemote && CommonConfig.GENERAL.debugTrace.get())
                StorageDrawers.log.info(message);
        }

        @Override
        protected int getStackCapacity () {
            return upgrades().getStorageMultiplier() * getEffectiveDrawerCapacity();
        }

        @Override
        protected void onItemChanged () {
            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        @Override
        protected void onAmountChanged () {
            if (getWorld() != null && !getWorld().isRemote) {
                PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(
                    getPos().getX(), getPos().getY(), getPos().getZ(), 500, getWorld().dimension.getType());
                MessageHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> point), new CountUpdateMessage(getPos(), 0, getPooledCount()));

                markDirty();
            }
        }

        private final LazyOptional<?> capabilityAttributes = LazyOptional.of(TileEntityDrawersComp.this::getDrawerAttributes);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability (@Nonnull Capability<T> capability, @Nullable Direction facing) {
            if (capability == TileEntityDrawersComp.DRAWER_ATTRIBUTES_CAPABILITY)
                return capabilityAttributes.cast();

            return super.getCapability(capability, facing);
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
        if (!getWorld().isRemote)
            return;

        Minecraft.getInstance().enqueue(() -> TileEntityDrawersComp.this.clientUpdateCountAsync(count));
    }

    @OnlyIn(Dist.CLIENT)
    private void clientUpdateCountAsync (int count) {
        groupData.setPooledCount(count);
    }

    /*@Override
    public String getName () {
        return hasCustomName() ? super.getName() : "storagedrawers.container.compDrawers";
    }*/
}
