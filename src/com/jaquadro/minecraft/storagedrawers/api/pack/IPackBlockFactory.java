package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.block.Block;

public interface IPackBlockFactory
{
    Block createBlock (BlockConfiguration blockConfig, IPackDataResolver dataResolver);

    Class getItemClass (BlockConfiguration blockConfig);
}
