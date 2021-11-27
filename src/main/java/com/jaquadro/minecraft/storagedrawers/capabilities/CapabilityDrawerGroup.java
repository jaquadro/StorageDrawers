package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup.DrawerData;

public class CapabilityDrawerGroup
{
    public static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register () {
        CapabilityManager.INSTANCE.register(IDrawerGroup.class, new DefaultStorage(), DefaultImplementation::new);
    }

    private static class DefaultStorage implements Capability.IStorage<IDrawerGroup>
    {
        @Nullable
        @Override
        public Tag writeNBT (Capability<IDrawerGroup> capability, IDrawerGroup instance, Direction side) {
            if (instance instanceof INBTSerializable)
                return ((INBTSerializable) instance).serializeNBT();

            return new CompoundTag();
        }

        @Override
        public void readNBT (Capability<IDrawerGroup> capability, IDrawerGroup instance, Direction side, Tag nbt) {
            if (instance instanceof INBTSerializable) {
                @SuppressWarnings("unchecked")
                INBTSerializable<Tag> serializer = (INBTSerializable)instance;
                serializer.deserializeNBT(nbt);
            }
        }
    }

    private static class DefaultImplementation extends StandardDrawerGroup
    {
        DefaultImplementation () {
            super(1);
        }

        @Nonnull
        @Override
        protected DrawerData createDrawer (int slot) {
            return new StandardDrawerGroup.DrawerData(this);
        }
    }
}
