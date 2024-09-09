package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
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
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ItemKeyring extends Item
{
    private static final String TAG_ITEMS = "Items";

    private final DeferredItem<? extends ItemKey> key;

    public ItemKeyring (DeferredItem<? extends ItemKey> key, Item.Properties properties) {
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
        if (targetStack.getCount() != 1)
            return false;

        KeyringContents contents = targetStack.getOrDefault(ModDataComponents.KEYRING_CONTENTS, KeyringContents.EMPTY);
        KeyringContents.Mutable mutable = new KeyringContents.Mutable(contents);
        if (stack.isEmpty()) {
            ItemStack removed = mutable.removeOne();
            if (removed != null)
                access.set(removed);

            if (mutable.size() == 0) {
                slot.set(new ItemStack(ModItems.KEYRING.get(), 1));
            }
        } else
            mutable.tryInsert(stack);

        contents = mutable.toImmutable();
        targetStack.set(ModDataComponents.KEYRING_CONTENTS, contents);

        if (targetStack.getItem() == ModItems.KEYRING.get() && contents.size() > 0) {
            ItemStack newStack = getKeyring(contents.itemCopyStream().findFirst().orElse(ItemStack.EMPTY));
            if (!newStack.isEmpty()) {
                newStack.set(ModDataComponents.KEYRING_CONTENTS, contents);
                slot.set(newStack);
            }
        }

        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use (Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown())
            return InteractionResultHolder.pass(stack);

        KeyringContents contents = stack.get(ModDataComponents.KEYRING_CONTENTS);
        if (contents == null || contents.size() == 0)
            return InteractionResultHolder.pass(stack);

        int index = 0;
        for (int i = 0; i < contents.size(); i++) {
            if (contents.getItemUnsafe(i).getItem() == key.get()) {
                index = i;
                break;
            }
        }

        index += 1;
        if (index >= contents.size())
            index = 0;

        ItemStack nextItem = contents.getItemUnsafe(index);
        ItemStack keyring = getKeyring(nextItem);
        keyring.set(ModDataComponents.KEYRING_CONTENTS, contents);

        return InteractionResultHolder.success(keyring);
    }

    @Override
    public InteractionResult useOn (UseOnContext context) {
        if (context.getPlayer().isShiftKeyDown())
            return InteractionResult.PASS;

        if (key != null && key.isBound())
            return key.get().useOn(context);

        return InteractionResult.PASS;
    }

    public static ItemStack getKeyring(ItemStack item) {
        if (item.isEmpty() || !(item.getItem() instanceof ItemKey))
            return ItemStack.EMPTY;

        Optional<ItemKeyring> keyring = ModItems.getKeyrings().filter(s -> s.getKey().getItem() == item.getItem()).findFirst();
        if (!keyring.isPresent())
            return ItemStack.EMPTY;

        return new ItemStack(keyring.get(), 1);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage (ItemStack stack) {
        if (stack.has(DataComponents.HIDE_TOOLTIP) || stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP))
            return Optional.empty();

        return Optional.ofNullable(stack.get(ModDataComponents.KEYRING_CONTENTS)).map(KeyringTooltip::new);
    }

    @Override
    public void appendHoverText (@NotNull ItemStack itemStack, Item.TooltipContext context, List<Component> list, TooltipFlag advanced) {
        list.add(Component.literal("").append(getDescription()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component getName (ItemStack stack) {
        if (key == null || !key.isBound())
            return super.getName(stack);

        MutableComponent name = Component.translatable(ModItems.KEYRING.get().getDescriptionId());
        MutableComponent subName = Component.translatable(key.get().getDescriptionId());
        return name.append(" (").append(subName).append(")");
    }

    @NotNull
    public Component getDescription() {
        return Component.translatable(ModItems.KEYRING.get().getDescriptionId() + ".desc");
    }

    @Override
    public void onDestroyed (ItemEntity entity) {
        KeyringContents contents = entity.getItem().get(ModDataComponents.KEYRING_CONTENTS);
        if (contents != null) {
            entity.getItem().set(ModDataComponents.KEYRING_CONTENTS, KeyringContents.EMPTY);
            ItemUtils.onContainerDestroyed(entity, contents.itemsCopy());
        }
    }
}
