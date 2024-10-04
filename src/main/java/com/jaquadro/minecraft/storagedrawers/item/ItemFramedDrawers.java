package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemFramedDrawers extends ItemDrawers
{
    public ItemFramedDrawers (Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock (BlockPlaceContext context, BlockState state) {
        if (!super.placeBlock(context, state))
            return false;

        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(context.getLevel(), context.getClickedPos(), BlockEntityDrawers.class);
        ItemStack stack = context.getItemInHand();
        if (blockEntity != null && !stack.isEmpty())
            blockEntity.material().read(context.getItemInHand().getOrCreateTag());

        return true;
    }

    /*public static ItemStack makeItemStack (BlockState blockState, int count, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        Block block = blockState.getBlock();
        Item item = Item.byBlock(block);
        if (!(item instanceof ItemFramedDrawers))
            return ItemStack.EMPTY;

        CompoundTag tag = new CompoundTag();
        MaterialData data = new MaterialData(matSide, matFront, matTrim);
        tag = data.write(tag);

        ItemStack stack = new ItemStack(item, count);
        stack.setTag(tag);

        return stack;
    }*/
}
