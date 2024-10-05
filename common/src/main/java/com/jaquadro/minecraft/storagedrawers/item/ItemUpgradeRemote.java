package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.components.item.ControllerBinding;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class ItemUpgradeRemote extends ItemUpgrade
{
    private static final int remoteGroupId;
    static {
        remoteGroupId = ItemUpgrade.getNextGroupId();
    }

    private final boolean groupUpgrade;
    private final boolean bound;

    public ItemUpgradeRemote (boolean groupUpgrade, boolean bound, Properties properties) {
        super(properties, remoteGroupId);
        this.groupUpgrade = groupUpgrade;
        this.bound = bound;
    }

    @Override
    public boolean isEnabled () {
        if (groupUpgrade)
            return ModCommonConfig.INSTANCE.UPGRADES.enableRemoteGroupUpgrade.get();
        else
            return ModCommonConfig.INSTANCE.UPGRADES.enableRemoteUpgrade.get();
    }

    @Override
    public void appendHoverText (ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        BlockPos pos = getBoundPosition(stack);
        if (pos != null)
            tooltip.add(Component.translatable(getDescriptionId() + ".bound", pos.getX(), pos.getY(), pos.getZ())
                .withStyle(ChatFormatting.YELLOW));
        else
            tooltip.add(Component.translatable(getDescriptionId() + ".bound")
                .withStyle(ChatFormatting.RED));
    }

    public static BlockPos getBoundPosition (ItemStack itemStack) {
        if (itemStack == null)
            return null;

        ControllerBinding binding = itemStack.getOrDefault(ModDataComponents.CONTROLLER_BINDING.get(), ControllerBinding.EMPTY);
        if (!binding.valid())
            return null;

        return binding.blockPos();
    }

    public static ItemStack setBoundController (ItemStack itemStack, BlockEntityController controller) {
        if (itemStack == null || controller == null)
            return itemStack;

        if (itemStack.getItem() instanceof ItemUpgradeRemote item) {
            ItemStack newStack = new ItemStack(item.isGroupUpgrade()
                ? ModItems.REMOTE_GROUP_UPGRADE_BOUND.get()
                : ModItems.REMOTE_UPGRADE_BOUND.get(), itemStack.getCount());

            newStack.set(ModDataComponents.CONTROLLER_BINDING.get(), new ControllerBinding(controller.getBlockPos()));
            return newStack;
        }

        return itemStack;
    }

    public static ItemStack setUnbound (ItemStack itemStack) {
        if (itemStack != null && itemStack.getItem() instanceof ItemUpgradeRemote item) {
            return new ItemStack(item.isGroupUpgrade()
                ? ModItems.REMOTE_GROUP_UPGRADE.get()
                : ModItems.REMOTE_UPGRADE.get(), itemStack.getCount());
        }

        return itemStack;
    }

    public static ItemStack copyControllerBinding (ItemStack refStack, ItemStack targetStack) {
        if (refStack == null || targetStack == null)
            return targetStack;

        if (refStack.getItem() instanceof ItemUpgradeRemote refItem
            && targetStack.getItem() instanceof ItemUpgradeRemote targetItem) {

            Item newItem;
            if (targetItem.isGroupUpgrade()) {
                newItem = refItem.isBound()
                    ? ModItems.REMOTE_GROUP_UPGRADE_BOUND.get()
                    : ModItems.REMOTE_GROUP_UPGRADE.get();
            } else {
                newItem = refItem.isBound()
                    ? ModItems.REMOTE_UPGRADE_BOUND.get()
                    : ModItems.REMOTE_UPGRADE.get();
            }

            ItemStack newStack = new ItemStack(newItem, targetStack.getCount());
            newStack.set(ModDataComponents.CONTROLLER_BINDING.get(),
                refStack.getOrDefault(ModDataComponents.CONTROLLER_BINDING.get(), ControllerBinding.EMPTY));

            return newStack;
        }

        return targetStack;
    }

    public static BlockEntityController getBoundController (ItemStack itemStack, LevelAccessor level) {
        if (level == null)
            return null;

        BlockPos pos = getBoundPosition(itemStack);
        if (pos == null)
            return null;

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof BlockEntityController bec)
            return bec;

        return null;
    }

    public static void validateInventory (Inventory inventory, Level level) {
        if (level == null || inventory == null)
            return;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemUpgradeRemote item) {
                if (item.bound && getBoundController(stack, level) == null)
                    inventory.setItem(i, setUnbound(stack));
            }
        }
    }

    public boolean isGroupUpgrade () {
        return groupUpgrade;
    }

    public boolean isBound () {
        return bound;
    }
}