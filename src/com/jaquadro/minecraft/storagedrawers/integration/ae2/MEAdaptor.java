package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.item.ItemStack;

/**
 * Created by Justin on 11/10/2014.
 */
public class MEAdaptor
{
    private final IMEInventory<IAEItemStack> target;
    private final BaseActionSource source;
    int maxSlots = 0;

    public MEAdaptor (IMEInventory<IAEItemStack> input, BaseActionSource src) {
        target = input;
        source = src;
    }

    IItemList<IAEItemStack> getList () {
        return target.getAvailableItems(AEApi.instance().storage().createItemList());
    }

    // Iterator

    public IAEItemStack addItems (IAEItemStack stack) {
        return addItems(stack, Actionable.MODULATE);
    }

    public IAEItemStack simulateAdd (IAEItemStack stack) {
        return addItems(stack, Actionable.SIMULATE);
    }

    private IAEItemStack addItems (IAEItemStack in, Actionable actionType) {
        if (in != null) {
            IAEItemStack out = target.injectItems(in, actionType, source);
            if (out != null)
                return out;
        }

        return null;
    }

    public IAEItemStack removeItems (IAEItemStack req) {
        return removeItems(req, Actionable.MODULATE);
    }

    public IAEItemStack simulateRemove (IAEItemStack req) {
        return removeItems(req, Actionable.SIMULATE);
    }

    private IAEItemStack removeItems (IAEItemStack req, Actionable actionType) {
        if (req == null) {
            IItemList<IAEItemStack> list = getList();
            if (!list.isEmpty())
                req = list.getFirstItem();
        }

        IAEItemStack out = null;
        if (req != null)
            out = target.extractItems(req, actionType, source);

        return out;
    }
}
