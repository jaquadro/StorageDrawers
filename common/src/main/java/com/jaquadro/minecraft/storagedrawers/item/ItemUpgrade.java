package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemUpgrade extends Item
{
    private static int nextGroupId = 0;

    private boolean allowMultiple;
    private final int groupId;

    public ItemUpgrade (Item.Properties properties) {
        this(properties, getNextGroupId());
    }

    protected ItemUpgrade (Item.Properties properties, int groupId) {
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
    public void appendHoverText (@NotNull ItemStack itemStack, @Nullable Level world, List<Component> list, TooltipFlag advanced) {
        super.appendHoverText(itemStack, world, list, advanced);
        ComponentUtil.appendSplitDescription(list, this);

        if (!isEnabled())
            list.add(Component.translatable("itemConfig.storagedrawers.disabled_upgrade")
                .withStyle(ChatFormatting.YELLOW));
    }

    @NotNull
    public Component getDescription() {
        return isEnabled()
            ? Component.translatable(this.getDescriptionId() + ".desc") : Component.empty();
    }

    // TODO: Forge extension
    //@Override
    public boolean doesSneakBypassUse (ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
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
