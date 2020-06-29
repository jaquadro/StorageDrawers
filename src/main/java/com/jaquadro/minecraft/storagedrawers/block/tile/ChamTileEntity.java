package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.TileDataShim;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeTileEntity;

import java.util.ArrayList;
import java.util.List;

public class ChamTileEntity extends TileEntity implements IForgeTileEntity
{
    private CompoundNBT failureSnapshot;
    private List<TileDataShim> fixedShims;
    private List<TileDataShim> portableShims;

    public ChamTileEntity (TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public boolean hasDataPacket () {
        return true;
    }

    public boolean dataPacketRequiresRenderUpdate () {
        return false;
    }

    public void injectData (TileDataShim shim) {
        if (fixedShims == null)
            fixedShims = new ArrayList<TileDataShim>();
        fixedShims.add(shim);
    }

    public void injectPortableData (TileDataShim shim) {
        if (portableShims == null)
            portableShims = new ArrayList<TileDataShim>();
        portableShims.add(shim);
    }

    @Override
    public final void func_230337_a_ (BlockState state, CompoundNBT tag) {
        super.func_230337_a_(state, tag);

        failureSnapshot = null;

        try {
            readFixed(tag);
            readPortable(tag);
        }
        catch (Throwable t) {
            trapLoadFailure(t, tag);
        }
    }

    public final void read (CompoundNBT tag) {
        func_230337_a_(null, tag);
    }

    @Override
    public final CompoundNBT write (CompoundNBT tag) {
        super.write(tag);

        if (failureSnapshot != null) {
            restoreLoadFailure(tag);
            return tag;
        }

        try {
            tag = writeFixed(tag);
            tag = writePortable(tag);
        }
        catch (Throwable t) {
            StorageDrawers.log.error("Tile Save Failure.", t);
        }

        return tag;
    }

    public void readPortable (CompoundNBT tag) {
        if (portableShims != null) {
            for (TileDataShim shim : portableShims)
                shim.read(tag);
        }
    }

    public CompoundNBT writePortable (CompoundNBT tag) {
        if (portableShims != null) {
            for (TileDataShim shim : portableShims)
                tag = shim.write(tag);
        }

        return tag;
    }

    protected void readFixed (CompoundNBT tag) {
        if (fixedShims != null) {
            for (TileDataShim shim : fixedShims)
                shim.read(tag);
        }
    }

    protected CompoundNBT writeFixed (CompoundNBT tag) {
        if (fixedShims != null) {
            for (TileDataShim shim : fixedShims)
                tag = shim.write(tag);
        }

        return tag;
    }

    private void trapLoadFailure (Throwable t, CompoundNBT tag) {
        failureSnapshot = tag.copy();
        StorageDrawers.log.error("Tile Load Failure.", t);
    }

    private void restoreLoadFailure (CompoundNBT tag) {
        for (String key : failureSnapshot.keySet()) {
            if (!tag.contains(key))
                tag.put(key, failureSnapshot.get(key).copy());
        }
    }

    protected boolean loadDidFail () {
        return failureSnapshot != null;
    }

    @Override
    public final CompoundNBT getUpdateTag () {
        CompoundNBT tag = new CompoundNBT();
        write(tag);

        return tag;
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket () {
        return hasDataPacket() ? new SUpdateTileEntityPacket(getPos(), 0, getUpdateTag()) : null;
    }

    @Override
    public final void onDataPacket (NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (pkt != null && pkt.getNbtCompound() != null)
            read(pkt.getNbtCompound());

        if (dataPacketRequiresRenderUpdate() && getWorld().isRemote) {
            BlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    /**
     * Calls server to sync data with client, update neighbors, and cause a delayed render update.
     */
    public void markBlockForUpdate () {
        if (getWorld() != null && !getWorld().isRemote) {
            BlockState state = getWorld().getBlockState(pos);
            getWorld().notifyBlockUpdate(pos, state, state, 3);
        }
    }

    public void markBlockForUpdateClient () {
        if (getWorld() != null && getWorld().isRemote) {
            BlockState state = getWorld().getBlockState(pos);
            getWorld().notifyBlockUpdate(pos, state, state, 3);
        }
    }

    /**
     * Causes immediate render update when called client-side, or delayed render update when called server-side.
     * Does not sync tile data or notify neighbors of any state change.
     */
    public void markBlockForRenderUpdate () {
        if (getWorld() == null)
            return;

        //if (getWorld().isRemote)
        //    getWorld().markBlockRangeForRenderUpdate(pos, pos);
        //else {
        BlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 2);
        //}
    }
}
