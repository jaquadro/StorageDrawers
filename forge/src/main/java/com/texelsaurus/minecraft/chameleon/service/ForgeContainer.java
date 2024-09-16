package com.texelsaurus.minecraft.chameleon.service;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeContainer implements ChameleonContainer
{
    @Override
    public <C extends AbstractContainerMenu> Supplier<MenuType<C>> getContainerSupplier (ChameleonContainerFactory<C> factory) {
        IContainerFactory<C> wrapped = new PlatformContainerFactory<>(factory);
        return () -> IForgeMenuType.create(wrapped);
    }

    @Override
    public void openMenu (Player player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraData) {
        ((ServerPlayer)player).openMenu(menuProvider, extraData);
    }

    static class PlatformContainerFactory<T extends AbstractContainerMenu> implements ChameleonContainerFactory<T>, IContainerFactory<T> {
        ChameleonContainerFactory<T> factory;

        public PlatformContainerFactory(ChameleonContainerFactory<T> factory) {
            this.factory = factory;
        }

        /*@Override
        public T create (int windowId, Inventory playerInv, RegistryFriendlyByteBuf data) {
            return factory.create(windowId, playerInv, data);
        }*/

        @Override
        public T create (int windowId, Inventory playerInv, FriendlyByteBuf data) {
            return factory.create(windowId, playerInv, data);
        }

        @Override
        public T create (int windowId, Inventory playerInv) {
            return create(windowId, playerInv, null);
        }
    }
}
