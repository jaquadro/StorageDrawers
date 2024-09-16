package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.core.Registry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

public class ForgeRegistry<T> implements ChameleonRegistry<T>
{
    private final DeferredRegister<T> register;
    private final RegistryEntryList<T> entries = new RegistryEntryList<>();

    public ForgeRegistry (Registry<T> registry, String id) {
        register = DeferredRegister.create(registry.key(), id);
    }

    @Override
    public <C extends T> RegistryEntry<C> register (String id, Supplier<C> supplier) {
        return entries.add(new ForgeRegistryEntry<>(register.register(id, supplier)));
    }

    @Override
    public Collection<RegistryEntry<T>> getEntries () {
        return entries.getEntries();
    }

    @Override
    public void init () {
        FMLModContainer container = (FMLModContainer)ModLoadingContext.get().getActiveContainer();
        register.register(container.getEventBus());
    }
}
