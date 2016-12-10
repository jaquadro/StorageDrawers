package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerDrawers extends Container
{
    private static final int InventoryX = 8;
    private static final int InventoryY = 117;
    private static final int HotbarY = 175;

    private static final int UpgradeX = 44;
    private static final int UpgradeY = 86;

    private IInventory storageInventory;
    private IInventory upgradeInventory;

    private List<Slot> storageSlots;
    private List<Slot> upgradeSlots;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;

    public ContainerDrawers (InventoryPlayer playerInventory, TileEntityDrawers tileEntity) {
        //storageInventory = new InventoryStorage(tileEntity, this);
        storageInventory = new InventoryStorage(tileEntity);
        upgradeInventory = new InventoryUpgrade(tileEntity);

        storageSlots = new ArrayList<Slot>();
        for (int i = 0; i < tileEntity.getDrawerCount(); i++)
            storageSlots.add(addSlotToContainer(new SlotStorage(storageInventory, i, getStorageSlotX(i), getStorageSlotY(i))));

        upgradeSlots = new ArrayList<Slot>();
        for (int i = 0; i < 5; i++)
            upgradeSlots.add(addSlotToContainer(new SlotUpgrade(upgradeInventory, i, UpgradeX + i * 18, UpgradeY)));

        playerSlots = new ArrayList<Slot>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                playerSlots.add(addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, InventoryX + j * 18, InventoryY + i * 18)));
        }

        hotbarSlots = new ArrayList<Slot>();
        for (int i = 0; i < 9; i++)
            hotbarSlots.add(addSlotToContainer(new Slot(playerInventory, i, InventoryX + i * 18, HotbarY)));
    }

    public InventoryStorage getStorageInventory () {
        return (InventoryStorage)storageInventory;
    }

    protected int getStorageSlotX (int slot) {
        return 0;
    }

    protected int getStorageSlotY (int slot) {
        return 0;
    }

    public List<Slot> getStorageSlots () {
        return storageSlots;
    }

    public List<Slot> getUpgradeSlots () {
        return upgradeSlots;
    }

    @Override
    public boolean canInteractWith (EntityPlayer player) {
        return storageInventory.isUsableByPlayer(player) || upgradeInventory.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer player, int slotIndex) {
        ItemStack itemStack = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);

        int storageStart = storageSlots.get(0).slotNumber;
        int storageEnd = storageSlots.get(storageSlots.size() - 1).slotNumber + 1;
        int upgradeStart = upgradeSlots.get(0).slotNumber;
        int upgradeEnd = upgradeSlots.get(upgradeSlots.size() - 1).slotNumber + 1;

        // Assume inventory and hotbar slot IDs are contiguous
        int inventoryStart = playerSlots.get(0).slotNumber;
        int hotbarStart = hotbarSlots.get(0).slotNumber;
        int hotbarEnd = hotbarSlots.get(hotbarSlots.size() - 1).slotNumber + 1;

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();

            // Try merge upgrades to inventory
            if (slotIndex >= upgradeStart && slotIndex < upgradeEnd) {
                if (!mergeItemStack(slotStack, inventoryStart, hotbarEnd, true))
                    return null;
                slot.onSlotChange(slotStack, itemStack);
            }

            // Try merge inventory to upgrades
            else if (slotIndex >= inventoryStart && slotIndex < hotbarEnd && slotStack != null) {
                /*if (slotStack.getItem() == ModItems.upgrade || slotStack.getItem() == ModItems.upgradeStatus || slotStack.getItem() == ModItems.upgradeVoid) {
                    ItemStack slotStack1 = slotStack.copy();
                    slotStack1.stackSize = 1;

                    if (!mergeItemStack(slotStack1, upgradeStart, upgradeEnd, false)) {
                        if (slotIndex >= inventoryStart && slotIndex < hotbarEnd) {
                            if (!mergeItemStack(slotStack, hotbarStart, hotbarEnd, false))
                                return null;
                        } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !mergeItemStack(slotStack, inventoryStart, hotbarStart, false))
                            return null;
                    }
                    else {
                        slotStack.stackSize--;
                    }
                }*/

                if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                    if (!mergeItemStack(slotStack, hotbarStart, hotbarEnd, false))
                        return null;
                } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !mergeItemStack(slotStack, inventoryStart, hotbarStart, false))
                    return null;
            }

            // Try merge stack into inventory
            else if (!mergeItemStack(slotStack, inventoryStart, hotbarEnd, false))
                return null;

            if (slotStack.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();

            if (slotStack.stackSize == itemStack.stackSize)
                return null;

            slot.onPickupFromSlot(player, slotStack);
        }

        return itemStack;
    }
}
