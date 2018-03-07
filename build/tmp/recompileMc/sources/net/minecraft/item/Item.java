package net.minecraft.item;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Item extends net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl<Item>
{
    public static final RegistryNamespaced<ResourceLocation, Item> REGISTRY = net.minecraftforge.fml.common.registry.GameData.getItemRegistry();;
    private static final Map<Block, Item> BLOCK_TO_ITEM = net.minecraftforge.fml.common.registry.GameData.getBlockItemMap();
    private static final IItemPropertyGetter DAMAGED_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
        {
            return stack.isItemDamaged() ? 1.0F : 0.0F;
        }
    };
    private static final IItemPropertyGetter DAMAGE_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
        {
            return MathHelper.clamp((float)stack.getItemDamage() / (float)stack.getMaxDamage(), 0.0F, 1.0F);
        }
    };
    private static final IItemPropertyGetter LEFTHANDED_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
        {
            return entityIn != null && entityIn.getPrimaryHand() != EnumHandSide.RIGHT ? 1.0F : 0.0F;
        }
    };
    private static final IItemPropertyGetter COOLDOWN_GETTER = new IItemPropertyGetter()
    {
        @SideOnly(Side.CLIENT)
        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
        {
            return entityIn instanceof EntityPlayer ? ((EntityPlayer)entityIn).getCooldownTracker().getCooldown(stack.getItem(), 0.0F) : 0.0F;
        }
    };
    private final IRegistry<ResourceLocation, IItemPropertyGetter> properties = new RegistrySimple();
    protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private CreativeTabs tabToDisplayOn;
    /** The RNG used by the Item subclasses. */
    protected static Random itemRand = new Random();
    /** Maximum size of the stack. */
    protected int maxStackSize = 64;
    /** Maximum damage an item can handle. */
    private int maxDamage;
    /** If true, render the object in full 3D, like weapons and tools. */
    protected boolean bFull3D;
    /** Some items (like dyes) have multiple subtypes on same item, this is field define this behavior */
    protected boolean hasSubtypes;
    private Item containerItem;
    /** The unlocalized name of this item. */
    private String unlocalizedName;

    public static int getIdFromItem(Item itemIn)
    {
        return itemIn == null ? 0 : REGISTRY.getIDForObject(itemIn);
    }

    public static Item getItemById(int id)
    {
        return (Item)REGISTRY.getObjectById(id);
    }

    @Nullable
    public static Item getItemFromBlock(Block blockIn)
    {
        return (Item)BLOCK_TO_ITEM.get(blockIn);
    }

    /**
     * Tries to get an Item by it's name (e.g. minecraft:apple) or a String representation of a numerical ID. If both
     * fail, null is returned.
     */
    public static Item getByNameOrId(String id)
    {
        Item item = (Item)REGISTRY.getObject(new ResourceLocation(id));

        if (item == null)
        {
            try
            {
                return getItemById(Integer.parseInt(id));
            }
            catch (NumberFormatException var3)
            {
                ;
            }
        }

        return item;
    }

    /**
     * Creates a new override param for item models. See usage in clock, compass, elytra, etc.
     */
    public final void addPropertyOverride(ResourceLocation key, IItemPropertyGetter getter)
    {
        this.properties.putObject(key, getter);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public IItemPropertyGetter getPropertyGetter(ResourceLocation key)
    {
        return (IItemPropertyGetter)this.properties.getObject(key);
    }

    /**
     * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
     */
    public boolean updateItemStackNBT(NBTTagCompound nbt)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomProperties()
    {
        return !this.properties.getKeys().isEmpty();
    }

    public Item()
    {
        this.addPropertyOverride(new ResourceLocation("lefthanded"), LEFTHANDED_GETTER);
        this.addPropertyOverride(new ResourceLocation("cooldown"), COOLDOWN_GETTER);
    }

    public Item setMaxStackSize(int maxStackSize)
    {
        this.maxStackSize = maxStackSize;
        return this;
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return EnumActionResult.PASS;
    }

    public float getStrVsBlock(ItemStack stack, IBlockState state)
    {
        return 1.0F;
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        return new ActionResult(EnumActionResult.PASS, itemStackIn);
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    @Nullable
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        return stack;
    }

    /**
     * Returns the maximum size of the stack for a specific item. *Isn't this more a Set than a Get?*
     */
    @Deprecated // Use ItemStack sensitive version below.
    public int getItemStackLimit()
    {
        return this.maxStackSize;
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return 0;
    }

    public boolean getHasSubtypes()
    {
        return this.hasSubtypes;
    }

    public Item setHasSubtypes(boolean hasSubtypes)
    {
        this.hasSubtypes = hasSubtypes;
        return this;
    }

    /**
     * Returns the maximum damage an item can take.
     */
    @Deprecated
    public int getMaxDamage()
    {
        return this.maxDamage;
    }

    /**
     * set max damage of an Item
     */
    public Item setMaxDamage(int maxDamageIn)
    {
        this.maxDamage = maxDamageIn;

        if (maxDamageIn > 0)
        {
            this.addPropertyOverride(new ResourceLocation("damaged"), DAMAGED_GETTER);
            this.addPropertyOverride(new ResourceLocation("damage"), DAMAGE_GETTER);
        }

        return this;
    }

    public boolean isDamageable()
    {
        return this.maxDamage > 0 && (!this.hasSubtypes || this.maxStackSize == 1);
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        return false;
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        return false;
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(IBlockState blockIn)
    {
        return false;
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        return false;
    }

    /**
     * Sets bFull3D to True and return the object.
     */
    public Item setFull3D()
    {
        this.bFull3D = true;
        return this;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return this.bFull3D;
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering()
    {
        return false;
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    public Item setUnlocalizedName(String unlocalizedName)
    {
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    /**
     * Translates the unlocalized name of this item, but without the .name suffix, so the translation fails and the
     * unlocalized name itself is returned.
     */
    public String getUnlocalizedNameInefficiently(ItemStack stack)
    {
        String s = this.getUnlocalizedName(stack);
        return s == null ? "" : I18n.translateToLocal(s);
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return "item." + this.unlocalizedName;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        return "item." + this.unlocalizedName;
    }

    public Item setContainerItem(Item containerItem)
    {
        this.containerItem = containerItem;
        return this;
    }

    /**
     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
     */
    public boolean getShareTag()
    {
        return true;
    }

    public Item getContainerItem()
    {
        return this.containerItem;
    }

    /**
     * True if this Item has a container item (a.k.a. crafting result)
     */
    @Deprecated // Use ItemStack sensitive version below.
    public boolean hasContainerItem()
    {
        return this.containerItem != null;
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
    }

    /**
     * false for all Items except sub-classes of ItemMapBase
     */
    public boolean isMap()
    {
        return false;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.NONE;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 0;
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return stack.isItemEnchanted();
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack stack)
    {
        return stack.isItemEnchanted() ? EnumRarity.RARE : EnumRarity.COMMON;
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isEnchantable(ItemStack stack)
    {
        return this.getItemStackLimit(stack) == 1 && this.isDamageable();
    }

    protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids)
    {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP)
        {
            d3 = ((net.minecraft.entity.player.EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 0;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        subItems.add(new ItemStack(itemIn));
    }

    /**
     * returns this;
     */
    public Item setCreativeTab(CreativeTabs tab)
    {
        this.tabToDisplayOn = tab;
        return this;
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return this.tabToDisplayOn;
    }

    /**
     * Returns true if players can use this item to affect the world (e.g. placing blocks, placing ender eyes in portal)
     * when not in creative
     */
    public boolean canItemEditBlocks()
    {
        return false;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Deprecated // Use ItemStack sensitive version below.
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        return HashMultimap.<String, AttributeModifier>create();
    }

    /* ======================================== FORGE START =====================================*/
    /**
     * ItemStack sensitive version of getItemAttributeModifiers
     */
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        return this.getItemAttributeModifiers(slot);
    }

    /**
     * Called when a player drops the item into the world,
     * returning false from this will prevent the item from
     * being removed from the players inventory and spawning
     * in the world
     *
     * @param player The player that dropped the item
     * @param item The item stack, before the item is removed.
     */
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
        return true;
    }

    /**
     * Allow the item one last chance to modify its name used for the
     * tool highlight useful for adding something extra that can't be removed
     * by a user in the displayed name, such as a mode of operation.
     *
     * @param item the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in this method.
     */
    public String getHighlightTip( ItemStack item, String displayName )
    {
        return displayName;
    }

    /**
     * This is called when the item is used, before the block is activated.
     * @param stack The Item Stack
     * @param player The Player that used the item
     * @param world The Current World
     * @param pos Target position
     * @param side The side of the target hit
     * @param hand Which hand the item is being held in.
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        return EnumActionResult.PASS;
    }

    protected boolean canRepair = true;
    /**
     * Called by CraftingManager to determine if an item is reparable.
     * @return True if reparable
     */
    public boolean isRepairable()
    {
        return canRepair && isDamageable();
    }

    /**
     * Call to disable repair recipes.
     * @return The current Item instance
     */
    public Item setNoRepair()
    {
        canRepair = false;
        return this;
    }

    /**
     * Override this method to change the NBT data being sent to the client.
     * You should ONLY override this when you have no other choice, as this might change behavior client side!
     *
     * @param stack The stack to send the NBT tag for
     * @return The NBT tag
     */
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        return stack.getTagCompound();
    }

    /**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param pos Block's position in world
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called each tick while using an item.
     * @param stack The Item being used
     * @param player The Player using the item
     * @param count The amount of time in tick the item has been used for continuously
     */
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
    }

    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }

    /**
     * ItemStack sensitive version of getContainerItem.
     * Returns a full ItemStack instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!hasContainerItem(itemStack))
        {
            return null;
        }
        return new ItemStack(getContainerItem());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    public boolean hasContainerItem(ItemStack stack)
    {
        return hasContainerItem();
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground as a EntityItem.
     * This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param world The world the entity is in
     * @return The normal lifespan in ticks.
     */
    public int getEntityLifespan(ItemStack itemStack, World world)
    {
        return 6000;
    }

    /**
     * Determines if this Item has a special entity for when they are in the world.
     * Is called when a EntityItem is spawned in the world, if true and Item#createCustomEntity
     * returns non null, the EntityItem will be destroyed and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true, Item#createCustomEntity will be called
     */
    public boolean hasCustomEntity(ItemStack stack)
    {
        return false;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to function normally.
     * Called when the item it placed in a world.
     *
     * @param world The world object
     * @param location The EntityItem object, useful for getting the position of the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        return null;
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing for cleaner
     * control over the update of the item without having to write a subclass.
     *
     * @param entityItem The entity Item
     * @return Return true to skip any further update code.
     */
    public boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem entityItem)
    {
        return false;
    }

    /**
     * Gets a list of tabs that items belonging to this class can display on,
     * combined properly with getSubItems allows for a single item to span
     * many sub-items across many tabs.
     *
     * @return A list of all tabs that this item could possibly be one.
     */
    public CreativeTabs[] getCreativeTabs()
    {
        return new CreativeTabs[]{ getCreativeTab() };
    }

    /**
     * Determines the base experience for a player when they remove this item from a furnace slot.
     * This number must be between 0 and 1 for it to be valid.
     * This number will be multiplied by the stack size to get the total experience.
     *
     * @param item The item stack the player is picking up.
     * @return The amount to award for each item.
     */
    public float getSmeltingExperience(ItemStack item)
    {
        return -1; //-1 will default to the old lookups.
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
     *
     * @param world The world
     * @param pos Block position in world
     * @param player The Player that is wielding the item
     * @return
     */
    public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){}

    /**
     * Determines if the specific ItemStack can be placed in the specified armor slot.
     *
     * @param stack The ItemStack
     * @param armorType Armor slot ID: 0: Helmet, 1: Chest, 2: Legs, 3: Boots
     * @param entity The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        return net.minecraft.entity.EntityLiving.getSlotForItemStack(stack) == armorType;
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book The book
     * @return if the enchantment is allowed
     */
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return true;
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that
     * should be use for the currently equipped item.
     * This will only be called on instances of ItemArmor.
     *
     * Returning null from this function will use the default value.
     *
     * @param stack ItemStack for the equipped armor
     * @param entity The entity wearing the armor
     * @param slot The slot the armor is in
     * @param type The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        return null;
    }

    /**
     * Returns the font renderer used to render tooltips and overlays for this item.
     * Returning null will use the standard font renderer.
     *
     * @param stack The current item stack
     * @return A instance of FontRenderer or null to use default
     */
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack)
    {
        return null;
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     *
     * @param  entityLiving  The entity wearing the armor
     * @param  itemStack  The itemStack to render the model of
     * @param  armorSlot  The slot the armor is in
     * @param _default Original armor model. Will have attributes set.
     * @return  A ModelBiped to render instead of the default
     */
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default)
    {
        return null;
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *
     * @param entityLiving The entity swinging the item.
     * @param stack The Item stack
     * @return True to cancel any further processing by EntityLiving
     */
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return false;
    }

    /**
     * Called when the client starts rendering the HUD, for whatever item the player currently has as a helmet.
     * This is where pumpkins would render there overlay.
     *
     * @param stack The ItemStack that is equipped
     * @param player Reference to the current client entity
     * @param resolution Resolution information about the current viewport and configured GUI Scale
     * @param partialTicks Partial ticks for the renderer, useful for interpolation
     */
    @SideOnly(Side.CLIENT)
    public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, net.minecraft.client.gui.ScaledResolution resolution, float partialTicks){}

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the itemDamage field on ItemStack, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    public int getDamage(ItemStack stack)
    {
        return stack.itemDamage;
    }

    /**
     * This used to be 'display damage' but its really just 'aux' data in the ItemStack, usually shares the same variable as damage.
     * @param stack
     * @return
     */
    public int getMetadata(ItemStack stack)
    {
        return stack.itemDamage;
    }

    /**
     * Determines if the durability bar should be rendered for this item.
     * Defaults to vanilla stack.isDamaged behavior.
     * But modders can use this for any data they wish.
     *
     * @param stack The current Item Stack
     * @return True if it should render the 'durability' bar.
     */
    public boolean showDurabilityBar(ItemStack stack)
    {
        return stack.isItemDamaged();
    }

    /**
     * Queries the percentage of the 'Durability' bar that should be drawn.
     *
     * @param stack The current ItemStack
     * @return 1.0 for 100% 0 for 0%
     */
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (double)stack.getItemDamage() / (double)stack.getMaxDamage();
    }

    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in this item,
     * but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    public int getMaxDamage(ItemStack stack)
    {
        return getMaxDamage();
    }

    /**
     * Return if this itemstack is damaged. Note only called if {@link #isDamageable()} is true.
     * @param stack the stack
     * @return if the stack is damaged
     */
    public boolean isDamaged(ItemStack stack)
    {
        return stack.itemDamage > 0;
    }

    /**
     * Set the damage for this itemstack. Note, this method is responsible for zero checking.
     * @param stack the stack
     * @param damage the new damage value
     */
    public void setDamage(ItemStack stack, int damage)
    {
        stack.itemDamage = damage;

        if (stack.itemDamage < 0)
        {
            stack.itemDamage = 0;
        }
    }

    /**
     * ItemStack sensitive version of {@link #canHarvestBlock(IBlockState)}
     * @param state The block trying to harvest
     * @param stack The itemstack used to harvest the block
     * @return true if can harvest the block
     */
    public boolean canHarvestBlock(IBlockState state, ItemStack stack)
    {
        return canHarvestBlock(state);
    }

    /**
     * Gets the maximum number of items that this stack should be able to hold.
     * This is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
     *
     * @param stack The ItemStack
     * @return The maximum number this item can be stacked to
     */
    public int getItemStackLimit(ItemStack stack)
    {
        return this.getItemStackLimit();
    }

    private java.util.Map<String, Integer> toolClasses = new java.util.HashMap<String, Integer>();
    /**
     * Sets or removes the harvest level for the specified tool class.
     *
     * @param toolClass Class
     * @param level Harvest level:
     *     Wood:    0
     *     Stone:   1
     *     Iron:    2
     *     Diamond: 3
     *     Gold:    0
     */
    public void setHarvestLevel(String toolClass, int level)
    {
        if (level < 0)
            toolClasses.remove(toolClass);
        else
            toolClasses.put(toolClass, level);
    }

    public java.util.Set<String> getToolClasses(ItemStack stack)
    {
        return toolClasses.keySet();
    }

    /**
     * Deprecated, Use the position aware variant instead
     */
    @Deprecated
    public int getHarvestLevel(ItemStack stack, String toolClass)
    {
        Integer ret = toolClasses.get(toolClass);
        return ret == null ? -1 : ret;
    }

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack This item stack instance
     * @param toolClass Tool Class
     * @param player The player trying to harvest the given blockstate
     * @param blockState The block to harvest
     * @return Harvest level, or -1 if not the specified tool type.
     */
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState)
    {
        return getHarvestLevel(stack, toolClass);
    }

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @param stack The ItemStack
     * @return the item echantability value
     */
    public int getItemEnchantability(ItemStack stack)
    {
        return getItemEnchantability();
    }

    /**
     * Whether this Item can be used as a payment to activate the vanilla beacon.
     * @param stack the ItemStack
     * @return true if this Item can be used
     */
    public boolean isBeaconPayment(ItemStack stack)
    {
        return this == Items.EMERALD || this == Items.DIAMOND || this == Items.GOLD_INGOT || this == Items.IRON_INGOT;
    }

    /**
     * Determine if the player switching between these two item stacks
     * @param oldStack The old stack that was equipped
     * @param newStack The new stack
     * @param slotChanged If the current equipped slot was changed,
     *                    Vanilla does not play the animation if you switch between two
     *                    slots that hold the exact same item.
     * @return True to play the item change animation
     */
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return !oldStack.equals(newStack); //!ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    /**
     * Called when the player is mining a block and the item in his hand changes.
     * Allows to not reset blockbreaking if only NBT or similar changes.
     * @param oldStack The old stack that was used for mining. Item in players main hand
     * @param newStack The new stack
     * @return True to reset block break progress
     */
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
    {
        return !(newStack.getItem() == oldStack.getItem() && ItemStack.areItemStackTagsEqual(newStack, oldStack) && (newStack.isItemStackDamageable() || newStack.getMetadata() == oldStack.getMetadata()));
    }

    /**
     * Called from ItemStack.setItem, will hold extra data for the life of this ItemStack.
     * Can be retrieved from stack.getCapabilities()
     * The NBT can be null if this is not called from readNBT or if the item the stack is
     * changing FROM is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem() to see the OLD item.
     * Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold capabilities for the life of this item.
     */
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return null;
    }

    public com.google.common.collect.ImmutableMap<String, net.minecraftforge.common.animation.ITimeValue> getAnimationParameters(final ItemStack stack, final World world, final EntityLivingBase entity)
    {
        com.google.common.collect.ImmutableMap.Builder<String, net.minecraftforge.common.animation.ITimeValue> builder = com.google.common.collect.ImmutableMap.builder();
        for(ResourceLocation location : properties.getKeys())
        {
            final IItemPropertyGetter parameter = properties.getObject(location);
            builder.put(location.toString(), new net.minecraftforge.common.animation.ITimeValue()
            {
                public float apply(float input)
                {
                    return parameter.apply(stack, world, entity);
                }
            });
        }
        return builder.build();
    }

    /* ======================================== FORGE END   =====================================*/

    public static void registerItems()
    {
        registerItemBlock(Blocks.STONE, (new ItemMultiTexture(Blocks.STONE, Blocks.STONE, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockStone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("stone"));
        registerItemBlock(Blocks.GRASS, new ItemColored(Blocks.GRASS, false));
        registerItemBlock(Blocks.DIRT, (new ItemMultiTexture(Blocks.DIRT, Blocks.DIRT, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockDirt.DirtType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("dirt"));
        registerItemBlock(Blocks.COBBLESTONE);
        registerItemBlock(Blocks.PLANKS, (new ItemMultiTexture(Blocks.PLANKS, Blocks.PLANKS, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("wood"));
        registerItemBlock(Blocks.SAPLING, (new ItemMultiTexture(Blocks.SAPLING, Blocks.SAPLING, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("sapling"));
        registerItemBlock(Blocks.BEDROCK);
        registerItemBlock(Blocks.SAND, (new ItemMultiTexture(Blocks.SAND, Blocks.SAND, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockSand.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("sand"));
        registerItemBlock(Blocks.GRAVEL);
        registerItemBlock(Blocks.GOLD_ORE);
        registerItemBlock(Blocks.IRON_ORE);
        registerItemBlock(Blocks.COAL_ORE);
        registerItemBlock(Blocks.LOG, (new ItemMultiTexture(Blocks.LOG, Blocks.LOG, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("log"));
        registerItemBlock(Blocks.LOG2, (new ItemMultiTexture(Blocks.LOG2, Blocks.LOG2, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata() + 4).getUnlocalizedName();
            }
        })).setUnlocalizedName("log"));
        registerItemBlock(Blocks.LEAVES, (new ItemLeaves(Blocks.LEAVES)).setUnlocalizedName("leaves"));
        registerItemBlock(Blocks.LEAVES2, (new ItemLeaves(Blocks.LEAVES2)).setUnlocalizedName("leaves"));
        registerItemBlock(Blocks.SPONGE, (new ItemMultiTexture(Blocks.SPONGE, Blocks.SPONGE, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return (p_apply_1_.getMetadata() & 1) == 1 ? "wet" : "dry";
            }
        })).setUnlocalizedName("sponge"));
        registerItemBlock(Blocks.GLASS);
        registerItemBlock(Blocks.LAPIS_ORE);
        registerItemBlock(Blocks.LAPIS_BLOCK);
        registerItemBlock(Blocks.DISPENSER);
        registerItemBlock(Blocks.SANDSTONE, (new ItemMultiTexture(Blocks.SANDSTONE, Blocks.SANDSTONE, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockSandStone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("sandStone"));
        registerItemBlock(Blocks.NOTEBLOCK);
        registerItemBlock(Blocks.GOLDEN_RAIL);
        registerItemBlock(Blocks.DETECTOR_RAIL);
        registerItemBlock(Blocks.STICKY_PISTON, new ItemPiston(Blocks.STICKY_PISTON));
        registerItemBlock(Blocks.WEB);
        registerItemBlock(Blocks.TALLGRASS, (new ItemColored(Blocks.TALLGRASS, true)).setSubtypeNames(new String[] {"shrub", "grass", "fern"}));
        registerItemBlock(Blocks.DEADBUSH);
        registerItemBlock(Blocks.PISTON, new ItemPiston(Blocks.PISTON));
        registerItemBlock(Blocks.WOOL, (new ItemCloth(Blocks.WOOL)).setUnlocalizedName("cloth"));
        registerItemBlock(Blocks.YELLOW_FLOWER, (new ItemMultiTexture(Blocks.YELLOW_FLOWER, Blocks.YELLOW_FLOWER, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.YELLOW, p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("flower"));
        registerItemBlock(Blocks.RED_FLOWER, (new ItemMultiTexture(Blocks.RED_FLOWER, Blocks.RED_FLOWER, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("rose"));
        registerItemBlock(Blocks.BROWN_MUSHROOM);
        registerItemBlock(Blocks.RED_MUSHROOM);
        registerItemBlock(Blocks.GOLD_BLOCK);
        registerItemBlock(Blocks.IRON_BLOCK);
        registerItemBlock(Blocks.STONE_SLAB, (new ItemSlab(Blocks.STONE_SLAB, Blocks.STONE_SLAB, Blocks.DOUBLE_STONE_SLAB)).setUnlocalizedName("stoneSlab"));
        registerItemBlock(Blocks.BRICK_BLOCK);
        registerItemBlock(Blocks.TNT);
        registerItemBlock(Blocks.BOOKSHELF);
        registerItemBlock(Blocks.MOSSY_COBBLESTONE);
        registerItemBlock(Blocks.OBSIDIAN);
        registerItemBlock(Blocks.TORCH);
        registerItemBlock(Blocks.END_ROD);
        registerItemBlock(Blocks.CHORUS_PLANT);
        registerItemBlock(Blocks.CHORUS_FLOWER);
        registerItemBlock(Blocks.PURPUR_BLOCK);
        registerItemBlock(Blocks.PURPUR_PILLAR);
        registerItemBlock(Blocks.PURPUR_STAIRS);
        registerItemBlock(Blocks.PURPUR_SLAB, (new ItemSlab(Blocks.PURPUR_SLAB, Blocks.PURPUR_SLAB, Blocks.PURPUR_DOUBLE_SLAB)).setUnlocalizedName("purpurSlab"));
        registerItemBlock(Blocks.MOB_SPAWNER);
        registerItemBlock(Blocks.OAK_STAIRS);
        registerItemBlock(Blocks.CHEST);
        registerItemBlock(Blocks.DIAMOND_ORE);
        registerItemBlock(Blocks.DIAMOND_BLOCK);
        registerItemBlock(Blocks.CRAFTING_TABLE);
        registerItemBlock(Blocks.FARMLAND);
        registerItemBlock(Blocks.FURNACE);
        registerItemBlock(Blocks.LADDER);
        registerItemBlock(Blocks.RAIL);
        registerItemBlock(Blocks.STONE_STAIRS);
        registerItemBlock(Blocks.LEVER);
        registerItemBlock(Blocks.STONE_PRESSURE_PLATE);
        registerItemBlock(Blocks.WOODEN_PRESSURE_PLATE);
        registerItemBlock(Blocks.REDSTONE_ORE);
        registerItemBlock(Blocks.REDSTONE_TORCH);
        registerItemBlock(Blocks.STONE_BUTTON);
        registerItemBlock(Blocks.SNOW_LAYER, new ItemSnow(Blocks.SNOW_LAYER));
        registerItemBlock(Blocks.ICE);
        registerItemBlock(Blocks.SNOW);
        registerItemBlock(Blocks.CACTUS);
        registerItemBlock(Blocks.CLAY);
        registerItemBlock(Blocks.JUKEBOX);
        registerItemBlock(Blocks.OAK_FENCE);
        registerItemBlock(Blocks.SPRUCE_FENCE);
        registerItemBlock(Blocks.BIRCH_FENCE);
        registerItemBlock(Blocks.JUNGLE_FENCE);
        registerItemBlock(Blocks.DARK_OAK_FENCE);
        registerItemBlock(Blocks.ACACIA_FENCE);
        registerItemBlock(Blocks.PUMPKIN);
        registerItemBlock(Blocks.NETHERRACK);
        registerItemBlock(Blocks.SOUL_SAND);
        registerItemBlock(Blocks.GLOWSTONE);
        registerItemBlock(Blocks.LIT_PUMPKIN);
        registerItemBlock(Blocks.TRAPDOOR);
        registerItemBlock(Blocks.MONSTER_EGG, (new ItemMultiTexture(Blocks.MONSTER_EGG, Blocks.MONSTER_EGG, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockSilverfish.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("monsterStoneEgg"));
        registerItemBlock(Blocks.STONEBRICK, (new ItemMultiTexture(Blocks.STONEBRICK, Blocks.STONEBRICK, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockStoneBrick.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("stonebricksmooth"));
        registerItemBlock(Blocks.BROWN_MUSHROOM_BLOCK);
        registerItemBlock(Blocks.RED_MUSHROOM_BLOCK);
        registerItemBlock(Blocks.IRON_BARS);
        registerItemBlock(Blocks.GLASS_PANE);
        registerItemBlock(Blocks.MELON_BLOCK);
        registerItemBlock(Blocks.VINE, new ItemColored(Blocks.VINE, false));
        registerItemBlock(Blocks.OAK_FENCE_GATE);
        registerItemBlock(Blocks.SPRUCE_FENCE_GATE);
        registerItemBlock(Blocks.BIRCH_FENCE_GATE);
        registerItemBlock(Blocks.JUNGLE_FENCE_GATE);
        registerItemBlock(Blocks.DARK_OAK_FENCE_GATE);
        registerItemBlock(Blocks.ACACIA_FENCE_GATE);
        registerItemBlock(Blocks.BRICK_STAIRS);
        registerItemBlock(Blocks.STONE_BRICK_STAIRS);
        registerItemBlock(Blocks.MYCELIUM);
        registerItemBlock(Blocks.WATERLILY, new ItemLilyPad(Blocks.WATERLILY));
        registerItemBlock(Blocks.NETHER_BRICK);
        registerItemBlock(Blocks.NETHER_BRICK_FENCE);
        registerItemBlock(Blocks.NETHER_BRICK_STAIRS);
        registerItemBlock(Blocks.ENCHANTING_TABLE);
        registerItemBlock(Blocks.END_PORTAL_FRAME);
        registerItemBlock(Blocks.END_STONE);
        registerItemBlock(Blocks.END_BRICKS);
        registerItemBlock(Blocks.DRAGON_EGG);
        registerItemBlock(Blocks.REDSTONE_LAMP);
        registerItemBlock(Blocks.WOODEN_SLAB, (new ItemSlab(Blocks.WOODEN_SLAB, Blocks.WOODEN_SLAB, Blocks.DOUBLE_WOODEN_SLAB)).setUnlocalizedName("woodSlab"));
        registerItemBlock(Blocks.SANDSTONE_STAIRS);
        registerItemBlock(Blocks.EMERALD_ORE);
        registerItemBlock(Blocks.ENDER_CHEST);
        registerItemBlock(Blocks.TRIPWIRE_HOOK);
        registerItemBlock(Blocks.EMERALD_BLOCK);
        registerItemBlock(Blocks.SPRUCE_STAIRS);
        registerItemBlock(Blocks.BIRCH_STAIRS);
        registerItemBlock(Blocks.JUNGLE_STAIRS);
        registerItemBlock(Blocks.COMMAND_BLOCK);
        registerItemBlock(Blocks.BEACON);
        registerItemBlock(Blocks.COBBLESTONE_WALL, (new ItemMultiTexture(Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockWall.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("cobbleWall"));
        registerItemBlock(Blocks.WOODEN_BUTTON);
        registerItemBlock(Blocks.ANVIL, (new ItemAnvilBlock(Blocks.ANVIL)).setUnlocalizedName("anvil"));
        registerItemBlock(Blocks.TRAPPED_CHEST);
        registerItemBlock(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        registerItemBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        registerItemBlock(Blocks.DAYLIGHT_DETECTOR);
        registerItemBlock(Blocks.REDSTONE_BLOCK);
        registerItemBlock(Blocks.QUARTZ_ORE);
        registerItemBlock(Blocks.HOPPER);
        registerItemBlock(Blocks.QUARTZ_BLOCK, (new ItemMultiTexture(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, new String[] {"default", "chiseled", "lines"})).setUnlocalizedName("quartzBlock"));
        registerItemBlock(Blocks.QUARTZ_STAIRS);
        registerItemBlock(Blocks.ACTIVATOR_RAIL);
        registerItemBlock(Blocks.DROPPER);
        registerItemBlock(Blocks.STAINED_HARDENED_CLAY, (new ItemCloth(Blocks.STAINED_HARDENED_CLAY)).setUnlocalizedName("clayHardenedStained"));
        registerItemBlock(Blocks.BARRIER);
        registerItemBlock(Blocks.IRON_TRAPDOOR);
        registerItemBlock(Blocks.HAY_BLOCK);
        registerItemBlock(Blocks.CARPET, (new ItemCloth(Blocks.CARPET)).setUnlocalizedName("woolCarpet"));
        registerItemBlock(Blocks.HARDENED_CLAY);
        registerItemBlock(Blocks.COAL_BLOCK);
        registerItemBlock(Blocks.PACKED_ICE);
        registerItemBlock(Blocks.ACACIA_STAIRS);
        registerItemBlock(Blocks.DARK_OAK_STAIRS);
        registerItemBlock(Blocks.SLIME_BLOCK);
        registerItemBlock(Blocks.GRASS_PATH);
        registerItemBlock(Blocks.DOUBLE_PLANT, (new ItemMultiTexture(Blocks.DOUBLE_PLANT, Blocks.DOUBLE_PLANT, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockDoublePlant.EnumPlantType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("doublePlant"));
        registerItemBlock(Blocks.STAINED_GLASS, (new ItemCloth(Blocks.STAINED_GLASS)).setUnlocalizedName("stainedGlass"));
        registerItemBlock(Blocks.STAINED_GLASS_PANE, (new ItemCloth(Blocks.STAINED_GLASS_PANE)).setUnlocalizedName("stainedGlassPane"));
        registerItemBlock(Blocks.PRISMARINE, (new ItemMultiTexture(Blocks.PRISMARINE, Blocks.PRISMARINE, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockPrismarine.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("prismarine"));
        registerItemBlock(Blocks.SEA_LANTERN);
        registerItemBlock(Blocks.RED_SANDSTONE, (new ItemMultiTexture(Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE, new Function<ItemStack, String>()
        {
            @Nullable
            public String apply(@Nullable ItemStack p_apply_1_)
            {
                return BlockRedSandstone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
            }
        })).setUnlocalizedName("redSandStone"));
        registerItemBlock(Blocks.RED_SANDSTONE_STAIRS);
        registerItemBlock(Blocks.STONE_SLAB2, (new ItemSlab(Blocks.STONE_SLAB2, Blocks.STONE_SLAB2, Blocks.DOUBLE_STONE_SLAB2)).setUnlocalizedName("stoneSlab2"));
        registerItemBlock(Blocks.REPEATING_COMMAND_BLOCK);
        registerItemBlock(Blocks.CHAIN_COMMAND_BLOCK);
        registerItemBlock(Blocks.MAGMA);
        registerItemBlock(Blocks.NETHER_WART_BLOCK);
        registerItemBlock(Blocks.RED_NETHER_BRICK);
        registerItemBlock(Blocks.BONE_BLOCK);
        registerItemBlock(Blocks.STRUCTURE_VOID);
        registerItemBlock(Blocks.STRUCTURE_BLOCK);
        registerItem(256, "iron_shovel", (new ItemSpade(Item.ToolMaterial.IRON)).setUnlocalizedName("shovelIron"));
        registerItem(257, "iron_pickaxe", (new ItemPickaxe(Item.ToolMaterial.IRON)).setUnlocalizedName("pickaxeIron"));
        registerItem(258, "iron_axe", (new ItemAxe(Item.ToolMaterial.IRON)).setUnlocalizedName("hatchetIron"));
        registerItem(259, "flint_and_steel", (new ItemFlintAndSteel()).setUnlocalizedName("flintAndSteel"));
        registerItem(260, "apple", (new ItemFood(4, 0.3F, false)).setUnlocalizedName("apple"));
        registerItem(261, "bow", (new ItemBow()).setUnlocalizedName("bow"));
        registerItem(262, "arrow", (new ItemArrow()).setUnlocalizedName("arrow"));
        registerItem(263, "coal", (new ItemCoal()).setUnlocalizedName("coal"));
        registerItem(264, "diamond", (new Item()).setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(265, "iron_ingot", (new Item()).setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(266, "gold_ingot", (new Item()).setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(267, "iron_sword", (new ItemSword(Item.ToolMaterial.IRON)).setUnlocalizedName("swordIron"));
        registerItem(268, "wooden_sword", (new ItemSword(Item.ToolMaterial.WOOD)).setUnlocalizedName("swordWood"));
        registerItem(269, "wooden_shovel", (new ItemSpade(Item.ToolMaterial.WOOD)).setUnlocalizedName("shovelWood"));
        registerItem(270, "wooden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.WOOD)).setUnlocalizedName("pickaxeWood"));
        registerItem(271, "wooden_axe", (new ItemAxe(Item.ToolMaterial.WOOD)).setUnlocalizedName("hatchetWood"));
        registerItem(272, "stone_sword", (new ItemSword(Item.ToolMaterial.STONE)).setUnlocalizedName("swordStone"));
        registerItem(273, "stone_shovel", (new ItemSpade(Item.ToolMaterial.STONE)).setUnlocalizedName("shovelStone"));
        registerItem(274, "stone_pickaxe", (new ItemPickaxe(Item.ToolMaterial.STONE)).setUnlocalizedName("pickaxeStone"));
        registerItem(275, "stone_axe", (new ItemAxe(Item.ToolMaterial.STONE)).setUnlocalizedName("hatchetStone"));
        registerItem(276, "diamond_sword", (new ItemSword(Item.ToolMaterial.DIAMOND)).setUnlocalizedName("swordDiamond"));
        registerItem(277, "diamond_shovel", (new ItemSpade(Item.ToolMaterial.DIAMOND)).setUnlocalizedName("shovelDiamond"));
        registerItem(278, "diamond_pickaxe", (new ItemPickaxe(Item.ToolMaterial.DIAMOND)).setUnlocalizedName("pickaxeDiamond"));
        registerItem(279, "diamond_axe", (new ItemAxe(Item.ToolMaterial.DIAMOND)).setUnlocalizedName("hatchetDiamond"));
        registerItem(280, "stick", (new Item()).setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(281, "bowl", (new Item()).setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(282, "mushroom_stew", (new ItemSoup(6)).setUnlocalizedName("mushroomStew"));
        registerItem(283, "golden_sword", (new ItemSword(Item.ToolMaterial.GOLD)).setUnlocalizedName("swordGold"));
        registerItem(284, "golden_shovel", (new ItemSpade(Item.ToolMaterial.GOLD)).setUnlocalizedName("shovelGold"));
        registerItem(285, "golden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.GOLD)).setUnlocalizedName("pickaxeGold"));
        registerItem(286, "golden_axe", (new ItemAxe(Item.ToolMaterial.GOLD)).setUnlocalizedName("hatchetGold"));
        registerItem(287, "string", (new ItemBlockSpecial(Blocks.TRIPWIRE)).setUnlocalizedName("string").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(288, "feather", (new Item()).setUnlocalizedName("feather").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(289, "gunpowder", (new Item()).setUnlocalizedName("sulphur").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(290, "wooden_hoe", (new ItemHoe(Item.ToolMaterial.WOOD)).setUnlocalizedName("hoeWood"));
        registerItem(291, "stone_hoe", (new ItemHoe(Item.ToolMaterial.STONE)).setUnlocalizedName("hoeStone"));
        registerItem(292, "iron_hoe", (new ItemHoe(Item.ToolMaterial.IRON)).setUnlocalizedName("hoeIron"));
        registerItem(293, "diamond_hoe", (new ItemHoe(Item.ToolMaterial.DIAMOND)).setUnlocalizedName("hoeDiamond"));
        registerItem(294, "golden_hoe", (new ItemHoe(Item.ToolMaterial.GOLD)).setUnlocalizedName("hoeGold"));
        registerItem(295, "wheat_seeds", (new ItemSeeds(Blocks.WHEAT, Blocks.FARMLAND)).setUnlocalizedName("seeds"));
        registerItem(296, "wheat", (new Item()).setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(297, "bread", (new ItemFood(5, 0.6F, false)).setUnlocalizedName("bread"));
        registerItem(298, "leather_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD)).setUnlocalizedName("helmetCloth"));
        registerItem(299, "leather_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST)).setUnlocalizedName("chestplateCloth"));
        registerItem(300, "leather_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.LEGS)).setUnlocalizedName("leggingsCloth"));
        registerItem(301, "leather_boots", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET)).setUnlocalizedName("bootsCloth"));
        registerItem(302, "chainmail_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.HEAD)).setUnlocalizedName("helmetChain"));
        registerItem(303, "chainmail_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.CHEST)).setUnlocalizedName("chestplateChain"));
        registerItem(304, "chainmail_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.LEGS)).setUnlocalizedName("leggingsChain"));
        registerItem(305, "chainmail_boots", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, EntityEquipmentSlot.FEET)).setUnlocalizedName("bootsChain"));
        registerItem(306, "iron_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.HEAD)).setUnlocalizedName("helmetIron"));
        registerItem(307, "iron_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.CHEST)).setUnlocalizedName("chestplateIron"));
        registerItem(308, "iron_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.LEGS)).setUnlocalizedName("leggingsIron"));
        registerItem(309, "iron_boots", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, EntityEquipmentSlot.FEET)).setUnlocalizedName("bootsIron"));
        registerItem(310, "diamond_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.HEAD)).setUnlocalizedName("helmetDiamond"));
        registerItem(311, "diamond_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.CHEST)).setUnlocalizedName("chestplateDiamond"));
        registerItem(312, "diamond_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.LEGS)).setUnlocalizedName("leggingsDiamond"));
        registerItem(313, "diamond_boots", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.FEET)).setUnlocalizedName("bootsDiamond"));
        registerItem(314, "golden_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.HEAD)).setUnlocalizedName("helmetGold"));
        registerItem(315, "golden_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.CHEST)).setUnlocalizedName("chestplateGold"));
        registerItem(316, "golden_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.LEGS)).setUnlocalizedName("leggingsGold"));
        registerItem(317, "golden_boots", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, EntityEquipmentSlot.FEET)).setUnlocalizedName("bootsGold"));
        registerItem(318, "flint", (new Item()).setUnlocalizedName("flint").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(319, "porkchop", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("porkchopRaw"));
        registerItem(320, "cooked_porkchop", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("porkchopCooked"));
        registerItem(321, "painting", (new ItemHangingEntity(EntityPainting.class)).setUnlocalizedName("painting"));
        registerItem(322, "golden_apple", (new ItemAppleGold(4, 1.2F, false)).setAlwaysEdible().setUnlocalizedName("appleGold"));
        registerItem(323, "sign", (new ItemSign()).setUnlocalizedName("sign"));
        registerItem(324, "wooden_door", (new ItemDoor(Blocks.OAK_DOOR)).setUnlocalizedName("doorOak"));
        Item item = (new ItemBucket(Blocks.AIR)).setUnlocalizedName("bucket").setMaxStackSize(16);
        registerItem(325, "bucket", item);
        registerItem(326, "water_bucket", (new ItemBucket(Blocks.FLOWING_WATER)).setUnlocalizedName("bucketWater").setContainerItem(item));
        registerItem(327, "lava_bucket", (new ItemBucket(Blocks.FLOWING_LAVA)).setUnlocalizedName("bucketLava").setContainerItem(item));
        registerItem(328, "minecart", (new ItemMinecart(EntityMinecart.Type.RIDEABLE)).setUnlocalizedName("minecart"));
        registerItem(329, "saddle", (new ItemSaddle()).setUnlocalizedName("saddle"));
        registerItem(330, "iron_door", (new ItemDoor(Blocks.IRON_DOOR)).setUnlocalizedName("doorIron"));
        registerItem(331, "redstone", (new ItemRedstone()).setUnlocalizedName("redstone"));
        registerItem(332, "snowball", (new ItemSnowball()).setUnlocalizedName("snowball"));
        registerItem(333, "boat", new ItemBoat(EntityBoat.Type.OAK));
        registerItem(334, "leather", (new Item()).setUnlocalizedName("leather").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(335, "milk_bucket", (new ItemBucketMilk()).setUnlocalizedName("milk").setContainerItem(item));
        registerItem(336, "brick", (new Item()).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(337, "clay_ball", (new Item()).setUnlocalizedName("clay").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(338, "reeds", (new ItemBlockSpecial(Blocks.REEDS)).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(339, "paper", (new Item()).setUnlocalizedName("paper").setCreativeTab(CreativeTabs.MISC));
        registerItem(340, "book", (new ItemBook()).setUnlocalizedName("book").setCreativeTab(CreativeTabs.MISC));
        registerItem(341, "slime_ball", (new Item()).setUnlocalizedName("slimeball").setCreativeTab(CreativeTabs.MISC));
        registerItem(342, "chest_minecart", (new ItemMinecart(EntityMinecart.Type.CHEST)).setUnlocalizedName("minecartChest"));
        registerItem(343, "furnace_minecart", (new ItemMinecart(EntityMinecart.Type.FURNACE)).setUnlocalizedName("minecartFurnace"));
        registerItem(344, "egg", (new ItemEgg()).setUnlocalizedName("egg"));
        registerItem(345, "compass", (new ItemCompass()).setUnlocalizedName("compass").setCreativeTab(CreativeTabs.TOOLS));
        registerItem(346, "fishing_rod", (new ItemFishingRod()).setUnlocalizedName("fishingRod"));
        registerItem(347, "clock", (new ItemClock()).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.TOOLS));
        registerItem(348, "glowstone_dust", (new Item()).setUnlocalizedName("yellowDust").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(349, "fish", (new ItemFishFood(false)).setUnlocalizedName("fish").setHasSubtypes(true));
        registerItem(350, "cooked_fish", (new ItemFishFood(true)).setUnlocalizedName("fish").setHasSubtypes(true));
        registerItem(351, "dye", (new ItemDye()).setUnlocalizedName("dyePowder"));
        registerItem(352, "bone", (new Item()).setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.MISC));
        registerItem(353, "sugar", (new Item()).setUnlocalizedName("sugar").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(354, "cake", (new ItemBlockSpecial(Blocks.CAKE)).setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.FOOD));
        registerItem(355, "bed", (new ItemBed()).setMaxStackSize(1).setUnlocalizedName("bed"));
        registerItem(356, "repeater", (new ItemBlockSpecial(Blocks.UNPOWERED_REPEATER)).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.REDSTONE));
        registerItem(357, "cookie", (new ItemFood(2, 0.1F, false)).setUnlocalizedName("cookie"));
        registerItem(358, "filled_map", (new ItemMap()).setUnlocalizedName("map"));
        registerItem(359, "shears", (new ItemShears()).setUnlocalizedName("shears"));
        registerItem(360, "melon", (new ItemFood(2, 0.3F, false)).setUnlocalizedName("melon"));
        registerItem(361, "pumpkin_seeds", (new ItemSeeds(Blocks.PUMPKIN_STEM, Blocks.FARMLAND)).setUnlocalizedName("seeds_pumpkin"));
        registerItem(362, "melon_seeds", (new ItemSeeds(Blocks.MELON_STEM, Blocks.FARMLAND)).setUnlocalizedName("seeds_melon"));
        registerItem(363, "beef", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("beefRaw"));
        registerItem(364, "cooked_beef", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("beefCooked"));
        registerItem(365, "chicken", (new ItemFood(2, 0.3F, true)).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F).setUnlocalizedName("chickenRaw"));
        registerItem(366, "cooked_chicken", (new ItemFood(6, 0.6F, true)).setUnlocalizedName("chickenCooked"));
        registerItem(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.8F).setUnlocalizedName("rottenFlesh"));
        registerItem(368, "ender_pearl", (new ItemEnderPearl()).setUnlocalizedName("enderPearl"));
        registerItem(369, "blaze_rod", (new Item()).setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.MATERIALS).setFull3D());
        registerItem(370, "ghast_tear", (new Item()).setUnlocalizedName("ghastTear").setCreativeTab(CreativeTabs.BREWING));
        registerItem(371, "gold_nugget", (new Item()).setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(372, "nether_wart", (new ItemSeeds(Blocks.NETHER_WART, Blocks.SOUL_SAND)).setUnlocalizedName("netherStalkSeeds"));
        registerItem(373, "potion", (new ItemPotion()).setUnlocalizedName("potion"));
        Item item1 = (new ItemGlassBottle()).setUnlocalizedName("glassBottle");
        registerItem(374, "glass_bottle", item1);
        registerItem(375, "spider_eye", (new ItemFood(2, 0.8F, false)).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 1.0F).setUnlocalizedName("spiderEye"));
        registerItem(376, "fermented_spider_eye", (new Item()).setUnlocalizedName("fermentedSpiderEye").setCreativeTab(CreativeTabs.BREWING));
        registerItem(377, "blaze_powder", (new Item()).setUnlocalizedName("blazePowder").setCreativeTab(CreativeTabs.BREWING));
        registerItem(378, "magma_cream", (new Item()).setUnlocalizedName("magmaCream").setCreativeTab(CreativeTabs.BREWING));
        registerItem(379, "brewing_stand", (new ItemBlockSpecial(Blocks.BREWING_STAND)).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.BREWING));
        registerItem(380, "cauldron", (new ItemBlockSpecial(Blocks.CAULDRON)).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.BREWING));
        registerItem(381, "ender_eye", (new ItemEnderEye()).setUnlocalizedName("eyeOfEnder"));
        registerItem(382, "speckled_melon", (new Item()).setUnlocalizedName("speckledMelon").setCreativeTab(CreativeTabs.BREWING));
        registerItem(383, "spawn_egg", (new ItemMonsterPlacer()).setUnlocalizedName("monsterPlacer"));
        registerItem(384, "experience_bottle", (new ItemExpBottle()).setUnlocalizedName("expBottle"));
        registerItem(385, "fire_charge", (new ItemFireball()).setUnlocalizedName("fireball"));
        registerItem(386, "writable_book", (new ItemWritableBook()).setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.MISC));
        registerItem(387, "written_book", (new ItemWrittenBook()).setUnlocalizedName("writtenBook").setMaxStackSize(16));
        registerItem(388, "emerald", (new Item()).setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(389, "item_frame", (new ItemHangingEntity(EntityItemFrame.class)).setUnlocalizedName("frame"));
        registerItem(390, "flower_pot", (new ItemBlockSpecial(Blocks.FLOWER_POT)).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.DECORATIONS));
        registerItem(391, "carrot", (new ItemSeedFood(3, 0.6F, Blocks.CARROTS, Blocks.FARMLAND)).setUnlocalizedName("carrots"));
        registerItem(392, "potato", (new ItemSeedFood(1, 0.3F, Blocks.POTATOES, Blocks.FARMLAND)).setUnlocalizedName("potato"));
        registerItem(393, "baked_potato", (new ItemFood(5, 0.6F, false)).setUnlocalizedName("potatoBaked"));
        registerItem(394, "poisonous_potato", (new ItemFood(2, 0.3F, false)).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 0.6F).setUnlocalizedName("potatoPoisonous"));
        registerItem(395, "map", (new ItemEmptyMap()).setUnlocalizedName("emptyMap"));
        registerItem(396, "golden_carrot", (new ItemFood(6, 1.2F, false)).setUnlocalizedName("carrotGolden").setCreativeTab(CreativeTabs.BREWING));
        registerItem(397, "skull", (new ItemSkull()).setUnlocalizedName("skull"));
        registerItem(398, "carrot_on_a_stick", (new ItemCarrotOnAStick()).setUnlocalizedName("carrotOnAStick"));
        registerItem(399, "nether_star", (new ItemSimpleFoiled()).setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(400, "pumpkin_pie", (new ItemFood(8, 0.3F, false)).setUnlocalizedName("pumpkinPie").setCreativeTab(CreativeTabs.FOOD));
        registerItem(401, "fireworks", (new ItemFirework()).setUnlocalizedName("fireworks"));
        registerItem(402, "firework_charge", (new ItemFireworkCharge()).setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.MISC));
        registerItem(403, "enchanted_book", (new ItemEnchantedBook()).setMaxStackSize(1).setUnlocalizedName("enchantedBook"));
        registerItem(404, "comparator", (new ItemBlockSpecial(Blocks.UNPOWERED_COMPARATOR)).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.REDSTONE));
        registerItem(405, "netherbrick", (new Item()).setUnlocalizedName("netherbrick").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(406, "quartz", (new Item()).setUnlocalizedName("netherquartz").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(407, "tnt_minecart", (new ItemMinecart(EntityMinecart.Type.TNT)).setUnlocalizedName("minecartTnt"));
        registerItem(408, "hopper_minecart", (new ItemMinecart(EntityMinecart.Type.HOPPER)).setUnlocalizedName("minecartHopper"));
        registerItem(409, "prismarine_shard", (new Item()).setUnlocalizedName("prismarineShard").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(410, "prismarine_crystals", (new Item()).setUnlocalizedName("prismarineCrystals").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(411, "rabbit", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("rabbitRaw"));
        registerItem(412, "cooked_rabbit", (new ItemFood(5, 0.6F, true)).setUnlocalizedName("rabbitCooked"));
        registerItem(413, "rabbit_stew", (new ItemSoup(10)).setUnlocalizedName("rabbitStew"));
        registerItem(414, "rabbit_foot", (new Item()).setUnlocalizedName("rabbitFoot").setCreativeTab(CreativeTabs.BREWING));
        registerItem(415, "rabbit_hide", (new Item()).setUnlocalizedName("rabbitHide").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(416, "armor_stand", (new ItemArmorStand()).setUnlocalizedName("armorStand").setMaxStackSize(16));
        registerItem(417, "iron_horse_armor", (new Item()).setUnlocalizedName("horsearmormetal").setMaxStackSize(1).setCreativeTab(CreativeTabs.MISC));
        registerItem(418, "golden_horse_armor", (new Item()).setUnlocalizedName("horsearmorgold").setMaxStackSize(1).setCreativeTab(CreativeTabs.MISC));
        registerItem(419, "diamond_horse_armor", (new Item()).setUnlocalizedName("horsearmordiamond").setMaxStackSize(1).setCreativeTab(CreativeTabs.MISC));
        registerItem(420, "lead", (new ItemLead()).setUnlocalizedName("leash"));
        registerItem(421, "name_tag", (new ItemNameTag()).setUnlocalizedName("nameTag"));
        registerItem(422, "command_block_minecart", (new ItemMinecart(EntityMinecart.Type.COMMAND_BLOCK)).setUnlocalizedName("minecartCommandBlock").setCreativeTab((CreativeTabs)null));
        registerItem(423, "mutton", (new ItemFood(2, 0.3F, true)).setUnlocalizedName("muttonRaw"));
        registerItem(424, "cooked_mutton", (new ItemFood(6, 0.8F, true)).setUnlocalizedName("muttonCooked"));
        registerItem(425, "banner", (new ItemBanner()).setUnlocalizedName("banner"));
        registerItem(426, "end_crystal", new ItemEndCrystal());
        registerItem(427, "spruce_door", (new ItemDoor(Blocks.SPRUCE_DOOR)).setUnlocalizedName("doorSpruce"));
        registerItem(428, "birch_door", (new ItemDoor(Blocks.BIRCH_DOOR)).setUnlocalizedName("doorBirch"));
        registerItem(429, "jungle_door", (new ItemDoor(Blocks.JUNGLE_DOOR)).setUnlocalizedName("doorJungle"));
        registerItem(430, "acacia_door", (new ItemDoor(Blocks.ACACIA_DOOR)).setUnlocalizedName("doorAcacia"));
        registerItem(431, "dark_oak_door", (new ItemDoor(Blocks.DARK_OAK_DOOR)).setUnlocalizedName("doorDarkOak"));
        registerItem(432, "chorus_fruit", (new ItemChorusFruit(4, 0.3F)).setAlwaysEdible().setUnlocalizedName("chorusFruit").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(433, "chorus_fruit_popped", (new Item()).setUnlocalizedName("chorusFruitPopped").setCreativeTab(CreativeTabs.MATERIALS));
        registerItem(434, "beetroot", (new ItemFood(1, 0.6F, false)).setUnlocalizedName("beetroot"));
        registerItem(435, "beetroot_seeds", (new ItemSeeds(Blocks.BEETROOTS, Blocks.FARMLAND)).setUnlocalizedName("beetroot_seeds"));
        registerItem(436, "beetroot_soup", (new ItemSoup(6)).setUnlocalizedName("beetroot_soup"));
        registerItem(437, "dragon_breath", (new Item()).setCreativeTab(CreativeTabs.BREWING).setUnlocalizedName("dragon_breath").setContainerItem(item1));
        registerItem(438, "splash_potion", (new ItemSplashPotion()).setUnlocalizedName("splash_potion"));
        registerItem(439, "spectral_arrow", (new ItemSpectralArrow()).setUnlocalizedName("spectral_arrow"));
        registerItem(440, "tipped_arrow", (new ItemTippedArrow()).setUnlocalizedName("tipped_arrow"));
        registerItem(441, "lingering_potion", (new ItemLingeringPotion()).setUnlocalizedName("lingering_potion"));
        registerItem(442, "shield", (new ItemShield()).setUnlocalizedName("shield"));
        registerItem(443, "elytra", (new ItemElytra()).setUnlocalizedName("elytra"));
        registerItem(444, "spruce_boat", new ItemBoat(EntityBoat.Type.SPRUCE));
        registerItem(445, "birch_boat", new ItemBoat(EntityBoat.Type.BIRCH));
        registerItem(446, "jungle_boat", new ItemBoat(EntityBoat.Type.JUNGLE));
        registerItem(447, "acacia_boat", new ItemBoat(EntityBoat.Type.ACACIA));
        registerItem(448, "dark_oak_boat", new ItemBoat(EntityBoat.Type.DARK_OAK));
        registerItem(2256, "record_13", (new ItemRecord("13", SoundEvents.RECORD_13)).setUnlocalizedName("record"));
        registerItem(2257, "record_cat", (new ItemRecord("cat", SoundEvents.RECORD_CAT)).setUnlocalizedName("record"));
        registerItem(2258, "record_blocks", (new ItemRecord("blocks", SoundEvents.RECORD_BLOCKS)).setUnlocalizedName("record"));
        registerItem(2259, "record_chirp", (new ItemRecord("chirp", SoundEvents.RECORD_CHIRP)).setUnlocalizedName("record"));
        registerItem(2260, "record_far", (new ItemRecord("far", SoundEvents.RECORD_FAR)).setUnlocalizedName("record"));
        registerItem(2261, "record_mall", (new ItemRecord("mall", SoundEvents.RECORD_MALL)).setUnlocalizedName("record"));
        registerItem(2262, "record_mellohi", (new ItemRecord("mellohi", SoundEvents.RECORD_MELLOHI)).setUnlocalizedName("record"));
        registerItem(2263, "record_stal", (new ItemRecord("stal", SoundEvents.RECORD_STAL)).setUnlocalizedName("record"));
        registerItem(2264, "record_strad", (new ItemRecord("strad", SoundEvents.RECORD_STRAD)).setUnlocalizedName("record"));
        registerItem(2265, "record_ward", (new ItemRecord("ward", SoundEvents.RECORD_WARD)).setUnlocalizedName("record"));
        registerItem(2266, "record_11", (new ItemRecord("11", SoundEvents.RECORD_11)).setUnlocalizedName("record"));
        registerItem(2267, "record_wait", (new ItemRecord("wait", SoundEvents.RECORD_WAIT)).setUnlocalizedName("record"));
    }

    /**
     * Register a default ItemBlock for the given Block.
     */
    private static void registerItemBlock(Block blockIn)
    {
        registerItemBlock(blockIn, new ItemBlock(blockIn));
    }

    /**
     * Register the given Item as the ItemBlock for the given Block.
     */
    protected static void registerItemBlock(Block blockIn, Item itemIn)
    {
        registerItem(Block.getIdFromBlock(blockIn), (ResourceLocation)Block.REGISTRY.getNameForObject(blockIn), itemIn);
        BLOCK_TO_ITEM.put(blockIn, itemIn);
    }

    private static void registerItem(int id, String textualID, Item itemIn)
    {
        registerItem(id, new ResourceLocation(textualID), itemIn);
    }

    private static void registerItem(int id, ResourceLocation textualID, Item itemIn)
    {
        REGISTRY.register(id, textualID, itemIn);
    }

    public static enum ToolMaterial
    {
        WOOD(0, 59, 2.0F, 0.0F, 15),
        STONE(1, 131, 4.0F, 1.0F, 5),
        IRON(2, 250, 6.0F, 2.0F, 14),
        DIAMOND(3, 1561, 8.0F, 3.0F, 10),
        GOLD(0, 32, 12.0F, 0.0F, 22);

        /** The level of material this tool can harvest (3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = WOOD/GOLD) */
        private final int harvestLevel;
        /** The number of uses this material allows. (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32) */
        private final int maxUses;
        /** The strength of this tool material against blocks which it is effective against. */
        private final float efficiencyOnProperMaterial;
        /** Damage versus entities. */
        private final float damageVsEntity;
        /** Defines the natural enchantability factor of the material. */
        private final int enchantability;

        //Added by forge for custom Tool materials.
        @Deprecated public Item customCraftingMaterial = null; // Remote in 1.8.1
        private ItemStack repairMaterial = null;

        private ToolMaterial(int harvestLevel, int maxUses, float efficiency, float damageVsEntity, int enchantability)
        {
            this.harvestLevel = harvestLevel;
            this.maxUses = maxUses;
            this.efficiencyOnProperMaterial = efficiency;
            this.damageVsEntity = damageVsEntity;
            this.enchantability = enchantability;
        }

        /**
         * The number of uses this material allows. (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32)
         */
        public int getMaxUses()
        {
            return this.maxUses;
        }

        /**
         * The strength of this tool material against blocks which it is effective against.
         */
        public float getEfficiencyOnProperMaterial()
        {
            return this.efficiencyOnProperMaterial;
        }

        /**
         * Returns the damage against a given entity.
         */
        public float getDamageVsEntity()
        {
            return this.damageVsEntity;
        }

        /**
         * The level of material this tool can harvest (3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = IRON/GOLD)
         */
        public int getHarvestLevel()
        {
            return this.harvestLevel;
        }

        /**
         * Return the natural enchantability factor of the material.
         */
        public int getEnchantability()
        {
            return this.enchantability;
        }

        @Deprecated // Use getRepairItemStack below
        public Item getRepairItem()
        {
            switch (this)
            {
                case WOOD:    return Item.getItemFromBlock(Blocks.PLANKS);
                case STONE:   return Item.getItemFromBlock(Blocks.COBBLESTONE);
                case GOLD:    return Items.GOLD_INGOT;
                case IRON:    return Items.IRON_INGOT;
                case DIAMOND: return Items.DIAMOND;
                default:      return customCraftingMaterial;
            }
        }

        public ToolMaterial setRepairItem(ItemStack stack)
        {
            if (this.repairMaterial != null || customCraftingMaterial != null) throw new RuntimeException("Can not change already set repair material");
            if (this == WOOD || this == STONE || this == GOLD || this == IRON || this == DIAMOND) throw new RuntimeException("Can not change vanilla tool repair materials");
            this.repairMaterial = stack;
            this.customCraftingMaterial = stack.getItem();
            return this;
        }

        public ItemStack getRepairItemStack()
        {
            if (repairMaterial != null) return repairMaterial;
            Item ret = this.getRepairItem();
            if (ret == null) return null;
            repairMaterial = new ItemStack(ret, 1, net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE);
            return repairMaterial;
        }
    }
}