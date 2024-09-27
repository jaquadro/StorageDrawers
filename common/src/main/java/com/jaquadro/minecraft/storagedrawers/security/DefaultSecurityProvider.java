package com.jaquadro.minecraft.storagedrawers.security;

import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.player.Player;

public class DefaultSecurityProvider implements ISecurityProvider
{
    @Override
    public String getProviderID () {
        return null;
    }

    @Override
    public boolean hasOwnership (GameProfile profile, IProtectable target) {
        if (target == null || profile == null)
            return false;

        return target.getOwner() == null || target.getOwner().equals(profile.getId());
    }

    @Override
    public boolean hasAccess (Player player, IProtectable target) {
        return hasOwnership(player.getGameProfile(), target);
    }
}
