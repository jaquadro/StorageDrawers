package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec4b;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MapItemRenderer
{
    private static final ResourceLocation TEXTURE_MAP_ICONS = new ResourceLocation("textures/map/map_icons.png");
    private final TextureManager textureManager;
    private final Map<String, MapItemRenderer.Instance> loadedMaps = Maps.<String, MapItemRenderer.Instance>newHashMap();

    public MapItemRenderer(TextureManager textureManagerIn)
    {
        this.textureManager = textureManagerIn;
    }

    /**
     * Updates a map texture
     */
    public void updateMapTexture(MapData mapdataIn)
    {
        this.getMapRendererInstance(mapdataIn).updateMapTexture();
    }

    public void renderMap(MapData mapdataIn, boolean p_148250_2_)
    {
        this.getMapRendererInstance(mapdataIn).render(p_148250_2_);
    }

    /**
     * Returns {@link net.minecraft.client.gui.MapItemRenderer.Instance MapItemRenderer.Instance} with given map data
     */
    private MapItemRenderer.Instance getMapRendererInstance(MapData mapdataIn)
    {
        MapItemRenderer.Instance mapitemrenderer$instance = (MapItemRenderer.Instance)this.loadedMaps.get(mapdataIn.mapName);

        if (mapitemrenderer$instance == null)
        {
            mapitemrenderer$instance = new MapItemRenderer.Instance(mapdataIn);
            this.loadedMaps.put(mapdataIn.mapName, mapitemrenderer$instance);
        }

        return mapitemrenderer$instance;
    }

    /**
     * Clears the currently loaded maps and removes their corresponding textures
     */
    public void clearLoadedMaps()
    {
        for (MapItemRenderer.Instance mapitemrenderer$instance : this.loadedMaps.values())
        {
            this.textureManager.deleteTexture(mapitemrenderer$instance.location);
        }

        this.loadedMaps.clear();
    }

    @SideOnly(Side.CLIENT)
    class Instance
    {
        private final MapData mapData;
        private final DynamicTexture mapTexture;
        private final ResourceLocation location;
        private final int[] mapTextureData;

        private Instance(MapData mapdataIn)
        {
            this.mapData = mapdataIn;
            this.mapTexture = new DynamicTexture(128, 128);
            this.mapTextureData = this.mapTexture.getTextureData();
            this.location = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapdataIn.mapName, this.mapTexture);

            for (int i = 0; i < this.mapTextureData.length; ++i)
            {
                this.mapTextureData[i] = 0;
            }
        }

        /**
         * Updates a map {@link net.minecraft.client.gui.MapItemRenderer.Instance#mapTexture texture}
         */
        private void updateMapTexture()
        {
            for (int i = 0; i < 16384; ++i)
            {
                int j = this.mapData.colors[i] & 255;

                if (j / 4 == 0)
                {
                    this.mapTextureData[i] = (i + i / 128 & 1) * 8 + 16 << 24;
                }
                else
                {
                    this.mapTextureData[i] = MapColor.COLORS[j / 4].getMapColor(j & 3);
                }
            }

            this.mapTexture.updateDynamicTexture();
        }

        /**
         * Renders map and players to it
         */
        private void render(boolean noOverlayRendering)
        {
            int i = 0;
            int j = 0;
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            float f = 0.0F;
            MapItemRenderer.this.textureManager.bindTexture(this.location);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlpha();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(0.0D, 128.0D, -0.009999999776482582D).tex(0.0D, 1.0D).endVertex();
            vertexbuffer.pos(128.0D, 128.0D, -0.009999999776482582D).tex(1.0D, 1.0D).endVertex();
            vertexbuffer.pos(128.0D, 0.0D, -0.009999999776482582D).tex(1.0D, 0.0D).endVertex();
            vertexbuffer.pos(0.0D, 0.0D, -0.009999999776482582D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.TEXTURE_MAP_ICONS);
            int k = 0;

            for (Vec4b vec4b : this.mapData.mapDecorations.values())
            {
                if (!noOverlayRendering || vec4b.getType() == 1)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F + (float)vec4b.getX() / 2.0F + 64.0F, 0.0F + (float)vec4b.getY() / 2.0F + 64.0F, -0.02F);
                    GlStateManager.rotate((float)(vec4b.getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.scale(4.0F, 4.0F, 3.0F);
                    GlStateManager.translate(-0.125F, 0.125F, 0.0F);
                    byte b0 = vec4b.getType();
                    float f1 = (float)(b0 % 4 + 0) / 4.0F;
                    float f2 = (float)(b0 / 4 + 0) / 4.0F;
                    float f3 = (float)(b0 % 4 + 1) / 4.0F;
                    float f4 = (float)(b0 / 4 + 1) / 4.0F;
                    vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
                    float f5 = -0.001F;
                    vertexbuffer.pos(-1.0D, 1.0D, (double)((float)k * -0.001F)).tex((double)f1, (double)f2).endVertex();
                    vertexbuffer.pos(1.0D, 1.0D, (double)((float)k * -0.001F)).tex((double)f3, (double)f2).endVertex();
                    vertexbuffer.pos(1.0D, -1.0D, (double)((float)k * -0.001F)).tex((double)f3, (double)f4).endVertex();
                    vertexbuffer.pos(-1.0D, -1.0D, (double)((float)k * -0.001F)).tex((double)f1, (double)f4).endVertex();
                    tessellator.draw();
                    GlStateManager.popMatrix();
                    ++k;
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, -0.04F);
            GlStateManager.scale(1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}