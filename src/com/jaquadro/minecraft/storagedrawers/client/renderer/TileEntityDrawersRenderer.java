package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
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

@SideOnly(Side.CLIENT)
public class TileEntityDrawersRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

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

        // The default Mojang code for item render does not handle glinted or multi-pass items gracefully in a non-UI
        // setting.  This modified implementation will render these items without unsightly Z-fighting.

        @Override
        public void renderItemIntoGUI (FontRenderer fontRenderer, TextureManager texManager, ItemStack itemStack, int x, int y, boolean renderEffect) {
            if (itemStack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType()))
                super.renderItemIntoGUI(fontRenderer, texManager, itemStack, x, y, renderEffect);
            else if (itemStack.getItem().requiresMultipleRenderPasses())
                renderItemIntoGUIMultiPass(texManager, itemStack, x, y, renderEffect);
            else
                super.renderItemIntoGUI(fontRenderer, texManager, itemStack, x, y, renderEffect);
        }

        private void renderItemIntoGUIMultiPass (TextureManager texManager, ItemStack itemStack, int x, int y, boolean renderEffect) {
            Item item = itemStack.getItem();
            int meta = itemStack.getItemDamage();

            for (int i = 0; i < item.getRenderPasses(meta); ++i) {
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                texManager.bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
                IIcon icon = item.getIcon(itemStack, i);

                int color = itemStack.getItem().getColorFromItemStack(itemStack, i);
                float r = (float)(color >> 16 & 255) / 255.0F;
                float g = (float)(color >> 8 & 255) / 255.0F;
                float b = (float)(color & 255) / 255.0F;

                if (renderWithColor)
                    GL11.glColor4f(r, g, b, 1.0F);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                renderIcon(x, y, icon, 16, 16);

                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_LIGHTING);

                if (renderEffect && itemStack.hasEffect(i))
                    renderEffect(texManager, x, y);
            }

            GL11.glEnable(GL11.GL_LIGHTING);
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
    };

    private float itemOffset2X[] = new float[] { .5f, .5f };
    private float itemOffset2Y[] = new float[] { 10.25f, 2.25f };

    private float itemOffset4X[] = new float[] { .25f, .25f, .75f, .75f };
    private float itemOffset4Y[] = new float[] { 10.25f, 2.25f, 10.25f, 2.25f };

    private RenderBlocks renderBlocks = new RenderBlocks();

    @Override
    public void renderTileEntityAt (TileEntity tile, double x, double y, double z, float partialTickTime) {
        TileEntityDrawers tileDrawers = (TileEntityDrawers) tile;
        if (tileDrawers == null)
            return;

        saveGLState();

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

        for (int i = 0; i < drawerCount; i++) {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);

            ItemStack itemStack = tileDrawers.getSingleItemStack(i);
            if (itemStack != null) {
                GL11.glPushMatrix();

                boolean blockType = itemStack.getItemSpriteNumber() == 0
                    && itemStack.getItem() instanceof ItemBlock
                    && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType());


                float xunit = (drawerCount == 2) ? itemOffset2X[i] : itemOffset4X[i];
                float yunit = (drawerCount == 2) ? itemOffset2Y[i] : itemOffset4Y[i];
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
                        zc = xunit;
                        break;
                    case 4:
                        xc = 1 - itemDepth + zunit;
                        zc = 1 - xunit;
                        break;
                }

                Minecraft mc = Minecraft.getMinecraft();
                boolean cache = mc.gameSettings.fancyGraphics;
                mc.gameSettings.fancyGraphics = true;

                if (StorageDrawers.config.isFancyItemRenderEnabled()) {
                    if (blockType) {
                        GL11.glTranslatef(xc, unit * (yunit + 1.25f), zc);
                        GL11.glScalef(1, 1, 1);
                        GL11.glRotatef(getRotationYForSide(side) - 90.0F, 0.0F, 1.0F, 0.0F);
                    } else {
                        GL11.glTranslatef(xc, unit * yunit, zc);
                        GL11.glScalef(.6f, .6f, .6f);
                        GL11.glRotatef(getRotationYForSide(side), 0.0F, 1.0F, 0.0F);
                    }

                    EntityItem itemEnt = new EntityItem(null, 0, 0, 0, itemStack);
                    itemEnt.hoverStart = 0;
                    itemRenderer.doRender(itemEnt, 0, 0, 0, 0, 0);
                }
                else {
                    alignRendering(side);
                    moveRendering(.25f, getOffsetXForSide(side, xunit) * 16 - 2, 12.5f - yunit, .999f - depth + unit);

                    if (!ForgeHooksClient.renderInventoryItem(this.renderBlocks, mc.renderEngine, itemStack, true, 0, 0, 0))
                        itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, itemStack, 0, 0, true);
                }

                mc.gameSettings.fancyGraphics = cache;

                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();

        loadGLState();
    }

    private void alignRendering (ForgeDirection side) {
        GL11.glTranslatef(.5f, .5f, .5f);
        GL11.glRotatef(180f, 0, 0, 1f);     // Render is upside-down: correct it.
        GL11.glRotatef(getRotationYForSide(side), 0, 1, 0);
        GL11.glTranslatef(-.5f, -.5f, -.5f);
    }

    private void moveRendering (float size, float offsetX, float offsetY, float offsetZ) {
        GL11.glTranslatef(0, 0, offsetZ);
        GL11.glScalef(1 / 16f, 1 / 16f, -.0001f);
        GL11.glTranslatef(offsetX, offsetY, 0);
        GL11.glScalef(size, size, 1);
    }

    private static final float[] sideRotationY = { 0, 0, 0, 2, 3, 1 };

    private float getRotationYForSide (ForgeDirection side) {
        return sideRotationY[side.ordinal()] * 90;
    }

    private static final float[] offsetX = { 0, 0, 0, 0, 1, 1 };

    private float getOffsetXForSide (ForgeDirection side, float x) {
        return Math.abs(offsetX[side.ordinal()] - x);
    }

    private boolean blendEnabled;
    private boolean lightEnabled;

    private void saveGLState () {
        blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);
        lightEnabled = GL11.glGetBoolean(GL11.GL_LIGHTING);
    }

    private void loadGLState () {
        if (blendEnabled)
            GL11.glEnable(GL11.GL_BLEND);
        else
            GL11.glDisable(GL11.GL_BLEND);

        if (lightEnabled)
            GL11.glEnable(GL11.GL_LIGHTING);
        else
            GL11.glDisable(GL11.GL_LIGHTING);
    }
}
