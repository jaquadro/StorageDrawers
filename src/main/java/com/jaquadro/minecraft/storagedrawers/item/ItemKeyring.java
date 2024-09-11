package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ItemKeyring extends Item
{
    private static final String TAG_ITEMS = "Items";

    private final RegistryObject<ItemKey> key;

    public ItemKeyring (RegistryObject<ItemKey> key, Item.Properties properties) {
        super(properties);
        this.key = key;
    }

    public ItemStack getKey() {
        if (key == null)
            return ItemStack.EMPTY;
        return new ItemStack(key.get(), 1);
    }

    @Override
    public boolean overrideOtherStackedOnMe (ItemStack targetStack, ItemStack stack, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player))
            return false;

        if (stack.isEmpty()) {
            removeOne(targetStack).ifPresent(s -> {
                access.set(s);
            });
            if (getContents(targetStack).count() == 0) {
                ItemStack newStack = new ItemStack(ModItems.KEYRING.get(), 1);
                newStack.setTag(targetStack.getTag());
                slot.set(newStack);
            } else {
                ItemStack newStack = rotateKeyring(targetStack);
                slot.set(newStack);
            }
            return true;
        }

        int added = add(targetStack, stack);
        stack.shrink(added);

        if (targetStack.getItem() == ModItems.KEYRING.get() && getContents(targetStack).count() > 0) {
            ItemStack newStack = getKeyring(getContents(targetStack).findFirst().orElse(ItemStack.EMPTY));
            if (!newStack.isEmpty()) {
                newStack.setTag(targetStack.getTag());
                slot.set(newStack);
            }
        }

        return true;
    }

    public static ItemStack getKeyring(ItemStack item) {
        if (item.isEmpty() || !(item.getItem() instanceof ItemKey))
            return ItemStack.EMPTY;

        Optional<ItemKeyring> keyring = ModItems.getKeyrings().filter(s -> s.getKey().getItem() == item.getItem()).findFirst();
        if (!keyring.isPresent())
            return ItemStack.EMPTY;

        return new ItemStack(keyring.get(), 1);
    }

    public static int add (ItemStack target, ItemStack item) {
        if (item.isEmpty() || !(item.getItem() instanceof ItemKey))
            return 0;

        CompoundTag tag = target.getOrCreateTag();
        if (!tag.contains(TAG_ITEMS))
            tag.put(TAG_ITEMS, new ListTag());

        ListTag list = tag.getList(TAG_ITEMS, 10);
        if (list.size() >= 64)
            return 0;

        Optional<CompoundTag> match = getMatchingItem(item, list);
        if (match.isPresent())
            return 0;

        ItemStack copy = item.copy();
        copy.setCount(1);
        CompoundTag entry = new CompoundTag();
        copy.save(entry);
        list.add(0, entry);

        return 1;
    }

    private static Optional<CompoundTag> getMatchingItem (ItemStack stack, ListTag list) {
        return list.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter(t -> {
            return ItemStack.isSameItemSameTags(ItemStack.of(t), stack);
        }).findFirst();
    }

    private static ItemStack getActiveKey (ItemStack stack, Item item) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_ITEMS))
            return null;

        ListTag list = tag.getList(TAG_ITEMS, 10);
        if (list.isEmpty())
            return null;

        return list.stream().filter(CompoundTag.class::isInstance)
            .map(CompoundTag.class::cast)
            .map(ItemStack::of)
            .filter(t -> {
                return ItemStack.isSameItem(t, new ItemStack(item));
        }).findFirst().orElse(null);
    }

    private static Optional<ItemStack> removeOne (ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_ITEMS))
            return Optional.empty();

        ListTag list = tag.getList(TAG_ITEMS, 10);
        if (list.isEmpty())
            return Optional.empty();

        CompoundTag first = list.getCompound(0);
        ItemStack firstStack = ItemStack.of(first);
        list.remove(0);
        if (list.isEmpty())
            tag.remove(TAG_ITEMS);

        return Optional.of(firstStack);
    }

    private static Stream<ItemStack> getContents (ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return Stream.empty();

        ListTag list = tag.getList(TAG_ITEMS, 10);
        return list.stream().map(CompoundTag.class::cast).map(ItemStack::of);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage (ItemStack stack) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        getContents(stack).forEach(nonnulllist::add);
        return Optional.of(new KeyringTooltip(nonnulllist));
    }

    @Override
    public Component getName (ItemStack stack) {
        if (key == null || !key.isPresent())
            return super.getName(stack);

        Component subName;
        ItemStack activeKey = getActiveKey(stack, key.get());
        if (activeKey != null)
            subName = activeKey.getHoverName();
        else
            subName = Component.translatable(key.get().getDescriptionId());

        MutableComponent name = Component.translatable(ModItems.KEYRING.get().getDescriptionId());
        return name.append(" (").append(subName).append(")");
    }

    @OnlyIn(Dist.CLIENT)
    @NotNull
    public Component getDescription() {
        return Component.translatable(ModItems.KEYRING.get().getDescriptionId() + ".desc");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (@NotNull ItemStack itemStack, @Nullable Level world, List<Component> list, TooltipFlag advanced) {
        List<MutableComponent> desc = Arrays.stream(getDescription().getString().split("\n")).map(Component::literal).toList();
        for (MutableComponent component : desc)
            list.add(component.withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onDestroyed (ItemEntity entity) {
        ItemUtils.onContainerDestroyed(entity, getContents(entity.getItem()));
    }

    @Override
    public InteractionResult useOn (UseOnContext context) {
        if (context.getPlayer().isShiftKeyDown())
            return InteractionResult.PASS;

        if (key != null && key.isPresent())
            return key.get().useOn(context);

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use (Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown())
            return InteractionResultHolder.pass(stack);

        List<ItemStack> list = getContents(stack).toList();
        if (list.isEmpty())
            return InteractionResultHolder.pass(stack);

        ItemStack keyring = rotateKeyring(stack);

        return InteractionResultHolder.success(keyring);
    }

    private ItemStack rotateKeyring(ItemStack stack) {
        List<ItemStack> list = getContents(stack).toList();

        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getItem() == key.get()) {
                index = i;
                break;
            }
        }

        index += 1;
        if (index >= list.size())
            index = 0;

        ItemStack nextItem = list.get(index);
        ItemStack keyring = getKeyring(nextItem);
        keyring.setTag(stack.getTag());

        return keyring;
    }
}
