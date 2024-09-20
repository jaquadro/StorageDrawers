package com.texelsaurus.minecraft.chameleon.service;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricContainer implements ChameleonContainer
{
    @Override
    public <C extends AbstractContainerMenu> Supplier<MenuType<C>> getContainerSupplier (ChameleonContainerFactory<C> factory) {
        StreamCodec<RegistryFriendlyByteBuf, Optional<C>> streamCodec = StreamCodec.unit(Optional.empty());

        return () -> new ExtendedScreenHandlerType<>((id, inventory, data) -> {
            //return factory.create(id, inventory, null);
            return factory.create(id, inventory);
        }, streamCodec);
    }

    @Override
    public void openMenu (Player player, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraData) {
        player.openMenu(menuProvider);
    }
}
