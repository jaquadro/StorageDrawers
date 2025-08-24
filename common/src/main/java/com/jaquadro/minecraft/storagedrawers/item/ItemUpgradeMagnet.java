package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemUpgradeMagnet extends ItemUpgrade
{
    private static final int magnetGroupId;
    static {
        magnetGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeMagnet type;

    public ItemUpgradeMagnet (EnumUpgradeMagnet type, Properties properties) {
        this(type, properties, magnetGroupId);
    }

    protected ItemUpgradeMagnet (EnumUpgradeMagnet type, Properties properties, int groupId) {
        super(properties, groupId);

        setAllowMultiple(true);
        this.type = type;
    }

    @Override
    @NotNull
    public Component getDescription() {
        if (!isEnabled())
            return super.getDescription();

        return Component.translatable("item.storagedrawers.magnet_upgrade.desc");
    }

    @Override
    public void appendHoverText (ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (!isEnabled())
            return;

        String rangeId = ModItems.MAGNET_UPGRADE.get().getDescriptionId() + ".range";
        tooltip.add(Component.translatable(rangeId, buildRangeString()).withStyle(ChatFormatting.DARK_GRAY));

        String rateId = ModItems.MAGNET_UPGRADE.get().getDescriptionId() + ".speed";
        int activeSpeed = getActiveSpeed();
        tooltip.add(Component.translatable(rateId, 1, activeSpeed).withStyle(ChatFormatting.DARK_GRAY));
    }

    private String buildRangeString () {
        ArrayList<String> result = new ArrayList<>();
        int hRange = getHorzRange();
        int upRange = getUpRange();
        int downRange = getDownRange();

        if (hRange == upRange && hRange == downRange)
            return Integer.toString(hRange);

        if (hRange > 0)
            result.add(hRange + "↔");
        if (upRange > 0)
            result.add(upRange + "↑");
        if (downRange > 0)
            result.add(downRange + "↓");

        return String.join(", ", result);
    }

    @Override
    public boolean isEnabled () {
        return getConfig().enableUpgrade.get();
    }

    public int getActiveSpeed () {
        return getConfig().activeSpeed.get();
    }

    public int getIdleSpeed () {
        return getConfig().idleSpeed.get();
    }

    public int getHorzRange () {
        return getIndexedRange(0);
    }

    public int getUpRange () {
        return getIndexedRange(1);
    }

    public int getDownRange () {
        return getIndexedRange(2);
    }

    private int getIndexedRange (int index) {
        return getConfig().range.get().get(index);
    }

    private ModCommonConfig.Upgrades.MagnetTierUpgrade getConfig () {
        return switch (type) {
            case LEVEL1 -> ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.tier1;
            case LEVEL2 -> ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.tier2;
            case LEVEL3 -> ModCommonConfig.INSTANCE.UPGRADES.magnetUpgrade.tier3;
        };
    }
}