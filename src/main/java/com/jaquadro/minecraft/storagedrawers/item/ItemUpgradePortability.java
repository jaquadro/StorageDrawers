package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemUpgradePortability extends ItemUpgrade
{
    public ItemUpgradePortability(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @NotNull
    public Component getDescription() {
        return CommonConfig.GENERAL.heavyDrawers.get()
            ? Component.translatable("item.storagedrawers.portability_upgrade.desc")
            : Component.translatable("item.storagedrawers.portability_upgrade.warn").withStyle(ChatFormatting.RED);
    }
}
