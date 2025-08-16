package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum EnumKeyType implements StringRepresentable
{
    DRAWER(0, "drawer"),
    CONCEALMENT(1, "concealment"),
    PERSONAL(2, "personal"),
    QUANTIFY(3, "quantify");

    private static final EnumKeyType[] META_LOOKUP;

    private final int meta;
    private final String name;

    EnumKeyType (int meta, String name) {
        this.meta = meta;
        this.name = name;
    }

    public int getMetadata () {
        return meta;
    }

    public static EnumKeyType byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    @Override
    @NotNull
    public String getSerializedName () {
        return name;
    }

    @Override
    public String toString () {
        return getSerializedName();
    }

    public boolean isEnabled () {
        boolean keyEnabled = false;
        if (this == EnumKeyType.DRAWER)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.drawerKey.enable.get();
        else if (this == EnumKeyType.QUANTIFY)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.quantifyKey.enable.get();
        else if (this == EnumKeyType.CONCEALMENT)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.concealmentKey.enable.get();
        else if (this == EnumKeyType.PERSONAL)
            keyEnabled = ModCommonConfig.INSTANCE.TOOLS.personalKey.enable.get();

        return keyEnabled;
    }

    static {
        META_LOOKUP = new EnumKeyType[values().length];
        for (EnumKeyType type : values()) {
            META_LOOKUP[type.getMetadata()] = type;
        }
    }
}
