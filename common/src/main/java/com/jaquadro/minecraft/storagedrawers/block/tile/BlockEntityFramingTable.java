package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.CustomNameData;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerFramingTable;
import com.texelsaurus.minecraft.chameleon.inventory.ContentMenuProvider;
import com.texelsaurus.minecraft.chameleon.inventory.content.PositionContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockEntityFramingTable extends BaseBlockEntity implements MenuProvider
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_SIDE = 1;
    public static final int SLOT_TRIM = 2;
    public static final int SLOT_FRONT = 3;
    public static final int SLOT_RESULT = 4;

    private final BlockInventory inventory;
    private final CustomNameData customNameData = new CustomNameData();
    private final MaterialData materialData = new MaterialData();
    protected ItemStack inputStack = ItemStack.EMPTY;
    protected ItemStack resultStack = ItemStack.EMPTY;

    public BlockEntityFramingTable (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

        injectPortableData(customNameData);

        injectData(materialData);
        inventory = new BlockInventory(this);
    }

    public BlockEntityFramingTable(BlockPos pos, BlockState state) {
        this(ModBlockEntities.FRAMING_TABLE.get(), pos, state);
    }

    public static boolean isMaterialSlot (int slotIndex) {
        return slotIndex == SLOT_SIDE || slotIndex == SLOT_TRIM || slotIndex == SLOT_FRONT;
    }

    public MaterialData material () {
        return materialData;
    }

    public CustomNameData customName () {
        return customNameData;
    }

    public BlockInventory inventory () {
        return inventory;
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

    public boolean isItemValidTarget (ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return false;

        if (blockItem.getBlock() instanceof IFramedBlock) {
            MaterialData data = new MaterialData();
            data.read(stack.getOrCreateTag());

            if (!data.getSide().isEmpty() && !materialData.getSide().isEmpty())
                return false;
            if (!data.getTrim().isEmpty() && !materialData.getTrim().isEmpty())
                return false;
            if (!data.getFront().isEmpty() && !materialData.getFront().isEmpty())
                return false;

            return true;
        }

        if (blockItem.getBlock() instanceof IFramedSourceBlock)
            return true;

        return false;
    }

    public static boolean isItemValidMaterial (ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return false;

        BlockState state = blockItem.getBlock().defaultBlockState();
        if (state.getBlock().hasDynamicShape())
            return false;

        if (!ModCommonConfig.INSTANCE.GENERAL.restrictFramingMaterials.get())
            return state.isSolid();

        if (!state.canOcclude())
            return false;

        try {
            // Will always throw unless overridden, which usually means it's a block that we don't
            // want to be a valid material
            if (state.getLightBlock(null, null) < 15)
                return false;
        } catch (Exception e) { }

        try {
            if (!Block.isShapeFullBlock(state.getOcclusionShape(null, null)))
                return false;
            if (state.propagatesSkylightDown(null, null))
                return false;
        } catch (Exception e) {
            return false;
        }

        return true;
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

    // Forge extension
    public AABB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        return BlockEntityFramingTable.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
    }

    static AABB encapsulatingFullBlocks(BlockPos $$0, BlockPos $$1) {
        return new AABB((double)Math.min($$0.getX(), $$1.getX()), (double)Math.min($$0.getY(), $$1.getY()), (double)Math.min($$0.getZ(), $$1.getZ()), (double)(Math.max($$0.getX(), $$1.getX()) + 1), (double)(Math.max($$0.getY(), $$1.getY()) + 1), (double)(Math.max($$0.getZ(), $$1.getZ()) + 1));
    }

    public static class ContentProvider implements ContentMenuProvider<PositionContent>
    {
        private BlockEntityFramingTable entity;

        public ContentProvider (BlockEntityFramingTable entity) {
            this.entity = entity;
        }

        @Override
        public PositionContent createContent (ServerPlayer player) {
            return new PositionContent(entity.getBlockPos());
        }

        @Override
        public Component getDisplayName () {
            if (entity.customNameData.hasCustomName())
                return entity.customNameData.getDisplayName();

            return Component.translatable("container.storagedrawers.framing_table");
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu (int id, Inventory inventory, Player player) {
            return new ContainerFramingTable(ModContainers.FRAMING_TABLE.get(), id, inventory, entity);
        }
    }

    public static class BlockInventory implements Container
    {
        private BlockEntityFramingTable entity;

        private BlockInventory (BlockEntityFramingTable entity) {
            this.entity = entity;
        }

        @Override
        public int getContainerSize () {
            return 5;
        }

        @Override
        public boolean isEmpty () {
            if (!entity.inputStack.isEmpty())
                return false;
            if (!entity.resultStack.isEmpty())
                return false;
            if (!entity.materialData.getSide().isEmpty())
                return false;
            if (!entity.materialData.getTrim().isEmpty())
                return false;
            if (!entity.materialData.getFront().isEmpty())
                return false;

            return true;
        }

        @Override
        public boolean canPlaceItem (int slotIndex, ItemStack stack) {
            if (slotIndex == SLOT_INPUT)
                return entity.isItemValidTarget(stack);
            if (slotIndex == SLOT_SIDE || slotIndex == SLOT_TRIM || slotIndex == SLOT_FRONT)
                return isItemValidMaterial(stack);

            return false;
        }

        @Override
        public ItemStack getItem (int slot) {
            return switch (slot) {
                case SLOT_INPUT -> entity.inputStack;
                case SLOT_FRONT -> entity.materialData.getFront();
                case SLOT_SIDE -> entity.materialData.getSide();
                case SLOT_TRIM -> entity.materialData.getTrim();
                case SLOT_RESULT -> entity.resultStack;
                default -> null;
            };
        }

        @Override
        public ItemStack removeItem (int slot, int amount) {
            if (slot < 0 || slot > getContainerSize() || amount <= 0)
                return ItemStack.EMPTY;

            ItemStack ret = getItem(slot).split(amount);

            rebuildResult();
            setChanged();

            return ret;
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
                case SLOT_INPUT -> setInputItem(stack);
                case SLOT_FRONT -> entity.materialData.setFront(stack);
                case SLOT_SIDE -> entity.materialData.setSide(stack);
                case SLOT_TRIM -> entity.materialData.setTrim(stack);
            }

            setChanged();
        }

        @Override
        public void setChanged () {
            rebuildResult();
            this.entity.setChanged();
        }

        private void setInputItem (ItemStack stack) {
            if (entity.level != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if (block instanceof IFramedBlock fb) {
                    MaterialData data = new MaterialData();
                    data.read(stack.getOrCreateTag());

                    if (fb.supportsFrameMaterial(FrameMaterial.SIDE)) {
                        if (!entity.materialData.getSide().isEmpty() && !data.getSide().isEmpty()) {
                            entity.resultStack = stack;
                            return;
                        }
                    }

                    if (fb.supportsFrameMaterial(FrameMaterial.TRIM)) {
                        if (!entity.materialData.getTrim().isEmpty() && !data.getTrim().isEmpty()) {
                            entity.resultStack = stack;
                            return;
                        }
                    }

                    if (fb.supportsFrameMaterial(FrameMaterial.FRONT)) {
                        if (!entity.materialData.getFront().isEmpty() && !data.getFront().isEmpty()) {
                            entity.resultStack = stack;
                            return;
                        }
                    }

                    ItemStack source = data.getFrameBase();
                    if (!source.isEmpty()) {
                        source.setTag(stack.getOrCreateTag().copy());
                        MaterialData empty = new MaterialData();
                        empty.write(source.getTag());

                        int count = stack.getCount();
                        entity.inputStack = source.copyWithCount(count);

                        entity.materialData.setSide(fb.supportsFrameMaterial(FrameMaterial.SIDE) ? data.getSide().copyWithCount(count) : ItemStack.EMPTY);
                        entity.materialData.setTrim(fb.supportsFrameMaterial(FrameMaterial.TRIM) ? data.getTrim().copyWithCount(count) : ItemStack.EMPTY);
                        entity.materialData.setFront(fb.supportsFrameMaterial(FrameMaterial.FRONT) ? data.getFront().copyWithCount(count) : ItemStack.EMPTY);

                        return;
                    }
                }
            }

            entity.inputStack = stack;
        }

        private void rebuildResult () {
            ItemStack target = getItem(BlockEntityFramingTable.SLOT_INPUT);
            if (target.isEmpty()) {
                entity.resultStack = ItemStack.EMPTY;
                return;
            }

            ItemStack matSide = getItem(BlockEntityFramingTable.SLOT_SIDE);
            ItemStack matTrim = getItem(BlockEntityFramingTable.SLOT_TRIM);
            ItemStack matFront = getItem(BlockEntityFramingTable.SLOT_FRONT);

            if (!target.isEmpty() && target.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if (block instanceof IFramedSourceBlock fsb) {
                    if (matSide.isEmpty())
                        entity.resultStack = ItemStack.EMPTY;
                    else
                        entity.resultStack = fsb.makeFramedItem(target, matSide, matTrim, matFront);
                }
            }

            int count = entity.resultStack.getCount();
            if (!matSide.isEmpty())
                count = Math.min(count, matSide.getCount());
            if (!matTrim.isEmpty())
                count = Math.min(count, matTrim.getCount());
            if (!matFront.isEmpty())
                count = Math.min(count, matFront.getCount());

            entity.resultStack.setCount(count);
        }

        @Override
        public boolean stillValid (Player player) {
            return Container.stillValidBlockEntity(entity, player);
        }

        @Override
        public void clearContent () {
            entity.inputStack = ItemStack.EMPTY;
            entity.resultStack = ItemStack.EMPTY;
            entity.materialData.clear();
        }
    }
}
