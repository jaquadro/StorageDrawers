package com.jaquadro.minecraft.storagedrawers.security;

import com.jaquadro.minecraft.storagedrawers.api.security.IInteractionProvider;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.client.ClientUtil;
import com.jaquadro.minecraft.storagedrawers.core.ModSecurity;
import com.mojang.authlib.GameProfile;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class SecurityManager
{
    private static final ISecurityProvider defaultProvider = new DefaultSecurityProvider();
    private static final IInteractionProvider defaultInteractProvider = new DefaultInteractionProvider();

    public static boolean hasOwnership (GameProfile profile, IProtectable target) {
        if (target == null || profile == null)
            return false;

        ISecurityProvider provider = target.getSecurityProvider();
        if (provider == null)
            provider = defaultProvider;

        return provider.hasOwnership(profile, target);
    }

    public static boolean hasAccess (Player player, IProtectable target) {
        if (target == null || player == null)
            return false;

        ISecurityProvider provider = target.getSecurityProvider();
        if (provider == null)
            provider = defaultProvider;

        return provider.hasAccess(player, target);
    }

    public static boolean canInteract (Player player, InteractionHand hand, BlockPos pos) {
        if (pos == null || player == null)
            return false;

        for (IInteractionProvider provider : ModSecurity.registry.getAllInteractionProviders()) {
            if (!provider.canInteract(player, hand, pos))
                return false;
        }

        return true;
    }

    public static boolean clientHasAccess (IProtectable target) {
        if (!ChameleonServices.PLATFORM.isPhysicalClient())
            return false;

        return hasAccess(ClientUtil.getLocalPlayer(), target);
    }
}
