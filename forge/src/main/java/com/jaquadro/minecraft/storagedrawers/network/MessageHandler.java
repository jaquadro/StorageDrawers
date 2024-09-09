package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public class MessageHandler
{
    private static final int PROTOCOL_VERSION = 1;
    public static final SimpleChannel INSTANCE = ChannelBuilder
        .named(StorageDrawers.rl("main_channel"))
        .networkProtocolVersion(PROTOCOL_VERSION)
        .clientAcceptedVersions(SimpleChannel.VersionTest.exact(PROTOCOL_VERSION))
        .serverAcceptedVersions(SimpleChannel.VersionTest.exact(PROTOCOL_VERSION))
        .simpleChannel();

    public static void init() {
        INSTANCE.messageBuilder(CountUpdateMessage.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CountUpdateMessage::decode)
                .encoder(CountUpdateMessage::encode)
                .consumerMainThread(CountUpdateMessage::handle)
                .add();
    }
}
