package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

public class ControllerData extends TileDataShim
{
    private BlockPos controllerCoord;

    @Override
    public void read (CompoundTag tag) {
        controllerCoord = null;
        if (tag.contains("Controller", Tag.TAG_COMPOUND)) {
            CompoundTag ctag = tag.getCompound("Controller");
            controllerCoord = new BlockPos(ctag.getInt("x"), ctag.getInt("y"), ctag.getInt("z"));
        }
    }

    @Override
    public CompoundTag write (CompoundTag tag) {
        if (controllerCoord != null) {
            CompoundTag ctag = new CompoundTag();
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

    public TileEntityController getController (BlockEntity host) {
        if (controllerCoord == null)
            return null;

        BlockEntity te = host.getLevel().getBlockEntity(controllerCoord);
        if (!(te instanceof TileEntityController)) {
            controllerCoord = null;
            host.setChanged();
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
