package com.jaquadro.minecraft.storagedrawers.network;

import com.google.common.collect.Maps;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfig;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfigSetting;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerBoolConfigMessage
{
    private String uuid;
    private String key;
    private boolean value;

    private final boolean failed;

    public PlayerBoolConfigMessage (String uuid, String key, boolean value) {
        this.uuid = uuid;
        this.key = key;
        this.value = value;
        this.failed = false;
    }

    private PlayerBoolConfigMessage (boolean failed) {
        this.failed = failed;
    }

    public static PlayerBoolConfigMessage decode (FriendlyByteBuf buf) {
        try {
            String uuid = buf.readUtf();
            String key = buf.readUtf();
            boolean value = buf.readBoolean();
            return new PlayerBoolConfigMessage(uuid, key, value);
        }
        catch (IndexOutOfBoundsException e) {
            StorageDrawers.log.error("PlayerBoolConfigMessage: Unexpected end of packet.\nMessage: " + ByteBufUtil.hexDump(buf, 0, buf.writerIndex()), e);
            return new PlayerBoolConfigMessage(true);
        }
    }

    public static void encode (PlayerBoolConfigMessage msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.uuid);
        buf.writeUtf(msg.key);
        buf.writeBoolean(msg.value);
    }

    public static void handle(PlayerBoolConfigMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> handleServer(msg, ctx.get()));
        ctx.get().setPacketHandled(true);
    }

    private static void handleServer(PlayerBoolConfigMessage msg, NetworkEvent.Context ctx) {
        if (!msg.failed) {
            UUID playerUniqueId;
            try {
                playerUniqueId = UUID.fromString(msg.uuid);
            } catch (IllegalArgumentException e) {
                return;
            }

            Map<String, PlayerConfigSetting<?>> clientMap = PlayerConfig.serverPlayerConfigSettings.get(playerUniqueId);
            if (clientMap == null) {
                clientMap = Maps.newHashMap();
            }

            clientMap.put(msg.key, new PlayerConfigSetting<>(msg.key, msg.value, playerUniqueId));
            PlayerConfig.serverPlayerConfigSettings.put(playerUniqueId, clientMap);
        }
    }
}
