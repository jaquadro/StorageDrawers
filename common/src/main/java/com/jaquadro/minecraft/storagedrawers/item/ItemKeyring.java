package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.components.item.KeyringContents;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import com.jaquadro.minecraft.storagedrawers.util.ComponentUtil;
import com.texelsaurus.minecraft.chameleon.registry.RegistryEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ItemKeyring extends Item
{
    private static final String TAG_ITEMS = "Items";

    private final RegistryEntry<? extends ItemKey> key;

    public ItemKeyring (RegistryEntry<? extends ItemKey> key, Properties properties) {
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

        KeyringContents contents = targetStack.getOrDefault(ModDataComponents.KEYRING_CONTENTS.get(), KeyringContents.EMPTY);
        KeyringContents.Mutable mutable = new KeyringContents.Mutable(contents);
        if (stack.isEmpty()) {
            ItemStack removed = mutable.removeOne();
            if (removed != null) {
                access.set(removed);
                if (mutable.size() == 0)
                    targetStack.remove(ModDataComponents.KEYRING_CONTENTS.get());
                else {
                    contents = mutable.toImmutable();
                    targetStack.set(ModDataComponents.KEYRING_CONTENTS.get(), contents);
                }
            }

            if (getContents(targetStack).findAny().isEmpty()) {
                ItemStack newStack = new ItemStack(ModItems.KEYRING.get(), 1);
                newStack.applyComponents(targetStack.getComponentsPatch());
                slot.set(newStack);
            } else {
                ItemStack newStack = rotateKeyring(targetStack);
                slot.set(newStack);
            }

            return true;
        } else
            mutable.tryInsert(stack);

        contents = mutable.toImmutable();
        targetStack.set(ModDataComponents.KEYRING_CONTENTS.get(), contents);

        if (targetStack.getItem() == ModItems.KEYRING.get() && contents.size() > 0) {
            ItemStack newStack = getKeyring(contents.itemCopyStream().findFirst().orElse(ItemStack.EMPTY));
            if (!newStack.isEmpty()) {
                newStack.applyComponents(targetStack.getComponentsPatch());
                newStack.set(ModDataComponents.KEYRING_CONTENTS.get(), contents);
                slot.set(newStack);
            }
        }

        return true;
    }

    @Override
    public InteractionResult use (Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown())
            return InteractionResult.PASS;

        List<ItemStack> list = getContents(stack).toList();
        if (list.isEmpty())
            return InteractionResult.PASS;

        ItemStack keyring = rotateKeyring(stack);

        return InteractionResult.SUCCESS.heldItemTransformedTo(keyring);
    }

    @Override
    public InteractionResult useOn (UseOnContext context) {
        if (context.getPlayer().isShiftKeyDown())
            return InteractionResult.PASS;

        if (key != null) {
            InteractionResult result = key.get().useOn(context);
            if (result == InteractionResult.SUCCESS)
                context.getPlayer().getCooldowns().addCooldown(context.getItemInHand(), 5);
            return result;
        }

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
        TooltipDisplay tooltipdisplay = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        if (!tooltipdisplay.shows(ModDataComponents.KEYRING_CONTENTS.get()))
            return Optional.empty();

        if (!stack.has(ModDataComponents.KEYRING_CONTENTS.get()))
            return Optional.of(new KeyringTooltip(new KeyringContents(List.of())));

        return Optional.ofNullable(stack.get(ModDataComponents.KEYRING_CONTENTS.get())).map(KeyringTooltip::new);
    }

    @Override
    public void appendHoverText (ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        KeyringContents contents = stack.get(ModDataComponents.KEYRING_CONTENTS.get());
        if (contents == null || contents.isEmpty())
            ComponentUtil.appendSplitDescription(tooltip, getDescription());
    }

    @Override
    public Component getName (ItemStack stack) {
        if (key == null || key.get() == null)
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

    @NotNull
    public Component getDescription() {
        return Component.translatable(ModItems.KEYRING.get().getDescriptionId() + ".desc");
    }

    @Override
    public void onDestroyed (ItemEntity entity) {
        KeyringContents contents = entity.getItem().get(ModDataComponents.KEYRING_CONTENTS.get());
        if (contents != null) {
            entity.getItem().set(ModDataComponents.KEYRING_CONTENTS.get(), KeyringContents.EMPTY);
            ItemUtils.onContainerDestroyed(entity, contents.itemCopyStream());
        }
    }

    private static ItemStack getActiveKey (ItemStack stack, Item item) {
        KeyringContents contents = stack.get(ModDataComponents.KEYRING_CONTENTS.get());
        if (contents == null || contents.size() == 0)
            return null;

        return contents.itemCopyStream()
            .filter(t -> ItemStack.isSameItem(t, new ItemStack(item))).findFirst().orElse(null);
    }

    private static Stream<ItemStack> getContents (ItemStack stack) {
        KeyringContents contents = stack.get(ModDataComponents.KEYRING_CONTENTS.get());
        if (contents == null || contents.size() == 0)
            return Stream.empty();

        return contents.itemCopyStream();
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
        keyring.applyComponents(stack.getComponentsPatch());

        return keyring;
    }
}
