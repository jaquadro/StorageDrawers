package com.texelsaurus.minecraft.chameleon.inventory;

public interface ContainerContent<T extends ContainerContent<T>>
{
    ContainerContentSerializer<T> serializer();
}
