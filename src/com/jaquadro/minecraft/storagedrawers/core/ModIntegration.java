package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.integration.IModIntegration;
import com.jaquadro.minecraft.storagedrawers.integration.NotEnoughItems;

import java.util.ArrayList;
import java.util.List;

public class ModIntegration
{
    private List<IModIntegration> mods = new ArrayList<IModIntegration>();

    public final NotEnoughItems NEI;

    public ModIntegration () {
        NEI = add(new NotEnoughItems());
    }

    private <E extends IModIntegration> E add (E mod) {
        mods.add(mod);
        return mod;
    }

    public void init () {
        for (int i = 0, n = mods.size(); i < n; i++)
            mods.get(i).init();
    }

    public void postInit () {
        for (int i = 0, n = mods.size(); i < n; i++)
            mods.get(i).postInit();
    }
}
