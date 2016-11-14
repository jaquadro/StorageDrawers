package com.jaquadro.minecraft.storagedrawers.storage.network;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ControllerSearch
{
    private final Queue<BlockPos> searchQueue = new LinkedList<BlockPos>();
    private final Set<BlockPos> searchDiscovered = new HashSet<BlockPos>();

    private final int range;

    public ControllerSearch (int range) {
        this.range = range;
    }

    public BlockPos search (TileEntity tile) {
        BlockPos root = tile.getPos();

        searchQueue.clear();
        searchQueue.add(root);

        searchDiscovered.clear();
        searchDiscovered.add(root);

        World world = tile.getWorld();

        while (!searchQueue.isEmpty()) {
            BlockPos coord = searchQueue.remove();
            int depth = maxAxisDistance(coord, root);
            if (depth > range)
                continue;

            Block block = world.getBlockState(coord).getBlock();
            if (!(block instanceof INetworked))
                continue;

            if (block instanceof BlockController)
                return coord;

            BlockPos[] neighbors = new BlockPos[]{
                coord.west(), coord.east(), coord.south(), coord.north(), coord.up(), coord.down()
            };

            for (BlockPos n : neighbors) {
                if (!searchDiscovered.contains(n)) {
                    searchQueue.add(n);
                    searchDiscovered.add(n);
                }
            }
        }

        return null;
    }

    private int maxAxisDistance (BlockPos coord1, BlockPos coord2) {
        return Math.max(Math.max(Math.abs(coord1.getX() - coord2.getX()), Math.abs(coord1.getY() - coord2.getY())), Math.abs(coord1.getZ() - coord2.getZ()));
    }
}
