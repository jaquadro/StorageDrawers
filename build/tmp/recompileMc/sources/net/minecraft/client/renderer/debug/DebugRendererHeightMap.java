package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebugRendererHeightMap implements DebugRenderer.IDebugRenderer
{
    private final Minecraft minecraft;

    public DebugRendererHeightMap(Minecraft minecraftIn)
    {
        this.minecraft = minecraftIn;
    }

    public void render(float p_190060_1_, long p_190060_2_)
    {
        EntityPlayer entityplayer = this.minecraft.player;
        World world = this.minecraft.world;
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)p_190060_1_;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)p_190060_1_;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)p_190060_1_;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        BlockPos blockpos = new BlockPos(entityplayer.posX, 0.0D, entityplayer.posZ);
        Iterable<BlockPos> iterable = BlockPos.MutableBlockPos.getAllInBox(blockpos.add(-40, 0, -40), blockpos.add(40, 0, 40));
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos blockpos1 : iterable)
        {
            int i = world.getHeight(blockpos1.getX(), blockpos1.getZ());

            if (world.getBlockState(blockpos1.add(0, i, 0).down()) == Blocks.AIR.getDefaultState())
            {
                RenderGlobal.addChainedFilledBoxVertices(vertexbuffer, (double)((float)blockpos1.getX() + 0.25F) - d0, (double)i - d1, (double)((float)blockpos1.getZ() + 0.25F) - d2, (double)((float)blockpos1.getX() + 0.75F) - d0, (double)i + 0.09375D - d1, (double)((float)blockpos1.getZ() + 0.75F) - d2, 0.0F, 0.0F, 1.0F, 0.5F);
            }
            else
            {
                RenderGlobal.addChainedFilledBoxVertices(vertexbuffer, (double)((float)blockpos1.getX() + 0.25F) - d0, (double)i - d1, (double)((float)blockpos1.getZ() + 0.25F) - d2, (double)((float)blockpos1.getX() + 0.75F) - d0, (double)i + 0.09375D - d1, (double)((float)blockpos1.getZ() + 0.75F) - d2, 0.0F, 1.0F, 0.0F, 0.5F);
            }
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}