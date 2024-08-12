package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ClientConfig;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = StorageDrawers.MOD_ID)
public class CommonEventBusSubscriber {
    @SubscribeEvent
    public static void playerLeftClick (@NotNull PlayerInteractEvent.LeftClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BlockDrawers blockDrawers) {
            Player player = event.getEntity();
            if (player.isCreative()) {
                BlockHitResult hit = WorldUtils.rayTraceEyes(level, player, pos);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    //boolean invertClick = ClientConfig.GENERAL.invertClick.get();
                    //if (!invertClick)
                        event.setCanceled(blockDrawers.interactTakeItems(state, level, pos, player, hit));
                    //else {
                    //    if (hit.getBlockPos().equals(pos))
                    //        blockDrawers.insertOrApplyItem(state, level, pos, player, hit);
                    //    event.setCanceled(true);
                    //}
                }
            }
        }
    }
}
