package com.jaquadro.minecraft.storagedrawers.network;

import com.google.common.collect.Maps;
import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfig;
import com.jaquadro.minecraft.storagedrawers.config.PlayerConfigSetting;
import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public record PlayerBoolConfigMessage(String uuid, String key, boolean value) implements ChameleonPacket
{
    public static final Type<PlayerBoolConfigMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "player_bool_config"));

    public static final StreamCodec<FriendlyByteBuf, PlayerBoolConfigMessage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        PlayerBoolConfigMessage::uuid,
        ByteBufCodecs.STRING_UTF8,
        PlayerBoolConfigMessage::key,
        ByteBufCodecs.BOOL,
        PlayerBoolConfigMessage::value,
        PlayerBoolConfigMessage::new
    );

    @Override
    public Type<? extends PlayerBoolConfigMessage> type () {
        return TYPE;
    }

    @Override
    public void handleMessage (Player player, Consumer<Runnable> workQueue) {
        if (player instanceof ServerPlayer) {
            workQueue.accept(() -> {
                UUID playerUniqueId;
                try {
                    playerUniqueId = UUID.fromString(uuid);
                } catch (IllegalArgumentException e) {
                    return;
                }

                Map<String, PlayerConfigSetting<?>> clientMap = PlayerConfig.serverPlayerConfigSettings.get(playerUniqueId);
                if (clientMap == null) {
                    clientMap = Maps.newHashMap();
                }

                clientMap.put(key, new PlayerConfigSetting<>(key, value, playerUniqueId));
                PlayerConfig.serverPlayerConfigSettings.put(playerUniqueId, clientMap);
            });
        }
    }
}
