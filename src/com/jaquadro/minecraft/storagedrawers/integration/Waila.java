package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaTooltipHandler;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.lang.reflect.Method;

import java.util.List;

public class Waila extends IntegrationModule
{
    private static Class classConfigHandler;

    private static Method methInstance;
    private static Method methAddConfig;

    @Override
    public String getModID () {
        return "Waila";
    }

    @Override
    public void init () throws Throwable {
        classConfigHandler = Class.forName("mcp.mobius.waila.api.impl.ConfigHandler");

        methInstance = classConfigHandler.getMethod("instance");
        methAddConfig = classConfigHandler.getMethod("addConfig", String.class, String.class, String.class);

        FMLInterModComms.sendMessage("Waila", "register", StorageDrawers.SOURCE_PATH + "integration.Waila.registerProvider");
    }

    @Override
    public void postInit () { }

    @SuppressWarnings("unused")
    public static void registerProvider(IWailaRegistrar registrar) {
        WailaDrawer provider = new WailaDrawer();

        registrar.registerBodyProvider(provider, BlockDrawers.class);
        registrar.registerStackProvider(provider, BlockDrawers.class);

        try {
            Object configHandler = methInstance.invoke(null);

            methAddConfig.invoke(configHandler, StorageDrawers.MOD_NAME, "display.content", I18n.format("storageDrawers.waila.config.displayContents"), true);
            methAddConfig.invoke(configHandler, StorageDrawers.MOD_NAME, "display.stacklimit", I18n.format("storageDrawers.waila.config.displayStackLimit"), true);
            methAddConfig.invoke(configHandler, StorageDrawers.MOD_NAME, "display.status", I18n.format("storageDrawers.waila.config.displayStatus"), true);
        }
        catch (Exception e) {
            // Oh well, we couldn't hook the waila config
        }
    }

    public static class WailaDrawer implements IWailaDataProvider
    {
        @Override
        public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config) {
            List<ItemStack> drops = accessor.getBlock().getDrops(accessor.getWorld(), accessor.getPosition(), accessor.getBlockState(), 0);
            if (drops.size() == 0)
                return null;

            return drops.get(0);
        }

        @Override
        public List<String> getWailaHead (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            TileEntityDrawers tile = (TileEntityDrawers) accessor.getTileEntity();

            if (SecurityManager.hasAccess(Minecraft.getMinecraft().player.getGameProfile(), tile)) {
                if (config.getConfig("display.content")) {
                    for (int i = 0; i < tile.getDrawerCount(); i++) {
                        IDrawer drawer = tile.getDrawerIfEnabled(i);
                        if (drawer == null)
                            continue;

                        String name = I18n.format("storageDrawers.waila.empty");

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
                            } else
                                name = stackName + " [" + drawer.getStoredItemCount() + "]";
                        }
                        currenttip.add(I18n.format("storageDrawers.waila.drawer", i + 1, name));
                    }
                }

                if (config.getConfig("display.stacklimit")) {
                    if (tile.isUnlimited() || tile.isVending())
                        currenttip.add(I18n.format("storageDrawers.waila.nolimit"));
                    else {
                        int limit = tile.getEffectiveDrawerCapacity() * tile.getEffectiveStorageMultiplier();
                        currenttip.add(I18n.format("storageDrawers.waila.limit", limit, tile.getEffectiveStorageMultiplier()));
                    }
                }
            }

            if (config.getConfig("display.status")) {
                String attrib = "";
                if (tile.isItemLocked(LockAttribute.LOCK_POPULATED))
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storageDrawers.waila.locked");
                if (tile.isVoid())
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storageDrawers.waila.void");
                if (tile.isSorting())
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storageDrawers.waila.sorting");
                if (tile.getOwner() != null)
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storageDrawers.waila.protected");

                if (!attrib.isEmpty())
                    currenttip.add(attrib);
            }

            return currenttip;
        }

        @Override
        public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            return null;
        }
    }
}
