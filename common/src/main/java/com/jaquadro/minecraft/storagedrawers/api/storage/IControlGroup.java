package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IControlGroup
{
    IDrawerGroup getDrawerGroup();

    IDrawerAttributesGroupControl getGroupControllableAttributes(Player player);

    IControlGroup getBoundControlGroup();

    List<INetworked> getBoundRemoteNodes();

    void invalidateRemoteNode (INetworked node);

    boolean addRemoteNode (INetworked node);
}
