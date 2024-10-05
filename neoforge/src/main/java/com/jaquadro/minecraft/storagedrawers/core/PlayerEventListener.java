package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IPortable;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRemote;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Punishes players holding filled drawers, if enabled in config */
public class PlayerEventListener
{

	private void applyDebuff(Player plr)
	{
		// slowness IV for 5 seconds
		plr.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, true, true));
	}

	@SubscribeEvent
	public void onPlayerPickup(ItemEntityPickupEvent.Post event) {
		if (!ModCommonConfig.INSTANCE.GENERAL.heavyDrawers.get())
			return;

		checkItemDebuf(event.getItemEntity().getItem(), event.getPlayer());
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent.Post event) {
		// every 3 seconds, in the END phase
		if(event.getEntity().tickCount % 60 != 0)
			return;

		if (event.getEntity() instanceof ServerPlayer)
			ItemUpgradeRemote.validateInventory(event.getEntity().getInventory(), event.getEntity().level());

		if (!ModCommonConfig.INSTANCE.GENERAL.heavyDrawers.get())
			return;

		for(var s : event.getEntity().getAllSlots()) {
			if (checkItemDebuf(s, event.getEntity()))
				return;
		}

		Inventory inv = event.getEntity().getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (checkItemDebuf(inv.getItem(i), event.getEntity()))
				return;
		}
	}

	private boolean checkItemDebuf (ItemStack stack, Player player) {
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
