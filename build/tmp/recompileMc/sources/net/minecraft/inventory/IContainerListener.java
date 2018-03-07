package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IContainerListener
{
    /**
     * update the crafting window inventory with the items in the list
     */
    void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList);

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack);

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue);

    void sendAllWindowProperties(Container containerIn, IInventory inventory);
}