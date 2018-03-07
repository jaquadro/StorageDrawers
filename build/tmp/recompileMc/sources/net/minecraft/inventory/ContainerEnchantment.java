package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerEnchantment extends Container
{
    /** SlotEnchantmentTable object with ItemStack to be enchanted */
    public IInventory tableInventory;
    /** current world (for bookshelf counting) */
    private final World worldPointer;
    private final BlockPos position;
    private final Random rand;
    public int xpSeed;
    /** 3-member array storing the enchantment levels of each slot */
    public int[] enchantLevels;
    public int[] enchantClue;
    public int[] worldClue;

    @SideOnly(Side.CLIENT)
    public ContainerEnchantment(InventoryPlayer playerInv, World worldIn)
    {
        this(playerInv, worldIn, BlockPos.ORIGIN);
    }

    public ContainerEnchantment(InventoryPlayer playerInv, World worldIn, BlockPos pos)
    {
        this.tableInventory = new InventoryBasic("Enchant", true, 2)
        {
            /**
             * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
             */
            public int getInventoryStackLimit()
            {
                return 64;
            }
            /**
             * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't
             * think it hasn't changed and skip it.
             */
            public void markDirty()
            {
                super.markDirty();
                ContainerEnchantment.this.onCraftMatrixChanged(this);
            }
        };
        this.rand = new Random();
        this.enchantLevels = new int[3];
        this.enchantClue = new int[] { -1, -1, -1};
        this.worldClue = new int[] { -1, -1, -1};
        this.worldPointer = worldIn;
        this.position = pos;
        this.xpSeed = playerInv.player.getXPSeed();
        this.addSlotToContainer(new Slot(this.tableInventory, 0, 15, 47)
        {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                return true;
            }
            /**
             * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
             * the case of armor slots)
             */
            public int getSlotStackLimit()
            {
                return 1;
            }
        });
        this.addSlotToContainer(new Slot(this.tableInventory, 1, 35, 47)
        {
            java.util.List<ItemStack> ores = net.minecraftforge.oredict.OreDictionary.getOres("gemLapis");
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean isItemValid(@Nullable ItemStack stack)
            {
                for (ItemStack ore : ores)
                    if (net.minecraftforge.oredict.OreDictionary.itemMatches(ore, stack, false)) return true;
                return false;
            }
        });

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

    protected void broadcastData(IContainerListener crafting)
    {
        crafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
        crafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
        crafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
        crafting.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
        crafting.sendProgressBarUpdate(this, 4, this.enchantClue[0]);
        crafting.sendProgressBarUpdate(this, 5, this.enchantClue[1]);
        crafting.sendProgressBarUpdate(this, 6, this.enchantClue[2]);
        crafting.sendProgressBarUpdate(this, 7, this.worldClue[0]);
        crafting.sendProgressBarUpdate(this, 8, this.worldClue[1]);
        crafting.sendProgressBarUpdate(this, 9, this.worldClue[2]);
    }

    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        this.broadcastData(listener);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = (IContainerListener)this.listeners.get(i);
            this.broadcastData(icontainerlistener);
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id >= 0 && id <= 2)
        {
            this.enchantLevels[id] = data;
        }
        else if (id == 3)
        {
            this.xpSeed = data;
        }
        else if (id >= 4 && id <= 6)
        {
            this.enchantClue[id - 4] = data;
        }
        else if (id >= 7 && id <= 9)
        {
            this.worldClue[id - 7] = data;
        }
        else
        {
            super.updateProgressBar(id, data);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        if (inventoryIn == this.tableInventory)
        {
            ItemStack itemstack = inventoryIn.getStackInSlot(0);

            if (itemstack != null && itemstack.isItemEnchantable())
            {
                if (!this.worldPointer.isRemote)
                {
                    int l = 0;
                    float power = 0;

                    for (int j = -1; j <= 1; ++j)
                    {
                        for (int k = -1; k <= 1; ++k)
                        {
                            if ((j != 0 || k != 0) && this.worldPointer.isAirBlock(this.position.add(k, 0, j)) && this.worldPointer.isAirBlock(this.position.add(k, 1, j)))
                            {
                                power += net.minecraftforge.common.ForgeHooks.getEnchantPower(worldPointer, position.add(k * 2, 0, j * 2));
                                power += net.minecraftforge.common.ForgeHooks.getEnchantPower(worldPointer, position.add(k * 2, 1, j * 2));
                                if (k != 0 && j != 0)
                                {
                                    power += net.minecraftforge.common.ForgeHooks.getEnchantPower(worldPointer, position.add(k * 2, 0, j));
                                    power += net.minecraftforge.common.ForgeHooks.getEnchantPower(worldPointer, position.add(k * 2, 1, j));
                                    power += net.minecraftforge.common.ForgeHooks.getEnchantPower(worldPointer, position.add(k, 0, j * 2));
                                    power += net.minecraftforge.common.ForgeHooks.getEnchantPower(worldPointer, position.add(k, 1, j * 2));
                                }
                            }
                        }
                    }

                    this.rand.setSeed((long)this.xpSeed);

                    for (int i1 = 0; i1 < 3; ++i1)
                    {
                        this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, (int)power, itemstack);
                        this.enchantClue[i1] = -1;
                        this.worldClue[i1] = -1;

                        if (this.enchantLevels[i1] < i1 + 1)
                        {
                            this.enchantLevels[i1] = 0;
                        }
                    }

                    for (int j1 = 0; j1 < 3; ++j1)
                    {
                        if (this.enchantLevels[j1] > 0)
                        {
                            List<EnchantmentData> list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1]);

                            if (list != null && !list.isEmpty())
                            {
                                EnchantmentData enchantmentdata = (EnchantmentData)list.get(this.rand.nextInt(list.size()));
                                this.enchantClue[j1] = Enchantment.getEnchantmentID(enchantmentdata.enchantmentobj);
                                this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                            }
                        }
                    }

                    this.detectAndSendChanges();
                }
            }
            else
            {
                for (int i = 0; i < 3; ++i)
                {
                    this.enchantLevels[i] = 0;
                    this.enchantClue[i] = -1;
                    this.worldClue[i] = -1;
                }
            }
        }
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean enchantItem(EntityPlayer playerIn, int id)
    {
        ItemStack itemstack = this.tableInventory.getStackInSlot(0);
        ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
        int i = id + 1;

        if ((itemstack1 == null || itemstack1.stackSize < i) && !playerIn.capabilities.isCreativeMode)
        {
            return false;
        }
        else if (this.enchantLevels[id] > 0 && itemstack != null && (playerIn.experienceLevel >= i && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.capabilities.isCreativeMode))
        {
            if (!this.worldPointer.isRemote)
            {
                List<EnchantmentData> list = this.getEnchantmentList(itemstack, id, this.enchantLevels[id]);
                boolean flag = itemstack.getItem() == Items.BOOK;

                if (list != null)
                {
                    playerIn.removeExperienceLevel(i);

                    if (flag)
                    {
                        itemstack.setItem(Items.ENCHANTED_BOOK);
                    }

                    for (int j = 0; j < list.size(); ++j)
                    {
                        EnchantmentData enchantmentdata = (EnchantmentData)list.get(j);

                        if (flag)
                        {
                            Items.ENCHANTED_BOOK.addEnchantment(itemstack, enchantmentdata);
                        }
                        else
                        {
                            itemstack.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
                        }
                    }

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        itemstack1.stackSize -= i;

                        if (itemstack1.stackSize <= 0)
                        {
                            this.tableInventory.setInventorySlotContents(1, (ItemStack)null);
                        }
                    }

                    playerIn.addStat(StatList.ITEM_ENCHANTED);
                    this.tableInventory.markDirty();
                    this.xpSeed = playerIn.getXPSeed();
                    this.onCraftMatrixChanged(this.tableInventory);
                    this.worldPointer.playSound((EntityPlayer)null, this.position, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.worldPointer.rand.nextFloat() * 0.1F + 0.9F);
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private List<EnchantmentData> getEnchantmentList(ItemStack stack, int p_178148_2_, int p_178148_3_)
    {
        this.rand.setSeed((long)(this.xpSeed + p_178148_2_));
        List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, stack, p_178148_3_, false);

        if (stack.getItem() == Items.BOOK && list.size() > 1)
        {
            list.remove(this.rand.nextInt(list.size()));
        }

        return list;
    }

    @SideOnly(Side.CLIENT)
    public int getLapisAmount()
    {
        ItemStack itemstack = this.tableInventory.getStackInSlot(1);
        return itemstack == null ? 0 : itemstack.stackSize;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.worldPointer.isRemote)
        {
            for (int i = 0; i < this.tableInventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.tableInventory.removeStackFromSlot(i);

                if (itemstack != null)
                {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.worldPointer.getBlockState(this.position).getBlock() != Blocks.ENCHANTING_TABLE ? false : playerIn.getDistanceSq((double)this.position.getX() + 0.5D, (double)this.position.getY() + 0.5D, (double)this.position.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 2, 38, true))
                {
                    return null;
                }
            }
            else if (index == 1)
            {
                if (!this.mergeItemStack(itemstack1, 2, 38, true))
                {
                    return null;
                }
            }
            else if (itemstack1.getItem() == Items.DYE && EnumDyeColor.byDyeDamage(itemstack1.getMetadata()) == EnumDyeColor.BLUE)
            {
                if (!this.mergeItemStack(itemstack1, 1, 2, true))
                {
                    return null;
                }
            }
            else
            {
                if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(itemstack1))
                {
                    return null;
                }

                if (itemstack1.hasTagCompound() && itemstack1.stackSize == 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(itemstack1.copy());
                    itemstack1.stackSize = 0;
                }
                else if (itemstack1.stackSize >= 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(itemstack1.getItem(), 1, itemstack1.getMetadata()));
                    --itemstack1.stackSize;
                }
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }
}