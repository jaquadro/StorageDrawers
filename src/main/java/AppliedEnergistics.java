/*package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.ae2.*;

import java.lang.reflect.Constructor;*/

//public class AppliedEnergistics extends IntegrationModule
//{
    /*private static class ReflectionFactory implements IStorageBusMonitorFactory
    {
        private Class classInventoryAdaptor;
        private Class classMEAdaptor;
        private Class classMonitor;

        private Constructor constMEAdaptor;
        private Constructor constMonitor;

        public boolean init () {
            try {
                classInventoryAdaptor = Class.forName("appeng.util.InventoryAdaptor");
                classMEAdaptor = Class.forName("appeng.util.inv.IMEAdaptor");
                classMonitor = Class.forName("appeng.me.storage.MEMonitorIInventory");

                constMEAdaptor = classMEAdaptor.getConstructor(IMEInventory.class, BaseActionSource.class);
                constMonitor = classMonitor.getConstructor(classInventoryAdaptor);

                return true;
            }
            catch (Throwable t) {
                return false;
            }
        }

        @Override
        public IMEMonitor<IAEItemStack> createStorageBusMonitor (IMEInventory<IAEItemStack> inventory, BaseActionSource src) {
            try {
                Object adaptor = constMEAdaptor.newInstance(inventory, src);
                Object monitor = constMonitor.newInstance(adaptor);

                return (IMEMonitor<IAEItemStack>) monitor;
            }
            catch (Throwable t) {
                return null;
            }
        }
    }

    private static class APIFactory implements IStorageBusMonitorFactory {

        @Override
        public IMEMonitor<IAEItemStack> createStorageBusMonitor (IMEInventory<IAEItemStack> inventory, BaseActionSource src) {
            return null;
        }
    }

    private IStorageBusMonitorFactory factory;*/

//    @Override
//    public String getModID () {
//        return "appliedenergistics2";
//    }
//
//    @Override
//    public void init () throws Throwable {
        /*ShapedRecipeHandler shapedHandler = new ShapedRecipeHandler();
        if (shapedHandler.isValid())
            StorageDrawers.recipeHandlerRegistry.registerRecipeHandler(shapedHandler.getRecipeClass(), shapedHandler);

        ShapelessRecipeHandler shapelessHandler = new ShapelessRecipeHandler();
        if (shapelessHandler.isValid())
            StorageDrawers.recipeHandlerRegistry.registerRecipeHandler(shapelessHandler.getRecipeClass(), shapelessHandler);

        StorageDrawers.recipeHandlerRegistry.registerIngredientHandler(IIngredient.class, new IngredientHandler());

        ReflectionFactory rfactory = new ReflectionFactory();
        if (!rfactory.init())
            throw new Exception("No valid Storage Bus Monitor factory");

        factory = rfactory;*/
//    }
//
//    @Override
//    public void postInit () {
//        //AEApi.instance().registries().externalStorage().addExternalStorageInterface(new DrawerExternalStorageHandler(factory));
//    }
//}
