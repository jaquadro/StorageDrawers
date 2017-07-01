package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.chameleon.block.tiledata.TileDataShim;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.storage.DrawerData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public abstract class StandardDrawerGroup extends TileDataShim implements IDrawerGroup
{
    private DrawerData[] slots;

    public StandardDrawerGroup (int slotCount) {
        slots = new DrawerData[slotCount];
        for (int i = 0; i < slotCount; i++)
            slots[i] = createDrawer(i);
    }

    public void setCapabilityProvider (ICapabilityProvider capProvider) {
        for (DrawerData slot : slots)
            slot.setCapabilityProvider(capProvider);
    }

    @Override
    public int getDrawerCount () {
        return slots.length;
    }

    @Override
    @Nonnull
    public IDrawer getDrawer (int slot) {
        if (slot < 0 || slot >= slots.length)
            return Drawers.DISABLED;

        return slots[slot];
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        if (!tag.hasKey("Drawers"))
            return;

        NBTTagList itemList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < itemList.tagCount(); i++) {
            if (i >= 0 && i < slots.length)
                slots[i].deserializeNBT(itemList.getCompoundTagAt(i));
        }
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        NBTTagList itemList = new NBTTagList();
        for (DrawerData slot : slots)
            itemList.appendTag(slot.serializeNBT());

        tag.setTag("Drawers", itemList);

        return tag;
    }

    @Nonnull
    protected abstract DrawerData createDrawer (int slot);
}
