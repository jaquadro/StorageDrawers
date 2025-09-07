package com.jaquadro.minecraft.storagedrawers.api.storage.attribute;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.SidedConnectData;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public enum ConnectionMode implements StringRepresentable
{
    DEFAULT(0, 2, "default", true, true, false, false),
    NONE(1, 0, "none", false, false, false, false),
    INPUT(2, 3, "input", true, false, false, true),
    OUTPUT(3, 4, "output", false, true, true, false),
    BOTH(4, 1, "both", true, true, true, true);

    public static final EnumCodec<ConnectionMode> CODEC = StringRepresentable.fromEnum(ConnectionMode::values);
    private static final ConnectionMode[] VALUES = values();

    private int data;
    private int cycleNext;
    private boolean extCanPush;
    private boolean extCanPull;
    private boolean intCanPush;
    private boolean intCanPull;
    private String name;

    ConnectionMode (int data, int cycleNext, String name, boolean extCanPush, boolean extCanPull, boolean intCanPush, boolean intCanPull) {
        this.data = data;
        this.cycleNext = cycleNext;
        this.name = name;
        this.extCanPush = extCanPush;
        this.extCanPull = extCanPull;
        this.intCanPush = intCanPush;
        this.intCanPull = intCanPull;
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

    public boolean canExtPush () {
        return extCanPush;
    }

    public boolean canExtPull () {
        return extCanPull;
    }

    public boolean canIntPush () {
        return intCanPush;
    }

    public boolean canIntPull () {
        return intCanPull;
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
