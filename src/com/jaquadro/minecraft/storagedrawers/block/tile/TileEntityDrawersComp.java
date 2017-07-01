package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.FractionalDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityDrawersComp extends TileEntityDrawers
{
    private GroupData groupData;

    private int capacity = 0;

    public TileEntityDrawersComp () {
        groupData = new GroupData(3);
        groupData.setCapabilityProvider(this);

        injectPortableData(groupData);
    }

    @Override
    protected IDrawerGroup getGroup () {
        return groupData;
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
            if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
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
                IMessage message = new CountUpdateMessage(getPos(), 0, getPooledCount());
                NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 500);

                StorageDrawers.network.sendToAllAround(message, targetPoint);

                markDirty();
            }
        }
    }

    // TODO
    /*
    public int getStoredItemRemainder (int slot) {
        groupData.getDrawer(slot)
        int count = centralInventory.getStoredItemCount(slot);
        if (slot > 0 && convRate[slot] > 0)
            count -= centralInventory.getStoredItemCount(slot - 1) * (convRate[slot - 1] / convRate[slot]);

        return count;
    }*/

    @Override
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerDrawersComp(playerInventory, this);
    }

    @Override
    public String getGuiID () {
        return StorageDrawers.MOD_ID + ":compDrawers";
    }

    @Override
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
    }

    /*
    @Override
    public int putItemsIntoSlot (int slot, @Nonnull ItemStack stack, int count) {
        int added = 0;
        if (!stack.isEmpty() && convRate != null && convRate[0] == 0) {
            populateSlots(stack);

            for (int i = 0; i < getDrawerCount(); i++) {
                if (convRate[i] != 0 && BaseDrawerData.areItemsEqual(protoStack[i], stack))
                    added = super.putItemsIntoSlot(i, stack, count);
            }

            for (int i = 0; i < getDrawerCount(); i++) {
                IDrawer drawer = getDrawer(i);
                if (drawer instanceof CompDrawerData)
                    ((CompDrawerData) drawer).refresh();
            }


        }

        return added + super.putItemsIntoSlot(slot, stack, count);
    }*/

    @Override
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    /*@Override
    public void readFromPortableNBT (NBTTagCompound tag) {
        super.readFromPortableNBT(tag);

        pooledCount = 0;

        for (int i = 0; i < getDrawerCount(); i++) {
            protoStack[i] = ItemStack.EMPTY;
            convRate[i] = 0;
        }

        super.readFromPortableNBT(tag);

        pooledCount = tag.getInteger("Count");

        if (tag.hasKey("Conv0"))
            convRate[0] = tag.getByte("Conv0");
        if (tag.hasKey("Conv1"))
            convRate[1] = tag.getByte("Conv1");
        if (tag.hasKey("Conv2"))
            convRate[2] = tag.getByte("Conv2");

        for (int i = 0; i < getDrawerCount(); i++) {
            IDrawer drawer = getDrawer(i);
            if (drawer instanceof CompDrawerData)
                ((CompDrawerData) drawer).refresh();
        }
    }

    @Override
    public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
        tag = super.writeToPortableNBT(tag);

        tag.setInteger("Count", pooledCount);

        if (convRate[0] > 0)
            tag.setByte("Conv0", (byte)convRate[0]);
        if (convRate[1] > 0)
            tag.setByte("Conv1", (byte)convRate[1]);
        if (convRate[2] > 0)
            tag.setByte("Conv2", (byte)convRate[2]);

        return tag;
    }*/

    @Override
    @SideOnly(Side.CLIENT)
    public void clientUpdateCount (final int slot, final int count) {
        if (!getWorld().isRemote)
            return;

        Minecraft.getMinecraft().addScheduledTask(() -> TileEntityDrawersComp.this.clientUpdateCountAsync(count));
    }

    @SideOnly(Side.CLIENT)
    private void clientUpdateCountAsync (int count) {
        groupData.setPooledCount(count);
    }

    @Override
    public String getName () {
        return hasCustomName() ? super.getName() : "storagedrawers.container.compDrawers";
    }
}
