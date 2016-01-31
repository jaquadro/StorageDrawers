package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IUserConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.IExtendedDataResolver;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandlerRegistry;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRenderRegistry;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaRegistry;
import com.jaquadro.minecraft.storagedrawers.core.api.PackFactory;
import com.jaquadro.minecraft.storagedrawers.core.api.PackRecipes;
import com.jaquadro.minecraft.storagedrawers.integration.ThermalExpansion;

public class Api implements IStorageDrawersApi
{
    public static IStorageDrawersApi instance;

    //private PackFactory packFactory = new PackFactory();

    public Api () {
        instance = this;
    }

    @Override
    public IRecipeHandlerRegistry recipeHandlerRegistry () {
        return StorageDrawers.recipeHandlerRegistry;
    }

    @Override
    public IRenderRegistry renderRegistry () {
        return StorageDrawers.renderRegistry;
    }

    @Override
    public IWailaRegistry wailaRegistry () {
        return StorageDrawers.wailaRegistry;
    }

    /*@Override
    public IPackBlockFactory packFactory () {
        return packFactory;
    }*/

    @Override
    public IUserConfig userConfig () {
        return StorageDrawers.config.userConfig;
    }

    @Override
    public void registerStandardPackRecipes (IExtendedDataResolver resolver) {
        PackRecipes.registerStandardRecipes(resolver);
        PackRecipes.registerSortingRecipes(resolver);
        ThermalExpansion.registerPackBlock(resolver);
    }
}
