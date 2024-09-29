package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            return CommonConfig.UPGRADES.enableRemoteGroupUpgrade.get();
        else
            return CommonConfig.UPGRADES.enableRemoteUpgrade.get();
    }

    @Override
    public void appendHoverText (@NotNull ItemStack itemStack, @Nullable Level world, List<Component> list, TooltipFlag advanced) {
        super.appendHoverText(itemStack, world, list, advanced);

        BlockPos pos = getBoundPosition(itemStack);
        if (pos != null)
            list.add(Component.translatable(getDescriptionId() + ".bound", pos.getX(), pos.getY(), pos.getZ())
                .withStyle(ChatFormatting.YELLOW));
        else
            list.add(Component.translatable(getDescriptionId() + ".bound")
                .withStyle(ChatFormatting.RED));
    }

    public static BlockPos getBoundPosition (ItemStack itemStack) {
        if (itemStack == null)
            return null;

        CompoundTag tag = itemStack.getTag();
        if (tag == null)
            return null;

        CompoundTag posTag = tag.getCompound("bound_controller");
        if (posTag.isEmpty())
            return null;

        int x = posTag.getInt("x");
        int y = posTag.getInt("y");
        int z = posTag.getInt("z");
        return new BlockPos(x, y, z);
    }

    public static ItemStack setBoundController (ItemStack itemStack, BlockEntityController controller) {
        if (itemStack == null || controller == null)
            return itemStack;

        if (itemStack.getItem() instanceof ItemUpgradeRemote item) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", controller.getBlockPos().getX());
            posTag.putInt("y", controller.getBlockPos().getY());
            posTag.putInt("z", controller.getBlockPos().getZ());

            CompoundTag tag = itemStack.getOrCreateTag();
            tag.put("bound_controller", posTag);

            ItemStack newStack = new ItemStack(item.isGroupUpgrade()
                ? ModItems.REMOTE_GROUP_UPGRADE_BOUND.get()
                : ModItems.REMOTE_UPGRADE_BOUND.get(), itemStack.getCount());

            newStack.setTag(tag);
            return newStack;
        }

        return itemStack;
    }

    public static ItemStack setUnbound (ItemStack itemStack) {
        if (itemStack != null && itemStack.getItem() instanceof ItemUpgradeRemote item) {
            ItemStack newStack = new ItemStack(item.isGroupUpgrade()
                ? ModItems.REMOTE_GROUP_UPGRADE.get()
                : ModItems.REMOTE_UPGRADE.get(), itemStack.getCount());

            CompoundTag tag = itemStack.getOrCreateTag();
            if (tag.contains("bound_controller"))
                tag.remove("bound_controller");

            newStack.setTag(tag);
            return newStack;
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

            CompoundTag targetTag = targetStack.getOrCreateTag().copy();
            CompoundTag refTag = refStack.getOrCreateTag();
            if (refTag.contains("bound_controller"))
                targetTag.put("bound_controller", refTag.getCompound("bound_controller"));

            newStack.setTag(targetTag);
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
