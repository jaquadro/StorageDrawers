package com.jaquadro.minecraft.storagedrawers.network;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfigSetting;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class BoolConfigUpdateMessage implements IMessage {

    private String uuid;
    private String key;
    private boolean value;

    public BoolConfigUpdateMessage() { }

    public BoolConfigUpdateMessage(String uuid, String key, boolean value) {
        this.uuid = uuid;
        this.key = key;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.uuid = ByteBufUtils.readUTF8String(buf);
        this.key = ByteBufUtils.readUTF8String(buf);
        this.value = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.uuid);
        ByteBufUtils.writeUTF8String(buf, this.key);
        buf.writeBoolean(this.value);
    }

    public static class Handler implements IMessageHandler<BoolConfigUpdateMessage, IMessage>
    {
        @Override
        public IMessage onMessage (BoolConfigUpdateMessage message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                UUID playerUniqueId = null;
                try {
                    playerUniqueId = UUID.fromString(message.uuid);
                } catch (IllegalArgumentException e) {
                    return null;
                }

                Map<String, PlayerConfigSetting<?>> clientMap = ConfigManager.serverPlayerConfigSettings.get(playerUniqueId);
                if (clientMap == null) {
                    clientMap = Maps.newHashMap();
                }

                clientMap.put(message.key, new PlayerConfigSetting<Boolean>(message.key, message.value, playerUniqueId));
                ConfigManager.serverPlayerConfigSettings.put(playerUniqueId, clientMap);
            }

            return null;
        }
    }
}
