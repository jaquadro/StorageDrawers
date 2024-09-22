package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.inventory.ContainerContent;
import com.texelsaurus.minecraft.chameleon.inventory.ContainerContentSerializer;
import com.texelsaurus.minecraft.chameleon.inventory.ContentMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Optional;
import java.util.function.Supplier;

public interface ChameleonContainer
{
    <T extends AbstractContainerMenu, C extends ContainerContent<C>> Supplier<MenuType<T>> getContainerSupplier(ChameleonContainerFactory<T, C> factory, ContainerContentSerializer<C> serializer);

    <C extends ContainerContent<C>> void openMenu(Player player, ContentMenuProvider<C> menuProvider);

    interface ChameleonContainerFactory<T extends AbstractContainerMenu, C extends ContainerContent<C>> extends MenuType.MenuSupplier<T>
    {
        T create(int windowId, Inventory playerInv, Optional<C> content);

        default T create(int windowId, Inventory playerInv) {
            return this.create(windowId, playerInv, Optional.empty());
        }
    }
}
