package net.minecraft.entity.ai;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomPositionGenerator
{
    /**
     * used to store a driection when the user passes a point to move towards or away from. WARNING: NEVER THREAD SAFE.
     * MULTIPLE findTowards and findAway calls, will share this var
     */
    private static Vec3d staticVector = Vec3d.ZERO;

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks
     */
    @Nullable
    public static Vec3d findRandomTarget(EntityCreature entitycreatureIn, int xz, int y)
    {
        return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3d)null);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
     */
    @Nullable
    public static Vec3d findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3d targetVec3)
    {
        staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
     */
    @Nullable
    public static Vec3d findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vec3d targetVec3)
    {
        staticVector = (new Vec3d(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ)).subtract(targetVec3);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    @Nullable
    private static Vec3d findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, @Nullable Vec3d targetVec3)
    {
        PathNavigate pathnavigate = entitycreatureIn.getNavigator();
        Random random = entitycreatureIn.getRNG();
        boolean flag = false;
        int i = 0;
        int j = 0;
        int k = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entitycreatureIn.hasHome())
        {
            double d0 = entitycreatureIn.getHomePosition().distanceSq((double)MathHelper.floor(entitycreatureIn.posX), (double)MathHelper.floor(entitycreatureIn.posY), (double)MathHelper.floor(entitycreatureIn.posZ)) + 4.0D;
            double d1 = (double)(entitycreatureIn.getMaximumHomeDistance() + (float)xz);
            flag1 = d0 < d1 * d1;
        }
        else
        {
            flag1 = false;
        }

        for (int j1 = 0; j1 < 10; ++j1)
        {
            int l = random.nextInt(2 * xz + 1) - xz;
            int k1 = random.nextInt(2 * y + 1) - y;
            int i1 = random.nextInt(2 * xz + 1) - xz;

            if (targetVec3 == null || (double)l * targetVec3.xCoord + (double)i1 * targetVec3.zCoord >= 0.0D)
            {
                if (entitycreatureIn.hasHome() && xz > 1)
                {
                    BlockPos blockpos = entitycreatureIn.getHomePosition();

                    if (entitycreatureIn.posX > (double)blockpos.getX())
                    {
                        l -= random.nextInt(xz / 2);
                    }
                    else
                    {
                        l += random.nextInt(xz / 2);
                    }

                    if (entitycreatureIn.posZ > (double)blockpos.getZ())
                    {
                        i1 -= random.nextInt(xz / 2);
                    }
                    else
                    {
                        i1 += random.nextInt(xz / 2);
                    }
                }

                BlockPos blockpos1 = new BlockPos((double)l + entitycreatureIn.posX, (double)k1 + entitycreatureIn.posY, (double)i1 + entitycreatureIn.posZ);

                if ((!flag1 || entitycreatureIn.isWithinHomeDistanceFromPosition(blockpos1)) && pathnavigate.canEntityStandOnPos(blockpos1))
                {
                    float f1 = entitycreatureIn.getBlockPathWeight(blockpos1);

                    if (f1 > f)
                    {
                        f = f1;
                        i = l;
                        j = k1;
                        k = i1;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
            return new Vec3d((double)i + entitycreatureIn.posX, (double)j + entitycreatureIn.posY, (double)k + entitycreatureIn.posZ);
        }
        else
        {
            return null;
        }
    }
}