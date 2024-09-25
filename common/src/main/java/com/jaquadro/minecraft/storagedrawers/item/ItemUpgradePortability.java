package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ItemUpgradePortability extends ItemUpgrade
{
    public ItemUpgradePortability (Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public Component getDescription() {
        return ModCommonConfig.INSTANCE.GENERAL.heavyDrawers.get()
            ? Component.translatable("item.storagedrawers.portability_upgrade.desc")
            : Component.translatable("itemConfig.storagedrawers.disabled_upgrade").withStyle(ChatFormatting.RED);
    }
}
