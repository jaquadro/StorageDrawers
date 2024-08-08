package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.BlockEntityDataShim;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BaseBlockEntity extends BlockEntity
{
    private CompoundTag failureSnapshot;
    private List<BlockEntityDataShim> fixedShims;
    private List<BlockEntityDataShim> portableShims;

    public BaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
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
    public final void loadAdditional (CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        //failureSnapshot = null;

        //try {
            readFixed(registries, tag);
            readPortable(registries, tag);
        //}
        //catch (Throwable t) {
        //    trapLoadFailure(t, tag);
        //}
    }

    public final void read (CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
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
    protected void saveAdditional (@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag = writeFixed(registries, tag);
        writePortable(registries, tag);
    }

    public void readPortable (HolderLookup.Provider provider, CompoundTag tag) {
        if (portableShims != null) {
            for (BlockEntityDataShim shim : portableShims)
                shim.read(provider, tag);
        }
    }

    public CompoundTag writePortable (HolderLookup.Provider provider, CompoundTag tag) {
        if (portableShims != null) {
            for (BlockEntityDataShim shim : portableShims)
                tag = shim.write(provider, tag);
        }

        return tag;
    }

    protected void readFixed (HolderLookup.Provider provider, CompoundTag tag) {
        if (fixedShims != null) {
            for (BlockEntityDataShim shim : fixedShims)
                shim.read(provider, tag);
        }
    }

    protected CompoundTag writeFixed (HolderLookup.Provider provider, CompoundTag tag) {
        if (fixedShims != null) {
            for (BlockEntityDataShim shim : fixedShims)
                tag = shim.write(provider, tag);
        }

        return tag;
    }

    private void trapLoadFailure (Throwable t, CompoundTag tag) {
        failureSnapshot = tag.copy();
        StorageDrawers.log.error("Tile Load Failure.", t);
    }

    private void restoreLoadFailure (CompoundTag tag) {
        for (String key : failureSnapshot.getAllKeys()) {
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
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket () {
        return hasDataPacket() ? ClientboundBlockEntityDataPacket.create(this) : null;
    }

    @Override
    public final void onDataPacket (Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        if (pkt != null && pkt.getTag() != null)
            read(pkt.getTag(), lookupProvider);

        if (getLevel() != null && getLevel().isClientSide && dataPacketRequiresRenderUpdate()) {
            BlockState state = getLevel().getBlockState(getBlockPos());
            getLevel().sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_ALL);
        }
    }

    /**
     * Calls server to sync data with client, update neighbors, and cause a delayed render update.
     */
    public void markBlockForUpdate () {
        if (getLevel() != null && !getLevel().isClientSide) {
            BlockState state = getLevel().getBlockState(worldPosition);
            getLevel().sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public void markBlockForUpdateClient () {
        if (getLevel() != null && getLevel().isClientSide) {
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
