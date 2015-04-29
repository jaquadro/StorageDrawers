package com.dynious.refinedrelocation.api.filter;

import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public interface IFilterGUI extends IFilter
{
    public int getSize();

    public void setValue(int place, boolean value);

    public boolean getValue(int place);

    public String getName(int place);

    public boolean isBlacklisting();

    public void setBlacklists(boolean blacklists);

    public List<String> getWAILAInformation(NBTTagCompound compound);

    public String getUserFilter();

    public void setUserFilter(String userFilter);

    public void writeToNBT(NBTTagCompound compound);

    public void readFromNBT(NBTTagCompound compound);
}
