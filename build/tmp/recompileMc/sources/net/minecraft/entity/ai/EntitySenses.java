package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses
{
    EntityLiving entityObj;
    /** Cache of entities which we can see */
    List<Entity> seenEntities = Lists.<Entity>newArrayList();
    /** Cache of entities which we cannot see */
    List<Entity> unseenEntities = Lists.<Entity>newArrayList();

    public EntitySenses(EntityLiving entityObjIn)
    {
        this.entityObj = entityObjIn;
    }

    /**
     * Clears canSeeCachePositive and canSeeCacheNegative.
     */
    public void clearSensingCache()
    {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    /**
     * Checks, whether 'our' entity can see the entity given as argument (true) or not (false), caching the result.
     */
    public boolean canSee(Entity entityIn)
    {
        if (this.seenEntities.contains(entityIn))
        {
            return true;
        }
        else if (this.unseenEntities.contains(entityIn))
        {
            return false;
        }
        else
        {
            this.entityObj.world.theProfiler.startSection("canSee");
            boolean flag = this.entityObj.canEntityBeSeen(entityIn);
            this.entityObj.world.theProfiler.endSection();

            if (flag)
            {
                this.seenEntities.add(entityIn);
            }
            else
            {
                this.unseenEntities.add(entityIn);
            }

            return flag;
        }
    }
}