package com.jaquadro.minecraft.storagedrawers.security;

import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.ILockableContainer;

public class SecurityManager
{
    private static ISecurityProvider defaultProvider = new DefaultSecurityProvider();

    public static boolean hasOwnership (GameProfile profile, IProtectable target) {
        if (target == null || profile == null)
            return false;

        ISecurityProvider provider = target.getSecurityProvider();
        if (provider == null)
            provider = defaultProvider;

        return provider.hasOwnership(profile, target);
    }

    public static boolean hasAccess (GameProfile profile, IProtectable target) {
        if (target == null || profile == null)
            return false;

        ILockableContainer lockable = target.getLockableContainer();
        if (lockable != null && lockable.isLocked())
            return false;

        ISecurityProvider provider = target.getSecurityProvider();
        if (provider == null)
            provider = defaultProvider;

        return provider.hasAccess(profile, target);
    }
}
