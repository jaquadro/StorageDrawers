package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ItemDrawerPuller extends ItemKey
{
    public ItemDrawerPuller (Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public Component getDescription() {
        return ModCommonConfig.INSTANCE.GENERAL.enableDetachedDrawers.get()
            ? Component.translatable("item.storagedrawers.drawer_puller.desc")
            : Component.translatable("itemConfig.storagedrawers.disabled_tool").withStyle(ChatFormatting.RED);
    }
}
