package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ProjectileHelper
{
    public static RayTraceResult forwardsRaycast(Entity p_188802_0_, boolean includeEntities, boolean p_188802_2_, Entity excludedEntity)
    {
        double d0 = p_188802_0_.posX;
        double d1 = p_188802_0_.posY;
        double d2 = p_188802_0_.posZ;
        double d3 = p_188802_0_.motionX;
        double d4 = p_188802_0_.motionY;
        double d5 = p_188802_0_.motionZ;
        World world = p_188802_0_.world;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        Vec3d vec3d1 = new Vec3d(d0 + d3, d1 + d4, d2 + d5);
        RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1, false, true, false);

        if (includeEntities)
        {
            if (raytraceresult != null)
            {
                vec3d1 = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
            }

            Entity entity = null;
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(p_188802_0_, p_188802_0_.getEntityBoundingBox().addCoord(d3, d4, d5).expandXyz(1.0D));
            double d6 = 0.0D;

            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity1 = (Entity)list.get(i);

                if (entity1.canBeCollidedWith() && (p_188802_2_ || !entity1.isEntityEqual(excludedEntity)) && !entity1.noClip)
                {
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                    if (raytraceresult1 != null)
                    {
                        double d7 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                        if (d7 < d6 || d6 == 0.0D)
                        {
                            entity = entity1;
                            d6 = d7;
                        }
                    }
                }
            }

            if (entity != null)
            {
                raytraceresult = new RayTraceResult(entity);
            }
        }

        return raytraceresult;
    }

    public static final void rotateTowardsMovement(Entity p_188803_0_, float p_188803_1_)
    {
        double d0 = p_188803_0_.motionX;
        double d1 = p_188803_0_.motionY;
        double d2 = p_188803_0_.motionZ;
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
        p_188803_0_.rotationYaw = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) + 90.0F;

        for (p_188803_0_.rotationPitch = (float)(MathHelper.atan2((double)f, d1) * (180D / Math.PI)) - 90.0F; p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch < -180.0F; p_188803_0_.prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch >= 180.0F)
        {
            p_188803_0_.prevRotationPitch += 360.0F;
        }

        while (p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw < -180.0F)
        {
            p_188803_0_.prevRotationYaw -= 360.0F;
        }

        while (p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw >= 180.0F)
        {
            p_188803_0_.prevRotationYaw += 360.0F;
        }

        p_188803_0_.rotationPitch = p_188803_0_.prevRotationPitch + (p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch) * p_188803_1_;
        p_188803_0_.rotationYaw = p_188803_0_.prevRotationYaw + (p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw) * p_188803_1_;
    }
}