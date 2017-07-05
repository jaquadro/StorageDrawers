package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import com.jaquadro.minecraft.storagedrawers.storage.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDrawersStandard extends TileEntityDrawers
{
    private static final String[] GUI_IDS = new String[] {
        null, StorageDrawers.MOD_ID + ":basicDrawers1", StorageDrawers.MOD_ID + ":basicDrawers2", null, StorageDrawers.MOD_ID + ":basicDrawers4"
    };

    private int capacity = 0;

    public static class Slot1 extends TileEntityDrawersStandard
    {
        private GroupData groupData = new GroupData(1);

        public Slot1 () {
            groupData.setCapabilityProvider(this);
            injectPortableData(groupData);
        }

        @Override
        protected IDrawerGroup getGroup () {
            return groupData;
        }
    }

    public static class Slot2 extends TileEntityDrawersStandard
    {
        private GroupData groupData = new GroupData(2);

        public Slot2 () {
            groupData.setCapabilityProvider(this);
            injectPortableData(groupData);
        }

        @Override
        protected IDrawerGroup getGroup () {
            return groupData;
        }
    }

    public static class Slot4 extends TileEntityDrawersStandard
    {
        private GroupData groupData = new GroupData(4);

        public Slot4 () {
            groupData.setCapabilityProvider(this);
            injectPortableData(groupData);
        }

        @Override
        protected IDrawerGroup getGroup () {
            return groupData;
        }
    }

    public static class Legacy extends TileEntityDrawersStandard
    {
        private GroupData groupData = new GroupData(4);
        private static boolean processed;

        public Legacy () {
            groupData.setCapabilityProvider(this);
            injectPortableData(groupData);
        }

        @Override
        protected IDrawerGroup getGroup () {
            return groupData;
        }

        public void replaceWithCurrent () {
            TileEntityDrawersStandard replacement = createEntity(groupData.getDrawerCount());
            if (replacement != null) {
                replacement.deserializeNBT(serializeNBT());
                getWorld().setTileEntity(getPos(), replacement);
                replacement.markDirty();
            }
        }

        @Override
        public void validate () {
            super.validate();
            getWorld().scheduleBlockUpdate(getPos(), ModBlocks.basicDrawers, 1, 0);
        }

        @Override
        public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
            return super.writeToPortableNBT(tag);
        }
    }

    public static TileEntityDrawersStandard createEntity (int slotCount) {
        switch (slotCount) {
            case 1:
                return new Slot1();
            case 2:
                return new Slot2();
            case 4:
                return new Slot4();
            default:
                return null;
        }
    }

    @Override
    protected IDrawerGroup getGroup () {
        return null;
    }

    @Override
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
        switch (getDrawerCount()) {
            case 1:
                return new ContainerDrawers1(playerInventory, this);
            case 2:
                return new ContainerDrawers2(playerInventory, this);
            case 4:
                return new ContainerDrawers4(playerInventory, this);
            default:
                return  null;
        }
    }

    @Override
    public String getGuiID () {
        return GUI_IDS[getDrawerCount()];
    }

    @Override
    public int getDrawerCapacity () {
        if (getWorld() == null || getWorld().isRemote)
            return super.getDrawerCapacity();

        if (capacity == 0) {
            IBlockState blockState = getWorld().getBlockState(this.pos);
            if (!blockState.getPropertyKeys().contains(BlockStandardDrawers.BLOCK))
                return 1;

            EnumBasicDrawer type = blockState.getValue(BlockStandardDrawers.BLOCK);
            ConfigManager config = StorageDrawers.config;

            switch (type) {
                case FULL1:
                    capacity = config.getBlockBaseStorage("fulldrawers1");
                    break;
                case FULL2:
                    capacity = config.getBlockBaseStorage("fulldrawers2");
                    break;
                case FULL4:
                    capacity = config.getBlockBaseStorage("fulldrawers4");
                    break;
                case HALF2:
                    capacity = config.getBlockBaseStorage("halfdrawers2");
                    break;
                case HALF4:
                    capacity = config.getBlockBaseStorage("halfdrawers4");
                    break;
                default:
                    capacity = 1;
            }

            if (capacity <= 0)
                capacity = 1;
        }

        return capacity;
    }

    private class GroupData extends StandardDrawerGroup
    {
        public GroupData () { }

        public GroupData (int slotCount) {
            super(slotCount);
        }

        @Nonnull
        @Override
        protected DrawerData createDrawer (int slot) {
            return new StandardDrawerData(slot);
        }

        @Override
        public boolean hasCapability (@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return TileEntityDrawersStandard.this.hasCapability(capability, facing);
        }

        @Nullable
        @Override
        public <T> T getCapability (@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return TileEntityDrawersStandard.this.getCapability(capability, facing);
        }
    }

    private class StandardDrawerData extends DrawerData
    {
        private int slot;

        public StandardDrawerData (int slot) {
            super();
            this.slot = slot;
        }

        @Override
        protected int getStackCapacity () {
            return upgrades().getStorageMultiplier() * getEffectiveDrawerCapacity();
        }

        @Override
        protected void onItemChanged () {
            DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
            MinecraftForge.EVENT_BUS.post(event);

            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        @Override
        protected void onAmountChanged () {
            if (getWorld() != null && !getWorld().isRemote) {
                syncClientCount(slot, getStoredItemCount());
                markDirty();
            }
        }
    }
}
