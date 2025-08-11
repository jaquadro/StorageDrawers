package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MaterialBlacklist extends ConfigItemList
{
    public static final MaterialBlacklist INSTANCE = new MaterialBlacklist();

    @Override
    protected void innerInitialize () {
        ModCommonConfig.INSTANCE.onLoad(() -> ModCommonConfig.INSTANCE.DRAWERS.framed.materialBlacklist.get().forEach(this::register));
    }

    public boolean isBlacklisted (ItemStack stack) {
        return isListed(stack);
    }

    @Override
    protected void logRegisterNamespace (@NotNull String namespace) {
        ModServices.log.info("New blacklisted framing material namespace " + namespace);
    }

    @Override
    protected void logRegisterItem (@NotNull ItemStack item) {
        ModServices.log.info("New blacklisted framing material item " + item.getItem());
    }

}
