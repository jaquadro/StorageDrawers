package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemUpgrade extends Item
{
    private static int nextGroupId = 0;

    private boolean allowMultiple;
    private final int groupId;

    public ItemUpgrade (Properties properties) {
        this(properties, getNextGroupId());
    }

    protected ItemUpgrade (Properties properties, int groupId) {
        super(properties);
        this.groupId = groupId;
    }

    protected static int getNextGroupId () {
        int groupId = nextGroupId;
        nextGroupId += 1;
        return groupId;
    }

    public int getUpgradeGroup() {
        return groupId;
    }

    @Override
    public void appendHoverText (ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("").append(getDescription()).withStyle(ChatFormatting.GRAY));
    }

    @NotNull
    public Component getDescription() {
        return Component.translatable(this.getDescriptionId() + ".desc");
    }

    public void setAllowMultiple (boolean allow) {
        allowMultiple = allow;
    }

    public boolean getAllowMultiple () {
        return allowMultiple;
    }
}
