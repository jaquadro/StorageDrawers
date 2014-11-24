package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandlerRegistry;

public class Api implements IStorageDrawersApi
{
    public static IStorageDrawersApi instance;

    public Api () {
        instance = this;
    }

    @Override
    public IRecipeHandlerRegistry recipeHandlerRegistry () {
        return StorageDrawers.recipeHandlerRegistry;
    }
}
