package com.jaquadro.minecraft.storagedrawers;

import net.minecraft.resources.Identifier;

public final class ModConstants
{
    public static final String MOD_ID = "storagedrawers";

    public static Identifier loc(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
