package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaTooltipHandler;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

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

    @SuppressWarnings("unused")
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
                if (!tile.isDrawerEnabled(i))
                    continue;

                String name = StatCollector.translateToLocal("storageDrawers.waila.empty");

                IDrawer drawer = tile.getDrawer(i);
                ItemStack stack = drawer.getStoredItemPrototype();
                if (stack != null && stack.getItem() != null) {
                    String stackName = stack.getDisplayName();
                    List<IWailaTooltipHandler> handlers = StorageDrawers.wailaRegistry.getTooltipHandlers();
                    for (int j = 0, n = handlers.size(); j < n; j++)
                        stackName = handlers.get(j).transformItemName(drawer, stackName);

                    if (drawer.getStoredItemCount() == Integer.MAX_VALUE)
                        name = stackName + " [\u221E]";
                    else if (drawer instanceof IFractionalDrawer && ((IFractionalDrawer) drawer).getConversionRate() > 1)
                        name = stackName + ((i == 0) ? " [" : " [+") + ((IFractionalDrawer) drawer).getStoredItemRemainder() + "]";
                    else if (StorageDrawers.config.cache.stackRemainderWaila) {
                        int stacks = drawer.getStoredItemCount() / drawer.getStoredItemStackSize();
                        int remainder = drawer.getStoredItemCount() - (stacks * drawer.getStoredItemStackSize());
                        if (stacks > 0 && remainder > 0)
                            name = stackName + " [" + stacks + "x" + drawer.getStoredItemStackSize() + " + " + remainder + "]";
                        else if (stacks > 0)
                            name = stackName + " [" + stacks + "x" + drawer.getStoredItemStackSize() + "]";
                        else
                            name = stackName + " [" + remainder + "]";
                    }
                    else
                        name = stackName + " [" + drawer.getStoredItemCount() + "]";
                }
                currenttip.add(StatCollector.translateToLocalFormatted("storageDrawers.waila.drawer", i + 1, name));
            }

            int limit = tile.getDrawerCapacity() * tile.getEffectiveStorageMultiplier();
            currenttip.add(StatCollector.translateToLocalFormatted("storageDrawers.waila.limit", limit, tile.getEffectiveStorageMultiplier()));

            String attrib = "";
            if (tile.isLocked(LockAttribute.LOCK_POPULATED))
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
        public NBTTagCompound getNBTData (EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            return null;
        }
    }
}
