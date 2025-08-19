package com.jaquadro.minecraft.storagedrawers.security;

import com.jaquadro.minecraft.storagedrawers.api.security.IInteractionProvider;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SecurityRegistry
{
    private final Map<String, ISecurityProvider> registry = new HashMap<>();
    private final Map<String, IInteractionProvider> interactRegistry = new HashMap<>();

    public void registerProvider (ISecurityProvider provider) {
        registry.put(provider.getProviderID(), provider);
    }

    public void registerProvider (IInteractionProvider provider) {
        interactRegistry.put(provider.getProviderID(), provider);
    }

    public ISecurityProvider getProvider (String name) {
        return registry.get(name);
    }

    public IInteractionProvider getInteractionProvider (String name) {
        return interactRegistry.get(name);
    }

    public Collection<IInteractionProvider> getAllInteractionProviders() {
        return interactRegistry.values();
    }
}
