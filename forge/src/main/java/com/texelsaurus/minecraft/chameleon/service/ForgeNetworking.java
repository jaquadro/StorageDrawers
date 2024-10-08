package com.texelsaurus.minecraft.chameleon.service;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.payload.PayloadProtocol;

import java.util.function.BiConsumer;

public class ForgeNetworking implements ChameleonNetworking
{
    public static PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> NETWORK_CHANNEL_BUILDER = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "main")).networkProtocolVersion(1).optional().payloadChannel().play();
    public static Channel<CustomPacketPayload> CHANNEL;

    public static void init (ChameleonInit init, ChameleonInit.InitContext context) {
        init.init(context);
        CHANNEL = NETWORK_CHANNEL_BUILDER.bidirectional().build();
    }

    @Override
    public <B extends FriendlyByteBuf, P extends ChameleonPacket> void registerPacketInternal (CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound) {
        BiConsumer<P, CustomPayloadEvent.Context> handler = (packet, context) -> {
            Player player = context.getSender() != null ? context.getSender() : Minecraft.getInstance().player;
            packet.handleMessage(player, context::enqueueWork);
            context.setPacketHandled(true);
        };

        if (clientBound)
            NETWORK_CHANNEL_BUILDER.clientbound().add(payloadType, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, handler);
        else
            NETWORK_CHANNEL_BUILDER.serverbound().add(payloadType, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, handler);
    }

    @Override
    public void sendToPlayer (ChameleonPacket packet, ServerPlayer player) {
        CHANNEL.send(packet, PacketDistributor.PLAYER.with(player));
    }

    @Override
    public void sendToPlayersNear(ChameleonPacket packet, ServerLevel level, double x, double y, double z, double radius) {
        CHANNEL.send(packet, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, radius, level.dimension())));
    }

    @Override
    public void sendToServer (ChameleonPacket packet) {
        CHANNEL.send(packet, PacketDistributor.SERVER.noArg());
    }
}
