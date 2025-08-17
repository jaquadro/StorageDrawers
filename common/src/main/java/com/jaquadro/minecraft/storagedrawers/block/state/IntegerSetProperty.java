package com.jaquadro.minecraft.storagedrawers.block.state;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IntegerSetProperty extends Property<Integer>
{
    private final ImmutableSet<Integer> values;

    protected IntegerSetProperty(String name, Set<Integer> validValues) {
        super(name, Integer.class);
        this.values = ImmutableSet.copyOf(validValues);
    }

    public Collection<Integer> getPossibleValues() {
        return this.values;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            if (other instanceof IntegerSetProperty otherProp) {
                if (super.equals(other))
                    return this.values.equals(otherProp.values);
            }

            return false;
        }
    }

    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }

    public static IntegerSetProperty create(String name, String valueList) {
        Set<Integer> values = Arrays.stream(valueList.split(",")).map(Integer::valueOf).collect(Collectors.toSet());
        return new IntegerSetProperty(name, values);
    }

    public Optional<Integer> getValue(String name) {
        try {
            Integer intVal = Integer.valueOf(name);
            return values.contains(intVal) ? Optional.of(intVal) : Optional.empty();
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public String getName(Integer value) {
        return value.toString();
    }
}
