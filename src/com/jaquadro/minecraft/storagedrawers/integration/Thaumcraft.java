package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaTooltipHandler;
import com.jaquadro.minecraft.storagedrawers.api.render.IRenderLabel;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.opengl.GL11;

public class Thaumcraft extends IntegrationModule
{
    private Item[] aspectItems;

    @Override
    public String getModID () {
        return "Thaumcraft";
    }

    @Override
    public void init () throws Throwable {
        /*MinecraftForge.EVENT_BUS.register(this);

        aspectItems = new Item[] {
            GameRegistry.findItem(getModID(), "ItemResource"),
            GameRegistry.findItem(getModID(), "ItemEssence"),
            GameRegistry.findItem(getModID(), "ItemWispEssence"),
            GameRegistry.findItem(getModID(), "ItemCrystalEssence"),
            GameRegistry.findItem(getModID(), "BlockJarFilledItem"),
            GameRegistry.findItem(getModID(), "ItemManaBean"),
        };

        StorageDrawersApi.instance().renderRegistry().registerPreLabelRenderHandler(new LabelRenderHandler());
        StorageDrawersApi.instance().wailaRegistry().registerTooltipHandler(new WailaTooltipHandler());*/
    }

    @Override
    public void postInit () {

    }

    /*@SubscribeEvent
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
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null)
            return;
        
        if (tag.hasKey("AspectFilter")) {
	        setDrawerAspectName(drawer, tag.getString("AspectFilter"));
	        return;
        }
        
        NBTTagList tagAspects = tag.getTagList("Aspects", Constants.NBT.TAG_COMPOUND);
        if (tagAspects == null || tagAspects.tagCount() == 0)
            return;

        NBTTagCompound tagAspect = tagAspects.getCompoundTagAt(0);
        if (tagAspect == null || !tagAspect.hasKey("key"))
            return;

        setDrawerAspectName(drawer, tagAspect.getString("key"));
    }
    
    private void setDrawerAspectName (IDrawer drawer, String aspectName) {
        AspectList allAspects = ThaumcraftApiHelper.getAllAspects(1);
        for (Aspect a : allAspects.aspects.keySet()) {
            if (a.getTag().equals(aspectName)) {
                drawer.setExtendedData("aspect", a);
                return;
            }
        }
    }

    private class WailaTooltipHandler implements IWailaTooltipHandler {

        @Override
        public String transformItemName (IDrawer drawer, String defaultName) {
            Object aspectObj = drawer.getExtendedData("aspect");
            if (!(aspectObj instanceof Aspect))
                return defaultName;

            Aspect aspect = (Aspect)aspectObj;
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

            if (!ThaumcraftApiHelper.hasDiscoveredAspect(player.getDisplayName(), aspect))
                return defaultName + " (???)";

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

            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            Vec3 blockPos = Vec3.createVectorHelper(tileEntity.xCoord + .5, tileEntity.yCoord + .5, tileEntity.zCoord + .5);
            double distance = blockPos.distanceTo(player.getPosition(partialTickTime));

            if (distance > 10)
                return;

            Aspect aspect = (Aspect)aspectObj;
            if (!ThaumcraftApiHelper.hasDiscoveredAspect(player.getDisplayName(), aspect))
                return;

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
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;
            GL11.glColor4f(r * brightness, g * brightness, b * brightness, alpha);

            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            ResourceLocation aspectResource = aspect.getImage();
            Minecraft.getMinecraft().renderEngine.bindTexture(aspectResource);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + h, 0, 0, 1);
            tessellator.addVertexWithUV(x + w, y + h, 0, 1, 1);
            tessellator.addVertexWithUV(x + w, y, 0, 1, 0);
            tessellator.addVertexWithUV(x, y, 0, 0, 0);
            tessellator.draw();

            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }*/
}
