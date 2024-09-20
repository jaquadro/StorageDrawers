package com.texelsaurus.minecraft.chameleon.client;

import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricClient
{
    public static <P extends ChameleonPacket> void registerPacket(CustomPacketPayload.Type<P> packetType) {
        ClientPlayNetworking.registerGlobalReceiver(packetType, (packet, context) ->
            packet.handleMessage(context.player(), context.client()::execute));
    }

    public static <P extends ChameleonPacket> void sendToServer(P packet) {
        ClientPlayNetworking.send(packet);
    }
}
