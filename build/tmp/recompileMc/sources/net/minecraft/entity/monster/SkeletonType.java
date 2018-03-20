package net.minecraft.entity.monster;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.LootTableList;

public enum SkeletonType
{
    NORMAL("Skeleton", LootTableList.ENTITIES_SKELETON),
    WITHER("WitherSkeleton", LootTableList.ENTITIES_WITHER_SKELETON),
    STRAY("Stray", LootTableList.ENTITIES_STRAY);

    private final TextComponentTranslation name;
    private final ResourceLocation lootTable;

    private SkeletonType(String p_i47153_3_, ResourceLocation lootTableIn)
    {
        this.name = new TextComponentTranslation("entity." + p_i47153_3_ + ".name", new Object[0]);
        this.lootTable = lootTableIn;
    }

    public int getId()
    {
        return this.ordinal();
    }

    public static SkeletonType getByOrdinal(int ordinal)
    {
        return values()[ordinal];
    }

    public ResourceLocation getLootTable()
    {
        return this.lootTable;
    }

    public SoundEvent getAmbientSound()
    {
        switch (this)
        {
            case WITHER:
                return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
            case STRAY:
                return SoundEvents.ENTITY_STRAY_AMBIENT;
            default:
                return SoundEvents.ENTITY_SKELETON_AMBIENT;
        }
    }

    public SoundEvent getHurtSound()
    {
        switch (this)
        {
            case WITHER:
                return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
            case STRAY:
                return SoundEvents.ENTITY_STRAY_HURT;
            default:
                return SoundEvents.ENTITY_SKELETON_HURT;
        }
    }

    public SoundEvent getDeathSound()
    {
        switch (this)
        {
            case WITHER:
                return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
            case STRAY:
                return SoundEvents.ENTITY_STRAY_DEATH;
            default:
                return SoundEvents.ENTITY_SKELETON_DEATH;
        }
    }

    public SoundEvent getStepSound()
    {
        switch (this)
        {
            case WITHER:
                return SoundEvents.ENTITY_WITHER_SKELETON_STEP;
            case STRAY:
                return SoundEvents.ENTITY_STRAY_STEP;
            default:
                return SoundEvents.ENTITY_SKELETON_STEP;
        }
    }
}