package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
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
    @NotNull
    public Component getDescription() {
        if (securityProvider != null) {
            if (securityProvider.equals("cofh") /* && !LocalIntegrationRegistry.isModLoaded("cofh_core") */)
                return Component.translatable("itemConfig.storagedrawers.disabled_upgrade").withStyle(ChatFormatting.RED);
        }

        return super.getDescription();
    }
}