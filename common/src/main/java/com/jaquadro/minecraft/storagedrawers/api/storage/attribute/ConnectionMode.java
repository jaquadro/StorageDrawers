package com.jaquadro.minecraft.storagedrawers.api.storage.attribute;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.SidedConnectData;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public enum ConnectionMode implements StringRepresentable
{
    DEFAULT(0, 2, "default"),
    NONE(1, 0, "none"),
    INPUT(2, 3, "input"),
    OUTPUT(3, 4, "output"),
    BOTH(4, 1, "both");

    public static final EnumCodec<ConnectionMode> CODEC = StringRepresentable.fromEnum(ConnectionMode::values);
    private static final ConnectionMode[] VALUES = values();

    private int data;
    private int cycleNext;
    private String name;

    ConnectionMode (int data, int cycleNext, String name) {
        this.data = data;
        this.cycleNext = cycleNext;
        this.name = name;
    }

    public int getData () {
        return data;
    }

    public String getName () {
        return name;
    }

    public ConnectionMode getNextMode () {
        return VALUES[cycleNext];
    }

    @Nullable
    public static ConnectionMode byName (@Nullable String name) {
        return CODEC.byName(name);
    }

    @Override
    public String getSerializedName () {
        return name;
    }
}
