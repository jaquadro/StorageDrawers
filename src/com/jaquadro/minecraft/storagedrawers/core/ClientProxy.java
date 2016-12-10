package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.resources.IconRegistry;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.network.BoolConfigUpdateMessage;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    @SubscribeEvent
    public void onEntityJoinWorldEvent(net.minecraftforge.event.entity.EntityJoinWorldEvent event) {
        if (!event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityPlayer))
            return;

        if (event.getEntity().getEntityId() == FMLClientHandler.instance().getClientPlayerEntity().getEntityId())
            StorageDrawers.network.sendToServer(new BoolConfigUpdateMessage(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID().toString(), "invertShift", StorageDrawers.config.cache.invertShift));
    }
}
