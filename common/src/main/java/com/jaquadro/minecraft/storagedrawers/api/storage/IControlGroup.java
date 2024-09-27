package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.world.entity.player.Player;

public interface IControlGroup
{
    IDrawerGroup getDrawerGroup();

    IDrawerAttributesGroupControl getGroupControllableAttributes(Player player);

    IControlGroup getBoundControlGroup();
}
