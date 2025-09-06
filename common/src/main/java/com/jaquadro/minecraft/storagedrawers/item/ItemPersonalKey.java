package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModSecurity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class ItemPersonalKey extends ItemKey
{
    private final String securityProvider;

    public ItemPersonalKey (String securityProvider, Item.Properties properties) {
        super(properties);
        this.securityProvider = securityProvider;
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs, UseOnContext context) {
        attrs.setIsShowingQuantity(!attrs.isShowingQuantity());
    }

    public String getSecurityProviderKey () {
        return securityProvider;
    }

    @Override
    public boolean isEnabled () {
        if (securityProvider != null && !securityProvider.equals("unlock")) {
            if (ModSecurity.registry.getProvider(securityProvider) == null)
                return false;
        }
        return ModCommonConfig.INSTANCE.TOOLS.personalKey.enable.get();
    }
}