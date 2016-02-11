package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.thermalfoundation.CoFHSecurityProvider;
import com.mojang.authlib.GameProfile;

import java.lang.reflect.Method;

public class ThermalFoundation extends IntegrationModule
{
    private Class classRegistrySocial;

    private Method methodPlayerHasAccess;

    @Override
    public String getModID () {
        return "ThermalFoundation";
    }

    @Override
    public void init () throws Throwable {
        classRegistrySocial = Class.forName("cofh.core.RegistrySocial");

        methodPlayerHasAccess = classRegistrySocial.getDeclaredMethod("playerHasAccess", String.class, GameProfile.class);
    }

    @Override
    public void postInit () {
        StorageDrawers.securityRegistry.registerProvider(new CoFHSecurityProvider(this));
    }

    public boolean playerHasAccess (String playerName, GameProfile owner) {
        try {
            return (Boolean) methodPlayerHasAccess.invoke(null, playerName, owner);
        }
        catch (Throwable t) {
            return false;
        }
    }
}
