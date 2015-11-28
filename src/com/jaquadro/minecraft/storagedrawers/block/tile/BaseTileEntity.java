package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.util.Iterator;

public class BaseTileEntity extends TileEntity
{
    private NBTTagCompound failureSnapshot;

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        failureSnapshot = null;

        try {
            readFromFixedNBT(tag);
            readFromPortableNBT(tag);
        }
        catch (Throwable t) {
            trapLoadFailure(t, tag);
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (failureSnapshot != null) {
            restoreLoadFailure(tag);
            return;
        }

        try {
            writeToFixedNBT(tag);
            writeToPortableNBT(tag);
        }
        catch (Throwable t) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.ERROR, t, "Tile Save Failure.");
        }
    }

    public void readFromPortableNBT (NBTTagCompound tag) { }

    public void writeToPortableNBT (NBTTagCompound tag) { }

    protected void readFromFixedNBT (NBTTagCompound tag) { }

    protected void writeToFixedNBT (NBTTagCompound tag) { }

    protected void trapLoadFailure (Throwable t, NBTTagCompound tag) {
        failureSnapshot = (NBTTagCompound)tag.copy();
        FMLLog.log(StorageDrawers.MOD_ID, Level.ERROR, t, "Tile Load Failure.");
    }

    protected void restoreLoadFailure (NBTTagCompound tag) {
        Iterator<String> iter = failureSnapshot.getKeySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (!tag.hasKey(key))
                tag.setTag(key, failureSnapshot.getTag(key).copy());
        }
    }

    protected boolean loadDidFail () {
        return failureSnapshot != null;
    }
}
