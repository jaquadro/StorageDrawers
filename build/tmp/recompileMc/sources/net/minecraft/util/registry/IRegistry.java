package net.minecraft.util.registry;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRegistry<K, V> extends Iterable<V>
{
    @Nullable
    @SideOnly(Side.CLIENT)
    V getObject(K name);

    /**
     * Register an object on this registry.
     */
    void putObject(K key, V value);

    /**
     * Gets all the keys recognized by this registry.
     */
    @SideOnly(Side.CLIENT)
    Set<K> getKeys();
}