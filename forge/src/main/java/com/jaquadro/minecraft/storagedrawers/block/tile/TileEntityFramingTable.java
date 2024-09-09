/*package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrimCustom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class TileEntityFramingTable extends TileEntity implements IInventory
{
    private ItemStack[] tableItemStacks = new ItemStack[5];

    private String customName;

    public TileEntityFramingTable () {
        for (int i = 0; i < tableItemStacks.length; i++)
            tableItemStacks[i] = ItemStack.EMPTY;
    }

    @Override
    public int getSizeInventory () {
        return tableItemStacks.length;
    }

    @Override
    public boolean isEmpty () {
        for (ItemStack item : tableItemStacks) {
            if (!item.isEmpty())
                return false;
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot (int slot) {
        return tableItemStacks[slot];
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize (int slot, int count) {
        if (!tableItemStacks[slot].isEmpty()) {
            if (tableItemStacks[slot].getCount() <= count) {
                ItemStack stack = tableItemStacks[slot];
                tableItemStacks[slot] = ItemStack.EMPTY;
                markDirty();
                return stack;
            }
            else {
                ItemStack stack = tableItemStacks[slot].splitStack(count);
                if (tableItemStacks[slot].getCount() == 0)
                    tableItemStacks[slot] = ItemStack.EMPTY;

                markDirty();
                return stack;
            }
        }
        else
            return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot (int index) {
        if (!tableItemStacks[index].isEmpty()) {
            ItemStack stack = tableItemStacks[index];
            tableItemStacks[index] = ItemStack.EMPTY;
            markDirty();
            return stack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents (int slot, @Nonnull ItemStack stack) {
        tableItemStacks[slot] = stack;

        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
            stack.setCount(getInventoryStackLimit());

        markDirty();
    }

    @Override
    public String getName () {
        return hasCustomName() ? customName : "storagedrawers.container.framingTable";
    }

    @Override
    public boolean hasCustomName () {
        return customName != null && customName.length() > 0;
    }

    @Override
    public ITextComponent getDisplayName () {
        return null;
    }

    public void setCustomName (String name) {
        customName = name;
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer (EntityPlayer player) {
        if (getWorld().getTileEntity(pos) != this)
            return false;

        return player.getDistanceSq(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64;
    }

    @Override
    public void openInventory (EntityPlayer player) {

    }

    @Override
    public void closeInventory (EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot (int slot, @Nonnull ItemStack stack) {
        if (slot == 0)
            return isItemValidDrawer(stack);
        if (slot == 4)
            return false;
        if (slot >= 1 && slot < 4)
            return isItemValidMaterial(stack);

        return false;
    }

    @Override
    public int getField (int id) {
        return 0;
    }

    @Override
    public void setField (int id, int value) {

    }

    @Override
    public int getFieldCount () {
        return 0;
    }

    @Override
    public void clear () {

    }

    public static boolean isItemValidDrawer (@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        Block block = Block.getBlockFromItem(stack.getItem());
        return block instanceof BlockDrawersCustom || block instanceof BlockTrimCustom;
    }

    public static boolean isItemValidMaterial (@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null)
            return false;

        IBlockState state = block.getStateFromMeta(stack.getMetadata());
        return state.isOpaqueCube();
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        NBTTagList itemList = tag.getTagList("Items", 10);
        tableItemStacks = new ItemStack[getSizeInventory()];
        for (int i = 0; i < tableItemStacks.length; i++)
            tableItemStacks[i] = ItemStack.EMPTY;

        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound item = itemList.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");

            if (slot >= 0 && slot < tableItemStacks.length)
                tableItemStacks[slot] = new ItemStack(item);
        }

        if (tag.hasKey("CustomName", Constants.NBT.TAG_STRING))
            customName = tag.getString("CustomName");
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < tableItemStacks.length; i++) {
            if (!tableItemStacks[i].isEmpty()) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte)i);
                tableItemStacks[i].writeToNBT(item);
                itemList.appendTag(item);
            }
        }

        tag.setTag("Items", itemList);

        if (hasCustomName())
            tag.setString("CustomName", customName);

        return tag;
    }

    @Override
    public NBTTagCompound getUpdateTag () {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return tag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket () {
        return new SPacketUpdateTileEntity(pos, 5, getUpdateTag());
    }

    @Override
    public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        //getgetWorld()().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
    }

    private static final AxisAlignedBB ZERO_EXTENT_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    @Override
    public AxisAlignedBB getRenderBoundingBox () {
        IBlockState state = getWorld().getBlockState(pos);
        if (!(state.getBlock() instanceof BlockFramingTable) || !state.getValue(BlockFramingTable.RIGHT_SIDE))
            return ZERO_EXTENT_AABB;

        int side = state.getValue(BlockFramingTable.FACING).getIndex();
        int xOff = 0;
        int zOff = 0;

        if (side == 2)
            xOff = 1;
        if (side == 3)
            xOff = -1;
        if (side == 4)
            zOff = -1;
        if (side == 5)
            zOff = 1;

        int xMin = Math.min(pos.getX(), pos.getX() + xOff);
        int xMax = Math.max(pos.getX(), pos.getX() + xOff) + 1;
        int zMin = Math.min(pos.getZ(), pos.getZ() + zOff);
        int zMax = Math.max(pos.getZ(), pos.getZ() + zOff) + 1;
        return new AxisAlignedBB(xMin, pos.getY() + 1, zMin, xMax, pos.getY() + 2, zMax);
    }
}
*/