package com.jaquadro.minecraft.storagedrawers.core.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class CapabilityDrawerAttributes
{
    @CapabilityInject(IDrawerAttributes.class)
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    public static void register () {
        CapabilityManager.INSTANCE.register(IDrawerAttributes.class, new DefaultStorage(), BasicDrawerAttributes::new);
    }

    private static class DefaultStorage implements Capability.IStorage<IDrawerAttributes>
    {
        @Nullable
        @Override
        public NBTBase writeNBT (Capability<IDrawerAttributes> capability, IDrawerAttributes instance, EnumFacing side) {
            if (instance instanceof INBTSerializable)
                return ((INBTSerializable) instance).serializeNBT();

            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("lockedPop", instance.isItemLocked(LockAttribute.LOCK_POPULATED));
            tag.setBoolean("lockedEmpty", instance.isItemLocked(LockAttribute.LOCK_EMPTY));
            tag.setBoolean("concealed", instance.isConcealed());
            tag.setBoolean("void", instance.isVoid());
            tag.setBoolean("quant", instance.isShowingQuantity());

            return tag;
        }

        @Override
        public void readNBT (Capability<IDrawerAttributes> capability, IDrawerAttributes instance, EnumFacing side, NBTBase nbt) {
            if (instance instanceof INBTSerializable) {
                @SuppressWarnings("unchecked")
                INBTSerializable<NBTBase> serializer = (INBTSerializable)instance;
                serializer.deserializeNBT(nbt);
                return;
            }

            if (!(instance instanceof IDrawerAttributesModifiable))
                throw new RuntimeException("IDrawerAttributes instance does not implement IDrawerAttributesModifiable");
            IDrawerAttributesModifiable modifiable = (IDrawerAttributesModifiable) instance;

            NBTTagCompound tag = (NBTTagCompound)nbt;
            modifiable.setItemLocked(LockAttribute.LOCK_POPULATED, tag.getBoolean("lockedPop"));
            modifiable.setItemLocked(LockAttribute.LOCK_EMPTY, tag.getBoolean("lockedEmpty"));
            modifiable.setIsConcealed(tag.getBoolean("concealed"));
            modifiable.setIsVoid(tag.getBoolean("void"));
            modifiable.setIsShowingQuantity(tag.getBoolean("quant"));
        }
    }
}
