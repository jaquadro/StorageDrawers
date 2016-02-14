package com.jaquadro.minecraft.storagedrawers.inventory;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class SlotCraftResult extends Slot
{
    private final IInventory inputInventory;
    private final int[] inputSlots;
    private EntityPlayer player;
    private int amountCrafted;

    public SlotCraftResult (EntityPlayer player, IInventory inputInventory, IInventory inventory, int[] inputSlots, int slot, int x, int y) {
        super(inventory, slot, x, y);

        this.player = player;
        this.inputSlots = inputSlots;
        this.inputInventory = inputInventory;
    }

    @Override
    public boolean isItemValid (ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize (int count) {
        if (getHasStack())
            amountCrafted += Math.min(count, getStack().stackSize);

        return super.decrStackSize(count);
    }

    @Override
    protected void onCrafting (ItemStack stack, int count) {
        amountCrafted += count;
        super.onCrafting(stack, count);
    }

    @Override
    protected void onCrafting (ItemStack stack) {
        stack.onCrafting(player.worldObj, player, amountCrafted);
        amountCrafted = 0;
    }

    @Override
    public void onPickupFromSlot (EntityPlayer player, ItemStack itemStack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, itemStack, inputInventory);
        onCrafting(itemStack);

        for (int slot : inputSlots) {
            ItemStack itemTarget = inputInventory.getStackInSlot(slot);
            if (itemTarget != null) {
                inputInventory.decrStackSize(slot, 1);

                if (itemTarget.getItem().hasContainerItem(itemTarget)) {
                    ItemStack itemContainer = itemTarget.getItem().getContainerItem(itemTarget);
                    if (itemContainer != null && itemContainer.isItemStackDamageable() && itemContainer.getItemDamage() > itemContainer.getMaxDamage()) {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this.player, itemContainer));
                        continue;
                    }

                    if (!itemTarget.getItem().doesContainerItemLeaveCraftingGrid(itemTarget) || !this.player.inventory.addItemStackToInventory(itemContainer)) {
                        if (inputInventory.getStackInSlot(slot) == null)
                            inputInventory.setInventorySlotContents(slot, itemContainer);
                        else
                            this.player.dropPlayerItemWithRandomChoice(itemContainer, false);
                    }
                }
            }
        }
    }
}
