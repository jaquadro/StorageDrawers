package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrimCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomTrim;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityFramingRenderer extends TileEntitySpecialRenderer<TileEntityFramingTable>
{
    @Override
    public void renderTileEntityAt (TileEntityFramingTable tile, double x, double y, double z, float partialTickTime, int destroyStage) {
        if (tile == null)
            return;

        IBlockState state = getWorld().getBlockState(tile.getPos());
        if (!(state.getBlock() instanceof BlockFramingTable))
            return;

        if (!state.getValue(BlockFramingTable.RIGHT_SIDE))
            return;

        ItemStack target = tile.getStackInSlot(0);
        if (target != null) {
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

    private void renderSlot (TileEntityFramingTable tileTable, double x, double y, double z, ItemStack item, float scale, float tx, float ty, float tz) {
        if (item == null)
            return;

        Block itemBlock = Block.getBlockFromItem(item.getItem());
        if (itemBlock == null)
            return;

        IBlockState state = getWorld().getBlockState(tileTable.getPos());
        EnumFacing dir = state.getValue(BlockFramingTable.FACING);
        int side = dir.getIndex();

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
    }
}
