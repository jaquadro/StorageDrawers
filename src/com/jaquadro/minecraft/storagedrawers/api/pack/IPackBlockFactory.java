package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.block.Block;

public interface IPackBlockFactory
{
    Block createBlock (BlockConfiguration blockConfig, IPackDataResolver dataResolver);

    /**
     * Registers a factory-produced block with an appropriate item class.
     */
    void registerBlock (Block block, String name);

    /**
     * Hides block from JEI if JEI is active.
     */
    void hideBlock (String blockID);

    /**
     * Registers block metadata from an initialized DataResolver with Storage Drawers.
     */
    void registerResolver (IPackDataResolver resolver);
}
