package net.minecraft.entity.passive;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum HorseType
{
    HORSE("EntityHorse", "horse_white", SoundEvents.ENTITY_HORSE_AMBIENT, SoundEvents.ENTITY_HORSE_HURT, SoundEvents.ENTITY_HORSE_DEATH, LootTableList.ENTITIES_HORSE),
    DONKEY("Donkey", "donkey", SoundEvents.ENTITY_DONKEY_AMBIENT, SoundEvents.ENTITY_DONKEY_HURT, SoundEvents.ENTITY_DONKEY_DEATH, LootTableList.ENTITIES_HORSE),
    MULE("Mule", "mule", SoundEvents.ENTITY_MULE_AMBIENT, SoundEvents.ENTITY_MULE_HURT, SoundEvents.ENTITY_MULE_DEATH, LootTableList.ENTITIES_HORSE),
    ZOMBIE("ZombieHorse", "horse_zombie", SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT, SoundEvents.ENTITY_ZOMBIE_HORSE_HURT, SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH, LootTableList.ENTITIES_ZOMBIE_HORSE),
    SKELETON("SkeletonHorse", "horse_skeleton", SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT, SoundEvents.ENTITY_SKELETON_HORSE_HURT, SoundEvents.ENTITY_SKELETON_HORSE_DEATH, LootTableList.ENTITIES_SKELETON_HORSE);

    /** The default name for this type of horse */
    private final TextComponentTranslation name;
    /** The default texture used by this type of horse */
    private final ResourceLocation texture;
    private final SoundEvent hurtSound;
    private final SoundEvent ambientSound;
    private final SoundEvent deathSound;
    private final ResourceLocation lootTable;

    private HorseType(String p_i46798_3_, String textureName, SoundEvent ambientSound, SoundEvent hurtSoundIn, SoundEvent deathSoundIn, ResourceLocation lootTableIn)
    {
        this.name = new TextComponentTranslation("entity." + p_i46798_3_ + ".name", new Object[0]);
        this.texture = new ResourceLocation("textures/entity/horse/" + textureName + ".png");
        this.hurtSound = hurtSoundIn;
        this.ambientSound = ambientSound;
        this.deathSound = deathSoundIn;
        this.lootTable = lootTableIn;
    }

    public SoundEvent getAmbientSound()
    {
        return this.ambientSound;
    }

    public SoundEvent getHurtSound()
    {
        return this.hurtSound;
    }

    public SoundEvent getDeathSound()
    {
        return this.deathSound;
    }

    /**
     * Gets the default name for horses of this type
     */
    public TextComponentTranslation getDefaultName()
    {
        return this.name;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getTexture()
    {
        return this.texture;
    }

    /**
     * Can this type of horse wear chests?
     */
    public boolean canBeChested()
    {
        return this == DONKEY || this == MULE;
    }

    /**
     * Should this type of horse be rendered with mule ears (true) or horse ears (false)?
     */
    public boolean hasMuleEars()
    {
        return this == DONKEY || this == MULE;
    }

    /**
     * Is this an undead (zombie or skeleton) horse?
     */
    public boolean isUndead()
    {
        return this == ZOMBIE || this == SKELETON;
    }

    /**
     * Can this type of horse be bred?
     */
    public boolean canMate()
    {
        return !this.isUndead() && this != MULE;
    }

    public boolean isHorse()
    {
        return this == HORSE;
    }

    public int getOrdinal()
    {
        return this.ordinal();
    }

    public static HorseType getArmorType(int armorID)
    {
        return values()[armorID];
    }

    public ResourceLocation getLootTable()
    {
        return this.lootTable;
    }
}