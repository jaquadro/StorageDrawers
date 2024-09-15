package com.texelsaurus.minecraft.chameleon.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface ChameleonPacket extends CustomPacketPayload
{
    void handleMessage(Player player, Consumer<Runnable> workQueue);
}
