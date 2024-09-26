package com.jaquadro.minecraft.storagedrawers.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientUtil
{
    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    public static GameProfile getLocalGameProfile() {
        Player player = getLocalPlayer();
        if (player == null)
            return null;

        return player.getGameProfile();
    }
}
