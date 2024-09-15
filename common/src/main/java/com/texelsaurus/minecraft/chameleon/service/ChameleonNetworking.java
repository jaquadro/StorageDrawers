package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface ChameleonNetworking
{
    static <B extends FriendlyByteBuf, P extends ChameleonPacket> void registerPacket(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound) {
        ChameleonServices.NETWORK.registerPacketInternal(payloadType, codec, clientBound);
    }

    <B extends FriendlyByteBuf, P extends ChameleonPacket> void registerPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound);

    void sendToPlayer(ChameleonPacket packet, ServerPlayer player);

    void sendToPlayersNear(ChameleonPacket packet, ServerLevel level, double x, double y, double z, double radius);

    void sendToServer(ChameleonPacket packet);
}
