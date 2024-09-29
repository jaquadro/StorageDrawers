package com.jaquadro.minecraft.storagedrawers.api.storage;

import java.util.List;

public interface IControlGroup
{
    IDrawerGroup getDrawerGroup();

    IControlGroup getBoundControlGroup();

    List<INetworked> getBoundRemoteNodes();

    void invalidateRemoteNode (INetworked node);

    boolean addRemoteNode (INetworked node);
}
