package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.texelsaurus.minecraft.chameleon.util.WorldUtils;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CommonEvents
{
    public static void init () {
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (player.isSpectator())
                return InteractionResult.PASS;

            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof BlockDrawers blockDrawers) {
                if (player.isCreative()) {
                    if (level.isClientSide)
                        return InteractionResult.CONSUME;

                    BlockHitResult hit = WorldUtils.rayTraceEyes(level, player, pos);
                    if (hit.getType() == HitResult.Type.BLOCK) {
                        if (blockDrawers.interactTakeItems(state, level, pos, player, hit))
                            return InteractionResult.CONSUME;
                    }
                }
            }

            return InteractionResult.PASS;
        });

        /*UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

        });*/
    }
}
