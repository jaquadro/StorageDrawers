package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.renderer.state.FramingRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class BlockEntityFramingRenderer implements BlockEntityRenderer<BlockEntityFramingTable, FramingRenderState>
{
    private final BlockEntityRendererProvider.Context context;

    public BlockEntityFramingRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public FramingRenderState createRenderState () {
        return new FramingRenderState();
    }

    @Override
    public void extractRenderState (BlockEntityFramingTable blockEntity, FramingRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumbleOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumbleOverlay);

        renderState.blockState = blockEntity.getBlockState();

        int longPos = (int)blockEntity.getBlockPos().asLong();

        MaterialData matData = blockEntity.material();
        if (matData != null) {
            renderState.sideSlotItem = getRenderState(matData.getSide(), blockEntity.getLevel(), longPos + 1);
            renderState.trimSlotItem = getRenderState(matData.getTrim(), blockEntity.getLevel(), longPos + 2);
            renderState.frontSlotItem = getRenderState(matData.getFront(), blockEntity.getLevel(), longPos + 3);

            ItemStack target = blockEntity.inventory().getItem(0);
            if (target != null && target.getItem() instanceof BlockItem blockItem) {
                Block targetBlock = blockItem.getBlock();
                if (targetBlock instanceof IFramedSourceBlock fsb) {
                    ItemStack result = fsb.makeFramedItem(target,
                        matData.getEffectiveSide(), matData.getEffectiveTrim(), matData.getEffectiveFront());

                    renderState.mainSlotItem = getRenderState(result, blockEntity.getLevel(), longPos);
                }
            }
        }
    }

    private ItemStackRenderState getRenderState (ItemStack itemStack, Level level, int id) {
        ItemStackRenderState itemState = new ItemStackRenderState();
        context.itemModelResolver().updateForTopItem(
            itemState, itemStack, ItemDisplayContext.GROUND, level, null, id
        );
        return itemState;
    }

    @Override
    public void submit (FramingRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!(renderState.blockState.getBlock() instanceof BlockFramingTable))
            return;

        if (renderState.blockState.getValue(BlockFramingTable.PART) != EnumFramingTablePart.RIGHT)
            return;

        renderSlot(renderState, renderState.mainSlotItem, poseStack, submitNodeCollector, cameraRenderState, 1.6f, .5f, .1f, -.5f);
        renderSlot(renderState, renderState.sideSlotItem, poseStack, submitNodeCollector, cameraRenderState, 1f, .5f + .65f, .15f, .225f - .5f);
        renderSlot(renderState, renderState.trimSlotItem, poseStack, submitNodeCollector, cameraRenderState, 1f, .5f - .65f, .15f, .225f - .5f);
        renderSlot(renderState, renderState.frontSlotItem, poseStack, submitNodeCollector, cameraRenderState, 1f, .5f + .65f, .15f, -.225f - .5f);
    }

    private void renderSlot (FramingRenderState renderState, ItemStackRenderState itemState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, float scale, float tx, float ty, float tz) {
        if (itemState == null || itemState.isEmpty())
            return;

        Direction facing = renderState.blockState.getValue(BlockFramingTable.FACING);
        poseStack.pushPose();

        switch (facing) {
            case NORTH -> poseStack.mulPose((new Matrix4f()).rotateY((float)Math.toRadians(180)));
            case EAST -> poseStack.mulPose((new Matrix4f()).rotateY((float)Math.toRadians(90)));
            case WEST -> poseStack.mulPose((new Matrix4f()).rotateY((float)Math.toRadians(270)));
            case SOUTH -> poseStack.mulPose((new Matrix4f()).rotateY((float)Math.toRadians(0)));
        }

        switch (facing) {
            case NORTH -> poseStack.translate(-.5f, 0.8f, 0f);
            case EAST -> poseStack.translate(-.5f, 0.8f, 1f);
            case WEST -> poseStack.translate(.5f, 0.8f, 0f);
            case SOUTH -> poseStack.translate(.5f, 0.8f, 1f);
        }

        poseStack.translate(tx, ty, tz);
        poseStack.mulPose((new Matrix4f()).scale(scale, scale, scale));

        try {
            itemState.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        } catch (Exception e) { }

        poseStack.popPose();
    }

    // NeoForge extension
    public AABB getRenderBoundingBox(BlockEntityFramingTable blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
    }
}