package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebugRenderer
{
    public final DebugRenderer.IDebugRenderer debugRendererPathfinding;
    public final DebugRenderer.IDebugRenderer debugRendererWater;
    public final DebugRenderer.IDebugRenderer debugRendererChunkBorder;
    public final DebugRenderer.IDebugRenderer debugRendererHeightMap;
    private boolean chunkBordersEnabled;
    private boolean pathfindingEnabled;
    private boolean waterEnabled;
    private boolean heightmapEnabled;

    public DebugRenderer(Minecraft clientIn)
    {
        this.debugRendererPathfinding = new DebugRendererPathfinding(clientIn);
        this.debugRendererWater = new DebugRendererWater(clientIn);
        this.debugRendererChunkBorder = new DebugRendererChunkBorder(clientIn);
        this.debugRendererHeightMap = new DebugRendererHeightMap(clientIn);
    }

    public boolean shouldRender()
    {
        return this.chunkBordersEnabled || this.pathfindingEnabled || this.waterEnabled;
    }

    /**
     * Toggles the debug screen's visibility.
     */
    public boolean toggleDebugScreen()
    {
        this.chunkBordersEnabled = !this.chunkBordersEnabled;
        return this.chunkBordersEnabled;
    }

    public void renderDebug(float partialTicks, long finishTimeNano)
    {
        if (this.pathfindingEnabled)
        {
            this.debugRendererPathfinding.render(partialTicks, finishTimeNano);
        }

        if (this.chunkBordersEnabled && !Minecraft.getMinecraft().isReducedDebug())
        {
            this.debugRendererChunkBorder.render(partialTicks, finishTimeNano);
        }

        if (this.waterEnabled)
        {
            this.debugRendererWater.render(partialTicks, finishTimeNano);
        }

        if (this.heightmapEnabled)
        {
            this.debugRendererHeightMap.render(partialTicks, finishTimeNano);
        }
    }

    public static void renderDebugText(String str, double x, double y, double z, float partialTicks, int color)
    {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.player != null && minecraft.getRenderManager() != null && minecraft.getRenderManager().options != null)
        {
            FontRenderer fontrenderer = minecraft.fontRendererObj;
            EntityPlayer entityplayer = minecraft.player;
            double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
            double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
            double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(x - d0), (float)(y - d1) + 0.07F, (float)(z - d2));
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.02F, -0.02F, 0.02F);
            RenderManager rendermanager = minecraft.getRenderManager();
            GlStateManager.rotate(-rendermanager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float)(rendermanager.options.thirdPersonView == 2 ? 1 : -1) * rendermanager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.disableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.scale(-1.0F, 1.0F, 1.0F);
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, 0, color);
            GlStateManager.enableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    public interface IDebugRenderer
    {
        void render(float p_190060_1_, long p_190060_2_);
    }
}