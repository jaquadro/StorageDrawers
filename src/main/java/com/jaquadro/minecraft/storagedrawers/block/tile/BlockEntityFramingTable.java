package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawersFramed;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerFramingTable;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemFramedDrawers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockEntityFramingTable extends BaseBlockEntity implements Container, MenuProvider
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_SIDE = 1;
    public static final int SLOT_TRIM = 2;
    public static final int SLOT_FRONT = 3;
    public static final int SLOT_RESULT = 4;

    private final MaterialData materialData = new MaterialData();
    protected ItemStack inputStack = ItemStack.EMPTY;
    protected ItemStack resultStack = ItemStack.EMPTY;

    public BlockEntityFramingTable (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

        injectData(materialData);
    }

    public BlockEntityFramingTable(BlockPos pos, BlockState state) {
        this(ModBlockEntities.FRAMING_TABLE.get(), pos, state);
    }

    public MaterialData material () {
        return materialData;
    }

    @Override
    public int getContainerSize () {
        return 5;
    }

    @Override
    public boolean isEmpty () {
        if (!inputStack.isEmpty())
            return false;
        if (!resultStack.isEmpty())
            return false;
        if (!materialData.getSide().isEmpty())
            return false;
        if (!materialData.getTrim().isEmpty())
            return false;
        if (!materialData.getFront().isEmpty())
            return false;

        return true;
    }

    @Override
    public ItemStack getItem (int slot) {
        return switch (slot) {
            case SLOT_INPUT -> inputStack;
            case SLOT_FRONT -> materialData.getFront();
            case SLOT_SIDE -> materialData.getSide();
            case SLOT_TRIM -> materialData.getTrim();
            case SLOT_RESULT -> resultStack;
            default -> null;
        };
    }

    @Override
    public ItemStack removeItem (int slot, int amount) {
        if (slot < 0 || slot > getContainerSize() || amount <= 0)
            return ItemStack.EMPTY;

        return getItem(slot).split(amount);
    }

    @Override
    public ItemStack removeItemNoUpdate (int slot) {
        if (slot < 0 || slot > getContainerSize())
            return ItemStack.EMPTY;

        ItemStack result = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return result;
    }

    @Override
    public void setItem (int slot, ItemStack stack) {
        if (slot == SLOT_RESULT)
            return;

        switch (slot) {
            case SLOT_INPUT -> inputStack = stack;
            case SLOT_FRONT -> materialData.setFront(stack);
            case SLOT_SIDE -> materialData.setSide(stack);
            case SLOT_TRIM -> materialData.setTrim(stack);
        }

        rebuildResult();
        setChanged();
    }

    private void rebuildResult () {
        ItemStack target = getItem(BlockEntityFramingTable.SLOT_INPUT);
        if (target.isEmpty()) {
            resultStack = ItemStack.EMPTY;
            return;
        }

        ItemStack matSide = getItem(BlockEntityFramingTable.SLOT_SIDE);
        ItemStack matTrim = getItem(BlockEntityFramingTable.SLOT_TRIM);
        ItemStack matFront = getItem(BlockEntityFramingTable.SLOT_FRONT);

        if (!target.isEmpty() && target.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof BlockStandardDrawersFramed) {
                BlockState state = block.defaultBlockState();
                if (matSide.isEmpty())
                    resultStack = ItemStack.EMPTY;
                else
                    resultStack = ItemFramedDrawers.makeItemStack(state, 1, matSide, matTrim, matFront);
            }
            /*else if (block instanceof BlockTrimCustom) {
                if (!matSide.isEmpty()) {
                    craftResult.setInventorySlotContents(0, ItemCustomTrim.makeItemStack(block, 1, matSide, matTrim));
                    return;
                }
            }*/
        }
    }

    @Override
    public boolean stillValid (Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent () {
        inputStack = ItemStack.EMPTY;
        resultStack = ItemStack.EMPTY;
        materialData.clear();
    }

    @Override
    protected void readFixed (CompoundTag tag) {
        super.readFixed(tag);

        inputStack = ItemStack.EMPTY;
        if (tag.contains("Input"))
            inputStack = ItemStack.of(tag.getCompound("Input"));

        resultStack = ItemStack.EMPTY;
        if (tag.contains("Result"))
            resultStack = ItemStack.of(tag.getCompound("Result"));
    }

    @Override
    protected CompoundTag writeFixed (CompoundTag tag) {
        tag = super.writeFixed(tag);

        if (!inputStack.isEmpty()) {
            CompoundTag itag = new CompoundTag();
            inputStack.save(itag);
            tag.put("Input", itag);
        }

        if (!resultStack.isEmpty()) {
            CompoundTag itag = new CompoundTag();
            resultStack.save(itag);
            tag.put("Result", itag);
        }

        return tag;
    }

    public static boolean isItemValidDrawer (ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return false;

        return blockItem.getBlock() instanceof BlockStandardDrawersFramed;
    }

    public static boolean isItemValidMaterial (ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return false;

        BlockState state = blockItem.getBlock().defaultBlockState();
        return state.isSolid();
    }

    @Override
    public @NotNull Component getDisplayName () {
        return Component.translatable("container.storagedrawers.framing_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu (int windowId, @NotNull Inventory playInventory, @NotNull Player player) {
        return new ContainerFramingTable(ModContainers.FRAMING_TABLE.get(), windowId, playInventory, this);
    }

    /*
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
    */
}
