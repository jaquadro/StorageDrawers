package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BlockEntityFramingRenderer implements BlockEntityRenderer<BlockEntityFramingTable>
{
    private final BlockEntityRendererProvider.Context context;

    public BlockEntityFramingRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render (@NotNull BlockEntityFramingTable blockEntityTable, float partialTickTime, @NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Level level = blockEntityTable.getLevel();
        if (level == null)
            return;

        BlockState state = blockEntityTable.getBlockState();
        if (!(state.getBlock() instanceof BlockFramingTable))
            return;

        if (state.getValue(BlockFramingTable.PART) != EnumFramingTablePart.RIGHT)
            return;

        MaterialData matData = blockEntityTable.material();
        if (matData == null)
            return;

        renderSlot(blockEntityTable, matData.getSide(), matrix, buffer, combinedLight, combinedOverlay, 1f, .5f + .65f, .15f, .225f - .5f);
        renderSlot(blockEntityTable, matData.getTrim(), matrix, buffer, combinedLight, combinedOverlay, 1f, .5f - .65f, .15f, .225f - .5f);
        renderSlot(blockEntityTable, matData.getFront(), matrix, buffer, combinedLight, combinedOverlay, 1f, .5f + .65f, .15f, -.225f - .5f);

        if (matData.getEffectiveSide().isEmpty())
            return;

        ItemStack target = blockEntityTable.getItem(0);
        if (target == null || target.isEmpty())
            return;

        if (target.getItem() instanceof BlockItem blockItem) {
            Block targetBlock = blockItem.getBlock();
            if (targetBlock instanceof IFramedSourceBlock fsb) {
                ItemStack result = fsb.makeFramedItem(target,
                    matData.getEffectiveSide(), matData.getEffectiveTrim(), matData.getEffectiveFront());

                renderSlot(blockEntityTable, result, matrix, buffer, combinedLight, combinedOverlay, 1.6f, .5f, .1f, -.5f);
            }
        }
    }

    private void renderSlot (BlockEntityFramingTable blockEntityTable, ItemStack item, PoseStack matrix, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay, float scale, float tx, float ty, float tz) {
        if (item == null)
            return;

        Block itemBlock = Block.byItem(item.getItem());
        if (itemBlock == Blocks.AIR)
            return;

        Direction facing = blockEntityTable.getBlockState().getValue(BlockFramingTable.FACING);
        matrix.pushPose();

        switch (facing) {
            case NORTH -> matrix.mulPoseMatrix((new Matrix4f()).rotateY((float)Math.toRadians(180)));
            case EAST -> matrix.mulPoseMatrix((new Matrix4f()).rotateY((float)Math.toRadians(90)));
            case WEST -> matrix.mulPoseMatrix((new Matrix4f()).rotateY((float)Math.toRadians(270)));
            case SOUTH -> matrix.mulPoseMatrix((new Matrix4f()).rotateY((float)Math.toRadians(0)));
        }

        switch (facing) {
            case NORTH -> matrix.translate(-.5f, 0.8f, 0f);
            case EAST -> matrix.translate(-.5f, 0.8f, 1f);
            case WEST -> matrix.translate(.5f, 0.8f, 0f);
            case SOUTH -> matrix.translate(.5f, 0.8f, 1f);
        }

        matrix.translate(tx, ty, tz);

        matrix.mulPoseMatrix((new Matrix4f()).scale(scale, scale, scale));

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = renderer.getModel(item, null, null, 0);

        try {
            renderer.render(item, ItemDisplayContext.GROUND, false, matrix, buffer, combinedLight, combinedOverlay, model);
        } catch (Exception e) { }

        matrix.popPose();
    }
}
