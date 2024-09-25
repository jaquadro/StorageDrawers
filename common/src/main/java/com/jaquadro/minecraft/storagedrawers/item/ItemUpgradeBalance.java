package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ItemUpgradeBalance extends ItemUpgrade
{
    public ItemUpgradeBalance (Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public Component getDescription() {
        return ModCommonConfig.INSTANCE.UPGRADES.enableBalanceUpgrade.get()
            ? Component.translatable("item.storagedrawers.balance_fill_upgrade.desc")
            : Component.translatable("itemConfig.storagedrawers.disabled_upgrade").withStyle(ChatFormatting.RED);
    }
}
