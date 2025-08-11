package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ItemPersonalKey extends ItemKey
{
    private final String securityProvider;

    public ItemPersonalKey (String securityProvider, Properties properties) {
        super(properties);
        this.securityProvider = securityProvider;
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        attrs.setIsShowingQuantity(!attrs.isShowingQuantity());
    }

    public String getSecurityProviderKey () {
        return securityProvider;
    }

    @Override
    public boolean isEnabled () {
        if (securityProvider != null && securityProvider.equals("cofh"))
            return false;
        return ModCommonConfig.INSTANCE.TOOLS.personalKey.enable.get();
    }
}