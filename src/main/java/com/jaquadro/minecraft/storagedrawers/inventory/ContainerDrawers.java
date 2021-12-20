package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerDrawers extends AbstractContainerMenu
{
    private static final int InventoryX = 8;
    private static final int InventoryY = 117;
    private static final int HotbarY = 175;

    private static final int UpgradeX = 26;
    private static final int UpgradeY = 86;

    private Container upgradeInventory;

    private List<Slot> storageSlots;
    private List<Slot> upgradeSlots;
    private List<Slot> playerSlots;
    private List<Slot> hotbarSlots;

    @OnlyIn(Dist.CLIENT)
    public StorageRenderItem activeRenderItem;

    private boolean isRemote;

    public ContainerDrawers (@Nullable MenuType<?> type, int windowId, Inventory playerInv, FriendlyByteBuf data) {
        this(type, windowId, playerInv, getTileEntity(playerInv, data.readBlockPos()));
    }

    protected static TileEntityDrawers getTileEntity (Inventory playerInv, BlockPos pos) {
        Level world = playerInv.player.getCommandSenderWorld();
        BlockEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof TileEntityDrawers))
            StorageDrawers.log.error("Expected a drawers tile entity at " + pos.toString());
        else
            return (TileEntityDrawers)tile;

        return null;
    }

    public ContainerDrawers (@Nullable MenuType<?> type, int windowId, Inventory playerInventory, TileEntityDrawers tileEntity) {
        super(type, windowId);

        int drawerCount = 0;

        upgradeInventory = new InventoryUpgrade(tileEntity);
        Block block = tileEntity.getBlockState().getBlock();
        IDrawerGroup group = tileEntity.getGroup();
        if (block instanceof BlockDrawers)
            drawerCount = ((BlockDrawers) block).getDrawerCount();

        storageSlots = new ArrayList<>();
        for (int i = 0; i < drawerCount; i++) {
            if (group.getDrawer(i).isEnabled())
                storageSlots.add(addSlot(new SlotDrawer(this, group, i, getStorageSlotX(i), getStorageSlotY(i))));
        }

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

        isRemote = playerInventory.player.getCommandSenderWorld().isClientSide;
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
    public boolean stillValid (Player player) {
        return upgradeInventory.stillValid(player);
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack (Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        int storageStart = storageSlots.get(0).index;
        int storageEnd = storageSlots.get(storageSlots.size() - 1).index + 1;
        int upgradeStart = upgradeSlots.get(0).index;
        int upgradeEnd = upgradeSlots.get(upgradeSlots.size() - 1).index + 1;

        // Assume inventory and hotbar slot IDs are contiguous
        int inventoryStart = playerSlots.get(0).index;
        int hotbarStart = hotbarSlots.get(0).index;
        int hotbarEnd = hotbarSlots.get(hotbarSlots.size() - 1).index + 1;

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            // Try merge upgrades to inventory
            if (slotIndex >= upgradeStart && slotIndex < upgradeEnd) {
                if (!moveItemStackTo(slotStack, inventoryStart, hotbarEnd, true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(slotStack, itemStack);
            }

            // Try merge inventory to upgrades
            else if (slotIndex >= inventoryStart && slotIndex < hotbarEnd && !slotStack.isEmpty()) {
                if (slotStack.getItem() instanceof ItemUpgrade) {
                    ItemStack slotStack1 = slotStack.copy();
                    slotStack1.setCount(1);

                    if (!moveItemStackTo(slotStack1, upgradeStart, upgradeEnd, false)) {
                        if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                            if (!moveItemStackTo(slotStack, hotbarStart, hotbarEnd, false))
                                return ItemStack.EMPTY;
                        } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !moveItemStackTo(slotStack, inventoryStart, hotbarStart, false))
                            return ItemStack.EMPTY;
                    }
                    else {
                        slotStack.shrink(1);
                        if (slotStack.getCount() == 0)
                            slot.set(ItemStack.EMPTY);
                        else
                            slot.setChanged();

                        slot.onTake(player, slotStack);
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= inventoryStart && slotIndex < hotbarStart) {
                    if (!moveItemStackTo(slotStack, hotbarStart, hotbarEnd, false))
                        return ItemStack.EMPTY;
                } else if (slotIndex >= hotbarStart && slotIndex < hotbarEnd && !moveItemStackTo(slotStack, inventoryStart, hotbarStart, false))
                    return ItemStack.EMPTY;
            }

            // Try merge stack into inventory
            else if (!moveItemStackTo(slotStack, inventoryStart, hotbarEnd, false))
                return ItemStack.EMPTY;

            int slotStackSize = slotStack.getCount();
            if (slotStackSize == 0)
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();

            if (slotStackSize == itemStack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, slotStack);
        }

        return itemStack;
    }
}
