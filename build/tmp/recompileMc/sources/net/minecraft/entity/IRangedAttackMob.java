package net.minecraft.entity;

public interface IRangedAttackMob
{
    /**
     * Attack the specified entity using a ranged attack.
     *  
     * @param distanceFactor How far the target is, normalized and clamped between 0.1 and 1.0
     */
    void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor);
}