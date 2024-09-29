package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IPortable;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;

import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRemote;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

/** Punishes players holding filled drawers, if enabled in config */
public class PlayerEventListener {

	private void applyDebuff(Player plr)
	{
		// slowness IV for 5 seconds
		plr.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, true, true));
	}

	@SubscribeEvent
	public void onPlayerPickup(EntityItemPickupEvent event) {
		if (!CommonConfig.GENERAL.heavyDrawers.get())
			return;

		checkItemDebuf(event.getItem().getItem(), event.getEntity());
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		// every 3 seconds, in the END phase
		if(event.phase != Phase.END || event.player.tickCount % 60 != 0)
			return;

		if (event.side == LogicalSide.SERVER)
			ItemUpgradeRemote.validateInventory(event.player.getInventory(), event.player.level());

		if (!CommonConfig.GENERAL.heavyDrawers.get())
			return;

		for(var s : event.player.getAllSlots()) {
			if (checkItemDebuf(s, event.player))
				return;
		}

		Inventory inv = event.player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (checkItemDebuf(inv.getItem(i), event.player))
				return;
		}
	}

	/*@SubscribeEvent
	public void onPlayerContainerOpen (PlayerContainerEvent.Open event) {
		if (!(event.getEntity() instanceof ServerPlayer))
			return;

		ItemUpgradeRemote.validateInventory(event.getEntity().getInventory(), event.getEntity().level());
		//if (event.getContainer() instanceof ContainerDrawers)
		//	ItemUpgradeRemote.validateInventory(event.getContainer()., event.getEntity().level());
	}*/

	private boolean checkItemDebuf (ItemStack stack, Player player) {
		Item item = stack.getItem();
		if (item instanceof IPortable ip) {
			if (ip.isHeavy(stack)) {
				applyDebuff(player);
				return true;
			}
		}

		return false;
	}
}
