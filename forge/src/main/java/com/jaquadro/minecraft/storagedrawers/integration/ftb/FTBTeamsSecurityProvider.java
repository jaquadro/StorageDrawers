package com.jaquadro.minecraft.storagedrawers.integration.ftb;

import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.security.DefaultSecurityProvider;
import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class FTBTeamsSecurityProvider implements ISecurityProvider
{
    private DefaultSecurityProvider defaultProvider = new DefaultSecurityProvider();

    @Override
    public String getProviderID () {
        return "ftb";
    }

    @Override
    public boolean hasOwnership (GameProfile profile, IProtectable target) {
        return defaultProvider.hasOwnership(profile, target);
    }

    @Override
    public boolean hasAccess (Player player, IProtectable target) {
        if (player instanceof ServerPlayer sp) {
            if (target == null || target.getOwner() == null)
                return true;

            GameProfile ownerProfile = new GameProfile(target.getOwner(), "owner");
            GameProfile playerProfile = player.getGameProfile();

            if (ownerProfile.getId() != null && ownerProfile.getId().equals(playerProfile.getId()))
                return true;

            if (!FTBTeamsAPI.api().isManagerLoaded())
                return false;

            return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(ownerProfile.getId(), playerProfile.getId());
        }

        return false;
    }
}
