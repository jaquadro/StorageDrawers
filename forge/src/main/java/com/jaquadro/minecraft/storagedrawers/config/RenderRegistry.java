package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.api.registry.IRenderRegistry;
import com.jaquadro.minecraft.storagedrawers.api.render.IRenderLabel;

import java.util.ArrayList;
import java.util.List;

public class RenderRegistry implements IRenderRegistry
{
    private final List<IRenderLabel> registry = new ArrayList<>();

    @Override
    public void registerPreLabelRenderHandler (IRenderLabel renderHandler) {
        registry.add(renderHandler);
    }

    public List<IRenderLabel> getRenderHandlers () {
        return registry;
    }
}
