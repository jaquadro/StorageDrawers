package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.storagedrawers.api.storage.IControlGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.stream.Stream;

public class ControllerHostData extends BlockEntityDataShim
{
    private Map<BlockPos, INetworked> nodeMap = new HashMap<>();

    @Override
    public void read (HolderLookup.Provider provider, CompoundTag tag) {
        nodeMap.clear();

        if (tag.contains("RemoteNodes", Tag.TAG_LIST)) {
            ListTag list = tag.getList("RemoteNodes", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag ctag = list.getCompound(i);
                nodeMap.put(new BlockPos(ctag.getInt("x"), ctag.getInt("y"), ctag.getInt("z")), null);
            }
        }
    }

    @Override
    public CompoundTag write (HolderLookup.Provider provider, CompoundTag tag) {
        ListTag list = new ListTag();
        for (BlockPos pos : nodeMap.keySet()) {
            CompoundTag ctag = new CompoundTag();
            ctag.putInt("x", pos.getX());
            ctag.putInt("y", pos.getY());
            ctag.putInt("z", pos.getZ());
            list.add(ctag);
        }

        tag.put("RemoteNodes", list);

        return tag;
    }

    public void validateRemoteNodes (IControlGroup host, Level level) {
        for (BlockPos pos : nodeMap.keySet()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof INetworked networked) {
                if (networked.getBoundControlGroup() == host) {
                    nodeMap.put(pos, networked);
                    continue;
                }
            }

            nodeMap.remove(pos);
        }
    }

    public boolean addRemoteNode (IControlGroup host, INetworked node) {
        if (node == null)
            return false;

        if (node instanceof BlockEntity blockEntity) {
            BlockPos pos = blockEntity.getBlockPos();
            if (node.getBoundControlGroup() == host) {
                nodeMap.put(pos, node);
                return true;
            }

            nodeMap.put(pos, null);
        }

        return false;
    }

    public boolean removeRemoteNode (IControlGroup host, INetworked node) {
        if (node == null)
            return false;

        if (node instanceof BlockEntity blockEntity) {
            BlockPos pos = blockEntity.getBlockPos();
            if (nodeMap.containsKey(pos)) {
                nodeMap.remove(pos);
                return true;
            }
        }

        return false;
    }

    public Stream<INetworked> getRemoteNodes () {
        return nodeMap.values().stream().filter(Objects::nonNull);
    }
}