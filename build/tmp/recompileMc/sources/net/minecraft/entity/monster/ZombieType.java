package net.minecraft.entity.monster;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;

public enum ZombieType
{
    NORMAL("Zombie", false),
    VILLAGER_FARMER("Zombie", true),
    VILLAGER_LIBRARIAN("Zombie", true),
    VILLAGER_PRIEST("Zombie", true),
    VILLAGER_SMITH("Zombie", true),
    VILLAGER_BUTCHER("Zombie", true),
    HUSK("Husk", false);

    private boolean villager;
    private final TextComponentTranslation name;

    private ZombieType(String nameIn, boolean villagerIn)
    {
        this.villager = villagerIn;
        this.name = new TextComponentTranslation("entity." + nameIn + ".name", new Object[0]);
    }

    public int getId()
    {
        return this.ordinal();
    }

    public boolean isVillager()
    {
        return this.villager;
    }

    public int getVillagerId()
    {
        return this.villager ? this.getId() - 1 : 0;
    }

    @javax.annotation.Nullable
    public static ZombieType getByOrdinal(int ordinal)
    {
        if (ordinal < 0 || ordinal >= values().length) return null;
        return values()[ordinal];
    }

    public static ZombieType getVillagerByOrdinal(int ordinal)
    {
        return ordinal >= 0 && ordinal < 5 ? getByOrdinal(ordinal + 1) : VILLAGER_FARMER;
    }

    public TextComponentTranslation getName()
    {
        return this.name;
    }

    public boolean isSunSensitive()
    {
        return this != HUSK;
    }

    public SoundEvent getAmbientSound()
    {
        switch (this)
        {
            case HUSK:
                return SoundEvents.ENTITY_HUSK_AMBIENT;
            case VILLAGER_FARMER:
            case VILLAGER_LIBRARIAN:
            case VILLAGER_PRIEST:
            case VILLAGER_SMITH:
            case VILLAGER_BUTCHER:
                return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
            default:
                return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
        }
    }

    public SoundEvent getHurtSound()
    {
        switch (this)
        {
            case HUSK:
                return SoundEvents.ENTITY_HUSK_HURT;
            case VILLAGER_FARMER:
            case VILLAGER_LIBRARIAN:
            case VILLAGER_PRIEST:
            case VILLAGER_SMITH:
            case VILLAGER_BUTCHER:
                return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
            default:
                return SoundEvents.ENTITY_ZOMBIE_HURT;
        }
    }

    public SoundEvent getDeathSound()
    {
        switch (this)
        {
            case HUSK:
                return SoundEvents.ENTITY_HUSK_DEATH;
            case VILLAGER_FARMER:
            case VILLAGER_LIBRARIAN:
            case VILLAGER_PRIEST:
            case VILLAGER_SMITH:
            case VILLAGER_BUTCHER:
                return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
            default:
                return SoundEvents.ENTITY_ZOMBIE_DEATH;
        }
    }

    public SoundEvent getStepSound()
    {
        switch (this)
        {
            case HUSK:
                return SoundEvents.ENTITY_HUSK_STEP;
            case VILLAGER_FARMER:
            case VILLAGER_LIBRARIAN:
            case VILLAGER_PRIEST:
            case VILLAGER_SMITH:
            case VILLAGER_BUTCHER:
                return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
            default:
                return SoundEvents.ENTITY_ZOMBIE_STEP;
        }
    }
}