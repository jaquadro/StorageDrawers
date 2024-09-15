package com.texelsaurus.minecraft.chameleon.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigSpec
{
    private boolean loaded = false;
    private final List<Runnable> loadActions = new ArrayList<>();

    public void init() { }

    public void setLoaded() {
        if (!loaded)
            loadActions.forEach(Runnable::run);
        loaded = true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void onLoad(Runnable action) {
        if (loaded)
            action.run();
        else
            loadActions.add(action);
    }
}
