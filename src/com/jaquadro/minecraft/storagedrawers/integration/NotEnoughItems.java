package com.jaquadro.minecraft.storagedrawers.integration;

import cpw.mods.fml.common.Loader;
import net.minecraft.client.renderer.entity.RenderItem;

import java.lang.reflect.Field;

public class NotEnoughItems implements IModIntegration
{
    private static final String MOD_ID = "NotEnoughItems";

    boolean loaded;

    Class clGuiContainerManager;
    Field fdDrawItems;

    @Override
    public void init () {
        if (!Loader.isModLoaded(MOD_ID))
            return;

        try {
            clGuiContainerManager = Class.forName("codechicken.nei.guihook.GuiContainerManager");
            fdDrawItems = clGuiContainerManager.getDeclaredField("drawItems");

            loaded = true;
        }
        catch (ClassNotFoundException e) { }
        catch (NoSuchFieldException e) { }
    }

    @Override
    public void postInit () { }

    public boolean isLoaded () {
        return loaded;
    }

    public RenderItem setItemRender (RenderItem itemRender) {
        if (fdDrawItems == null)
            return null;

        try {
            RenderItem prev = (RenderItem) fdDrawItems.get(null);
            fdDrawItems.set(null, itemRender);
            return prev;
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }
}
