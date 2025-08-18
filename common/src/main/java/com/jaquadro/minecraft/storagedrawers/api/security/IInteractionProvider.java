package com.jaquadro.minecraft.storagedrawers.api.security;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public interface IInteractionProvider
{
    String getProviderID ();

    boolean canInteract (Player player, InteractionHand hand, BlockPos pos);
}
