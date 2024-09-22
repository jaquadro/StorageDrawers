package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.inventory.ContainerContent;
import com.texelsaurus.minecraft.chameleon.inventory.ContainerContentSerializer;
import com.texelsaurus.minecraft.chameleon.inventory.ContentMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class NeoforgeContainer implements ChameleonContainer
{
    @Override
    public <T extends AbstractContainerMenu, C extends ContainerContent<C>> Supplier<MenuType<T>> getContainerSupplier (ChameleonContainerFactory<T, C> factory, ContainerContentSerializer<C> serializer) {
        return () -> IMenuTypeExtension.create((id, inventory, data) -> {
            if (serializer != null)
                return factory.create(id, inventory, Optional.ofNullable(serializer.from(data)));
            return factory.create(id, inventory, Optional.empty());
        });
    }

    @Override
    public <C extends ContainerContent<C>> void openMenu (Player player, ContentMenuProvider<C> menuProvider) {
        player.openMenu(new PlatformContainerFactory<>(menuProvider), buf -> {
            C content = menuProvider.createContent((ServerPlayer) player);
            if (content != null)
                content.serializer().to(buf, content);
        });
    }

    private record PlatformContainerFactory<T extends ContainerContent<T>> (ContentMenuProvider<T> provider) implements MenuProvider
    {
        @Override
        public Component getDisplayName () {
            return provider.getDisplayName();
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu (int i, Inventory inventory, Player player) {
            return provider.createMenu(i, inventory, player);
        }

        @Override
        public boolean shouldTriggerClientSideContainerClosingOnOpen () {
            return true;
        }
    }
}
