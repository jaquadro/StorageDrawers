package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.client.FabricClient;
import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworking implements ChameleonNetworking
{
    @Override
    public <B extends FriendlyByteBuf, P extends ChameleonPacket> void registerPacketInternal (CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound) {
        if (clientBound) {
            PayloadTypeRegistry.playS2C().register(payloadType, (StreamCodec<FriendlyByteBuf, P>) codec);
            if (ChameleonServices.PLATFORM.isPhysicalClient())
                FabricClient.registerPacket(payloadType);
        } else {
            PayloadTypeRegistry.playC2S().register(payloadType, (StreamCodec<FriendlyByteBuf, P>)codec);
            ServerPlayNetworking.registerGlobalReceiver(payloadType, (packet, context) ->
                packet.handleMessage(context.player(), context.player().getServer()::execute));
        }
    }

    @Override
    public void sendToPlayer (ChameleonPacket packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet);
    }

    @Override
    public void sendToPlayersNear (ChameleonPacket packet, ServerLevel level, double x, double y, double z, double radius) {
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(new BlockPos((int)x, (int)y, (int)z)) <= radius)
                sendToPlayer(packet, player);
        }
    }

    @Override
    public void sendToServer (ChameleonPacket packet) {
        FabricClient.sendToServer(packet);
    }
}
