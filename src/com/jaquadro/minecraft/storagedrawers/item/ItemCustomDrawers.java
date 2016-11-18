package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemCustomDrawers extends ItemDrawers
{
    private Function nameFunction;

    public ItemCustomDrawers (Block block) {
        this(block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                return EnumBasicDrawer.byMetadata(stack.getMetadata()).getUnlocalizedName();
            }
        });
    }

    protected ItemCustomDrawers (Block block, Function function) {
        super(block);
        setHasSubtypes(true);
        nameFunction = function;
    }

    @Override
    public String getUnlocalizedName (ItemStack stack) {
        return super.getUnlocalizedName() + "." + nameFunction.apply(stack);
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

    @Nonnull
    public static ItemStack makeItemStack (IBlockState blockState, int count, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        Block block = blockState.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemCustomDrawers))
            return ItemStack.field_190927_a;

        NBTTagCompound tag = new NBTTagCompound();

        if (matSide != null)
            tag.setTag("MatS", getMaterialTag(matSide));

        if (matTrim != null)
            tag.setTag("MatT", getMaterialTag(matTrim));

        if (matFront != null)
            tag.setTag("MatF", getMaterialTag(matFront));

        ItemStack stack = new ItemStack(item, count, block.getMetaFromState(blockState));
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
