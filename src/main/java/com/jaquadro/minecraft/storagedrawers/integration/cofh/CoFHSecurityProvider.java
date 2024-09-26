package com.jaquadro.minecraft.storagedrawers.integration.cofh;

import cofh.lib.util.SocialUtils;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.security.DefaultSecurityProvider;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class CoFHSecurityProvider implements ISecurityProvider
{
    private DefaultSecurityProvider defaultProvider = new DefaultSecurityProvider();

    @Override
    public String getProviderID () {
        return "cofh";
    }

    @Override
    public boolean hasOwnership (GameProfile profile, IProtectable target) {
        return defaultProvider.hasOwnership(profile, target);
    }

    @Override
    public boolean hasAccess (Player player, IProtectable target) {
        if (player instanceof ServerPlayer sp) {
            if (target == null || target.getOwner() == null)
                return true;

            GameProfile ownerProfile = new GameProfile(target.getOwner(), null);
            GameProfile playerProfile = player.getGameProfile();

            if (ownerProfile.getId() != null && ownerProfile.getId().equals(playerProfile.getId()))
                return true;

            return SocialUtils.isFriendOrSelf(ownerProfile, sp);
        }

        return false;
    }
}
