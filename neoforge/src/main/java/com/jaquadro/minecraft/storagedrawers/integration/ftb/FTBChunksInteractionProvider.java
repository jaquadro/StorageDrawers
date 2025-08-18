package com.jaquadro.minecraft.storagedrawers.integration.ftb;

import com.jaquadro.minecraft.storagedrawers.api.security.IInteractionProvider;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.api.Protection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class FTBChunksInteractionProvider implements IInteractionProvider
{
    @Override
    public String getProviderID () {
        return "ftb";
    }

    @Override
    public boolean canInteract (Player player, InteractionHand hand, BlockPos pos) {
        if (!(player instanceof ServerPlayer))
            return true;

        if (!FTBChunksAPI.api().isManagerLoaded())
            return false;

        return !FTBChunksAPI.api().getManager().shouldPreventInteraction(player, hand, pos, Protection.INTERACT_BLOCK, null);
    }
}
