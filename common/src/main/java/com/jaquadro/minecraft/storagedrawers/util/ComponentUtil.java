package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ComponentUtil
{
    public static List<MutableComponent> getSplitDescription (Item item) {
        Component description = item.getDescription();
        if (description.equals(Component.empty()))
            return new LinkedList<>();

        return Arrays.stream(description.getString().split("\n")).map(Component::literal).toList();
    }

    public static void appendSplitDescription (List<Component> list, Item item) {
        appendSplitDescription(list, item, ChatFormatting.GRAY);
    }

    public static void appendSplitDescription (List<Component> list, Item item, ChatFormatting formatting) {
        for (MutableComponent component : getSplitDescription(item))
            list.add(component.withStyle(formatting));
    }
}