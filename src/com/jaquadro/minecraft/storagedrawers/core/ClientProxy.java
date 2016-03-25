package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.resources.IconRegistry;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.block.Block;

public class ClientProxy extends CommonProxy
{
    @Override
    public void initDynamic () {
        StorageDrawers.blocks.initDynamic();
    }

    @Override
    public void initClient () {
        StorageDrawers.blocks.initClient();
        StorageDrawers.items.initClient();
    }

    @Override
    public void registerRenderers () {
        IconRegistry iconRegistry = Chameleon.instance.iconRegistry;
        iconRegistry.registerIcon(iconIndicatorCompOnResource);
        iconRegistry.registerIcon(iconIndicatorCompOffResource);

        for (int i = 0; i < 5; i++) {
            if (iconIndicatorOffResource[i] != null)
                iconRegistry.registerIcon(iconIndicatorOffResource[i]);
            if (iconIndicatorOnResource[i] != null)
                iconRegistry.registerIcon(iconIndicatorOnResource[i]);
        }
    }

    @Override
    public void registerDrawer (Block block) {
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), itemRenderer);
    }
}
