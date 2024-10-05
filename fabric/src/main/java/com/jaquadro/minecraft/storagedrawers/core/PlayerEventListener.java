package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IPortable;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRemote;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlayerEventListener
{
    private static void applyDebuff(Player plr)
    {
        // slowness IV for 5 seconds
        plr.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, true, true));
    }

    public static void onPlayerTick(Player player) {
        // every 3 seconds, in the END phase
        if(player.tickCount % 60 != 0)
            return;

        ItemUpgradeRemote.validateInventory(player.getInventory(), player.level());

        if (!ModCommonConfig.INSTANCE.GENERAL.heavyDrawers.get())
            return;

        for(var s : player.getAllSlots()) {
            if (checkItemDebuf(s, player))
                return;
        }

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (checkItemDebuf(inv.getItem(i), player))
                return;
        }
    }

    private static boolean checkItemDebuf (ItemStack stack, Player player) {
        Item item = stack.getItem();
        if (item instanceof IPortable ip) {
            if (ip.isHeavy(player.level().registryAccess(), stack)) {
                applyDebuff(player);
                return true;
            }
        }

        return false;
    }
}
