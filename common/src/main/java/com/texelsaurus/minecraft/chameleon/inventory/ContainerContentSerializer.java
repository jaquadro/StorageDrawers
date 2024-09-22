package com.texelsaurus.minecraft.chameleon.inventory;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public interface ContainerContentSerializer<T extends ContainerContent<T>>
{
    @Nullable
    T from (FriendlyByteBuf buf);

    void to(FriendlyByteBuf buf, T content);
}
