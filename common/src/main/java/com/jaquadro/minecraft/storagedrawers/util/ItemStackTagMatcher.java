package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.config.ConversionRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemStackTagMatcher extends ItemStackMatcher
{
    private List<ItemStack> tagMatches;

    public ItemStackTagMatcher (ItemStack stack) {
        super(stack);
        refreshTagMatches();
    }

    @Override
    public boolean matches (ItemStack stack) {
        if (!ItemStack.isSameItem(this.stack, stack)) {
            if (tagMatches == null)
                return false;
            if (this.stack.getItem() == stack.getItem())
                return false;

            boolean tagMatch = false;
            for (ItemStack tagStack : tagMatches) {
                if (ItemStack.isSameItem(stack, tagStack)) {
                    tagMatch = true;
                    break;
                }
            }

            return tagMatch;
        }

        return ItemStack.isSameItemSameComponents(this.stack, stack);
    }

    public void refreshTagMatches () {
        if (stack.isEmpty()) {
            tagMatches = null;
            return;
        }

        tagMatches = ConversionRegistry.INSTANCE.getEquivItems(stack.getItem());

        List<TagKey<Item>> tags = stack.typeHolder().tags().toList();
        for (TagKey<Item> tag : tags) {
            if (!ConversionRegistry.INSTANCE.isEntryValid(tag))
                continue;

            if (BuiltInRegistries.ITEM.get(tag).isPresent()) {
                BuiltInRegistries.ITEM.get(tag).get().stream().forEach(e -> {
                    tagMatches.add(new ItemStack(e));
                });
            }
        }

        if (tagMatches.isEmpty())
            tagMatches = null;
    }
}