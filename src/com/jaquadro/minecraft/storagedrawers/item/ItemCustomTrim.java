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

public class ItemCustomTrim extends ItemBlock
{
    public ItemCustomTrim (Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityTrim tile = (TileEntityTrim) world.getTileEntity(pos);
        if (tile != null && stack.hasTagCompound() && !stack.getTagCompound().hasKey("tile")) {
            if (stack.getTagCompound().hasKey("MatS"))
                tile.setMaterialSide(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatS")));
            if (stack.getTagCompound().hasKey("MatT"))
                tile.setMaterialTrim(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatT")));
        }

        return true;
    }

    public static ItemStack makeItemStack (Block block, int count, ItemStack matSide, ItemStack matTrim) {
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemCustomTrim))
            return null;

        NBTTagCompound tag = new NBTTagCompound();

        if (matSide != null)
            tag.setTag("MatS", getMaterialTag(matSide));

        if (matTrim != null)
            tag.setTag("MatT", getMaterialTag(matTrim));

        ItemStack stack = new ItemStack(item, count, 0);
        if (!tag.hasNoTags())
            stack.setTagCompound(tag);

        return stack;
    }

    private static NBTTagCompound getMaterialTag (ItemStack mat) {
        mat = mat.copy();
        mat.stackSize = 1;

        NBTTagCompound itag = new NBTTagCompound();
        mat.writeToNBT(itag);

        return itag;
    }
}
