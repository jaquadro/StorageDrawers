package com.jaquadro.minecraft.storagedrawers.item;

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

public class ItemCustomTrim extends ItemBlock
{
    public ItemCustomTrim (Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt (@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityTrim tile = (TileEntityTrim) world.getTileEntity(pos);
        if (tile != null && stack.hasTagCompound() && !stack.getTagCompound().hasKey("tile")) {
            if (stack.getTagCompound().hasKey("MatS"))
                tile.setMaterialSide(new ItemStack(stack.getTagCompound().getCompoundTag("MatS")));
            if (stack.getTagCompound().hasKey("MatT"))
                tile.setMaterialTrim(new ItemStack(stack.getTagCompound().getCompoundTag("MatT")));
        }

        return true;
    }

    @Nonnull
    public static ItemStack makeItemStack (Block block, int count, @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim) {
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemCustomTrim))
            return ItemStack.field_190927_a;

        NBTTagCompound tag = new NBTTagCompound();

        if (!matSide.func_190926_b())
            tag.setTag("MatS", getMaterialTag(matSide));

        if (!matTrim.func_190926_b())
            tag.setTag("MatT", getMaterialTag(matTrim));

        ItemStack stack = new ItemStack(item, count, 0);
        if (!tag.hasNoTags())
            stack.setTagCompound(tag);

        return stack;
    }

    private static NBTTagCompound getMaterialTag (@Nonnull ItemStack mat) {
        mat = mat.copy();
        mat.func_190920_e(1);

        NBTTagCompound itag = new NBTTagCompound();
        mat.writeToNBT(itag);

        return itag;
    }
}
