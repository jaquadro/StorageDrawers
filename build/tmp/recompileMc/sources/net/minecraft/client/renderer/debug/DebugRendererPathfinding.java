package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebugRendererPathfinding implements DebugRenderer.IDebugRenderer
{
    private final Minecraft minecraft;
    private final Map<Integer, Path> pathMap = Maps.<Integer, Path>newHashMap();
    private final Map<Integer, Float> pathMaxDistance = Maps.<Integer, Float>newHashMap();
    private final Map<Integer, Long> creationMap = Maps.<Integer, Long>newHashMap();
    private EntityPlayer player;
    private double xo;
    private double yo;
    private double zo;

    public DebugRendererPathfinding(Minecraft minecraftIn)
    {
        this.minecraft = minecraftIn;
    }

    public void addPath(int p_188289_1_, Path p_188289_2_, float p_188289_3_)
    {
        this.pathMap.put(Integer.valueOf(p_188289_1_), p_188289_2_);
        this.creationMap.put(Integer.valueOf(p_188289_1_), Long.valueOf(System.currentTimeMillis()));
        this.pathMaxDistance.put(Integer.valueOf(p_188289_1_), Float.valueOf(p_188289_3_));
    }

    public void render(float p_190060_1_, long p_190060_2_)
    {
        if (this.pathMap.size() != 0)
        {
            long i = System.currentTimeMillis();
            this.player = this.minecraft.player;
            this.xo = this.player.lastTickPosX + (this.player.posX - this.player.lastTickPosX) * (double)p_190060_1_;
            this.yo = this.player.lastTickPosY + (this.player.posY - this.player.lastTickPosY) * (double)p_190060_1_;
            this.zo = this.player.lastTickPosZ + (this.player.posZ - this.player.lastTickPosZ) * (double)p_190060_1_;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color(0.0F, 1.0F, 0.0F, 0.75F);
            GlStateManager.disableTexture2D();
            GlStateManager.glLineWidth(6.0F);

            for (Integer integer : this.pathMap.keySet())
            {
                Path path = (Path)this.pathMap.get(integer);
                float f = ((Float)this.pathMaxDistance.get(integer)).floatValue();
                this.renderPathLine(p_190060_1_, path);
                PathPoint pathpoint = path.getTarget();

                if (this.addDistanceToPlayer(pathpoint) <= 40.0F)
                {
                    RenderGlobal.renderFilledBox((new AxisAlignedBB((double)((float)pathpoint.xCoord + 0.25F), (double)((float)pathpoint.yCoord + 0.25F), (double)pathpoint.zCoord + 0.25D, (double)((float)pathpoint.xCoord + 0.75F), (double)((float)pathpoint.yCoord + 0.75F), (double)((float)pathpoint.zCoord + 0.75F))).offset(-this.xo, -this.yo, -this.zo), 0.0F, 1.0F, 0.0F, 0.5F);

                    for (int j = 0; j < path.getCurrentPathLength(); ++j)
                    {
                        PathPoint pathpoint1 = path.getPathPointFromIndex(j);

                        if (this.addDistanceToPlayer(pathpoint1) <= 40.0F)
                        {
                            float f1 = j == path.getCurrentPathIndex() ? 1.0F : 0.0F;
                            float f2 = j == path.getCurrentPathIndex() ? 0.0F : 1.0F;
                            RenderGlobal.renderFilledBox((new AxisAlignedBB((double)((float)pathpoint1.xCoord + 0.5F - f), (double)((float)pathpoint1.yCoord + 0.01F * (float)j), (double)((float)pathpoint1.zCoord + 0.5F - f), (double)((float)pathpoint1.xCoord + 0.5F + f), (double)((float)pathpoint1.yCoord + 0.25F + 0.01F * (float)j), (double)((float)pathpoint1.zCoord + 0.5F + f))).offset(-this.xo, -this.yo, -this.zo), f1, 0.0F, f2, 0.5F);
                        }
                    }
                }
            }

            for (Integer integer1 : this.pathMap.keySet())
            {
                Path path1 = (Path)this.pathMap.get(integer1);

                for (PathPoint pathpoint3 : path1.getClosedSet())
                {
                    if (this.addDistanceToPlayer(pathpoint3) <= 40.0F)
                    {
                        DebugRenderer.renderDebugText(String.format("%s", new Object[] {pathpoint3.nodeType}), (double)pathpoint3.xCoord + 0.5D, (double)pathpoint3.yCoord + 0.75D, (double)pathpoint3.zCoord + 0.5D, p_190060_1_, -65536);
                        DebugRenderer.renderDebugText(String.format("%.2f", new Object[] {Float.valueOf(pathpoint3.costMalus)}), (double)pathpoint3.xCoord + 0.5D, (double)pathpoint3.yCoord + 0.25D, (double)pathpoint3.zCoord + 0.5D, p_190060_1_, -65536);
                    }
                }

                for (PathPoint pathpoint4 : path1.getOpenSet())
                {
                    if (this.addDistanceToPlayer(pathpoint4) <= 40.0F)
                    {
                        DebugRenderer.renderDebugText(String.format("%s", new Object[] {pathpoint4.nodeType}), (double)pathpoint4.xCoord + 0.5D, (double)pathpoint4.yCoord + 0.75D, (double)pathpoint4.zCoord + 0.5D, p_190060_1_, -16776961);
                        DebugRenderer.renderDebugText(String.format("%.2f", new Object[] {Float.valueOf(pathpoint4.costMalus)}), (double)pathpoint4.xCoord + 0.5D, (double)pathpoint4.yCoord + 0.25D, (double)pathpoint4.zCoord + 0.5D, p_190060_1_, -16776961);
                    }
                }

                for (int k = 0; k < path1.getCurrentPathLength(); ++k)
                {
                    PathPoint pathpoint2 = path1.getPathPointFromIndex(k);

                    if (this.addDistanceToPlayer(pathpoint2) <= 40.0F)
                    {
                        DebugRenderer.renderDebugText(String.format("%s", new Object[] {pathpoint2.nodeType}), (double)pathpoint2.xCoord + 0.5D, (double)pathpoint2.yCoord + 0.75D, (double)pathpoint2.zCoord + 0.5D, p_190060_1_, -1);
                        DebugRenderer.renderDebugText(String.format("%.2f", new Object[] {Float.valueOf(pathpoint2.costMalus)}), (double)pathpoint2.xCoord + 0.5D, (double)pathpoint2.yCoord + 0.25D, (double)pathpoint2.zCoord + 0.5D, p_190060_1_, -1);
                    }
                }
            }

            for (Integer integer2 : (Integer[])this.creationMap.keySet().toArray(new Integer[0]))
            {
                if (i - ((Long)this.creationMap.get(integer2)).longValue() > 20000L)
                {
                    this.pathMap.remove(integer2);
                    this.creationMap.remove(integer2);
                }
            }

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public void renderPathLine(float p_190067_1_, Path p_190067_2_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < p_190067_2_.getCurrentPathLength(); ++i)
        {
            PathPoint pathpoint = p_190067_2_.getPathPointFromIndex(i);

            if (this.addDistanceToPlayer(pathpoint) <= 40.0F)
            {
                float f = (float)i / (float)p_190067_2_.getCurrentPathLength() * 0.33F;
                int j = i == 0 ? 0 : MathHelper.hsvToRGB(f, 0.9F, 0.9F);
                int k = j >> 16 & 255;
                int l = j >> 8 & 255;
                int i1 = j & 255;
                vertexbuffer.pos((double)pathpoint.xCoord - this.xo + 0.5D, (double)pathpoint.yCoord - this.yo + 0.5D, (double)pathpoint.zCoord - this.zo + 0.5D).color(k, l, i1, 255).endVertex();
            }
        }

        tessellator.draw();
    }

    private float addDistanceToPlayer(PathPoint p_190066_1_)
    {
        return (float)(Math.abs((double)p_190066_1_.xCoord - this.player.posX) + Math.abs((double)p_190066_1_.yCoord - this.player.posY) + Math.abs((double)p_190066_1_.zCoord - this.player.posZ));
    }
}