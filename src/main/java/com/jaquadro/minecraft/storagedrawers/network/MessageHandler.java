package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MessageHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(StorageDrawers.MOD_ID, "main_channel"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();

    public static void init() {
        INSTANCE.registerMessage(0, CountUpdateMessage.class, CountUpdateMessage::encode, CountUpdateMessage::decode, CountUpdateMessage::handle);
    }
}
