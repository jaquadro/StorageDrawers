package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Punishes players holding filled drawers, if enabled in config */
public class PlayerEventListener {

	private void applyDebuff(Player plr)
	{
		// slowness IV for 5 seconds
		plr.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, true, true));
	}

	@SubscribeEvent
	public void onPlayerPickup(EntityItemPickupEvent event) {
		if(ItemDrawers.isHeavy(event.getItem().getItem()))
			applyDebuff(event.getEntity());
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		// every 3 seconds, in the END phase
		if(event.phase != Phase.END || event.player.tickCount % 60 != 0)
			return;

		for(var s : event.player.getAllSlots())
		{
			if(s.getCount() > 0 && ItemDrawers.isHeavy(s))
			{
				applyDebuff(event.player);
				return;
			}
		}
	}
}
