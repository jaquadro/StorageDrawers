package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelReader;
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

    // TODO: Forge extension
    // @Override
    public boolean doesSneakBypassUseItem (ItemStack stack, LevelReader level, BlockPos pos) {
        return true;
    }

    public int getUpgradeGroup() {
        return groupId;
    }

    @Override
    public void appendHoverText (ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ComponentUtil.appendSplitDescription(tooltip, this);
    }

    @NotNull
    public Component getDescription() {
        return isEnabled()
            ? Component.translatable(this.getDescriptionId() + ".desc")
            : Component.translatable("itemConfig.storagedrawers.disabled_upgrade").withStyle(ChatFormatting.RED);
    }

    public boolean isEnabled () {
        return true;
    }

    public void setAllowMultiple (boolean allow) {
        allowMultiple = allow;
    }

    public boolean getAllowMultiple () {
        return allowMultiple;
    }
}
