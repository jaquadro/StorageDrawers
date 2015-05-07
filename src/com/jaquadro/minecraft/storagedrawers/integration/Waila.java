package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaTooltipHandler;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

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
        //FMLInterModComms.sendMessage("Waila", "register", StorageDrawers.SOURCE_PATH + "integration.Waila.registerProvider");
    }

    @Override
    public void postInit () { }

    /*public static void registerProvider(IWailaRegistrar registrar) {
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
                    String stackName = stack.getDisplayName();
                    List<IWailaTooltipHandler> handlers = StorageDrawers.wailaRegistry.getTooltipHandlers();
                    for (int j = 0, n = handlers.size(); j < n; j++)
                        stackName = handlers.get(j).transformItemName(drawer, stackName);

                    if (tile instanceof TileEntityDrawersComp)
                        name = stackName + ((i == 0) ? " [" : " [+") + ((TileEntityDrawersComp) tile).getStoredItemRemainder(i) + "]";
                    else
                        name = stackName + " [" + drawer.getStoredItemCount() + "]";
                }
                currenttip.add(StatCollector.translateToLocalFormatted("storageDrawers.waila.drawer", i + 1, name));
            }

            String attrib = "";
            if (tile.isLocked())
                attrib += (attrib.isEmpty() ? "" : ", ") + StatCollector.translateToLocal("storageDrawers.waila.locked");
            if (tile.isVoid())
                attrib += (attrib.isEmpty() ? "" : ", ") + StatCollector.translateToLocal("storageDrawers.waila.void");
            if (tile.isSorting())
                attrib += (attrib.isEmpty() ? "" : ", ") + StatCollector.translateToLocal("storageDrawers.waila.sorting");

            if (!attrib.isEmpty())
                currenttip.add(attrib);

            return currenttip;
        }

        @Override
        public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public NBTTagCompound getNBTData (EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
            return null;
        }
    }*/
}
