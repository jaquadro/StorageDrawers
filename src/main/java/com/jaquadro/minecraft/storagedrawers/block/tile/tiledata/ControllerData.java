/*package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.chameleon.block.tiledata.TileDataShim;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class ControllerData extends TileDataShim
{
    private BlockPos controllerCoord;

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        controllerCoord = null;
        if (tag.hasKey("Controller", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound ctag = tag.getCompoundTag("Controller");
            controllerCoord = new BlockPos(ctag.getInteger("x"), ctag.getInteger("y"), ctag.getInteger("z"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        if (controllerCoord != null) {
            NBTTagCompound ctag = new NBTTagCompound();
            ctag.setInteger("x", controllerCoord.getX());
            ctag.setInteger("y", controllerCoord.getY());
            ctag.setInteger("z", controllerCoord.getZ());
            tag.setTag("Controller", ctag);
        }

        return tag;
    }

    public BlockPos getCoord () {
        return controllerCoord;
    }

    public TileEntityController getController (TileEntity host) {
        if (controllerCoord == null)
            return null;

        TileEntity te = host.getWorld().getTileEntity(controllerCoord);
        if (!(te instanceof TileEntityController)) {
            controllerCoord = null;
            host.markDirty();
            return null;
        }

        return (TileEntityController)te;
    }

    public boolean bindCoord (BlockPos pos) {
        if (controllerCoord == null || !controllerCoord.equals(pos)) {
            controllerCoord = pos;
            return true;
        }

        return false;
    }
}
*/