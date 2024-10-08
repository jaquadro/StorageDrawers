package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Supplier;

public class FabricRegistry<T> implements ChameleonRegistry<T>
{
    private final String modid;
    private final Registry<T> registry;
    private final RegistryEntryList<T> entries = new RegistryEntryList<>();

    public FabricRegistry(Registry<T> registry, String id) {
        this.modid = id;
        this.registry = registry;
    }

    @Override
    public <C extends T> RegistryEntry<C> register (String id, Supplier<C> supplier) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(modid, id);
        return entries.add(new FabricRegistryEntry<>(loc,supplier));
    }

    @Override
    public Collection<RegistryEntry<T>> getEntries () {
        return entries.getEntries();
    }

    @Override
    public void init (InitContext context) {
        for (RegistryEntry<T> entry : entries.getEntries())
            Registry.register(registry, entry.getId(), entry.get());
    }
}
