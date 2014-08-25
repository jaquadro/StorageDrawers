package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.RotationHelper;
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
        if (tileDrawers == null)
            return;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        GL11.glEnable(GL11.GL_LIGHTING);

        int drawerCount = tileDrawers.getDrawerCount();
        double depth = 1;
        double unit = .0625;

        Block block = tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord, tile.zCoord);
        if (block instanceof BlockDrawers)
            depth = ((BlockDrawers) block).halfDepth ? .5 : 1;
        else
            return;

        itemRenderer.setRenderManager(RenderManager.instance);

        for (int i = 0; i < drawerCount; i++) {
            ItemStack itemStack = tileDrawers.getSingleItemStack(i);
            if (itemStack != null) {
                GL11.glPushMatrix();

                boolean blockType = itemStack.getItemSpriteNumber() == 0
                    && itemStack.getItem() instanceof ItemBlock
                    && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType());


                double xunit = (drawerCount == 2) ? itemOffset2X[i] : itemOffset4X[i];
                double yunit = (drawerCount == 2) ? itemOffset2Y[i] : itemOffset4Y[i];
                double zunit = blockType ? 2 * unit : unit;

                double xc = 0, zc = 0;
                float r = 0;
                switch (tileDrawers.getDirection()) {
                    case 3:
                        xc = xunit;
                        zc = depth - zunit;
                        r = 180;
                        break;
                    case 2:
                        xc = 1 - xunit;
                        zc = 1 - depth + zunit;
                        break;
                    case 5:
                        xc = depth - zunit;
                        zc = xunit;
                        r = -90;
                        break;
                    case 4:
                        xc = 1 - depth + zunit;
                        zc = 1 - xunit;
                        r = 90;
                        break;
                }

                if (blockType) {
                    GL11.glTranslated(xc, unit * (yunit + 1.25), zc);
                    GL11.glScaled(1, 1, 1);
                    GL11.glRotatef(r - 90.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    GL11.glTranslated(xc, unit * yunit, zc);
                    GL11.glScaled(.6, .6, .6);
                    GL11.glRotatef(r, 0.0F, 1.0F, 0.0F);
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
