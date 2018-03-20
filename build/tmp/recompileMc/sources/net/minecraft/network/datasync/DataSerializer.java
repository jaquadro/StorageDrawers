package net.minecraft.network.datasync;

import net.minecraft.network.PacketBuffer;

public interface DataSerializer<T>
{
    void write(PacketBuffer buf, T value);

    T read(PacketBuffer buf) throws java.io.IOException;

    DataParameter<T> createKey(int id);
}