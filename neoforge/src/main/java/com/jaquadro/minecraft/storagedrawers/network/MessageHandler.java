package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MessageHandler
{
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(StorageDrawers.MOD_ID);

        registrar.playToClient(CountUpdateMessage.TYPE, CountUpdateMessage.STREAM_CODEC, CountUpdateMessage::handle);
    }

    public static void sendTo(ServerPlayer player, CustomPacketPayload message) {
        if (!(player instanceof FakePlayer))
            PacketDistributor.sendToPlayer(player, message);
    }
}
