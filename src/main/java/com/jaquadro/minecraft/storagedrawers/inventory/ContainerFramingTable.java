package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ContainerFramingTable extends AbstractContainerMenu
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

    private final Container tableInventory;
    private final Container craftResult;
    private final ContainerLevelAccess access;
    private final Player player;

    private Slot inputSlot;
    private Slot materialSideSlot;
    private Slot materialTrimSlot;
    private Slot materialFrontSlot;
    private Slot outputSlot;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;

    public ContainerFramingTable (@Nullable MenuType<?> type, int windowId, Inventory playerInv, FriendlyByteBuf data) {
        this(type, windowId, playerInv, getBlockEntity(playerInv, data.readBlockPos()));
    }

    public ContainerFramingTable (int windowId, Inventory playerInventory, FriendlyByteBuf packet) {
        this(ModContainers.FRAMING_TABLE.get(), windowId, playerInventory, packet);
    }

    protected static BlockEntityFramingTable getBlockEntity(Inventory playerInv, BlockPos pos) {
        Level level = playerInv.player.getCommandSenderWorld();
        BlockEntityFramingTable blockEntity = WorldUtils.getBlockEntity(level, pos, BlockEntityFramingTable.class);
        if (blockEntity == null)
            StorageDrawers.log.error("Expected a framing table tile entity at " + pos);
        else
            return blockEntity;

        return null;
    }

    public ContainerFramingTable (@Nullable MenuType<?> type, int windowId, Inventory playerInventory, BlockEntityFramingTable blockEntity) {
        super(type, windowId);

        tableInventory = blockEntity;
        craftResult = blockEntity;
        access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        player = playerInventory.player;

        inputSlot = addSlot(new RestrictedSlot(tableInventory, 0, InputX, InputY));
        materialSideSlot = addSlot(new RestrictedSlot(tableInventory, 1, MaterialSideX, MaterialSideY));
        materialTrimSlot = addSlot(new RestrictedSlot(tableInventory, 2, MaterialTrimX, MaterialTrimY));
        materialFrontSlot = addSlot(new RestrictedSlot(tableInventory, 3, MaterialFrontX, MaterialFrontY));
        outputSlot = addSlot(new CraftResultSlot(playerInventory.player, tableInventory, craftResult, new int[] { 0, 1, 2, 3 }, 4, OutputX, OutputY));

        playerSlots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                playerSlots.add(addSlot(new Slot(playerInventory, j + i * 9 + 9, InventoryX + j * 18, InventoryY + i * 18)));
        }

        hotbarSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            hotbarSlots.add(addSlot(new Slot(playerInventory, i, InventoryX + i * 18, HotbarY)));

        slotsChanged(tableInventory);
    }

    /*
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
    */

    @Override
    public @NotNull ItemStack quickMoveStack (Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        // Assume inventory and hotbar slot IDs are contiguous
        int inventoryStart = playerSlots.get(0).index;
        int hotbarStart = hotbarSlots.get(0).index;
        int hotbarEnd = hotbarSlots.get(hotbarSlots.size() - 1).index + 1;

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (slotIndex == BlockEntityFramingTable.SLOT_RESULT) {
                if (!moveItemStackTo(slotStack, inventoryStart, hotbarEnd, true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(slotStack, itemStack);
            }

            else if (slotIndex == BlockEntityFramingTable.SLOT_INPUT || BlockEntityFramingTable.isMaterialSlot(slotIndex)) {
                if (!moveItemStackTo(slotStack, inventoryStart, hotbarEnd, true))
                    return ItemStack.EMPTY;
            }

            else if (slotIndex >= inventoryStart && slotIndex < hotbarEnd) {
                if (BlockEntityFramingTable.isItemValidTarget(slotStack)) {
                    if (!moveItemStackTo(slotStack, BlockEntityFramingTable.SLOT_INPUT, BlockEntityFramingTable.SLOT_INPUT + 1, false))
                        return ItemStack.EMPTY;
                } else if (BlockEntityFramingTable.isItemValidMaterial(slotStack)) {
                    if (!moveItemStackTo(slotStack, BlockEntityFramingTable.SLOT_SIDE, BlockEntityFramingTable.SLOT_FRONT + 1, false))
                        return ItemStack.EMPTY;
                } else if (slotIndex < hotbarStart) { // Inventory Area
                    if (!moveItemStackTo(slotStack, hotbarStart, hotbarEnd, false))
                        return ItemStack.EMPTY;
                } else { // Hotbar Area
                    if (!moveItemStackTo(slotStack, inventoryStart, hotbarStart, false))
                        return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty())
                slot.setByPlayer(ItemStack.EMPTY);
            else
                slot.setChanged();

            if (slotStack.getCount() == itemStack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, slotStack);
        }

        return itemStack;
    }

    @Override
    public boolean stillValid (Player player) {
        return tableInventory.stillValid(player);
    }
}
