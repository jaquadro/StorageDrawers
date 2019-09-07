package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class ControllerData extends TileDataShim
{
    private BlockPos controllerCoord;

    @Override
    public void read (CompoundNBT tag) {
        controllerCoord = null;
        if (tag.contains("Controller", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT ctag = tag.getCompound("Controller");
            controllerCoord = new BlockPos(ctag.getInt("x"), ctag.getInt("y"), ctag.getInt("z"));
        }
    }

    @Override
    public CompoundNBT write (CompoundNBT tag) {
        if (controllerCoord != null) {
            CompoundNBT ctag = new CompoundNBT();
            ctag.putInt("x", controllerCoord.getX());
            ctag.putInt("y", controllerCoord.getY());
            ctag.putInt("z", controllerCoord.getZ());
            tag.put("Controller", ctag);
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
