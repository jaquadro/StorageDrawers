package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.event.INetworkedLoadedEvent;
import com.jaquadro.minecraft.storagedrawers.api.event.INetworkedUnloadedEvent;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.BlockEntityDataShim;
import com.jaquadro.minecraft.storagedrawers.core.ModINetworkedLocations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BaseBlockEntity extends BlockEntity
{
    private CompoundTag failureSnapshot;
    private List<BlockEntityDataShim> fixedShims;
    private List<BlockEntityDataShim> portableShims;

    public BaseBlockEntity (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    public void setRemoved () {
        BlockPos pos = getBlockPos();
        super.setRemoved();
        if (this instanceof INetworked && getLevel() != null && !getLevel().isClientSide()) {
            ModINetworkedLocations.EVENT_BUS.post(new INetworkedUnloadedEvent(this, pos));
        }
    }

    public void onEntityLoad() {
        if (this instanceof INetworked && getLevel() != null && !getLevel().isClientSide()) {
            BlockPos ourPos = getBlockPos();
            ModINetworkedLocations.EVENT_BUS.post(new INetworkedLoadedEvent(this, ourPos));
        }
    }

    public boolean hasDataPacket () {
        return true;
    }

    public boolean dataPacketRequiresRenderUpdate () {
        return false;
    }

    public void injectData (BlockEntityDataShim shim) {
        if (fixedShims == null)
            fixedShims = new ArrayList<>();
        fixedShims.add(shim);
    }

    public void injectPortableData (BlockEntityDataShim shim) {
        if (portableShims == null)
            portableShims = new ArrayList<>();
        portableShims.add(shim);
    }

    @Override
    public final void loadAdditional (ValueInput input) {
        super.loadAdditional(input);

        //failureSnapshot = null;

        //try {
            readFixed(input);
            readPortable(input);
            onLoadFinished();
        //}
        //catch (Throwable t) {
        //    trapLoadFailure(t, tag);
        //}

        if (level != null && level.isClientSide() && dataPacketRequiresRenderUpdate())
            markBlockForRenderUpdate();
    }

    protected void onLoadFinished () { }

    public final void read (ValueInput input) {
        loadAdditional(input);
    }

    /*@Override
    public final CompoundTag save (CompoundTag tag) {
        super.save(tag);

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
    }*/

    @Override
    protected void saveAdditional (ValueOutput output) {
        super.saveAdditional(output);

        writeFixed(output);
        writePortable(output);
    }

    public void readPortable (ValueInput input) {
        if (portableShims != null) {
            for (BlockEntityDataShim shim : portableShims)
                shim.read(input);
        }
    }

    public void writePortable (ValueOutput output) {
        if (portableShims != null) {
            for (BlockEntityDataShim shim : portableShims)
                shim.write(output);
        }
    }

    protected void readFixed (ValueInput input) {
        if (fixedShims != null) {
            for (BlockEntityDataShim shim : fixedShims)
                shim.read(input);
        }
    }

    protected void writeFixed (ValueOutput output) {
        if (fixedShims != null) {
            for (BlockEntityDataShim shim : fixedShims)
                shim.write(output);
        }
    }

    private void trapLoadFailure (Throwable t, CompoundTag tag) {
        failureSnapshot = tag.copy();
        // StorageDrawers.log.error("Tile Load Failure.", t);
    }

    private void restoreLoadFailure (CompoundTag tag) {
        for (String key : failureSnapshot.keySet()) {
            if (!tag.contains(key))
                tag.put(key, failureSnapshot.get(key).copy());
        }
    }

    protected boolean loadDidFail () {
        return failureSnapshot != null;
    }

    @Override
    @NotNull
    public final CompoundTag getUpdateTag (HolderLookup.Provider provider) {
        //save(tag);
        return this.saveCustomOnly(provider);
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket () {
        return hasDataPacket() ? ClientboundBlockEntityDataPacket.create(this) : null;
    }

    // TODO: Forge Extension
    // @Override
    public final void onDataPacket (Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        if (pkt != null)
            read(TagValueInput.create(ProblemReporter.DISCARDING, lookupProvider, pkt.getTag()));

        if (getLevel() != null && getLevel().isClientSide() && dataPacketRequiresRenderUpdate()) {
            BlockState state = getLevel().getBlockState(getBlockPos());
            getLevel().sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_ALL);
        }
    }

    /**
     * Calls server to sync data with client, update neighbors, and cause a delayed render update.
     */
    public void markBlockForUpdate () {
        if (getLevel() != null && !getLevel().isClientSide()) {
            BlockState state = getLevel().getBlockState(worldPosition);
            getLevel().sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public void markBlockForUpdateClient () {
        if (getLevel() != null && getLevel().isClientSide()) {
            BlockState state = getLevel().getBlockState(worldPosition);
            getLevel().sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    /**
     * Causes immediate render update when called client-side, or delayed render update when called server-side.
     * Does not sync tile data or notify neighbors of any state change.
     */
    public void markBlockForRenderUpdate () {
        if (getLevel() == null)
            return;

        //if (getWorld().isRemote)
        //    getWorld().markBlockRangeForRenderUpdate(pos, pos);
        //else {
        BlockState state = getLevel().getBlockState(worldPosition);
        getLevel().sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        //}
    }
}
