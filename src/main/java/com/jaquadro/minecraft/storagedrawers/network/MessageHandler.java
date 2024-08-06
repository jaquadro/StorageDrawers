package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class MessageHandler
{
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(StorageDrawers.MOD_ID);

        registrar.play(CountUpdateMessage.ID, CountUpdateMessage::new, handler -> handler
            .client(CountUpdateMessage::handleClient)
        );
    }

    public static void sendTo(ServerPlayer player, CustomPacketPayload message) {
        if (!(player instanceof FakePlayer))
            PacketDistributor.PLAYER.with(player).send(message);
    }

    public static void sendTo(PacketDistributor.PacketTarget target, CustomPacketPayload message) {
        target.send(message);
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.SERVER.noArg().send(message);
    }
}
