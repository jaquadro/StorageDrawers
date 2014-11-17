package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * @author dmillerw
 * @author jaquadro
 */
public class Waila extends IntegrationModule
{
    @Override
    public String getModID () {
        return "Waila";
    }

    @Override
    public void init () throws Throwable {
        FMLInterModComms.sendMessage("Waila", "register", StorageDrawers.SOURCE_PATH + "integration.Waila.registerProvider");
    }

    @Override
    public void postInit () { }

    public static void registerProvider(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new WailaDrawer(), BlockDrawers.class);
    }

    public static class WailaDrawer implements IWailaDataProvider
    {
        @Override
        public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return null;
        }

        @Override
        public List<String> getWailaHead (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            TileEntityDrawers tile = (TileEntityDrawers) accessor.getTileEntity();

            for (int i = 0; i < tile.getDrawerCount(); i++) {
                String name = StatCollector.translateToLocal("storageDrawers.waila.empty");

                IDrawer drawer = tile.getDrawer(i);
                ItemStack stack = drawer.getStoredItemPrototype();
                if (stack != null && stack.getItem() != null) {
                    if (tile instanceof TileEntityDrawersComp)
                        name = stack.getDisplayName() + ((i == 0) ? " [" : " [+") + ((TileEntityDrawersComp) tile).getStoredItemRemainder(i) + "]";
                    else
                        name = stack.getDisplayName() + " [" + drawer.getStoredItemCount() + "]";
                }
                currenttip.add(StatCollector.translateToLocalFormatted("storageDrawers.waila.drawer", i + 1, name));
            }

            return currenttip;
        }

        @Override
        public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }
    }
}
