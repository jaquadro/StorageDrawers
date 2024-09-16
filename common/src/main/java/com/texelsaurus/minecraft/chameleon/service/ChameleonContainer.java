package com.texelsaurus.minecraft.chameleon.service;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ChameleonContainer
{
    <C extends AbstractContainerMenu> Supplier<MenuType<C>> getContainerSupplier(ChameleonContainerFactory<C> factory);

    void openMenu(Player player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraData);

    interface ChameleonContainerFactory<T extends AbstractContainerMenu> extends MenuType.MenuSupplier<T> {
        T create(int windowId, Inventory playerInv, RegistryFriendlyByteBuf data);

        default T create(int windowId, Inventory playerInv) {
            return this.create(windowId, playerInv, null);
        }
    }
}
