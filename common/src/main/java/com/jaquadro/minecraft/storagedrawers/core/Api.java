package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;

public class Api implements IStorageDrawersApi
{
    public static IStorageDrawersApi instance;

    public Api () {
        instance = this;
    }

    /*@Override
    public IRenderRegistry renderRegistry () {
        return StorageDrawers.renderRegistry;
    }

    @Override
    public IWailaRegistry wailaRegistry () {
        return StorageDrawers.wailaRegistry;
    }*/
}
