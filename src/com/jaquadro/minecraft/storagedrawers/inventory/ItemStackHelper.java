package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class ItemStackHelper
{
    private static boolean initialized;
    private static Field itemField;
    private static Field itemDamageField;
    private static Field stackTagCompoundField;
    private static Field capabilitiesField;

    public static Item getTrueItem (@Nonnull ItemStack stack) {
        if (!initialized)
            return stack.getItem();

        try {
            return (Item)itemField.get(stack);
        } catch (IllegalAccessException e) {
            return stack.getItem();
        }
    }

    @Nonnull
    public static ItemStack getItemPrototype (@Nonnull ItemStack stack) {
        if (!initialized)
            return stack.copy();

        try {
            CapabilityDispatcher capabilities = (CapabilityDispatcher) capabilitiesField.get(stack);
            Item item = (Item) itemField.get(stack);
            int itemDamage = itemDamageField.getInt(stack);
            NBTTagCompound stackTagCompound = (NBTTagCompound) stackTagCompoundField.get(stack);

            ItemStack proto = new ItemStack(item, 1, itemDamage, capabilities != null ? capabilities.serializeNBT() : null);
            if (stackTagCompound != null)
                proto.setTagCompound(stackTagCompound);

            return proto;
        } catch (IllegalAccessException e) {
            return stack.copy();
        }
    }

    @Nonnull
    public static ItemStack encodeItemStack (@Nonnull ItemStack stack) {
        if (!stack.func_190926_b())
            return stack;

        ItemStack proto = getItemPrototype(stack);
        if (proto.func_190926_b())
            return stack;

        NBTTagCompound tag = proto.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            proto.setTagCompound(tag);
        }

        tag.setInteger("__storagedrawers_count", 0);

        return proto;
    }

    public static ItemStack encodeItemStack (@Nonnull ItemStack proto, int count) {
        if (!proto.func_190926_b() && count > 0) {
            ItemStack stack = proto.copy();
            stack.func_190920_e(count);
            return stack;
        }

        if (count == 0) {
            ItemStack stack = proto.copy();

            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }

            tag.setInteger("__storagedrawers_count", 0);
            return stack;
        }

        return proto.copy();
    }

    public static boolean isStackEncoded (@Nonnull ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
            return false;

        return tag.hasKey("__storagedrawers_count");
    }

    static {
        try {
            itemField = ItemStack.class.getDeclaredField("item");
            itemDamageField = ItemStack.class.getDeclaredField("itemDamage");
            stackTagCompoundField = ItemStack.class.getDeclaredField("stackTagCompound");
            capabilitiesField = ItemStack.class.getDeclaredField("capabilities");

            itemField.setAccessible(true);
            itemDamageField.setAccessible(true);
            stackTagCompoundField.setAccessible(true);
            capabilitiesField.setAccessible(true);

            initialized = true;
        } catch (NoSuchFieldException e) {
            initialized = false;
        }
    }
}
