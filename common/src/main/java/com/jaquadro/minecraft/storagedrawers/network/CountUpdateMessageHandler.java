package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CountUpdateMessageHandler
{
    void handle(CountUpdateMessage msg) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            BlockPos pos = new BlockPos(msg.x(), msg.y(), msg.z());
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof BlockEntityDrawers) {
                ((BlockEntityDrawers) blockEntity).clientUpdateCount(msg.slot(), msg.count());
            }
        }
    }
}
