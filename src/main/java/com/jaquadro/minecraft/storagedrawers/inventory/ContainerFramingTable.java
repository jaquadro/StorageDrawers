package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IFrameable;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContainerFramingTable extends Container
{
    private static final int InventoryX = 8;
    private static final int InventoryY = 84;
    private static final int HotbarY = 142;

    private static final int InputX = 23;
    private static final int InputY = 35;
    private static final int MaterialSideX = 50;
    private static final int MaterialSideY = 17;
    private static final int MaterialTrimX = 102;
    private static final int MaterialTrimY = 17;
    private static final int MaterialFrontX = 50;
    private static final int MaterialFrontY = 53;
    private static final int OutputX = 133;
    private static final int OutputY = 35;

    private IInventory tableInventory;
    private IInventory craftResult = new InventoryCraftResult();

    private Slot inputSlot;
    private Slot materialSideSlot;
    private Slot materialTrimSlot;
    private Slot materialFrontSlot;
    private Slot outputSlot;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;

    public ContainerFramingTable (InventoryPlayer inventory, TileEntityFramingTable tileEntity) {
        tableInventory = new InventoryContainerProxy(tileEntity, this);

        inputSlot = addSlotToContainer(new SlotRestricted(tableInventory, 0, InputX, InputY));

        materialSideSlot = addSlotToContainer(new SlotRestricted(tableInventory, 1, MaterialSideX, MaterialSideY));
        materialTrimSlot = addSlotToContainer(new SlotRestricted(tableInventory, 2, MaterialTrimX, MaterialTrimY));
        materialFrontSlot = addSlotToContainer(new SlotRestricted(tableInventory, 3, MaterialFrontX, MaterialFrontY));
        outputSlot = addSlotToContainer(new FramingSlotResult(inventory.player, tableInventory, craftResult, new int[] { 0, 1, 2, 3 }, 0, 4, OutputX, OutputY));

        playerSlots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                playerSlots.add(addSlotToContainer(new Slot(inventory, j + i * 9 + 9, InventoryX + j * 18, InventoryY + i * 18)));
        }

        hotbarSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            hotbarSlots.add(addSlotToContainer(new Slot(inventory, i, InventoryX + i * 18, HotbarY)));

        onCraftMatrixChanged(tableInventory);
    }

    @Override
    public boolean canInteractWith (EntityPlayer player) {
        return tableInventory.isUsableByPlayer(player);
    }

    @Override
    public void onCraftMatrixChanged (IInventory inventory) {
        ItemStack input = tableInventory.getStackInSlot(inputSlot.getSlotIndex());
        ItemStack matSide = tableInventory.getStackInSlot(materialSideSlot.getSlotIndex());
        ItemStack matTrim = tableInventory.getStackInSlot(materialTrimSlot.getSlotIndex());
        ItemStack matFront = tableInventory.getStackInSlot(materialFrontSlot.getSlotIndex());

        if (!input.isEmpty() && input.getItem() instanceof IFrameable && !matSide.isEmpty()) {
            craftResult.setInventorySlotContents(0, ((IFrameable) input.getItem())
                    .decorate(input.copy(), matSide.copy(), matTrim.copy(), matFront.copy()));
            return;
        }

        craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot (EntityPlayer player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        // Assume inventory and hotbar slot IDs are contiguous
        int inventoryStart = playerSlots.get(0).slotNumber;
        int hotbarStart = hotbarSlots.get(0).slotNumber;
        int hotbarEnd = hotbarSlots.get(hotbarSlots.size() - 1).slotNumber + 1;

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();

            // Try merge output into inventory and signal change
            if (slotIndex == outputSlot.slotNumber) {
                if (!mergeItemStack(slotStack, inventoryStart, hotbarEnd, true))
                    return ItemStack.EMPTY;
                slot.onSlotChange(slotStack, itemStack);
            }

            // Try merge stacks within inventory and hotbar spaces
            else if (slotIndex >= inventoryStart && slotIndex < hotbarEnd) {
                boolean merged = false;
                if (TileEntityFramingTable.isItemValidDrawer(slotStack))
                    merged = mergeItemStack(slotStack, inputSlot.slotNumber, inputSlot.slotNumber + 1, false);
                else if (TileEntityFramingTable.isItemValidMaterial(slotStack))
                    merged = mergeItemStack(slotStack, materialSideSlot.slotNumber, materialFrontSlot.slotNumber + 1, false);

                if (!merged) {
                    if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                        if (!mergeItemStack(slotStack, hotbarStart, hotbarEnd, false))
                            return ItemStack.EMPTY;
                    } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !this.mergeItemStack(slotStack, inventoryStart, hotbarStart, false))
                        return ItemStack.EMPTY;
                }
            }

            // Try merge stack into inventory
            else if (!mergeItemStack(slotStack, inventoryStart, hotbarEnd, false))
                return ItemStack.EMPTY;

            int slotStackSize = slotStack.getCount();
            if (slotStackSize == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if (slotStackSize == itemStack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, slotStack);
        }

        return itemStack;
    }

    /**
     * A special Slot Craft Result class, which respects the config of `consumeDecorateBlocks`.
     */
    public static class FramingSlotResult extends SlotCraftResult {
        private final IInventory inputInventory;
        private final int alwaysConsumeSlot;
        public FramingSlotResult(EntityPlayer player, IInventory inputInventory, IInventory inventory, int[] inputSlots, int alwaysConsumeSlot, int slot, int x, int y) {
            super(player, inputInventory, inventory, inputSlots, slot, x, y);

            this.inputInventory = inputInventory;
            this.alwaysConsumeSlot = alwaysConsumeSlot;
        }

        @Nonnull
        @Override
        public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack stack) {
            if (StorageDrawers.config.cache.consumeDecorateBlocks)
                return super.onTake(player, stack);

            FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, inputInventory);
            onCrafting(stack);

            // Only decrease stack size of Drawer/Trim slot
            inputInventory.decrStackSize(alwaysConsumeSlot, 1);

            return stack;
        }
    }
}
