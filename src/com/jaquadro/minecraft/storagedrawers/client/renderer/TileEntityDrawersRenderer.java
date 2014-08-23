package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TileEntityDrawersRenderer extends TileEntitySpecialRenderer
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

    private double itemOffset2X[] = new double[] { .5, .5 };
    private double itemOffset2Y[] = new double[] { 10.25, 2.25 };

    private double itemOffset4X[] = new double[] { .25, .25, .75, .75 };
    private double itemOffset4Y[] = new double[] { 10.25, 2.25, 10.25, 2.25 };

    @Override
    public void renderTileEntityAt (TileEntity tile, double x, double y, double z, float partialTickTime) {
        TileEntityDrawers tileDrawers = (TileEntityDrawers) tile;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        GL11.glEnable(GL11.GL_LIGHTING);

        double unit = .0625;

        itemRenderer.setRenderManager(RenderManager.instance);

        for (int i = 0; i < 2; i++) {
            ItemStack itemStack = tileDrawers.getSingleItemStack(i);
            if (itemStack != null) {
                GL11.glPushMatrix();

                if (itemStack.getItemSpriteNumber() == 0 && itemStack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType())) {
                    GL11.glTranslated(itemOffset2X[i], unit * (itemOffset2Y[i] + 1.25), 1 - unit * 2);
                    GL11.glScaled(1, 1, 1);
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    GL11.glTranslated(itemOffset2X[i], unit * itemOffset2Y[i], 1 - unit);
                    GL11.glScaled(.6, .6, .6);
                }

                EntityItem itemEnt = new EntityItem(null, 0, 0, 0, itemStack);
                itemEnt.hoverStart = 0;
                itemRenderer.doRender(itemEnt, 0, 0, 0, 0, 0);

                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
    }
}
