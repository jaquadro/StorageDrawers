package com.jaquadro.minecraft.storagedrawers.api.framing;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum FrameMaterial implements StringRepresentable
{
    SIDE("side", "MatS"),
    TRIM("trim", "MatT"),
    FRONT("front", "MatF"),
    ;

    private final String name;
    private final String tagKey;

    FrameMaterial (String name, String tagKey) {
        this.name = name;
        this.tagKey = tagKey;
    }

    @Override
    public @NotNull String getSerializedName () {
        return name;
    }

    public String getTagKey () {
        return tagKey;
    }
}
