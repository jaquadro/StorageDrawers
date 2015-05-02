package com.jaquadro.minecraft.storagedrawers.client.model;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DrawerModelBakeEventHandler
{
    public static final DrawerModelBakeEventHandler instance = new DrawerModelBakeEventHandler();

    private DrawerModelBakeEventHandler () { };

    @SubscribeEvent
    public void onModelBakeEvent (ModelBakeEvent event) {
        DrawerModel.initialize(event.modelRegistry);
    }
}
