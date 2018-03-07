package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGuardian extends RenderLiving<EntityGuardian>
{
    private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
    int lastModelVersion;

    public RenderGuardian(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelGuardian(), 0.5F);
        this.lastModelVersion = ((ModelGuardian)this.mainModel).getModelVersion();
    }

    public boolean shouldRender(EntityGuardian livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ))
        {
            return true;
        }
        else
        {
            if (livingEntity.hasTargetedEntity())
            {
                EntityLivingBase entitylivingbase = livingEntity.getTargetedEntity();

                if (entitylivingbase != null)
                {
                    Vec3d vec3d = this.getPosition(entitylivingbase, (double)entitylivingbase.height * 0.5D, 1.0F);
                    Vec3d vec3d1 = this.getPosition(livingEntity, (double)livingEntity.getEyeHeight(), 1.0F);

                    if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord, vec3d.xCoord, vec3d.yCoord, vec3d.zCoord)))
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private Vec3d getPosition(EntityLivingBase entityLivingBaseIn, double p_177110_2_, float p_177110_4_)
    {
        double d0 = entityLivingBaseIn.lastTickPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.lastTickPosX) * (double)p_177110_4_;
        double d1 = p_177110_2_ + entityLivingBaseIn.lastTickPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.lastTickPosY) * (double)p_177110_4_;
        double d2 = entityLivingBaseIn.lastTickPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.lastTickPosZ) * (double)p_177110_4_;
        return new Vec3d(d0, d1, d2);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (this.lastModelVersion != ((ModelGuardian)this.mainModel).getModelVersion())
        {
            this.mainModel = new ModelGuardian();
            this.lastModelVersion = ((ModelGuardian)this.mainModel).getModelVersion();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        EntityLivingBase entitylivingbase = entity.getTargetedEntity();

        if (entitylivingbase != null)
        {
            float f = entity.getAttackAnimationScale(partialTicks);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            this.bindTexture(GUARDIAN_BEAM_TEXTURE);
            GlStateManager.glTexParameteri(3553, 10242, 10497);
            GlStateManager.glTexParameteri(3553, 10243, 10497);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            float f1 = 240.0F;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            float f2 = (float)entity.world.getTotalWorldTime() + partialTicks;
            float f3 = f2 * 0.5F % 1.0F;
            float f4 = entity.getEyeHeight();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y + f4, (float)z);
            Vec3d vec3d = this.getPosition(entitylivingbase, (double)entitylivingbase.height * 0.5D, partialTicks);
            Vec3d vec3d1 = this.getPosition(entity, (double)f4, partialTicks);
            Vec3d vec3d2 = vec3d.subtract(vec3d1);
            double d0 = vec3d2.lengthVector() + 1.0D;
            vec3d2 = vec3d2.normalize();
            float f5 = (float)Math.acos(vec3d2.yCoord);
            float f6 = (float)Math.atan2(vec3d2.zCoord, vec3d2.xCoord);
            GlStateManager.rotate((((float)Math.PI / 2F) + -f6) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f5 * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            int i = 1;
            double d1 = (double)f2 * 0.05D * -1.5D;
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            float f7 = f * f;
            int j = 64 + (int)(f7 * 191.0F);
            int k = 32 + (int)(f7 * 191.0F);
            int l = 128 - (int)(f7 * 64.0F);
            double d2 = 0.2D;
            double d3 = 0.282D;
            double d4 = 0.0D + Math.cos(d1 + 2.356194490192345D) * 0.282D;
            double d5 = 0.0D + Math.sin(d1 + 2.356194490192345D) * 0.282D;
            double d6 = 0.0D + Math.cos(d1 + (Math.PI / 4D)) * 0.282D;
            double d7 = 0.0D + Math.sin(d1 + (Math.PI / 4D)) * 0.282D;
            double d8 = 0.0D + Math.cos(d1 + 3.9269908169872414D) * 0.282D;
            double d9 = 0.0D + Math.sin(d1 + 3.9269908169872414D) * 0.282D;
            double d10 = 0.0D + Math.cos(d1 + 5.497787143782138D) * 0.282D;
            double d11 = 0.0D + Math.sin(d1 + 5.497787143782138D) * 0.282D;
            double d12 = 0.0D + Math.cos(d1 + Math.PI) * 0.2D;
            double d13 = 0.0D + Math.sin(d1 + Math.PI) * 0.2D;
            double d14 = 0.0D + Math.cos(d1 + 0.0D) * 0.2D;
            double d15 = 0.0D + Math.sin(d1 + 0.0D) * 0.2D;
            double d16 = 0.0D + Math.cos(d1 + (Math.PI / 2D)) * 0.2D;
            double d17 = 0.0D + Math.sin(d1 + (Math.PI / 2D)) * 0.2D;
            double d18 = 0.0D + Math.cos(d1 + (Math.PI * 3D / 2D)) * 0.2D;
            double d19 = 0.0D + Math.sin(d1 + (Math.PI * 3D / 2D)) * 0.2D;
            double d20 = 0.0D;
            double d21 = 0.4999D;
            double d22 = (double)(-1.0F + f3);
            double d23 = d0 * 2.5D + d22;
            vertexbuffer.pos(d12, d0, d13).tex(0.4999D, d23).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d12, 0.0D, d13).tex(0.4999D, d22).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d14, 0.0D, d15).tex(0.0D, d22).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d14, d0, d15).tex(0.0D, d23).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d16, d0, d17).tex(0.4999D, d23).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d16, 0.0D, d17).tex(0.4999D, d22).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d18, 0.0D, d19).tex(0.0D, d22).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d18, d0, d19).tex(0.0D, d23).color(j, k, l, 255).endVertex();
            double d24 = 0.0D;

            if (entity.ticksExisted % 2 == 0)
            {
                d24 = 0.5D;
            }

            vertexbuffer.pos(d4, d0, d5).tex(0.5D, d24 + 0.5D).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d6, d0, d7).tex(1.0D, d24 + 0.5D).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d10, d0, d11).tex(1.0D, d24).color(j, k, l, 255).endVertex();
            vertexbuffer.pos(d8, d0, d9).tex(0.5D, d24).color(j, k, l, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityGuardian entitylivingbaseIn, float partialTickTime)
    {
        if (entitylivingbaseIn.isElder())
        {
            GlStateManager.scale(2.35F, 2.35F, 2.35F);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityGuardian entity)
    {
        return entity.isElder() ? GUARDIAN_ELDER_TEXTURE : GUARDIAN_TEXTURE;
    }
}