package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemUpgradePush extends ItemUpgrade
{
    private static final int upgradeGroupId;
    static {
        upgradeGroupId = ItemUpgrade.getNextGroupId();
    }

    public final int level;

    public ItemUpgradePush (int level, Properties properties) {
        this(level, properties, upgradeGroupId);
    }

    protected ItemUpgradePush (int level, Properties properties, int groupId) {
        super(properties, groupId);

        setAllowMultiple(false);
        this.level = level;
    }

    @Override
    public boolean isEnabled () {
        return getConfig().enableUpgrade.get();
    }

    @Override
    public Component getDescription() {
        if (!isEnabled())
            return super.getDescription();

        return Component.translatable("item.storagedrawers.push_upgrade.desc");
    }

    @Override
    public void appendHoverText (@NotNull ItemStack itemStack, @Nullable Level world, List<Component> list, TooltipFlag advanced) {
        super.appendHoverText(itemStack, world, list, advanced);
        if (!isEnabled())
            return;

        String rateId = ModItems.PUSH_UPGRADE.get().getDescriptionId() + ".rate";
        list.add(Component.translatable(rateId, getItemRate(), getActiveSpeed()).withStyle(ChatFormatting.DARK_GRAY));
    }

    public int getActiveSpeed () {
        return getConfig().activeSpeed.get();
    }

    public int getIdleSpeed () {
        return getConfig().idleSpeed.get();
    }

    public int getItemRate () {
        return getConfig().itemRate.get();
    }

    private ModCommonConfig.Upgrades.ItemTransferUpgrade getConfig () {
        return switch (level) {
            case 1 -> ModCommonConfig.INSTANCE.UPGRADES.pushUpgrade.tier1;
            case 2 -> ModCommonConfig.INSTANCE.UPGRADES.pushUpgrade.tier2;
            case 3 -> ModCommonConfig.INSTANCE.UPGRADES.pushUpgrade.tier3;
            default -> null;
        };
    }
}
