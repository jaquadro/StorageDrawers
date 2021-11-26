package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class CapabilityDrawerAttributes
{
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register () {
        CapabilityManager.INSTANCE.register(IDrawerAttributes.class, new DefaultStorage(), BasicDrawerAttributes::new);
    }

    private static class DefaultStorage implements Capability.IStorage<IDrawerAttributes>
    {
        @Nullable
        @Override
        public INBT writeNBT (Capability<IDrawerAttributes> capability, IDrawerAttributes instance, Direction side) {
            if (instance instanceof INBTSerializable)
                return ((INBTSerializable) instance).serializeNBT();

            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean("lockedPop", instance.isItemLocked(LockAttribute.LOCK_POPULATED));
            tag.putBoolean("lockedEmpty", instance.isItemLocked(LockAttribute.LOCK_EMPTY));
            tag.putBoolean("concealed", instance.isConcealed());
            tag.putBoolean("void", instance.isVoid());
            tag.putBoolean("quant", instance.isShowingQuantity());
            tag.putBoolean("unlimited", instance.isUnlimitedStorage());
            tag.putBoolean("vending", instance.isUnlimitedVending());

            return tag;
        }

        @Override
        public void readNBT (Capability<IDrawerAttributes> capability, IDrawerAttributes instance, Direction side, INBT nbt) {
            if (instance instanceof INBTSerializable) {
                @SuppressWarnings("unchecked")
                INBTSerializable<INBT> serializer = (INBTSerializable)instance;
                serializer.deserializeNBT(nbt);
                return;
            }

            if (!(instance instanceof IDrawerAttributesModifiable))
                throw new RuntimeException("IDrawerAttributes instance does not implement IDrawerAttributesModifiable");
            IDrawerAttributesModifiable modifiable = (IDrawerAttributesModifiable) instance;

            CompoundNBT tag = (CompoundNBT) nbt;
            modifiable.setItemLocked(LockAttribute.LOCK_POPULATED, tag.getBoolean("lockedPop"));
            modifiable.setItemLocked(LockAttribute.LOCK_EMPTY, tag.getBoolean("lockedEmpty"));
            modifiable.setIsConcealed(tag.getBoolean("concealed"));
            modifiable.setIsVoid(tag.getBoolean("void"));
            modifiable.setIsShowingQuantity(tag.getBoolean("quant"));
            modifiable.setIsUnlimitedStorage(tag.getBoolean("unlimited"));
            modifiable.setIsUnlimitedVending(tag.getBoolean("vending"));
        }
    }
}
