package com.jaquadro.minecraft.storagedrawers.components.item;

import com.google.common.collect.Lists;
import com.jaquadro.minecraft.storagedrawers.item.ItemKey;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class KeyringContents implements TooltipComponent
{
    public static final KeyringContents EMPTY = new KeyringContents(List.of());
    public static final Codec<KeyringContents> CODEC = ItemStack.CODEC
        .listOf().xmap(KeyringContents::new, kc -> kc.items);
    public static final StreamCodec<RegistryFriendlyByteBuf, KeyringContents> STREAM_CODEC = ItemStack.STREAM_CODEC
        .apply(ByteBufCodecs.list())
        .map(KeyringContents::new, kc -> kc.items);

    private static final int NO_STACK_INDEX = -1;

    final List<ItemStack> items;

    public KeyringContents (List<ItemStack> items) {
        this.items = items;
    }

    public ItemStack getItemUnsafe (int index) {
        return items.get(index);
    }

    public Stream<ItemStack> itemCopyStream () {
        return items.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> items () {
        return items;
    }

    public Iterable<ItemStack> itemsCopy () {
        return Lists.transform(items, ItemStack::copy);
    }

    public int size() {
        return items.size();
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj)
            return true;
        return !(obj instanceof KeyringContents contents) ? false : ItemStack.listMatches(items, contents.items);
    }

    @Override
    public int hashCode () {
        return ItemStack.hashStackList(items);
    }

    @Override
    public String toString () {
        return "KeyringContents" + items;
    }

    public static class Mutable {
        private final List<ItemStack> items;

        public Mutable (KeyringContents contents) {
            items = new ArrayList<>(contents.items);
        }

        public int size() {
            return items.size();
        }

        public Mutable clearItems() {
            items.clear();
            return this;
        }

        private int findStackIndex (ItemStack stack) {
            for (int i = 0; i < this.items.size(); i++) {
                if (ItemStack.isSameItemSameComponents(this.items.get(i), stack)) {
                    return i;
                }
            }

            return -1;
        }

        private int getMaxAmountToAdd (ItemStack stack) {
            return 1;
        }

        public int tryInsert (ItemStack stack) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemKey))
                return 0;

            int toAdd = Math.min(stack.getCount(), getMaxAmountToAdd(stack));
            if (toAdd == 0)
                return 0;

            int index = findStackIndex(stack);
            if (index >= 0)
                return 0;

            items.add(0, stack.split(toAdd));
            return toAdd;
        }

        public int tryTransfer (Slot slot, Player player) {
            ItemStack stack = slot.getItem();
            int toAdd = getMaxAmountToAdd(stack);
            return tryInsert(slot.safeTake(stack.getCount(), toAdd, player));
        }

        @Nullable
        public ItemStack removeOne () {
            if (items.isEmpty())
                return null;

            return items.remove(0).copy();
        }

        public KeyringContents toImmutable () {
            return new KeyringContents(List.copyOf(items));
        }
    }
}
