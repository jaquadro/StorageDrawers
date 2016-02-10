package com.jaquadro.minecraft.storagedrawers.api.security;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.mojang.authlib.GameProfile;

public interface ISecurityProvider
{
    boolean hasAccess (GameProfile profile, IProtectable target);
}
