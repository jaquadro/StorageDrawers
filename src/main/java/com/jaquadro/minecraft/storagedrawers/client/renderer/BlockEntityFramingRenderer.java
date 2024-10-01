package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawersFramed;
import com.jaquadro.minecraft.storagedrawers.block.EnumFramingTablePart;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.item.ItemFramedDrawers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

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
        if (matData != null) {
            renderSlot(blockEntityTable, matData.getSide(), matrix, buffer, combinedLight, combinedOverlay, 1f, .5f + .65f, .15f, .225f - .5f);
            renderSlot(blockEntityTable, matData.getTrim(), matrix, buffer, combinedLight, combinedOverlay, 1f, .5f - .65f, .15f, .225f - .5f);
            renderSlot(blockEntityTable, matData.getFront(), matrix, buffer, combinedLight, combinedOverlay, 1f, .5f + .65f, .15f, -.225f - .5f);
        }

        if (matData != null && !matData.getSide().isEmpty()) {
            ItemStack target = blockEntityTable.getItem(0);
            if (target != null && target.getItem() instanceof BlockItem blockItem) {
                Block targetBlock = blockItem.getBlock();
                if (targetBlock instanceof BlockStandardDrawersFramed) {
                    ItemStack result = ItemFramedDrawers.makeItemStack(targetBlock.defaultBlockState(), 1,
                        matData.getEffectiveSide(), matData.getEffectiveTrim(), matData.getEffectiveFront());

                    renderSlot(blockEntityTable, result, matrix, buffer, combinedLight, combinedOverlay, 1.6f, .5f, .1f, -.5f);
                }
            }
        }
    }

    /*
    @Override
    public void render (TileEntityFramingTable tile, double x, double y, double z, float partialTickTime, int destroyStage, float par7) {
        if (tile == null)
            return;

        IBlockState state = getWorld().getBlockState(tile.getPos());
        if (!(state.getBlock() instanceof BlockFramingTable))
            return;

        if (!state.getValue(BlockFramingTable.RIGHT_SIDE))
            return;

        ItemStack target = tile.getStackInSlot(0);
        if (!target.isEmpty()) {
            Block block = Block.getBlockFromItem(target.getItem());
            IBlockState blockState = block.getStateFromMeta(target.getMetadata());
            if (block instanceof BlockDrawersCustom) {
                ItemStack result = ItemCustomDrawers.makeItemStack(blockState, 1, tile.getStackInSlot(1), tile.getStackInSlot(2), tile.getStackInSlot(3));
                renderSlot(tile, x, y, z, result, 1f, .5f, .25f, -.5f);
            }
            else if (block instanceof BlockTrimCustom) {
                ItemStack result = ItemCustomTrim.makeItemStack(block, 1, tile.getStackInSlot(1), tile.getStackInSlot(2));
                renderSlot(tile, x, y, z, result, 1f, .5f, .25f, -.5f);
            }
        }

        renderSlot(tile, x, y, z, tile.getStackInSlot(1), .575f, .5f + .65f, .15f, .225f - .5f);
        renderSlot(tile, x, y, z, tile.getStackInSlot(2), .575f, .5f - .65f, .15f, .225f - .5f);
        renderSlot(tile, x, y, z, tile.getStackInSlot(3), .575f, .5f + .65f, .15f, -.225f - .5f);
    }
    */

    private void renderSlot (BlockEntityFramingTable blockEntityTable, ItemStack item, PoseStack matrix, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay, float scale, float tx, float ty, float tz) {
        if (item == null)
            return;

        Block itemBlock = Block.byItem(item.getItem());
        if (itemBlock == Blocks.AIR)
            return;

        BlockPos pos = blockEntityTable.getBlockPos();
        Direction facing = blockEntityTable.getBlockState().getValue(BlockFramingTable.FACING);

        matrix.pushPose();
        //matrix.translate(pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f);


        //switch (facing) {
        //    case EAST -> matrix.rotateAround(Axis.YP.rotationDegrees(0), 0, );
        //}

        matrix.mulPoseMatrix((new Matrix4f()).rotateY((float)Math.toRadians(-90)));
        matrix.translate(.5f, .8f, 0f);
        matrix.translate(tx, ty, tz);

        matrix.mulPoseMatrix((new Matrix4f()).scale(scale, scale, scale));

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = renderer.getModel(item, null, null, 0);

        try {
            renderer.render(item, ItemDisplayContext.GROUND, false, matrix, buffer, combinedLight, combinedOverlay, model);
        } catch (Exception e) { }

        matrix.popPose();

        /*
        GL11.glPushMatrix();

        GL11.glTranslated(x + .5, y + 1, z + .5);

        if (side == 3)
            GL11.glRotatef(180, 0, 1, 0);
        if (side == 4)
            GL11.glRotatef(90, 0, 1, 0);
        if (side == 5)
            GL11.glRotatef(270, 0, 1, 0);

        GL11.glTranslatef(0, 0f, .5f);
        GL11.glTranslatef(tx, ty, tz);
        GL11.glScalef(scale, scale, scale);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        try {
            Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.FIXED);
        }
        catch (Exception e) { }

        GL11.glPopMatrix();
        */
    }
}
