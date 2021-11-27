package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository.ItemRecord;

public class CapabilityItemRepository
{
    public static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register () {
        CapabilityManager.INSTANCE.register(IItemRepository.class, new DefaultStorage(), DefaultImplementation::new);
    }

    private static class DefaultStorage implements Capability.IStorage<IItemRepository>
    {
        @Nullable
        @Override
        public Tag writeNBT (Capability<IItemRepository> capability, IItemRepository instance, Direction side) {
            if (instance instanceof INBTSerializable)
                return ((INBTSerializable) instance).serializeNBT();

            return new CompoundTag();
        }

        @Override
        public void readNBT (Capability<IItemRepository> capability, IItemRepository instance, Direction side, Tag nbt) {
            if (instance instanceof INBTSerializable) {
                @SuppressWarnings("unchecked")
                INBTSerializable<Tag> serializer = (INBTSerializable)instance;
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
