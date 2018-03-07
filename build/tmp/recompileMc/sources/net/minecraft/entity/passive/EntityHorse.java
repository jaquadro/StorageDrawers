package net.minecraft.entity.passive;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISkeletonRiders;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHorse extends EntityAnimal implements IInventoryChangedListener, IJumpingMount
{
    private static final Predicate<Entity> IS_HORSE_BREEDING = new Predicate<Entity>()
    {
        public boolean apply(@Nullable Entity p_apply_1_)
        {
            return p_apply_1_ instanceof EntityHorse && ((EntityHorse)p_apply_1_).isBreeding();
        }
    };
    private static final IAttribute JUMP_STRENGTH = (new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).setDescription("Jump Strength").setShouldWatch(true);
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataParameter<Byte> STATUS = EntityDataManager.<Byte>createKey(EntityHorse.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> HORSE_TYPE = EntityDataManager.<Integer>createKey(EntityHorse.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(EntityHorse.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityHorse.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Integer> HORSE_ARMOR = EntityDataManager.<Integer>createKey(EntityHorse.class, DataSerializers.VARINT);
    private static final String[] HORSE_TEXTURES = new String[] {"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] HORSE_TEXTURES_ABBR = new String[] {"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] HORSE_MARKING_TEXTURES = new String[] {null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[] {"", "wo_", "wmo", "wdo", "bdo"};
    private final EntityAISkeletonRiders skeletonTrapAI = new EntityAISkeletonRiders(this);
    private int eatingHaystackCounter;
    private int openMouthCounter;
    private int jumpRearingCounter;
    public int tailCounter;
    public int sprintCounter;
    protected boolean horseJumping;
    private AnimalChest horseChest;
    private boolean hasReproduced;
    /** "The higher this value, the more likely the horse is to be tamed next time a player rides it." */
    protected int temper;
    protected float jumpPower;
    private boolean allowStandSliding;
    private boolean skeletonTrap;
    private int skeletonTrapTime;
    private float headLean;
    private float prevHeadLean;
    private float rearingAmount;
    private float prevRearingAmount;
    private float mouthOpenness;
    private float prevMouthOpenness;
    /** Used to determine the sound that the horse should make when it steps */
    private int gallopTime;
    private String texturePrefix;
    private final String[] horseTexturesArray = new String[3];
    @SideOnly(Side.CLIENT)
    private boolean hasTexture;

    public EntityHorse(World worldIn)
    {
        super(worldIn);
        this.setSize(1.3964844F, 1.6F);
        this.isImmuneToFire = false;
        this.setChested(false);
        this.stepHeight = 1.0F;
        this.initHorseChest();
    }

    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.2D));
        this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWander(this, 0.7D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(STATUS, Byte.valueOf((byte)0));
        this.dataManager.register(HORSE_TYPE, Integer.valueOf(HorseType.HORSE.getOrdinal()));
        this.dataManager.register(HORSE_VARIANT, Integer.valueOf(0));
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
        this.dataManager.register(HORSE_ARMOR, Integer.valueOf(HorseArmorType.NONE.getOrdinal()));
    }

    public void setType(HorseType armorType)
    {
        this.dataManager.set(HORSE_TYPE, Integer.valueOf(armorType.getOrdinal()));
        this.resetTexturePrefix();
    }

    public HorseType getType()
    {
        return HorseType.getArmorType(((Integer)this.dataManager.get(HORSE_TYPE)).intValue());
    }

    public void setHorseVariant(int variant)
    {
        this.dataManager.set(HORSE_VARIANT, Integer.valueOf(variant));
        this.resetTexturePrefix();
    }

    public int getHorseVariant()
    {
        return ((Integer)this.dataManager.get(HORSE_VARIANT)).intValue();
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.getCustomNameTag() : this.getType().getDefaultName().getUnformattedText();
    }

    private boolean getHorseWatchableBoolean(int p_110233_1_)
    {
        return (((Byte)this.dataManager.get(STATUS)).byteValue() & p_110233_1_) != 0;
    }

    private void setHorseWatchableBoolean(int p_110208_1_, boolean p_110208_2_)
    {
        byte b0 = ((Byte)this.dataManager.get(STATUS)).byteValue();

        if (p_110208_2_)
        {
            this.dataManager.set(STATUS, Byte.valueOf((byte)(b0 | p_110208_1_)));
        }
        else
        {
            this.dataManager.set(STATUS, Byte.valueOf((byte)(b0 & ~p_110208_1_)));
        }
    }

    public boolean isAdultHorse()
    {
        return !this.isChild();
    }

    public boolean isTame()
    {
        return this.getHorseWatchableBoolean(2);
    }

    public boolean isRidable()
    {
        return this.isAdultHorse();
    }

    @Nullable
    public UUID getOwnerUniqueId()
    {
        return (UUID)((Optional)this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

    public void setOwnerUniqueId(@Nullable UUID uniqueId)
    {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uniqueId));
    }

    public float getHorseSize()
    {
        return 0.5F;
    }

    /**
     * "Sets the scale for an ageable entity according to the boolean parameter, which says if it's a child."
     */
    public void setScaleForAge(boolean child)
    {
        if (child)
        {
            this.setScale(this.getHorseSize());
        }
        else
        {
            this.setScale(1.0F);
        }
    }

    public boolean isHorseJumping()
    {
        return this.horseJumping;
    }

    public void setHorseTamed(boolean tamed)
    {
        this.setHorseWatchableBoolean(2, tamed);
    }

    public void setHorseJumping(boolean jumping)
    {
        this.horseJumping = jumping;
    }

    public boolean canBeLeashedTo(EntityPlayer player)
    {
        return !this.getType().isUndead() && super.canBeLeashedTo(player);
    }

    protected void onLeashDistance(float p_142017_1_)
    {
        if (p_142017_1_ > 6.0F && this.isEatingHaystack())
        {
            this.setEatingHaystack(false);
        }
    }

    public boolean isChested()
    {
        return this.getType().canBeChested() && this.getHorseWatchableBoolean(8);
    }

    public HorseArmorType getHorseArmorType()
    {
        return HorseArmorType.getByOrdinal(((Integer)this.dataManager.get(HORSE_ARMOR)).intValue());
    }

    public boolean isEatingHaystack()
    {
        return this.getHorseWatchableBoolean(32);
    }

    public boolean isRearing()
    {
        return this.getHorseWatchableBoolean(64);
    }

    public boolean isBreeding()
    {
        return this.getHorseWatchableBoolean(16);
    }

    public boolean getHasReproduced()
    {
        return this.hasReproduced;
    }

    /**
     * Set horse armor stack (for example: new ItemStack(Items.iron_horse_armor))
     */
    public void setHorseArmorStack(ItemStack itemStackIn)
    {
        HorseArmorType horsearmortype = HorseArmorType.getByItemStack(itemStackIn);
        this.dataManager.set(HORSE_ARMOR, Integer.valueOf(horsearmortype.getOrdinal()));
        this.resetTexturePrefix();

        if (!this.world.isRemote)
        {
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            int i = horsearmortype.getProtection();

            if (i != 0)
            {
                this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, 0)).setSaved(false));
            }
        }
    }

    public void setBreeding(boolean breeding)
    {
        this.setHorseWatchableBoolean(16, breeding);
    }

    public void setChested(boolean chested)
    {
        this.setHorseWatchableBoolean(8, chested);
    }

    public void setHasReproduced(boolean hasReproducedIn)
    {
        this.hasReproduced = hasReproducedIn;
    }

    public void setHorseSaddled(boolean saddled)
    {
        this.setHorseWatchableBoolean(4, saddled);
    }

    public int getTemper()
    {
        return this.temper;
    }

    public void setTemper(int temperIn)
    {
        this.temper = temperIn;
    }

    public int increaseTemper(int p_110198_1_)
    {
        int i = MathHelper.clamp(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
        this.setTemper(i);
        return i;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        Entity entity = source.getEntity();
        return this.isBeingRidden() && entity != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(source, amount);
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return !this.isBeingRidden();
    }

    public boolean prepareChunkForSpawn()
    {
        int i = MathHelper.floor(this.posX);
        int j = MathHelper.floor(this.posZ);
        this.world.getBiome(new BlockPos(i, 0, j));
        return true;
    }

    public void dropChests()
    {
        if (!this.world.isRemote && this.isChested())
        {
            this.dropItem(Item.getItemFromBlock(Blocks.CHEST), 1);
            this.setChested(false);
        }
    }

    private void eatingHorse()
    {
        this.openHorseMouth();

        if (!this.isSilent())
        {
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
        }
    }

    public void fall(float distance, float damageMultiplier)
    {
        if (distance > 1.0F)
        {
            this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
        }

        int i = MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);

        if (i > 0)
        {
            this.attackEntityFrom(DamageSource.fall, (float)i);

            if (this.isBeingRidden())
            {
                for (Entity entity : this.getRecursivePassengers())
                {
                    entity.attackEntityFrom(DamageSource.fall, (float)i);
                }
            }

            IBlockState iblockstate = this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.2D - (double)this.prevRotationYaw, this.posZ));
            Block block = iblockstate.getBlock();

            if (iblockstate.getMaterial() != Material.AIR && !this.isSilent())
            {
                SoundType soundtype = block.getSoundType(iblockstate, world, new BlockPos(this.posX, this.posY - 0.2D - (double)this.prevRotationYaw, this.posZ), this);
                this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, soundtype.getStepSound(), this.getSoundCategory(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
            }
        }
    }

    /**
     * Returns number of slots depending horse type
     */
    private int getChestSize()
    {
        HorseType horsetype = this.getType();
        return this.isChested() && horsetype.canBeChested() ? 17 : 2;
    }

    private void initHorseChest()
    {
        AnimalChest animalchest = this.horseChest;
        this.horseChest = new AnimalChest("HorseChest", this.getChestSize());
        this.horseChest.setCustomName(this.getName());

        if (animalchest != null)
        {
            animalchest.removeInventoryChangeListener(this);
            int i = Math.min(animalchest.getSizeInventory(), this.horseChest.getSizeInventory());

            for (int j = 0; j < i; ++j)
            {
                ItemStack itemstack = animalchest.getStackInSlot(j);

                if (itemstack != null)
                {
                    this.horseChest.setInventorySlotContents(j, itemstack.copy());
                }
            }
        }

        this.horseChest.addInventoryChangeListener(this);
        this.updateHorseSlots();
        this.itemHandler = new net.minecraftforge.items.wrapper.InvWrapper(this.horseChest);
    }

    /**
     * Updates the items in the saddle and armor slots of the horse's inventory.
     */
    private void updateHorseSlots()
    {
        if (!this.world.isRemote)
        {
            this.setHorseSaddled(this.horseChest.getStackInSlot(0) != null);

            if (this.getType().isHorse())
            {
                this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
            }
        }
    }

    /**
     * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
     */
    public void onInventoryChanged(InventoryBasic invBasic)
    {
        HorseArmorType horsearmortype = this.getHorseArmorType();
        boolean flag = this.isHorseSaddled();
        this.updateHorseSlots();

        if (this.ticksExisted > 20)
        {
            if (horsearmortype == HorseArmorType.NONE && horsearmortype != this.getHorseArmorType())
            {
                this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
            }
            else if (horsearmortype != this.getHorseArmorType())
            {
                this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
            }

            if (!flag && this.isHorseSaddled())
            {
                this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
            }
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        this.prepareChunkForSpawn();
        return super.getCanSpawnHere();
    }

    protected EntityHorse getClosestHorse(Entity entityIn, double distance)
    {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;

        for (Entity entity1 : this.world.getEntitiesInAABBexcluding(entityIn, entityIn.getEntityBoundingBox().addCoord(distance, distance, distance), IS_HORSE_BREEDING))
        {
            double d1 = entity1.getDistanceSq(entityIn.posX, entityIn.posY, entityIn.posZ);

            if (d1 < d0)
            {
                entity = entity1;
                d0 = d1;
            }
        }

        return (EntityHorse)entity;
    }

    public double getHorseJumpStrength()
    {
        return this.getEntityAttribute(JUMP_STRENGTH).getAttributeValue();
    }

    protected SoundEvent getDeathSound()
    {
        this.openHorseMouth();
        return this.getType().getDeathSound();
    }

    protected SoundEvent getHurtSound()
    {
        this.openHorseMouth();

        if (this.rand.nextInt(3) == 0)
        {
            this.makeHorseRear();
        }

        return this.getType().getHurtSound();
    }

    public boolean isHorseSaddled()
    {
        return this.getHorseWatchableBoolean(4);
    }

    protected SoundEvent getAmbientSound()
    {
        this.openHorseMouth();

        if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked())
        {
            this.makeHorseRear();
        }

        return this.getType().getAmbientSound();
    }

    @Nullable
    protected SoundEvent getAngrySound()
    {
        this.openHorseMouth();
        this.makeHorseRear();
        HorseType horsetype = this.getType();
        return horsetype.isUndead() ? null : (horsetype.hasMuleEars() ? SoundEvents.ENTITY_DONKEY_ANGRY : SoundEvents.ENTITY_HORSE_ANGRY);
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        SoundType soundtype = blockIn.getSoundType(world.getBlockState(pos), world, pos, this);

        if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER)
        {
            soundtype = Blocks.SNOW_LAYER.getSoundType();
        }

        if (!blockIn.getDefaultState().getMaterial().isLiquid())
        {
            HorseType horsetype = this.getType();

            if (this.isBeingRidden() && !horsetype.hasMuleEars())
            {
                ++this.gallopTime;

                if (this.gallopTime > 5 && this.gallopTime % 3 == 0)
                {
                    this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, soundtype.getVolume() * 0.15F, soundtype.getPitch());

                    if (horsetype == HorseType.HORSE && this.rand.nextInt(10) == 0)
                    {
                        this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, soundtype.getVolume() * 0.6F, soundtype.getPitch());
                    }
                }
                else if (this.gallopTime <= 5)
                {
                    this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            }
            else if (soundtype == SoundType.WOOD)
            {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
            else
            {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(JUMP_STRENGTH);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(53.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22499999403953552D);
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 6;
    }

    public int getMaxTemper()
    {
        return 100;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.8F;
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 400;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasLayeredTextures()
    {
        return this.getType() == HorseType.HORSE || this.getHorseArmorType() != HorseArmorType.NONE;
    }

    private void resetTexturePrefix()
    {
        this.texturePrefix = null;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasTexture()
    {
        return this.hasTexture;
    }

    @SideOnly(Side.CLIENT)
    private void setHorseTexturePaths()
    {
        this.texturePrefix = "horse/";
        this.horseTexturesArray[0] = null;
        this.horseTexturesArray[1] = null;
        this.horseTexturesArray[2] = null;
        HorseType horsetype = this.getType();
        int i = this.getHorseVariant();

        if (horsetype == HorseType.HORSE)
        {
            int j = i & 255;
            int k = (i & 65280) >> 8;

            if (j >= HORSE_TEXTURES.length)
            {
                this.hasTexture = false;
                return;
            }

            this.horseTexturesArray[0] = HORSE_TEXTURES[j];
            this.texturePrefix = this.texturePrefix + HORSE_TEXTURES_ABBR[j];

            if (k >= HORSE_MARKING_TEXTURES.length)
            {
                this.hasTexture = false;
                return;
            }

            this.horseTexturesArray[1] = HORSE_MARKING_TEXTURES[k];
            this.texturePrefix = this.texturePrefix + HORSE_MARKING_TEXTURES_ABBR[k];
        }
        else
        {
            this.horseTexturesArray[0] = "";
            this.texturePrefix = this.texturePrefix + "_" + horsetype + "_";
        }

        HorseArmorType horsearmortype = this.getHorseArmorType();
        this.horseTexturesArray[2] = horsearmortype.getTextureName();
        this.texturePrefix = this.texturePrefix + horsearmortype.getHash();
        this.hasTexture = true;
    }

    @SideOnly(Side.CLIENT)
    public String getHorseTexture()
    {
        if (this.texturePrefix == null)
        {
            this.setHorseTexturePaths();
        }

        return this.texturePrefix;
    }

    @SideOnly(Side.CLIENT)
    public String[] getVariantTexturePaths()
    {
        if (this.texturePrefix == null)
        {
            this.setHorseTexturePaths();
        }

        return this.horseTexturesArray;
    }

    public void openGUI(EntityPlayer playerEntity)
    {
        if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity)) && this.isTame())
        {
            this.horseChest.setCustomName(this.getName());
            playerEntity.openGuiHorseInventory(this, this.horseChest);
        }
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
    {
        if (stack != null && stack.getItem() == Items.SPAWN_EGG)
        {
            return super.processInteract(player, hand, stack);
        }
        else if (!this.isTame() && this.getType().isUndead())
        {
            return false;
        }
        else if (this.isTame() && this.isAdultHorse() && player.isSneaking())
        {
            this.openGUI(player);
            return true;
        }
        else if (this.isRidable() && this.isBeingRidden())
        {
            return super.processInteract(player, hand, stack);
        }
        else
        {
            if (stack != null)
            {
                if (this.getType().isHorse())
                {
                    HorseArmorType horsearmortype = HorseArmorType.getByItemStack(stack);

                    if (horsearmortype != HorseArmorType.NONE)
                    {
                        if (!this.isTame())
                        {
                            this.makeHorseRearWithSound();
                            return true;
                        }

                        this.openGUI(player);
                        return true;
                    }
                }

                boolean flag = false;

                if (!this.getType().isUndead())
                {
                    float f = 0.0F;
                    int i = 0;
                    int j = 0;

                    if (stack.getItem() == Items.WHEAT)
                    {
                        f = 2.0F;
                        i = 20;
                        j = 3;
                    }
                    else if (stack.getItem() == Items.SUGAR)
                    {
                        f = 1.0F;
                        i = 30;
                        j = 3;
                    }
                    else if (Block.getBlockFromItem(stack.getItem()) == Blocks.HAY_BLOCK)
                    {
                        f = 20.0F;
                        i = 180;
                    }
                    else if (stack.getItem() == Items.APPLE)
                    {
                        f = 3.0F;
                        i = 60;
                        j = 3;
                    }
                    else if (stack.getItem() == Items.GOLDEN_CARROT)
                    {
                        f = 4.0F;
                        i = 60;
                        j = 5;

                        if (this.isTame() && this.getGrowingAge() == 0)
                        {
                            flag = true;
                            this.setInLove(player);
                        }
                    }
                    else if (stack.getItem() == Items.GOLDEN_APPLE)
                    {
                        f = 10.0F;
                        i = 240;
                        j = 10;

                        if (this.isTame() && this.getGrowingAge() == 0 && !this.isInLove())
                        {
                            flag = true;
                            this.setInLove(player);
                        }
                    }

                    if (this.getHealth() < this.getMaxHealth() && f > 0.0F)
                    {
                        this.heal(f);
                        flag = true;
                    }

                    if (!this.isAdultHorse() && i > 0)
                    {
                        if (!this.world.isRemote)
                        {
                            this.addGrowth(i);
                        }

                        flag = true;
                    }

                    if (j > 0 && (flag || !this.isTame()) && this.getTemper() < this.getMaxTemper())
                    {
                        flag = true;

                        if (!this.world.isRemote)
                        {
                            this.increaseTemper(j);
                        }
                    }

                    if (flag)
                    {
                        this.eatingHorse();
                    }
                }

                if (!this.isTame() && !flag)
                {
                    if (stack.interactWithEntity(player, this, hand))
                    {
                        return true;
                    }

                    this.makeHorseRearWithSound();
                    return true;
                }

                if (!flag && this.getType().canBeChested() && !this.isChested() && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST))
                {
                    this.setChested(true);
                    this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    flag = true;
                    this.initHorseChest();
                }

                if (!flag && this.isRidable() && !this.isHorseSaddled() && stack.getItem() == Items.SADDLE)
                {
                    this.openGUI(player);
                    return true;
                }

                if (flag)
                {
                    if (!player.capabilities.isCreativeMode)
                    {
                        --stack.stackSize;
                    }

                    return true;
                }
            }

            if (this.isRidable() && !this.isBeingRidden())
            {
                if (stack != null && stack.interactWithEntity(player, this, hand))
                {
                    return true;
                }
                else
                {
                    this.mountTo(player);
                    return true;
                }
            }
            else
            {
                return super.processInteract(player, hand, stack);
            }
        }
    }

    private void mountTo(EntityPlayer player)
    {
        player.rotationYaw = this.rotationYaw;
        player.rotationPitch = this.rotationPitch;
        this.setEatingHaystack(false);
        this.setRearing(false);

        if (!this.world.isRemote)
        {
            player.startRiding(this);
        }
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return this.isBeingRidden() && this.isHorseSaddled() ? true : this.isEatingHaystack() || this.isRearing();
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(@Nullable ItemStack stack)
    {
        return false;
    }

    private void moveTail()
    {
        this.tailCounter = 1;
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause)
    {
        super.onDeath(cause);

        if (!this.world.isRemote)
        {
            this.dropChestItems();
        }
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.rand.nextInt(200) == 0)
        {
            this.moveTail();
        }

        super.onLivingUpdate();

        if (!this.world.isRemote)
        {
            if (this.rand.nextInt(900) == 0 && this.deathTime == 0)
            {
                this.heal(1.0F);
            }

            if (!this.isEatingHaystack() && !this.isBeingRidden() && this.rand.nextInt(300) == 0 && this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY) - 1, MathHelper.floor(this.posZ))).getBlock() == Blocks.GRASS)
            {
                this.setEatingHaystack(true);
            }

            if (this.isEatingHaystack() && ++this.eatingHaystackCounter > 50)
            {
                this.eatingHaystackCounter = 0;
                this.setEatingHaystack(false);
            }

            if (this.isBreeding() && !this.isAdultHorse() && !this.isEatingHaystack())
            {
                EntityHorse entityhorse = this.getClosestHorse(this, 16.0D);

                if (entityhorse != null && this.getDistanceSqToEntity(entityhorse) > 4.0D)
                {
                    this.navigator.getPathToEntityLiving(entityhorse);
                }
            }

            if (this.isSkeletonTrap() && this.skeletonTrapTime++ >= 18000)
            {
                this.setDead();
            }
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.world.isRemote && this.dataManager.isDirty())
        {
            this.dataManager.setClean();
            this.resetTexturePrefix();
        }

        if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30)
        {
            this.openMouthCounter = 0;
            this.setHorseWatchableBoolean(128, false);
        }

        if (this.canPassengerSteer() && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20)
        {
            this.jumpRearingCounter = 0;
            this.setRearing(false);
        }

        if (this.tailCounter > 0 && ++this.tailCounter > 8)
        {
            this.tailCounter = 0;
        }

        if (this.sprintCounter > 0)
        {
            ++this.sprintCounter;

            if (this.sprintCounter > 300)
            {
                this.sprintCounter = 0;
            }
        }

        this.prevHeadLean = this.headLean;

        if (this.isEatingHaystack())
        {
            this.headLean += (1.0F - this.headLean) * 0.4F + 0.05F;

            if (this.headLean > 1.0F)
            {
                this.headLean = 1.0F;
            }
        }
        else
        {
            this.headLean += (0.0F - this.headLean) * 0.4F - 0.05F;

            if (this.headLean < 0.0F)
            {
                this.headLean = 0.0F;
            }
        }

        this.prevRearingAmount = this.rearingAmount;

        if (this.isRearing())
        {
            this.headLean = 0.0F;
            this.prevHeadLean = this.headLean;
            this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;

            if (this.rearingAmount > 1.0F)
            {
                this.rearingAmount = 1.0F;
            }
        }
        else
        {
            this.allowStandSliding = false;
            this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F;

            if (this.rearingAmount < 0.0F)
            {
                this.rearingAmount = 0.0F;
            }
        }

        this.prevMouthOpenness = this.mouthOpenness;

        if (this.getHorseWatchableBoolean(128))
        {
            this.mouthOpenness += (1.0F - this.mouthOpenness) * 0.7F + 0.05F;

            if (this.mouthOpenness > 1.0F)
            {
                this.mouthOpenness = 1.0F;
            }
        }
        else
        {
            this.mouthOpenness += (0.0F - this.mouthOpenness) * 0.7F - 0.05F;

            if (this.mouthOpenness < 0.0F)
            {
                this.mouthOpenness = 0.0F;
            }
        }
    }

    private void openHorseMouth()
    {
        if (!this.world.isRemote)
        {
            this.openMouthCounter = 1;
            this.setHorseWatchableBoolean(128, true);
        }
    }

    /**
     * Return true if the horse entity ready to mate. (no rider, not riding, tame, adult, not steril...)
     */
    private boolean canMate()
    {
        return !this.isBeingRidden() && !this.isRiding() && this.isTame() && this.isAdultHorse() && this.getType().canMate() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    public void setEatingHaystack(boolean p_110227_1_)
    {
        this.setHorseWatchableBoolean(32, p_110227_1_);
    }

    public void setRearing(boolean rearing)
    {
        if (rearing)
        {
            this.setEatingHaystack(false);
        }

        this.setHorseWatchableBoolean(64, rearing);
    }

    private void makeHorseRear()
    {
        if (this.canPassengerSteer())
        {
            this.jumpRearingCounter = 1;
            this.setRearing(true);
        }
    }

    public void makeHorseRearWithSound()
    {
        this.makeHorseRear();
        SoundEvent soundevent = this.getAngrySound();

        if (soundevent != null)
        {
            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    public void dropChestItems()
    {
        this.dropItemsInChest(this, this.horseChest);
        this.dropChests();
    }

    private void dropItemsInChest(Entity entityIn, AnimalChest animalChestIn)
    {
        if (animalChestIn != null && !this.world.isRemote)
        {
            for (int i = 0; i < animalChestIn.getSizeInventory(); ++i)
            {
                ItemStack itemstack = animalChestIn.getStackInSlot(i);

                if (itemstack != null)
                {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }
        }
    }

    public boolean setTamedBy(EntityPlayer player)
    {
        this.setOwnerUniqueId(player.getUniqueID());
        this.setHorseTamed(true);
        return true;
    }

    /**
     * Moves the entity based on the specified heading.
     */
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled())
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.getControllingPassenger();
            this.rotationYaw = entitylivingbase.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = entitylivingbase.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;
            strafe = entitylivingbase.moveStrafing * 0.5F;
            forward = entitylivingbase.moveForward;

            if (forward <= 0.0F)
            {
                forward *= 0.25F;
                this.gallopTime = 0;
            }

            if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.allowStandSliding)
            {
                strafe = 0.0F;
                forward = 0.0F;
            }

            if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround)
            {
                this.motionY = this.getHorseJumpStrength() * (double)this.jumpPower;

                if (this.isPotionActive(MobEffects.JUMP_BOOST))
                {
                    this.motionY += (double)((float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                }

                this.setHorseJumping(true);
                this.isAirBorne = true;

                if (forward > 0.0F)
                {
                    float f = MathHelper.sin(this.rotationYaw * 0.017453292F);
                    float f1 = MathHelper.cos(this.rotationYaw * 0.017453292F);
                    this.motionX += (double)(-0.4F * f * this.jumpPower);
                    this.motionZ += (double)(0.4F * f1 * this.jumpPower);
                    this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
                }

                this.jumpPower = 0.0F;
                net.minecraftforge.common.ForgeHooks.onLivingJump(this);
            }

            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

            if (this.canPassengerSteer())
            {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                super.moveEntityWithHeading(strafe, forward);
            }
            else if (entitylivingbase instanceof EntityPlayer)
            {
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            if (this.onGround)
            {
                this.jumpPower = 0.0F;
                this.setHorseJumping(false);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f2 > 1.0F)
            {
                f2 = 1.0F;
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else
        {
            this.jumpMovementFactor = 0.02F;
            super.moveEntityWithHeading(strafe, forward);
        }
    }

    public static void registerFixesHorse(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, "EntityHorse");
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackDataLists("EntityHorse", new String[] {"Items"}));
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackData("EntityHorse", new String[] {"ArmorItem", "SaddleItem"}));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("EatingHaystack", this.isEatingHaystack());
        compound.setBoolean("ChestedHorse", this.isChested());
        compound.setBoolean("HasReproduced", this.getHasReproduced());
        compound.setBoolean("Bred", this.isBreeding());
        compound.setInteger("Type", this.getType().getOrdinal());
        compound.setInteger("Variant", this.getHorseVariant());
        compound.setInteger("Temper", this.getTemper());
        compound.setBoolean("Tame", this.isTame());
        compound.setBoolean("SkeletonTrap", this.isSkeletonTrap());
        compound.setInteger("SkeletonTrapTime", this.skeletonTrapTime);

        if (this.getOwnerUniqueId() != null)
        {
            compound.setString("OwnerUUID", this.getOwnerUniqueId().toString());
        }

        if (this.isChested())
        {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.horseChest.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.horseChest.getStackInSlot(i);

                if (itemstack != null)
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte)i);
                    itemstack.writeToNBT(nbttagcompound);
                    nbttaglist.appendTag(nbttagcompound);
                }
            }

            compound.setTag("Items", nbttaglist);
        }

        if (this.horseChest.getStackInSlot(1) != null)
        {
            compound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
        }

        if (this.horseChest.getStackInSlot(0) != null)
        {
            compound.setTag("SaddleItem", this.horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setEatingHaystack(compound.getBoolean("EatingHaystack"));
        this.setBreeding(compound.getBoolean("Bred"));
        this.setChested(compound.getBoolean("ChestedHorse"));
        this.setHasReproduced(compound.getBoolean("HasReproduced"));
        this.setType(HorseType.getArmorType(compound.getInteger("Type")));
        this.setHorseVariant(compound.getInteger("Variant"));
        this.setTemper(compound.getInteger("Temper"));
        this.setHorseTamed(compound.getBoolean("Tame"));
        this.setSkeletonTrap(compound.getBoolean("SkeletonTrap"));
        this.skeletonTrapTime = compound.getInteger("SkeletonTrapTime");
        String s;

        if (compound.hasKey("OwnerUUID", 8))
        {
            s = compound.getString("OwnerUUID");
        }
        else
        {
            String s1 = compound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }

        if (!s.isEmpty())
        {
            this.setOwnerUniqueId(UUID.fromString(s));
        }

        IAttributeInstance iattributeinstance = this.getAttributeMap().getAttributeInstanceByName("Speed");

        if (iattributeinstance != null)
        {
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(iattributeinstance.getBaseValue() * 0.25D);
        }

        if (this.isChested())
        {
            NBTTagList nbttaglist = compound.getTagList("Items", 10);
            this.initHorseChest();

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;

                if (j >= 2 && j < this.horseChest.getSizeInventory())
                {
                    this.horseChest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
                }
            }
        }

        if (compound.hasKey("ArmorItem", 10))
        {
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("ArmorItem"));

            if (itemstack != null && HorseArmorType.isHorseArmor(itemstack.getItem()))
            {
                this.horseChest.setInventorySlotContents(1, itemstack);
            }
        }

        if (compound.hasKey("SaddleItem", 10))
        {
            ItemStack itemstack1 = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("SaddleItem"));

            if (itemstack1 != null && itemstack1.getItem() == Items.SADDLE)
            {
                this.horseChest.setInventorySlotContents(0, itemstack1);
            }
        }

        this.updateHorseSlots();
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal == this)
        {
            return false;
        }
        else if (otherAnimal.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            EntityHorse entityhorse = (EntityHorse)otherAnimal;

            if (this.canMate() && entityhorse.canMate())
            {
                HorseType horsetype = this.getType();
                HorseType horsetype1 = entityhorse.getType();
                return horsetype == horsetype1 || horsetype == HorseType.HORSE && horsetype1 == HorseType.DONKEY || horsetype == HorseType.DONKEY && horsetype1 == HorseType.HORSE;
            }
            else
            {
                return false;
            }
        }
    }

    public EntityAgeable createChild(EntityAgeable ageable)
    {
        EntityHorse entityhorse = (EntityHorse)ageable;
        EntityHorse entityhorse1 = new EntityHorse(this.world);
        HorseType horsetype = this.getType();
        HorseType horsetype1 = entityhorse.getType();
        HorseType horsetype2 = HorseType.HORSE;

        if (horsetype == horsetype1)
        {
            horsetype2 = horsetype;
        }
        else if (horsetype == HorseType.HORSE && horsetype1 == HorseType.DONKEY || horsetype == HorseType.DONKEY && horsetype1 == HorseType.HORSE)
        {
            horsetype2 = HorseType.MULE;
        }

        if (horsetype2 == HorseType.HORSE)
        {
            int j = this.rand.nextInt(9);
            int i;

            if (j < 4)
            {
                i = this.getHorseVariant() & 255;
            }
            else if (j < 8)
            {
                i = entityhorse.getHorseVariant() & 255;
            }
            else
            {
                i = this.rand.nextInt(7);
            }

            int k = this.rand.nextInt(5);

            if (k < 2)
            {
                i = i | this.getHorseVariant() & 65280;
            }
            else if (k < 4)
            {
                i = i | entityhorse.getHorseVariant() & 65280;
            }
            else
            {
                i = i | this.rand.nextInt(5) << 8 & 65280;
            }

            entityhorse1.setHorseVariant(i);
        }

        entityhorse1.setType(horsetype2);
        double d1 = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + ageable.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() + (double)this.getModifiedMaxHealth();
        entityhorse1.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(d1 / 3.0D);
        double d2 = this.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + ageable.getEntityAttribute(JUMP_STRENGTH).getBaseValue() + this.getModifiedJumpStrength();
        entityhorse1.getEntityAttribute(JUMP_STRENGTH).setBaseValue(d2 / 3.0D);
        double d0 = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + ageable.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() + this.getModifiedMovementSpeed();
        entityhorse1.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(d0 / 3.0D);
        return entityhorse1;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        int i = 0;
        HorseType horsetype;

        if (livingdata instanceof EntityHorse.GroupData)
        {
            horsetype = ((EntityHorse.GroupData)livingdata).horseType;
            i = ((EntityHorse.GroupData)livingdata).horseVariant & 255 | this.rand.nextInt(5) << 8;
        }
        else
        {
            if (this.rand.nextInt(10) == 0)
            {
                horsetype = HorseType.DONKEY;
            }
            else
            {
                int j = this.rand.nextInt(7);
                int k = this.rand.nextInt(5);
                horsetype = HorseType.HORSE;
                i = j | k << 8;
            }

            livingdata = new EntityHorse.GroupData(horsetype, i);
        }

        this.setType(horsetype);
        this.setHorseVariant(i);

        if (this.rand.nextInt(5) == 0)
        {
            this.setGrowingAge(-24000);
        }

        if (horsetype.isUndead())
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());

            if (horsetype == HorseType.HORSE)
            {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
            }
            else
            {
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17499999701976776D);
            }
        }

        if (horsetype.hasMuleEars())
        {
            this.getEntityAttribute(JUMP_STRENGTH).setBaseValue(0.5D);
        }
        else
        {
            this.getEntityAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
        }

        this.setHealth(this.getMaxHealth());
        return livingdata;
    }

    /**
     * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
     * by a player and the player is holding a carrot-on-a-stick
     */
    public boolean canBeSteered()
    {
        Entity entity = this.getControllingPassenger();
        return entity instanceof EntityLivingBase;
    }

    @SideOnly(Side.CLIENT)
    public float getGrassEatingAmount(float p_110258_1_)
    {
        return this.prevHeadLean + (this.headLean - this.prevHeadLean) * p_110258_1_;
    }

    @SideOnly(Side.CLIENT)
    public float getRearingAmount(float p_110223_1_)
    {
        return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * p_110223_1_;
    }

    @SideOnly(Side.CLIENT)
    public float getMouthOpennessAngle(float p_110201_1_)
    {
        return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * p_110201_1_;
    }

    @SideOnly(Side.CLIENT)
    public void setJumpPower(int jumpPowerIn)
    {
        if (this.isHorseSaddled())
        {
            if (jumpPowerIn < 0)
            {
                jumpPowerIn = 0;
            }
            else
            {
                this.allowStandSliding = true;
                this.makeHorseRear();
            }

            if (jumpPowerIn >= 90)
            {
                this.jumpPower = 1.0F;
            }
            else
            {
                this.jumpPower = 0.4F + 0.4F * (float)jumpPowerIn / 90.0F;
            }
        }
    }

    public boolean canJump()
    {
        return this.isHorseSaddled();
    }

    public void handleStartJump(int p_184775_1_)
    {
        this.allowStandSliding = true;
        this.makeHorseRear();
    }

    public void handleStopJump()
    {
    }

    /**
     * "Spawns particles for the horse entity. par1 tells whether to spawn hearts. If it is false, it spawns smoke."
     */
    @SideOnly(Side.CLIENT)
    protected void spawnHorseParticles(boolean p_110216_1_)
    {
        EnumParticleTypes enumparticletypes = p_110216_1_ ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

        for (int i = 0; i < 7; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 7)
        {
            this.spawnHorseParticles(true);
        }
        else if (id == 6)
        {
            this.spawnHorseParticles(false);
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    public void updatePassenger(Entity passenger)
    {
        super.updatePassenger(passenger);

        if (passenger instanceof EntityLiving)
        {
            EntityLiving entityliving = (EntityLiving)passenger;
            this.renderYawOffset = entityliving.renderYawOffset;
        }

        if (this.prevRearingAmount > 0.0F)
        {
            float f3 = MathHelper.sin(this.renderYawOffset * 0.017453292F);
            float f = MathHelper.cos(this.renderYawOffset * 0.017453292F);
            float f1 = 0.7F * this.prevRearingAmount;
            float f2 = 0.15F * this.prevRearingAmount;
            passenger.setPosition(this.posX + (double)(f1 * f3), this.posY + this.getMountedYOffset() + passenger.getYOffset() + (double)f2, this.posZ - (double)(f1 * f));

            if (passenger instanceof EntityLivingBase)
            {
                ((EntityLivingBase)passenger).renderYawOffset = this.renderYawOffset;
            }
        }
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        double d0 = super.getMountedYOffset();

        if (this.getType() == HorseType.SKELETON)
        {
            d0 -= 0.1875D;
        }
        else if (this.getType() == HorseType.DONKEY)
        {
            d0 -= 0.25D;
        }

        return d0;
    }

    /**
     * Returns randomized max health
     */
    private float getModifiedMaxHealth()
    {
        return 15.0F + (float)this.rand.nextInt(8) + (float)this.rand.nextInt(9);
    }

    /**
     * Returns randomized jump strength
     */
    private double getModifiedJumpStrength()
    {
        return 0.4000000059604645D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D;
    }

    /**
     * Returns randomized movement speed
     */
    private double getModifiedMovementSpeed()
    {
        return (0.44999998807907104D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D) * 0.25D;
    }

    public boolean isSkeletonTrap()
    {
        return this.skeletonTrap;
    }

    public void setSkeletonTrap(boolean skeletonTrapIn)
    {
        if (skeletonTrapIn != this.skeletonTrap)
        {
            this.skeletonTrap = skeletonTrapIn;

            if (skeletonTrapIn)
            {
                this.tasks.addTask(1, this.skeletonTrapAI);
            }
            else
            {
                this.tasks.removeTask(this.skeletonTrapAI);
            }
        }
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return false;
    }

    public float getEyeHeight()
    {
        return this.height;
    }

    public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn)
    {
        if (inventorySlot == 499 && this.getType().canBeChested())
        {
            if (itemStackIn == null && this.isChested())
            {
                this.setChested(false);
                this.initHorseChest();
                return true;
            }

            if (itemStackIn != null && itemStackIn.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !this.isChested())
            {
                this.setChested(true);
                this.initHorseChest();
                return true;
            }
        }

        int i = inventorySlot - 400;

        if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory())
        {
            if (i == 0 && itemStackIn != null && itemStackIn.getItem() != Items.SADDLE)
            {
                return false;
            }
            else if (i != 1 || (itemStackIn == null || HorseArmorType.isHorseArmor(itemStackIn.getItem())) && this.getType().isHorse())
            {
                this.horseChest.setInventorySlotContents(i, itemStackIn);
                this.updateHorseSlots();
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            int j = inventorySlot - 500 + 2;

            if (j >= 2 && j < this.horseChest.getSizeInventory())
            {
                this.horseChest.setInventorySlotContents(j, itemStackIn);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return this.getType().isUndead() ? EnumCreatureAttribute.UNDEAD : EnumCreatureAttribute.UNDEFINED;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return this.getType().getLootTable();
    }

    public static class GroupData implements IEntityLivingData
        {
            public HorseType horseType;
            public int horseVariant;

            public GroupData(HorseType p_i46589_1_, int p_i46589_2_)
            {
                this.horseType = p_i46589_1_;
                this.horseVariant = p_i46589_2_;
            }
        }

    // FORGE
    private net.minecraftforge.items.IItemHandler itemHandler = null; // Initialized by initHorseChest above.

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) itemHandler;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}