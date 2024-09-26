package com.jaquadro.minecraft.storagedrawers.api.security;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.player.Player;

public interface ISecurityProvider
{
    String getProviderID ();

    boolean hasOwnership (GameProfile profile, IProtectable target);

    boolean hasAccess (Player player, IProtectable target);
}
