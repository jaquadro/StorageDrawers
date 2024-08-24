package com.jaquadro.minecraft.storagedrawers.config;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerConfig
{
    public static final Map<UUID, Map<String, PlayerConfigSetting<?>>> serverPlayerConfigSettings = Maps.newHashMap();

    static Optional<Boolean> getBooleanValue (Player player, String key) {
        if (!serverPlayerConfigSettings.containsKey(player.getUUID()))
            return Optional.empty();

        Map<String, PlayerConfigSetting<?>> map = serverPlayerConfigSettings.get(player.getUUID());
        if (!map.containsKey(key))
            return Optional.empty();

        return Optional.of((Boolean) map.get(key).getValue());
    }

    public static boolean getInvertShift (Player player) {
        Optional<Boolean> val = getBooleanValue(player, "invertShift");
        if (val.isEmpty()) {
            if (!(player instanceof ServerPlayer))
                return ClientConfig.GENERAL.invertShift.get();
            return false;
        }

        return val.get();
    }

    public static boolean getInvertClick (Player player) {
        Optional<Boolean> val = getBooleanValue(player, "invertClick");
        if (val.isEmpty()) {
            if (!(player instanceof ServerPlayer))
                return ClientConfig.GENERAL.invertClick.get();
            return false;
        }

        return val.get();
    }
}
