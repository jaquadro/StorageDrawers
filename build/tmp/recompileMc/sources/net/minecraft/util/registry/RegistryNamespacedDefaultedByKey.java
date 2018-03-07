package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V>
{
    /** The key of the default value. */
    private final K defaultValueKey;
    /** The default value for this registry, retrurned in the place of a null value. */
    private V defaultValue;

    public RegistryNamespacedDefaultedByKey(K defaultValueKeyIn)
    {
        this.defaultValueKey = defaultValueKeyIn;
    }

    public void register(int id, K key, V value)
    {
        if (this.defaultValueKey.equals(key))
        {
            this.defaultValue = value;
        }

        super.register(id, key, value);
    }

    /**
     * validates that this registry's key is non-null
     */
    public void validateKey()
    {
        Validate.notNull(this.defaultValue, "Missing default of DefaultedMappedRegistry: " + this.defaultValueKey, new Object[0]);
    }

    /**
     * Gets the integer ID we use to identify the given object.
     */
    public int getIDForObject(V value)
    {
        int i = super.getIDForObject(value);
        return i == -1 ? super.getIDForObject(this.defaultValue) : i;
    }

    /**
     * Gets the name we use to identify the given object.
     */
    @Nonnull
    public K getNameForObject(V value)
    {
        K k = super.getNameForObject(value);
        return (K)(k == null ? this.defaultValueKey : k);
    }

    @Nonnull
    public V getObject(@Nullable K name)
    {
        V v = super.getObject(name);
        return (V)(v == null ? this.defaultValue : v);
    }

    /**
     * Gets the object identified by the given ID.
     */
    @Nonnull
    public V getObjectById(int id)
    {
        V v = super.getObjectById(id);
        return (V)(v == null ? this.defaultValue : v);
    }

    @Nonnull
    public V getRandomObject(Random random)
    {
        V v = super.getRandomObject(random);
        return (V)(v == null ? this.defaultValue : v);
    }

    //Bypass functions to allow querying this registry WITHOUT getting the defaulted value.
    // MODDERS DO NOT USE THIS IS FOR FORGE INTERNAL CHECKS
    public int getIDForObjectBypass(V bypass) { return super.getIDForObject(bypass); }
    public K getNameForObjectBypass(V value) { return super.getNameForObject(value); }
    public V getObjectBypass(K name) { return super.getObject(name); }
    public V getObjectByIdBypass(int id){ return super.getObjectById(id); }
}