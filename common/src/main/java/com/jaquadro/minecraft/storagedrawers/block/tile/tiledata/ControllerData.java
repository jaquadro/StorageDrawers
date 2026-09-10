package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ControllerData extends BlockEntityDataShim
{
    private BlockPos controllerCoord;
    private boolean needsValidation;

    @Override
    public void read (ValueInput input) {
        controllerCoord = null;

        input.child("Controller").ifPresent(t ->
            controllerCoord = new BlockPos(t.getIntOr("x", 0), t.getIntOr("y", 0), t.getIntOr("z", 0))
        );

        needsValidation = input.getBooleanOr("Validate", false);
    }

    @Override
    public void write (ValueOutput output) {
        if (controllerCoord != null) {
            var ctag = output.child("Controller");
            ctag.putInt("x", controllerCoord.getX());
            ctag.putInt("y", controllerCoord.getY());
            ctag.putInt("z", controllerCoord.getZ());
        }

        if (needsValidation)
            output.putBoolean("Validate", needsValidation);
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
        if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
            ModServices.log.info("ControllerData [{}] bind coord [{}]", controllerCoord, pos);

        if (controllerCoord == null && pos == null)
            return false;
        
        if (controllerCoord == null || !controllerCoord.equals(pos)) {
            controllerCoord = pos;
            return true;
        }

        return false;
    }

    public boolean needsValidation () {
        return needsValidation;
    }

    public void setNeedsValidation (boolean state) {
        needsValidation = state;
    }
}
