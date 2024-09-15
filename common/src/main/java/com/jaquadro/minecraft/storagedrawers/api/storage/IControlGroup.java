package com.jaquadro.minecraft.storagedrawers.api.storage;

import com.mojang.authlib.GameProfile;

public interface IControlGroup
{
    IDrawerGroup getDrawerGroup();

    IDrawerAttributesGroupControl getGroupControllableAttributes(GameProfile profile);

    IControlGroup getBoundControlGroup();
}
