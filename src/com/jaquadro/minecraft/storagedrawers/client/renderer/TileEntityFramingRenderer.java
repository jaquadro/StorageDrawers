package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/*
@SideOnly(Side.CLIENT)
public class TileEntityFramingRenderer extends TileEntitySpecialRenderer
{
    private RenderItem itemRenderer = new RenderItem() {
        @Override
        public byte getMiniBlockCount (ItemStack stack, byte original) {
            return 1;
        }

        @Override
        public boolean shouldBob () {
            return false;
        }

        @Override
        public boolean shouldSpreadItems () {
            return false;
        }
    };

    @Override
    public void renderTileEntityAt (TileEntity tile, double x, double y, double z, float partialTickTime) {
        TileEntityFramingTable tileTable = (TileEntityFramingTable) tile;
        if (tileTable == null)
            return;

        int meta = tile.getBlockMetadata();
        if ((meta & 8) != 0)
            return;

        itemRenderer.setRenderManager(RenderManager.instance);

        ItemStack target = tileTable.getStackInSlot(0);
        if (target != null) {
            Block block = Block.getBlockFromItem(target.getItem());
            if (block instanceof BlockDrawersCustom) {
                ItemStack result = ItemCustomDrawers.makeItemStack(block, 1, tileTable.getStackInSlot(1), tileTable.getStackInSlot(2), tileTable.getStackInSlot(3));
                renderSlot(tileTable, x, y, z, result, 2f, 0f, .25f, 0f);
            }
        }

        renderSlot(tileTable, x, y, z, tileTable.getStackInSlot(1), 1.15f, -.225f, .15f, .65f);
        renderSlot(tileTable, x, y, z, tileTable.getStackInSlot(2), 1.15f, -.225f, .15f, -.65f);
        renderSlot(tileTable, x, y, z, tileTable.getStackInSlot(3), 1.15f, .225f, .15f, .65f);
    }

    private void renderSlot (TileEntityFramingTable tileTable, double x, double y, double z, ItemStack item, float scale, float tx, float ty, float tz) {
        if (item == null)
            return;

        Block itemBlock = Block.getBlockFromItem(item.getItem());
        if (itemBlock == null)
            return;

        int meta = tileTable.getBlockMetadata();
        int side = meta & 0x07;

        itemBlock.setBlockBoundsBasedOnState(tileTable.getWorldObj(), 0, 0, 0);
        itemBlock.setBlockBoundsForItemRender();

        GL11.glPushMatrix();

        GL11.glTranslated(x + .5, y + 1, z + .5);

        if (side == 2)
            GL11.glRotatef(90, 0, 1, 0);
        if (side == 3)
            GL11.glRotatef(270, 0, 1, 0);
        if (side == 4)
            GL11.glRotatef(180, 0, 1, 0);

        GL11.glTranslatef(0, 0f, .5f);
        GL11.glTranslatef(tx, ty, tz);
        GL11.glScalef(scale, scale, scale);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        try {
            EntityItem itemEnt = new EntityItem(null, 0, 0, 0, item);
            itemEnt.hoverStart = 0;
            itemRenderer.doRender(itemEnt, 0, 0, 0, 0, 0);
        }
        catch (Exception e) { }

        GL11.glPopMatrix();
    }
}*/
