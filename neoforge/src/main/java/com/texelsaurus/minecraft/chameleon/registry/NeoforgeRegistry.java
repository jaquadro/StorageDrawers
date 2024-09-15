package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.core.Registry;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

public class NeoforgeRegistry<T> implements ChameleonRegistry<T>
{
    private final DeferredRegister<T> register;
    private final RegistryEntryList<T> entries = new RegistryEntryList<>();

    public NeoforgeRegistry(Registry<T> registry, String id) {
        register = DeferredRegister.create(registry.key(), id);
    }

    @Override
    public <C extends T> RegistryEntry<C> register (String id, Supplier<C> supplier) {
        return entries.add(new NeoforgeRegistryEntry<>(register.register(id, supplier)));
    }

    @Override
    public Collection<RegistryEntry<T>> getEntries () {
        return entries.getEntries();
    }

    @Override
    public void init () {
        register.register(ModLoadingContext.get().getActiveContainer().getEventBus());
    }
}
