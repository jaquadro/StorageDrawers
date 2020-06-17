package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IFrameableItem;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemCustomTrim extends ItemBlock implements IFrameableItem
{
    public ItemCustomTrim (Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt (@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityTrim tile = (TileEntityTrim) world.getTileEntity(pos);
        if (tile != null && stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("MatS"))
                tile.material().setSide(new ItemStack(stack.getTagCompound().getCompoundTag("MatS")));
            if (stack.getTagCompound().hasKey("MatT"))
                tile.material().setTrim(new ItemStack(stack.getTagCompound().getCompoundTag("MatT")));
        }

        return true;
    }

    @Nonnull
    @Override
    public ConsumeType consumeSide() {
        return ConsumeType.REQUIRED;
    }

    @Nonnull
    @Override
    public ConsumeType consumeTrim() {
        return ConsumeType.OPTIONAL;
    }

    @Nonnull
    @Override
    public ConsumeType consumeFront() {
        return ConsumeType.UNACCEPTABLE;
    }

    @Override
    @Nonnull
    public ItemStack makeItemStack (@Nonnull ItemStack source, @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim, @Nonnull ItemStack matFront) {
        Item item = source.getItem();
        if (!(item instanceof ItemCustomTrim))
            return ItemStack.EMPTY;

        NBTTagCompound tag = new NBTTagCompound();

        if (!matSide.isEmpty())
            tag.setTag("MatS", getMaterialTag(matSide));

        if (!matTrim.isEmpty())
            tag.setTag("MatT", getMaterialTag(matTrim));

        ItemStack stack = new ItemStack(item);
        if (!tag.hasNoTags())
            stack.setTagCompound(tag);

        return stack;
    }

    private static NBTTagCompound getMaterialTag (@Nonnull ItemStack mat) {
        mat = mat.copy();
        mat.setCount(1);

        NBTTagCompound itag = new NBTTagCompound();
        mat.writeToNBT(itag);

        return itag;
    }
}
