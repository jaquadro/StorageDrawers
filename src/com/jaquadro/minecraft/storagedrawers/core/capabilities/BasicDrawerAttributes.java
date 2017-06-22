package com.jaquadro.minecraft.storagedrawers.core.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.EnumSet;

public class BasicDrawerAttributes implements IDrawerAttributes, IDrawerAttributesModifiable, INBTSerializable<NBTTagCompound>
{
    private EnumSet<LockAttribute> itemLock = EnumSet.noneOf(LockAttribute.class);
    private boolean isConcealed;
    private boolean isShowingQuantity;
    private boolean isVoid;

    @Override
    public boolean canItemLock (LockAttribute attr) {
        return true;
    }

    @Override
    public boolean isItemLocked (LockAttribute attr) {
        return itemLock.contains(attr);
    }

    @Override
    public boolean setItemLocked (LockAttribute attr, boolean isLocked) {
        if (isItemLocked(attr) != isLocked) {
            if (isLocked)
                itemLock.add(attr);
            else
                itemLock.remove(attr);

            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isConcealed () {
        return isConcealed;
    }

    @Override
    public boolean setIsConcealed (boolean state) {
        if (isConcealed != state) {
            isConcealed = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isVoid () {
        return isVoid;
    }

    @Override
    public boolean setIsVoid (boolean state) {
        if (isVoid != state) {
            isVoid = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public boolean isShowingQuantity () {
        return isShowingQuantity;
    }

    @Override
    public boolean setIsShowingQuantity (boolean state) {
        if (isShowingQuantity != state) {
            isShowingQuantity = state;
            onAttributeChanged();
        }

        return true;
    }

    @Override
    public NBTTagCompound serializeNBT () {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("itemLock", LockAttribute.getBitfield(itemLock));
        tag.setBoolean("concealed", isConcealed);
        tag.setBoolean("void", isVoid);
        tag.setBoolean("quant", isShowingQuantity);

        return tag;
    }

    @Override
    public void deserializeNBT (NBTTagCompound nbt) {
        itemLock = LockAttribute.getEnumSet(nbt.getInteger("itemLock"));
        isConcealed = nbt.getBoolean("concealed");
        isVoid = nbt.getBoolean("void");
        isShowingQuantity = nbt.getBoolean("quant");
    }

    protected void onAttributeChanged () { }
}
