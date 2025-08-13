package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedSourceBlock;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.components.item.FrameData;
import com.jaquadro.minecraft.storagedrawers.config.MaterialBlacklist;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.inventory.*;
import com.texelsaurus.minecraft.chameleon.inventory.ContentMenuProvider;
import com.texelsaurus.minecraft.chameleon.inventory.content.PositionContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class BlockEntityFramingTable extends BaseBlockEntity implements Nameable
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_SIDE = 1;
    public static final int SLOT_TRIM = 2;
    public static final int SLOT_FRONT = 3;
    public static final int SLOT_RESULT = 4;

    private final BlockInventory inventory;
    private final MaterialData materialData = new MaterialData();
    protected ItemStack inputStack = ItemStack.EMPTY;
    protected ItemStack resultStack = ItemStack.EMPTY;

    private Component name;

    public BlockEntityFramingTable (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

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

    public BlockInventory inventory () {
        return inventory;
    }

    @Override
    protected void readFixed (HolderLookup.Provider provider, CompoundTag tag) {
        super.readFixed(provider, tag);

        inputStack = ItemStack.EMPTY;
        if (tag.contains("Input"))
            inputStack = ItemStack.parseOptional(provider, tag.getCompound("Input"));

        resultStack = ItemStack.EMPTY;
        if (tag.contains("Result"))
            resultStack = ItemStack.parseOptional(provider, tag.getCompound("Result"));
    }

    @Override
    protected CompoundTag writeFixed (HolderLookup.Provider provider, CompoundTag tag) {
        tag = super.writeFixed(provider, tag);

        if (!inputStack.isEmpty())
            tag.put("Input", inputStack.saveOptional(provider));

        if (!resultStack.isEmpty())
            tag.put("Result", resultStack.saveOptional(provider));

        return tag;
    }

    public boolean isItemValidTarget (ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return false;

        if (blockItem.getBlock() instanceof IFramedBlock) {
            FrameData data = stack.getOrDefault(ModDataComponents.FRAME_DATA.get(), FrameData.EMPTY);

            if (!data.side().isEmpty() && !materialData.getSide().isEmpty())
                return false;
            if (!data.trim().isEmpty() && !materialData.getTrim().isEmpty())
                return false;
            if (!data.front().isEmpty() && !materialData.getFront().isEmpty())
                return false;

            // TODO: For potential use in supporting unpacking drawer into table with existing mats
            /*if (!testTargetFrameMaterial(data.side(), materialData.getSide(), stack.getCount(), inventory().getItem(SLOT_SIDE).getCount()))
                return false;
            if (!testTargetFrameMaterial(data.front(), materialData.getFront(), stack.getCount(), inventory().getItem(SLOT_FRONT).getCount()))
                return false;
            if (!testTargetFrameMaterial(data.trim(), materialData.getTrim(), stack.getCount(), inventory().getItem(SLOT_TRIM).getCount()))
                return false;*/

            return true;
        }

        if (blockItem.getBlock() instanceof IFramedSourceBlock)
            return true;

        return false;
    }

    private boolean testTargetFrameMaterial(ItemStack sourceMat, ItemStack targetMat, int sourceCount, int targetCount) {
        if (sourceMat.isEmpty() || targetMat.isEmpty())
            return true;

        if (!ItemStack.isSameItemSameComponents(sourceMat, targetMat))
            return false;

        return true;
        //int limit = Math.min(sourceMat.getMaxStackSize(), targetMat.getMaxStackSize());
        //return (sourceCount + targetCount) <= limit;
    }

    public static boolean isItemValidMaterial (ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (!(stack.getItem() instanceof BlockItem blockItem))
            return false;

        BlockState state = blockItem.getBlock().defaultBlockState();

        if (ModCommonConfig.INSTANCE.DRAWERS.framed.enforceSolidMaterials.get()) {
            if (state.getBlock().hasDynamicShape())
                return false;

            try {
                if (!Block.isShapeFullBlock(state.getOcclusionShape(null, null)))
                    return false;
            } catch (Exception e) { }
        }

        if (ModCommonConfig.INSTANCE.DRAWERS.framed.enforceOpaqueMaterials.get()) {
            if (!state.canOcclude())
                return false;
        }

        if (MaterialBlacklist.INSTANCE.isBlacklisted(stack))
            return false;

        return true;
    }

    @Override
    public Component getName() {
        return (this.name != null ? this.name : Component.translatable("block.storagedrawers.framing_table"));
    }

    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.name = input.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.name);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("CustomName");
    }

    @Override
    public void readPortable (HolderLookup.Provider provider, CompoundTag tag) {
        super.readPortable(provider, tag);
        if (tag.contains("CustomName", 8))
            name = parseCustomNameSafe(tag.getString("CustomName"), provider);
    }

    @Override
    public CompoundTag writePortable (HolderLookup.Provider provider, CompoundTag tag) {
        tag = super.writePortable(provider, tag);
        if (name != null)
            tag.putString("CustomName", Component.Serializer.toJson(name, provider));

        return tag;
    }

    // Forge extension
    public AABB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        return AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
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
            return entity.getDisplayName();
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
                    FrameData data = stack.getOrDefault(ModDataComponents.FRAME_DATA.get(), FrameData.EMPTY);

                    if (fb.supportsFrameMaterial(FrameMaterial.SIDE)) {
                        if (!entity.materialData.getSide().isEmpty() && !data.side().isEmpty()) {
                            entity.resultStack = stack;
                            return;
                        }
                    }

                    if (fb.supportsFrameMaterial(FrameMaterial.TRIM)) {
                        if (!entity.materialData.getTrim().isEmpty() && !data.trim().isEmpty()) {
                            entity.resultStack = stack;
                            return;
                        }
                    }

                    if (fb.supportsFrameMaterial(FrameMaterial.FRONT)) {
                        if (!entity.materialData.getFront().isEmpty() && !data.front().isEmpty()) {
                            entity.resultStack = stack;
                            return;
                        }
                    }

                    ItemStack source = data.base();
                    if (!source.isEmpty()) {
                        int count = stack.getCount();
                        entity.inputStack = stack.transmuteCopy(source.getItem());
                        entity.inputStack.remove(ModDataComponents.FRAME_DATA.get());

                        entity.materialData.setSide(fb.supportsFrameMaterial(FrameMaterial.SIDE) ? data.side().copyWithCount(count) : ItemStack.EMPTY);
                        entity.materialData.setTrim(fb.supportsFrameMaterial(FrameMaterial.TRIM) ? data.trim().copyWithCount(count) : ItemStack.EMPTY);
                        entity.materialData.setFront(fb.supportsFrameMaterial(FrameMaterial.FRONT) ? data.front().copyWithCount(count) : ItemStack.EMPTY);

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