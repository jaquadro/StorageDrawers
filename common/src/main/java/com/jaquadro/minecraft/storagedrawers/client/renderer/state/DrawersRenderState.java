package com.jaquadro.minecraft.storagedrawers.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

public class DrawersRenderState extends BlockEntityRenderState
{
    public BlockState blockState;
    public Vec3 cameraPos;
    public int enforcedLightLevel;
    public List<SlotState> items = Collections.emptyList();
    public boolean isConcealed;
    public boolean showFill;
    public boolean showCount;

    public DrawersRenderState() { }

    public record SlotState(ItemStackRenderState itemState, int count, int limit) { }
}
