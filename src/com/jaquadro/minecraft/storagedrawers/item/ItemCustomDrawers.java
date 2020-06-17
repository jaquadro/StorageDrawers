package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IFrameableItem;
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

public class ItemCustomDrawers extends ItemDrawers implements IFrameableItem
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
    public String getUnlocalizedName (@Nonnull ItemStack stack) {
        return super.getUnlocalizedName() + "." + nameFunction.apply(stack);
    }

    @Override
    public boolean placeBlockAt (@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);
        if (tile != null && stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("MatS"))
                tile.material().setSide(new ItemStack(stack.getTagCompound().getCompoundTag("MatS")));
            if (stack.getTagCompound().hasKey("MatT"))
                tile.material().setTrim(new ItemStack(stack.getTagCompound().getCompoundTag("MatT")));
            if (stack.getTagCompound().hasKey("MatF"))
                tile.material().setFront(new ItemStack(stack.getTagCompound().getCompoundTag("MatF")));
        }

        return true;
    }

    @Nonnull
    @Override
    public ConsumeType consumeSide () {
        return ConsumeType.REQUIRED;
    }

    @Nonnull
    @Override
    public ConsumeType consumeTrim () {
        return ConsumeType.OPTIONAL;
    }

    @Nonnull
    @Override
    public ConsumeType consumeFront () {
        return ConsumeType.OPTIONAL;
    }

    @Nonnull
    public ItemStack makeItemStack (@Nonnull ItemStack source, @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim, @Nonnull ItemStack matFront) {
        Item item = source.getItem();
        if (!(item instanceof ItemCustomDrawers))
            return ItemStack.EMPTY;

        NBTTagCompound tag = source.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();

        if (!matSide.isEmpty())
            tag.setTag("MatS", getMaterialTag(matSide));
        else tag.removeTag("MatS");

        if (!matTrim.isEmpty())
            tag.setTag("MatT", getMaterialTag(matTrim));
        else tag.removeTag("MatT");

        if (!matFront.isEmpty())
            tag.setTag("MatF", getMaterialTag(matFront));
        else tag.removeTag("MatF");

        if(tag.hasNoTags()) tag = null;
        source.setTagCompound(tag);

        return source;
    }

    private static NBTTagCompound getMaterialTag (@Nonnull ItemStack mat) {
        mat = mat.copy();
        mat.setCount(1);

        NBTTagCompound itag = new NBTTagCompound();
        mat.writeToNBT(itag);

        return itag;
    }
}
