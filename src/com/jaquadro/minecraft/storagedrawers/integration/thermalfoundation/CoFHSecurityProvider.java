/*package com.jaquadro.minecraft.storagedrawers.integration.thermalfoundation;

import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.integration.ThermalFoundation;
import com.jaquadro.minecraft.storagedrawers.security.DefaultSecurityProvider;
import com.mojang.authlib.GameProfile;

public class CoFHSecurityProvider implements ISecurityProvider
{
    ThermalFoundation foundation;
    private DefaultSecurityProvider defaultProvider = new DefaultSecurityProvider();

    public CoFHSecurityProvider (ThermalFoundation foundation) {
        this.foundation = foundation;
    }

    @Override
    public String getProviderID () {
        return "cofh";
    }

    @Override
    public boolean hasOwnership (GameProfile profile, IProtectable target) {
        return defaultProvider.hasOwnership(profile, target);
    }

    @Override
    public boolean hasAccess (GameProfile profile, IProtectable target) {
        if (target.getOwner() == null)
            return true;

        GameProfile ownerProfile = (profile.getId().equals(target.getOwner()))
            ? new GameProfile(profile.getId(), profile.getName())
            : new GameProfile(target.getOwner(), null);

        return foundation.playerHasAccess(profile.getName(), ownerProfile);
    }
}
*/