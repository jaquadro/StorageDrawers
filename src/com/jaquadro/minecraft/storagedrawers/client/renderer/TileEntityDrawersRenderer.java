package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TileEntityDrawersRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private RenderItem itemRenderer = new RenderItem() {
        private RenderBlocks renderBlocksRi = new RenderBlocks();

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

        // The default Mojang code for item render does not handle glinted or multi-pass items gracefully in a non-UI
        // setting.  This modified implementation will render these items without unsightly Z-fighting.

        @Override
        public void renderItemIntoGUI (FontRenderer fontRenderer, TextureManager texManager, ItemStack itemStack, int x, int y, boolean renderEffect) {
            if (itemStack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType())) {
                renderItemIntoGUIBlock(fontRenderer, texManager, itemStack, x, y, renderEffect);
                return;
            }

            Item item = itemStack.getItem();
            int meta = itemStack.getItemDamage();

            ResourceLocation loc = itemStack.getItem().requiresMultipleRenderPasses()
                ? (item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture)
                : (texManager.getResourceLocation(itemStack.getItemSpriteNumber()));

            for (int i = 0; i < item.getRenderPasses(meta); ++i) {
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                texManager.bindTexture(loc);

                IIcon icon = itemStack.getItem().requiresMultipleRenderPasses()
                    ? item.getIcon(itemStack, i)
                    : itemStack.getIconIndex();

                if (icon == null)
                    continue;

                int color = itemStack.getItem().getColorFromItemStack(itemStack, i);
                float r = (float)(color >> 16 & 255) / 255.0F;
                float g = (float)(color >> 8 & 255) / 255.0F;
                float b = (float)(color & 255) / 255.0F;

                if (renderWithColor)
                    GL11.glColor4f(r * brightness, g * brightness, b * brightness, 1.0F);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                renderIcon(x, y, icon, 16, 16);

                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);

                if (renderEffect && itemStack.hasEffect(i))
                    renderEffect(texManager, x, y);
            }
        }

        @Override
        public void renderEffect (TextureManager manager, int x, int y) {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            manager.bindTexture(RES_ITEM_GLINT);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
            renderGlint(x, y, 16, 16);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        private void renderGlint (int x, int y, int w, int h)
        {
            for (int i = 0; i < 2; ++i)
            {
                OpenGlHelper.glBlendFunc(772, 1, 0, 0);
                float uScale = 0.00390625F;
                float vScale = 0.00390625F;
                float u = (Minecraft.getSystemTime() % (3000 + i * 1873)) / (3000.0F + i * 1873) * 256.0F;
                float v = 0.0F;

                float hScale = (i < 1) ? 4.0F : -1.0F;

                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.addVertexWithUV(x + 0, y + h, 0, (u + (float)h * hScale) * uScale, (v + (float)h) * vScale);
                tessellator.addVertexWithUV(x + w, y + h, 0, (u + (float)w + (float)h * hScale) * uScale, (v + (float)h) * vScale);
                tessellator.addVertexWithUV(x + w, y + 0, 0, (u + (float)w) * uScale, (v + 0.0F) * vScale);
                tessellator.addVertexWithUV(x + 0, y + 0, 0, (u + 0.0F) * uScale, (v + 0.0F) * vScale);
                tessellator.draw();
            }
        }

        private void renderItemIntoGUIBlock (FontRenderer fontRenderer, TextureManager texManager, ItemStack itemStack, int x, int y, boolean renderEffect) {
            texManager.bindTexture(TextureMap.locationBlocksTexture);
            Block block = Block.getBlockFromItem(itemStack.getItem());
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            if (block.getRenderBlockPass() != 0) {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }
            else {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
                GL11.glDisable(GL11.GL_BLEND);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(x - 2, y + 3, zLevel - 3);
            GL11.glScalef(10, 10, 10);
            GL11.glTranslatef(1, 0.5f, 1);
            GL11.glScalef(1, 1, -1);
            GL11.glRotatef(210, 1, 0, 0);
            GL11.glRotatef(45, 0, 1, 0);

            int color = itemStack.getItem().getColorFromItemStack(itemStack, 0);
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;

            if (this.renderWithColor)
                GL11.glColor4f(r * brightness, g * brightness, b * brightness, 1.0F);

            GL11.glDisable(GL11.GL_LIGHTING);

            GL11.glRotatef(-90, 0, 1, 0);
            this.renderBlocksRi.useInventoryTint = this.renderWithColor;
            this.renderBlocksRi.renderBlockAsItem(block, itemStack.getItemDamage(), brightness);
            this.renderBlocksRi.useInventoryTint = true;

            GL11.glEnable(GL11.GL_LIGHTING);

            if (block.getRenderBlockPass() == 0)
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

            GL11.glPopMatrix();
        }
    };

    private float itemOffset2X[] = new float[] { .5f, .5f };
    private float itemOffset2Y[] = new float[] { 10.25f, 2.25f };

    private float itemOffset4X[] = new float[] { .25f, .25f, .75f, .75f };
    private float itemOffset4Y[] = new float[] { 10.25f, 2.25f, 10.25f, 2.25f };

    private float itemOffset3X[] = new float[] { .5f, .25f, .75f };
    private float itemOffset3Y[] = new float[] { 9.75f, 2.25f, 2.25f };

    private RenderBlocks renderBlocks = new RenderBlocks();

    private float brightness;

    private static int[] glStateRender = { GL11.GL_LIGHTING, GL11.GL_BLEND };
    private List<int[]> savedGLStateRender = GLUtil.makeGLState(glStateRender);

    private static int[] glStateItemRender = { GL11.GL_LIGHTING, GL11.GL_ALPHA_TEST, GL11.GL_BLEND };
    private List<int[]> savedGLStateItemRender = GLUtil.makeGLState(glStateItemRender);

    @Override
    public void renderTileEntityAt (TileEntity tile, double x, double y, double z, float partialTickTime) {
        TileEntityDrawersBase tileDrawers = (TileEntityDrawersBase) tile;
        if (tileDrawers == null)
            return;

        GLUtil.saveGLState(savedGLStateRender, glStateRender);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        int drawerCount = tileDrawers.getDrawerCount();
        float depth = 1;
        float unit = .0625f;

        Block block = tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord, tile.zCoord);
        if (block instanceof BlockDrawers)
            depth = ((BlockDrawers) block).halfDepth ? .5f : 1;
        else
            return;

        itemRenderer.setRenderManager(RenderManager.instance);

        ForgeDirection side = ForgeDirection.getOrientation(tileDrawers.getDirection());
        int ambLight = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord + side.offsetX, tile.yCoord + side.offsetY, tile.zCoord + side.offsetZ, 0);
        int lu = ambLight % 65536;
        int lv = ambLight / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lu, lv);

        brightness = tile.getWorldObj().getLightBrightness(tile.xCoord + side.offsetX, tile.yCoord + side.offsetY, tile.zCoord + side.offsetZ) * 1.25f;
        if (brightness > 1)
            brightness = 1;

        for (int i = 0; i < drawerCount; i++) {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);

            ItemStack itemStack = tileDrawers.getSingleItemStack(i);
            if (itemStack != null) {
                GL11.glPushMatrix();

                boolean blockType = itemStack.getItemSpriteNumber() == 0
                    && itemStack.getItem() instanceof ItemBlock
                    && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType());

                float xunit, yunit;
                if (drawerCount == 2) {
                    xunit = itemOffset2X[i];
                    yunit = itemOffset2Y[i];
                }
                else if (drawerCount == 3) {
                    xunit = itemOffset3X[i];
                    yunit = itemOffset3Y[i];
                }
                else {
                    xunit = itemOffset4X[i];
                    yunit = itemOffset4Y[i];
                }

                float zunit = blockType ? 1.95f * unit : unit;

                float xc = 0, zc = 0;
                float itemDepth = depth + .001f;

                if (blockType) {
                    Block itemBlock = Block.getBlockFromItem(itemStack.getItem());
                    itemBlock.setBlockBoundsForItemRender();

                    double zDepth = 1 - itemBlock.getBlockBoundsMaxZ();
                    itemDepth += zDepth * zunit;
                }

                switch (tileDrawers.getDirection()) {
                    case 3:
                        xc = xunit;
                        zc = itemDepth - zunit;
                        break;
                    case 2:
                        xc = 1 - xunit;
                        zc = 1 - itemDepth + zunit;
                        break;
                    case 5:
                        xc = itemDepth - zunit;
                        zc = 1 - xunit;
                        break;
                    case 4:
                        xc = 1 - itemDepth + zunit;
                        zc = xunit;
                        break;
                }

                Minecraft mc = Minecraft.getMinecraft();
                boolean cache = mc.gameSettings.fancyGraphics;
                mc.gameSettings.fancyGraphics = true;

                if (StorageDrawers.config.isFancyItemRenderEnabled()) {
                    float yAdj = 0;
                    if (drawerCount == 2 || drawerCount == 4)
                        yAdj = -.5f;

                    if (blockType) {
                        GL11.glTranslatef(xc, unit * (yunit + 1.75f + yAdj), zc);
                        GL11.glScalef(1, 1, 1);
                        GL11.glRotatef(getRotationYForSide(side) + 90.0F, 0.0F, 1.0F, 0.0F);
                    } else {
                        GL11.glTranslatef(xc, unit * (yunit + 0.75f + yAdj), zc);
                        GL11.glScalef(.5f, .5f, .5f);
                        GL11.glRotatef(getRotationYForSide(side), 0.0F, 1.0F, 0.0F);
                    }

                    EntityItem itemEnt = new EntityItem(null, 0, 0, 0, itemStack);
                    itemEnt.hoverStart = 0;
                    itemRenderer.doRender(itemEnt, 0, 0, 0, 0, 0);
                }
                else {
                    alignRendering(side);
                    moveRendering(.25f, getOffsetXForSide(side, xunit) * 16 - 2, 12.25f - yunit, .999f - depth + unit);

                    GLUtil.saveGLState(savedGLStateItemRender, glStateItemRender);

                    if (!ForgeHooksClient.renderInventoryItem(this.renderBlocks, mc.renderEngine, itemStack, true, 0, 0, 0))
                        itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, itemStack, 0, 0, true);

                    GLUtil.restoreGLState(savedGLStateItemRender);
                }

                mc.gameSettings.fancyGraphics = cache;

                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();

        GLUtil.restoreGLState(savedGLStateRender);
    }

    private void alignRendering (ForgeDirection side) {
        GL11.glTranslatef(.5f, .5f, .5f);
        GL11.glRotatef(180f, 0, 0, 1f);     // Render is upside-down: correct it.
        GL11.glRotatef(getRotationYForSide2D(side), 0, 1, 0);
        GL11.glTranslatef(-.5f, -.5f, -.5f);
    }

    private void moveRendering (float size, float offsetX, float offsetY, float offsetZ) {
        GL11.glTranslatef(0, 0, offsetZ);
        GL11.glScalef(1 / 16f, 1 / 16f, -.0001f);
        GL11.glTranslatef(offsetX, offsetY, 0);
        GL11.glScalef(size, size, 1);
    }

    private static final float[] sideRotationY = { 0, 0, 0, 2, 1, 3 };

    private float getRotationYForSide (ForgeDirection side) {
        return sideRotationY[side.ordinal()] * 90;
    }

    private static final float[] sideRotationY2D = { 0, 0, 0, 2, 3, 1 };

    private float getRotationYForSide2D (ForgeDirection side) {
        return sideRotationY2D[side.ordinal()] * 90;
    }

    private static final float[] offsetX = { 0, 0, 0, 0, 0, 0 };

    private float getOffsetXForSide (ForgeDirection side, float x) {
        return Math.abs(offsetX[side.ordinal()] - x);
    }
}
