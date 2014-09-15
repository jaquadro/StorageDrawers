package com.jaquadro.minecraft.storagedrawers.compat;

import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * @author dmillerw
 */
public class WailaProvider
{
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
            TileEntityDrawersBase tile = (TileEntityDrawersBase) accessor.getTileEntity();

            currenttip.add(StatCollector.translateToLocalFormatted("storageDrawers.waila.level", tile.getLevel()));
            for (int i = 0; i < tile.getDrawerCount(); i++) {
                String name = StatCollector.translateToLocal("storageDrawers.waila.empty");
                ItemStack stack = tile.getSingleItemStack(i);
                if (stack != null && stack.getItem() != null) {
                    if (tile instanceof TileEntityCompDrawers)
                        name = stack.getDisplayName() + ((i == 0) ? " [" : " [+") + ((TileEntityCompDrawers) tile).getItemRemainderCount(i) + "]";
                    else
                        name = stack.getDisplayName() + " [" + tile.getItemCount(i) + "]";
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
