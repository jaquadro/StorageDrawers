package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class CapabilityItemRepository
{
    @CapabilityInject(IItemRepository.class)
    public static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = null;

    public static void register () {
        CapabilityManager.INSTANCE.register(IItemRepository.class, new DefaultStorage(), DefaultImplementation::new);
    }

    private static class DefaultStorage implements Capability.IStorage<IItemRepository>
    {
        @Nullable
        @Override
        public INBT writeNBT (Capability<IItemRepository> capability, IItemRepository instance, Direction side) {
            if (instance instanceof INBTSerializable)
                return ((INBTSerializable) instance).serializeNBT();

            return new CompoundNBT();
        }

        @Override
        public void readNBT (Capability<IItemRepository> capability, IItemRepository instance, Direction side, INBT nbt) {
            if (instance instanceof INBTSerializable) {
                @SuppressWarnings("unchecked")
                INBTSerializable<INBT> serializer = (INBTSerializable)instance;
                serializer.deserializeNBT(nbt);
            }
        }
    }

    private static class DefaultImplementation implements IItemRepository
    {
        @Nonnull
        @Override
        public NonNullList<ItemRecord> getAllItems () {
            return NonNullList.create();
        }

        @Nonnull
        @Override
        public ItemStack insertItem (@Nonnull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem (@Nonnull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {
            return ItemStack.EMPTY;
        }
    }
}
