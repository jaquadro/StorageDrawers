package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityDrawerGroup
{
    @CapabilityInject(IDrawerGroup.class)
    public static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = null;

    public static void register () {
        CapabilityManager.INSTANCE.register(IDrawerGroup.class, new DefaultStorage(), DefaultImplementation::new);
    }

    private static class DefaultStorage implements Capability.IStorage<IDrawerGroup>
    {
        @Nullable
        @Override
        public NBTBase writeNBT (Capability<IDrawerGroup> capability, IDrawerGroup instance, EnumFacing side) {
            if (instance instanceof INBTSerializable)
                return ((INBTSerializable) instance).serializeNBT();

            return new NBTTagCompound();
        }

        @Override
        public void readNBT (Capability<IDrawerGroup> capability, IDrawerGroup instance, EnumFacing side, NBTBase nbt) {
            if (instance instanceof INBTSerializable) {
                @SuppressWarnings("unchecked")
                INBTSerializable<NBTBase> serializer = (INBTSerializable)instance;
                serializer.deserializeNBT(nbt);
                return;
            }
        }
    }

    private static class DefaultImplementation extends StandardDrawerGroup
    {
        public DefaultImplementation () {
            super(1);
        }

        @Nonnull
        @Override
        protected DrawerData createDrawer (int slot) {
            return new StandardDrawerGroup.DrawerData(this);
        }
    }
}
