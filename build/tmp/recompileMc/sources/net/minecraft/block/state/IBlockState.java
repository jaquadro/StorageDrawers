package net.minecraft.block.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public interface IBlockState extends IBlockBehaviors, IBlockProperties
{
    Collection < IProperty<? >> getPropertyKeys();

    /**
     * Get the value of the given Property for this BlockState
     */
    <T extends Comparable<T>> T getValue(IProperty<T> property);

    /**
     * Get a version of this BlockState with the given Property now set to the given value
     */
    <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value);

    /**
     * Create a version of this BlockState with the given property cycled to the next value in order. If the property
     * was at the highest possible value, it is set to the lowest one instead.
     */
    <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property);

    ImmutableMap < IProperty<?>, Comparable<? >> getProperties();

    Block getBlock();
}