package com.jaquadro.minecraft.storagedrawers.integration;

import net.minecraft.client.renderer.entity.RenderItem;

import java.lang.reflect.Field;

public class NotEnoughItems extends IntegrationModule
{
    private static final String MOD_ID = "NotEnoughItems";

    boolean loaded;

    static Class clGuiContainerManager;
    static Field fdDrawItems;

    @Override
    public String getModID () {
        return MOD_ID;
    }

    @Override
    public void init () throws Throwable {
        clGuiContainerManager = Class.forName("codechicken.nei.guihook.GuiContainerManager");
        fdDrawItems = clGuiContainerManager.getDeclaredField("drawItems");

        loaded = true;
    }

    @Override
    public void postInit () { }

    public static RenderItem setItemRender (RenderItem itemRender) {
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
