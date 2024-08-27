package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemUpgradeBalance extends ItemUpgrade
{
    public ItemUpgradeBalance(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @NotNull
    public Component getDescription() {
        return CommonConfig.UPGRADES.enableBalanceUpgrade.get()
            ? Component.translatable("item.storagedrawers.balance_fill_upgrade.desc")
            : Component.translatable("itemConfig.storagedrawers.disabled_upgrade").withStyle(ChatFormatting.RED);
    }
}
