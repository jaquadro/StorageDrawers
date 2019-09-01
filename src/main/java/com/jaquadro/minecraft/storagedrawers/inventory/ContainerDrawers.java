package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerDrawers extends Container
{
    private static final int InventoryX = 8;
    private static final int InventoryY = 117;
    private static final int HotbarY = 175;

    private static final int UpgradeX = 26;
    private static final int UpgradeY = 86;

    private IInventory upgradeInventory;

    private List<Slot> storageSlots;
    private List<Slot> upgradeSlots;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;

    @OnlyIn(Dist.CLIENT)
    public StorageRenderItem activeRenderItem;

    private boolean isRemote;

    public ContainerDrawers (@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, PacketBuffer data) {
        this(type, windowId, playerInv, getTileEntity(playerInv, data.readBlockPos()));
    }

    protected static TileEntityDrawers getTileEntity (PlayerInventory playerInv, BlockPos pos) {
        World world = playerInv.player.getEntityWorld();
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityDrawers))
            StorageDrawers.log.error("Expected a drawers tile entity at " + pos.toString());
        else
            return (TileEntityDrawers)tile;

        return null;
    }

    public ContainerDrawers (@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInventory, TileEntityDrawers tileEntity) {
        super(type, windowId);

        int drawerCount = 0;

        upgradeInventory = new InventoryUpgrade(tileEntity);
        Block block = tileEntity.getBlockState().getBlock();
        if (block instanceof BlockDrawers)
            drawerCount = ((BlockDrawers) block).getDrawerCount();

        storageSlots = new ArrayList<>();
        for (int i = 0; i < drawerCount; i++)
            storageSlots.add(addSlot(new SlotDrawer(this, tileEntity.getGroup(), i, getStorageSlotX(i), getStorageSlotY(i))));

        upgradeSlots = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            upgradeSlots.add(addSlot(new SlotUpgrade(upgradeInventory, i, UpgradeX + i * 18, UpgradeY)));

        playerSlots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                playerSlots.add(addSlot(new Slot(playerInventory, j + i * 9 + 9, InventoryX + j * 18, InventoryY + i * 18)));
        }

        hotbarSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            hotbarSlots.add(addSlot(new Slot(playerInventory, i, InventoryX + i * 18, HotbarY)));

        isRemote = playerInventory.player.getEntityWorld().isRemote;
    }

    public void setLastAccessedItem (ItemStack stack) {
        if (isRemote && activeRenderItem != null)
            activeRenderItem.overrideStack = stack;
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
    public boolean canInteractWith (PlayerEntity player) {
        return upgradeInventory.isUsableByPlayer(player);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot (PlayerEntity player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

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
                    return ItemStack.EMPTY;
                slot.onSlotChange(slotStack, itemStack);
            }

            // Try merge inventory to upgrades
            else if (slotIndex >= inventoryStart && slotIndex < hotbarEnd && !slotStack.isEmpty()) {
                if (slotStack.getItem() instanceof ItemUpgrade) {
                    ItemStack slotStack1 = slotStack.copy();
                    slotStack1.setCount(1);

                    if (!mergeItemStack(slotStack1, upgradeStart, upgradeEnd, false)) {
                        if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                            if (!mergeItemStack(slotStack, hotbarStart, hotbarEnd, false))
                                return ItemStack.EMPTY;
                        } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !mergeItemStack(slotStack, inventoryStart, hotbarStart, false))
                            return ItemStack.EMPTY;
                    }
                    else {
                        slotStack.shrink(1);
                        if (slotStack.getCount() == 0)
                            slot.putStack(ItemStack.EMPTY);
                        else
                            slot.onSlotChanged();

                        slot.onTake(player, slotStack);
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                    if (!mergeItemStack(slotStack, hotbarStart, hotbarEnd, false))
                        return ItemStack.EMPTY;
                } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !mergeItemStack(slotStack, inventoryStart, hotbarStart, false))
                    return ItemStack.EMPTY;
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
}
