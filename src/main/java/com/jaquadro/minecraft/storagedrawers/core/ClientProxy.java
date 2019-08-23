package com.jaquadro.minecraft.storagedrawers.core;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderers () {
        /*IconRegistry iconRegistry = Chameleon.instance.iconRegistry;
        iconRegistry.registerIcon(iconConcealmentOverlayResource);
        iconRegistry.registerIcon(iconIndicatorCompOnResource);
        iconRegistry.registerIcon(iconIndicatorCompOffResource);

        for (int i = 0; i < 5; i++) {
            if (iconIndicatorOffResource[i] != null)
                iconRegistry.registerIcon(iconIndicatorOffResource[i]);
            if (iconIndicatorOnResource[i] != null)
                iconRegistry.registerIcon(iconIndicatorOnResource[i]);
        }*/
    }

    /*
    @SubscribeEvent
    public void onEntityJoinWorldEvent(net.minecraftforge.event.entity.EntityJoinWorldEvent event) {
        if (!event.getEntity().getEntityWorld().isRemote || !(event.getEntity() instanceof EntityPlayer))
            return;

        if (event.getEntity().getEntityId() == FMLClientHandler.instance().getClientPlayerEntity().getEntityId())
            StorageDrawers.network.sendToServer(new BoolConfigUpdateMessage(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID().toString(), "invertShift", StorageDrawers.config.cache.invertShift));
    }*/
}
