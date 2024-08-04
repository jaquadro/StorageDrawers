/*package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.chameleon.block.ChamTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityKeyButton extends ChamTileEntity
{
    EnumFacing facing = EnumFacing.NORTH;
    boolean powered;

    public EnumFacing getDirection () {
        return facing;
    }

    public void setDirection (EnumFacing facing) {
        this.facing = facing;
        markDirty();
        markBlockForUpdate();
    }

    public boolean isPowered () {
        return powered;
    }

    public void setPowered (boolean powered) {
        this.powered = powered;
        markDirty();
        markBlockForUpdate();
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    protected void readFromFixedNBT (NBTTagCompound tag) {
        super.readFromFixedNBT(tag);

        setDirection(EnumFacing.NORTH);
        setPowered(false);

        if (tag.hasKey("dir"))
            this.facing = EnumFacing.getFront(tag.getInteger("dir"));
        if (tag.hasKey("powered"))
            this.powered = tag.getBoolean("powered");
    }

    @Override
    protected NBTTagCompound writeToFixedNBT (NBTTagCompound tag) {
        tag = super.writeToFixedNBT(tag);

        if (facing != null)
            tag.setInteger("dir", facing.getIndex());
        if (powered)
            tag.setBoolean("powered", powered);

        return tag;
    }
}
*/