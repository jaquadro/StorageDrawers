package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import mcp.mobius.waila.api.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

import java.util.List;

@WailaPlugin(StorageDrawers.MOD_ID)
public class Waila implements IWailaPlugin
{
    @Override
    public void register (IRegistrar registrar) {
        registerProvider(registrar);
    }

    private void registerProvider(IRegistrar registrar) {
        WailaDrawer provider = new WailaDrawer();

        registrar.addConfig(new ResourceLocation(StorageDrawers.MOD_ID, "display.content"), true);
        registrar.addConfig(new ResourceLocation(StorageDrawers.MOD_ID, "display.stacklimit"), true);
        registrar.addConfig(new ResourceLocation(StorageDrawers.MOD_ID, "display.status"), true);
        registrar.registerComponentProvider(provider, TooltipPosition.BODY, BlockDrawers.class);
    }

    public static class WailaDrawer implements IComponentProvider
    {
        @Override
        @Nonnull
        public ItemStack getStack (IDataAccessor accessor, IPluginConfig config) {
            return new ItemStack(accessor.getBlock(), 1);
        }

        @Override
        public void appendBody (List<ITextComponent> currenttip, IDataAccessor accessor, IPluginConfig config) {
            TileEntityDrawers tile = (TileEntityDrawers) accessor.getTileEntity();
            IDrawerAttributes attr = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, null).orElse(EmptyDrawerAttributes.EMPTY);

            //if (SecurityManager.hasAccess(Minecraft.getInstance().player.getGameProfile(), tile)) {
                if (config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.content"))) {
                    for (int i = 0; i < tile.getDrawerCount(); i++) {
                        IDrawer drawer = tile.getDrawer(i);
                        if (!drawer.isEnabled())
                            continue;

                        ITextComponent name = new TranslationTextComponent("tooltip.storagedrawers.waila.empty");

                        ItemStack stack = drawer.getStoredItemPrototype();
                        if (!stack.isEmpty()) {
                            IFormattableTextComponent stackName = new StringTextComponent("").append(stack.getHoverName());

                            if (drawer.getStoredItemCount() == Integer.MAX_VALUE) {
                                name = stackName.append("[\u221E]");
                            }
                            else if (drawer instanceof IFractionalDrawer && ((IFractionalDrawer) drawer).getConversionRate() > 1) {
                                String text = ((i == 0) ? " [" : " [+") + ((IFractionalDrawer) drawer).getStoredItemRemainder() + "]";
                                name = stackName.append(text);
                            }
                            else if (CommonConfig.INTEGRATION.wailaStackRemainder.get()) {
                                int stacks = drawer.getStoredItemCount() / drawer.getStoredItemStackSize();
                                int remainder = drawer.getStoredItemCount() - (stacks * drawer.getStoredItemStackSize());
                                if (stacks > 0 && remainder > 0)
                                    name = stackName.append(" [" + stacks + "x" + drawer.getStoredItemStackSize() + " + " + remainder + "]");
                                else if (stacks > 0)
                                    name = stackName.append(" [" + stacks + "x" + drawer.getStoredItemStackSize() + "]");
                                else
                                    name = stackName.append(" [" + remainder + "]");
                            } else
                                name = stackName.append(" [" + drawer.getStoredItemCount() + "]");
                        }
                        currenttip.add(new TranslationTextComponent("tooltip.storagedrawers.waila.drawer", i + 1, name));
                    }
                }

                if (config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.stacklimit"))) {
                    if (tile.getDrawerAttributes().isUnlimitedStorage() || tile.getDrawerAttributes().isUnlimitedVending())
                        currenttip.add(new TranslationTextComponent("tooltip.storagedrawers.waila.nolimit"));
                    else {
                        int multiplier = tile.upgrades().getStorageMultiplier();
                        int limit = tile.getEffectiveDrawerCapacity() * multiplier;
                        currenttip.add(new TranslationTextComponent("tooltip.storagedrawers.waila.limit", limit, multiplier));
                    }
                }
            //}

            if (config.get(new ResourceLocation(StorageDrawers.MOD_ID, "display.status"))) {
                String attrib = "";
                if (attr.isItemLocked(LockAttribute.LOCK_POPULATED))
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.get("tooltip.storagedrawers.waila.locked");
                if (attr.isVoid())
                    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.get("tooltip.storagedrawers.waila.void");
                //if (tile.getOwner() != null)
                //    attrib += (attrib.isEmpty() ? "" : ", ") + I18n.format("storagedrawers.waila.protected");

                if (!attrib.isEmpty())
                    currenttip.add(new StringTextComponent(attrib));
            }
        }
    }
}
