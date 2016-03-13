package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemCustomDrawers extends ItemDrawers
{
    public ItemCustomDrawers (Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);
        if (tile != null && stack.hasTagCompound() && !stack.getTagCompound().hasKey("tile")) {
            if (stack.getTagCompound().hasKey("MatS"))
                tile.setMaterialSide(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatS")));
            if (stack.getTagCompound().hasKey("MatT"))
                tile.setMaterialTrim(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatT")));
            if (stack.getTagCompound().hasKey("MatF"))
                tile.setMaterialFront(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatF")));
        }

        return true;
    }

    public static ItemStack makeItemStack (Block block, int count, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemCustomDrawers))
            return null;

        NBTTagCompound tag = new NBTTagCompound();

        if (matSide != null) {
            NBTTagCompound itag = new NBTTagCompound();
            matSide.writeToNBT(itag);
            tag.setTag("MatS", itag);
        }

        if (matTrim != null) {
            NBTTagCompound itag = new NBTTagCompound();
            matTrim.writeToNBT(itag);
            tag.setTag("MatT", itag);
        }

        if (matFront != null) {
            NBTTagCompound itag = new NBTTagCompound();
            matFront.writeToNBT(itag);
            tag.setTag("MatF", itag);
        }

        ItemStack stack = new ItemStack(item, count, 0);
        if (!tag.hasNoTags())
            stack.setTagCompound(tag);

        return stack;
    }
}
