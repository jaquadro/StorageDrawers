package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
        if (!stack.isEmpty())
            return stack;

        ItemStack proto = getItemPrototype(stack);
        if (proto.isEmpty())
            return stack;

        NBTTagCompound tag = proto.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            proto.setTagCompound(tag);
        }

        tag.setInteger("__storagedrawers_count", stack.getCount());

        return proto;
    }

    public static ItemStack encodeItemStack (@Nonnull ItemStack proto, int count) {
        if (!proto.isEmpty() && count > 0 && count < 128) {
            ItemStack stack = proto.copy();
            stack.setCount(count);
            return stack;
        }

        if (count == 0 || count >= 128) {
            ItemStack stack = proto.copy();

            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }

            tag.setInteger("__storagedrawers_count", count);
            return stack;
        }

        return proto.copy();
    }

    public static ItemStack decodeItemStack (@Nonnull ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
            return stack;

        if (tag.hasKey("__storagedrawers_count")) {
            ItemStack decode = stack.copy();
            decode.setCount(tag.getInteger("__storagedrawers_count"));

            tag = decode.getTagCompound();
            if (tag != null) {
                decode.getTagCompound().removeTag("__storagedrawers_count");
                if (tag.getSize() == 0)
                    decode.setTagCompound(null);
            }

            return decode;
        }

        return stack;
    }

    public static boolean isStackEncoded (@Nonnull ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
            return false;

        return tag.hasKey("__storagedrawers_count");
    }

    static {
        try {
            itemField = ReflectionHelper.findField(ItemStack.class,  "item", "field_151002_e");
            itemDamageField = ReflectionHelper.findField(ItemStack.class,"itemDamage", "field_77991_e");
            stackTagCompoundField = ReflectionHelper.findField(ItemStack.class,"stackTagCompound", "field_77990_d");
            capabilitiesField = ReflectionHelper.findField(ItemStack.class,"capabilities");

            itemField.setAccessible(true);
            itemDamageField.setAccessible(true);
            stackTagCompoundField.setAccessible(true);
            capabilitiesField.setAccessible(true);

            initialized = true;
        } catch (ReflectionHelper.UnableToFindFieldException e) {
            initialized = false;
        }
    }
}
