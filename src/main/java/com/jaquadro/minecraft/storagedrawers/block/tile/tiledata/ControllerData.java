package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ControllerData extends BlockEntityDataShim
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

    public BlockEntityController getController (BlockEntity host) {
        if (controllerCoord == null)
            return null;
        if (host.getLevel() == null)
            return null;

        BlockEntity blockEntity = host.getLevel().getBlockEntity(controllerCoord);
        if (!(blockEntity instanceof BlockEntityController)) {
            controllerCoord = null;
            host.setChanged();
            return null;
        }

        return (BlockEntityController)blockEntity;
    }

    public boolean bind (BlockEntityController entity) {
        return bindCoord(entity != null ? entity.getBlockPos() : null);
    }

    public boolean bindCoord (BlockPos pos) {
        if (controllerCoord == null || !controllerCoord.equals(pos)) {
            controllerCoord = pos;
            return true;
        }

        return false;
    }
}
