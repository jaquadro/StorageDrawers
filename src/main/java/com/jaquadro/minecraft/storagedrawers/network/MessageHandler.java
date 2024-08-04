package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class MessageHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
        .named(StorageDrawers.rl("main_channel"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();

    public static void init() {
        INSTANCE.messageBuilder(CountUpdateMessage.class, 0, PlayNetworkDirection.PLAY_TO_CLIENT)
                .decoder(CountUpdateMessage::new)
                .encoder(CountUpdateMessage::write)
                .consumerMainThread(CountUpdateMessage::handle).add();
    }
}
