package net.minecraft.entity.ai;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;

public class EntityAISkeletonRiders extends EntityAIBase
{
    private final EntityHorse horse;

    public EntityAISkeletonRiders(EntityHorse horseIn)
    {
        this.horse = horseIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.horse.world.isAnyPlayerWithinRangeAt(this.horse.posX, this.horse.posY, this.horse.posZ, 10.0D);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        DifficultyInstance difficultyinstance = this.horse.world.getDifficultyForLocation(new BlockPos(this.horse));
        this.horse.setSkeletonTrap(false);
        this.horse.setType(HorseType.SKELETON);
        this.horse.setHorseTamed(true);
        this.horse.setGrowingAge(0);
        this.horse.world.addWeatherEffect(new EntityLightningBolt(this.horse.world, this.horse.posX, this.horse.posY, this.horse.posZ, true));
        EntitySkeleton entityskeleton = this.createSkeleton(difficultyinstance, this.horse);
        entityskeleton.startRiding(this.horse);

        for (int i = 0; i < 3; ++i)
        {
            EntityHorse entityhorse = this.createHorse(difficultyinstance);
            EntitySkeleton entityskeleton1 = this.createSkeleton(difficultyinstance, entityhorse);
            entityskeleton1.startRiding(entityhorse);
            entityhorse.addVelocity(this.horse.getRNG().nextGaussian() * 0.5D, 0.0D, this.horse.getRNG().nextGaussian() * 0.5D);
        }
    }

    private EntityHorse createHorse(DifficultyInstance p_188515_1_)
    {
        EntityHorse entityhorse = new EntityHorse(this.horse.world);
        entityhorse.onInitialSpawn(p_188515_1_, (IEntityLivingData)null);
        entityhorse.setPosition(this.horse.posX, this.horse.posY, this.horse.posZ);
        entityhorse.hurtResistantTime = 60;
        entityhorse.enablePersistence();
        entityhorse.setType(HorseType.SKELETON);
        entityhorse.setHorseTamed(true);
        entityhorse.setGrowingAge(0);
        entityhorse.world.spawnEntity(entityhorse);
        return entityhorse;
    }

    private EntitySkeleton createSkeleton(DifficultyInstance p_188514_1_, EntityHorse p_188514_2_)
    {
        EntitySkeleton entityskeleton = new EntitySkeleton(p_188514_2_.world);
        entityskeleton.onInitialSpawn(p_188514_1_, (IEntityLivingData)null);
        entityskeleton.setPosition(p_188514_2_.posX, p_188514_2_.posY, p_188514_2_.posZ);
        entityskeleton.hurtResistantTime = 60;
        entityskeleton.enablePersistence();

        if (entityskeleton.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null)
        {
            entityskeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }

        EnchantmentHelper.addRandomEnchantment(entityskeleton.getRNG(), entityskeleton.getHeldItemMainhand(), (int)(5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float)entityskeleton.getRNG().nextInt(18)), false);
        EnchantmentHelper.addRandomEnchantment(entityskeleton.getRNG(), entityskeleton.getItemStackFromSlot(EntityEquipmentSlot.HEAD), (int)(5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float)entityskeleton.getRNG().nextInt(18)), false);
        entityskeleton.world.spawnEntity(entityskeleton);
        return entityskeleton;
    }
}