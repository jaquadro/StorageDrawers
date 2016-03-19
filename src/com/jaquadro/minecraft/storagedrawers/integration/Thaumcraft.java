/*package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.chameleon.integration.IntegrationModule;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaTooltipHandler;
import com.jaquadro.minecraft.storagedrawers.api.render.IRenderLabel;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

public class Thaumcraft extends IntegrationModule
{
    private Item[] aspectItems;

    @Override
    public String getModID () {
        return "Thaumcraft";
    }

    @Override
    public void init () throws Throwable {
        MinecraftForge.EVENT_BUS.register(this);

        aspectItems = new Item[] {
            GameRegistry.findItem(getModID(), "phial"),
            GameRegistry.findItem(getModID(), "wispy_essence"),
            GameRegistry.findItem(getModID(), "crystal_essence"),
            GameRegistry.findItem(getModID(), "jar"),
        };

        StorageDrawersApi.instance().renderRegistry().registerPreLabelRenderHandler(new LabelRenderHandler());
        StorageDrawersApi.instance().wailaRegistry().registerTooltipHandler(new WailaTooltipHandler());
    }

    @Override
    public void postInit () {

    }

    @SubscribeEvent
    public void onDrawerPopulated (DrawerPopulatedEvent event) {
        IDrawer drawer = event.drawer;
        if (drawer.isEmpty()) {
            drawer.setExtendedData("aspect", null);
            return;
        }

        ItemStack protoStack = drawer.getStoredItemPrototype();
        for (Item item : aspectItems) {
            if (item == protoStack.getItem()) {
                setDrawerAspect(drawer, protoStack);
                return;
            }
        }
    }

    private void setDrawerAspect (IDrawer drawer, ItemStack itemStack) {
        AspectList aspects = AspectHelper.getObjectAspects(itemStack);
        if (aspects == null || aspects.size() == 0)
            return;

        drawer.setExtendedData("aspect", aspects.getAspects()[0]);
    }

    private class WailaTooltipHandler implements IWailaTooltipHandler {

        @Override
        public String transformItemName (IDrawer drawer, String defaultName) {
            Object aspectObj = drawer.getExtendedData("aspect");
            if (!(aspectObj instanceof Aspect))
                return defaultName;

            Aspect aspect = (Aspect)aspectObj;
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

            //if (!ThaumcraftApiHelper.hasDiscoveredAspect(player.getDisplayName(), aspect))
            //    return defaultName + " (???)";

            return defaultName + " (" + aspect.getName() + ")";
        }
    }

    private class LabelRenderHandler implements IRenderLabel {

        @Override
        public void render (TileEntity tileEntity, IDrawerGroup drawerGroup, int slot, float brightness, float partialTickTime) {
            IDrawer drawer = drawerGroup.getDrawer(slot);
            if (drawer == null)
                return;

            Object aspectObj = drawer.getExtendedData("aspect");
            if (!(aspectObj instanceof Aspect))
                return;

            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            BlockPos blockPos = tileEntity.getPos().add(.5, .5, .5);
            double distance = Math.sqrt(blockPos.distanceSq(player.getPosition()));

            if (distance > 10)
                return;

            Aspect aspect = (Aspect)aspectObj;
            //if (!ThaumcraftApiHelper.hasDiscoveredAspect(player.getDisplayName(), aspect))
            //    return;

            int x = -4;
            int y = -4;
            int w = 8;
            int h = 8;

            if (drawerGroup.getDrawerCount() == 2) {
                x = -16;
                y = 0;
                w = 16;
                h = 16;
            }

            float alpha = 1;
            if (distance > 3)
                alpha = 1f - (float)((distance - 3) / 7);

            int color = aspect.getColor();
            float r = (color >> 16 & 255);
            float g = (color >> 8 & 255);
            float b = (color & 255);

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();

            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(-1, -1);

            ResourceLocation aspectResource = aspect.getImage();
            Minecraft.getMinecraft().renderEngine.bindTexture(aspectResource);

            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer worldRenderer = tessellator.getBuffer();
            renderQuad(worldRenderer, x, y, w, h, (int)(r), (int)(g), (int)(b), (int)(alpha * 255));

            GlStateManager.disablePolygonOffset();

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        }
    }

    private void renderQuad (VertexBuffer tessellator, int x, int y, int w, int h, int r, int g, int b, int a)
    {
        tessellator.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        tessellator.pos(x + 0, y + 0, 0).tex(0, 0).color(r, g, b, a).endVertex();
        tessellator.pos(x + 0, y + h, 0).tex(0, 1).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + h, 0).tex(1, 1).color(r, g, b, a).endVertex();
        tessellator.pos(x + w, y + 0, 0).tex(1, 0).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();
    }
}
*/