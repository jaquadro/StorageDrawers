package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaTooltipHandler;
import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

import java.util.List;

public class Waila extends IntegrationModule
{
    private static Class classConfigHandler;

    private static Method methInstance;
    private static Method methAddConfig;

    @Override
    public String getModID () {
        return "waila";
    }

    @Override
    public void init () throws Throwable {
        classConfigHandler = Class.forName("mcp.mobius.waila.api.impl.ConfigHandler");

        methInstance = classConfigHandler.getMethod("instance");
        methAddConfig = classConfigHandler.getMethod("addConfig", String.class, String.class, String.class);

        FMLInterModComms.sendMessage("waila", "register", StorageDrawers.SOURCE_PATH + "integration.Waila.registerProvider");
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
            StorageDrawers.log.error("Failed to hook the Waila Config. Could not add in custom Storage Drawers related configs.");
        }
    }

    public static class WailaDrawer implements IWailaDataProvider
    {
        @Override
        @Nonnull
        public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config) {
            // Only replace info if it needs to (trim or drawers). Else, leave it, some other mod might change it.
            // Returning ItemStack.EMPTY tells Hwyla that an override is not required.

            Block block = accessor.getBlock();

            if ((block instanceof BlockDrawers))
                return ((BlockDrawers) accessor.getBlock()).getWailaTOPBlock(accessor.getWorld(), accessor.getPosition(), accessor.getBlockState());

            if (block instanceof BlockTrim){
                List<ItemStack> drops = ((BlockTrim) block).getDrops(accessor.getWorld(), accessor.getPosition(), accessor.getBlockState(), 0);
                if (drops == null || drops.isEmpty())
                    return ItemStack.EMPTY;

                return drops.get(0);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public List<String> getWailaHead (@Nonnull ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public List<String> getWailaBody (@Nonnull ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            TileEntityDrawers tile = (TileEntityDrawers) accessor.getTileEntity();
            IDrawerAttributes attr = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null);
            if (attr == null)
                attr = new EmptyDrawerAttributes();

            if (SecurityManager.hasAccess(Minecraft.getMinecraft().player.getGameProfile(), tile)) {
                if (config.getConfig("display.content")) {
                    for (int i = 0; i < tile.getDrawerCount(); i++) {
                        IDrawer drawer = tile.getDrawer(i);
                        if (!drawer.isEnabled())
                            continue;

                        String name = I18n.format("storagedrawers.waila.empty");

                        ItemStack stack = drawer.getStoredItemPrototype();
                        if (!stack.isEmpty()) {
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
                        currenttip.add(I18n.format("storagedrawers.waila.drawer", i + 1, name));
                    }
                }

                if (config.getConfig("display.stacklimit")) {
                    if (tile.getDrawerAttributes().isUnlimitedStorage() || tile.getDrawerAttributes().isUnlimitedVending())
                        currenttip.add(I18n.format("storagedrawers.waila.nolimit"));
                    else {
                        int multiplier = tile.upgrades().getStorageMultiplier();
                        int limit = tile.getEffectiveDrawerCapacity() * multiplier;
                        currenttip.add(I18n.format("storagedrawers.waila.limit", limit, multiplier));
                    }
                }
            }

            if (config.getConfig("display.status")) {
                String attrib = "";
                if (attr.isItemLocked(LockAttribute.LOCK_POPULATED))
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storagedrawers.waila.locked");
                if (attr.isVoid())
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storagedrawers.waila.void");
                if (tile.getOwner() != null)
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storagedrawers.waila.protected");

                if (!attrib.isEmpty())
                    currenttip.add(attrib);
            }

            return currenttip;
        }

        @Override
        public List<String> getWailaTail (@Nonnull ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            return null;
        }
    }
}
