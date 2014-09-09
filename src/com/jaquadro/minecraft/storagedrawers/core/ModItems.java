package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems
{
    public static ItemUpgrade upgrade;

    public void init () {
        upgrade = new ItemUpgrade("upgrade");

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            GameRegistry.registerItem(upgrade, "upgrade");
    }
}
