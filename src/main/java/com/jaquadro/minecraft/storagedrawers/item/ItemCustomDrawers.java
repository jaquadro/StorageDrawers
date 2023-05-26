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
    public String getUnlocalizedName (@Nonnull ItemStack stack) {
        return super.getUnlocalizedName() + "." + nameFunction.apply(stack);
    }

    @Override
    public boolean placeBlockAt (@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);
        if (tile != null && stack.hasTagCompound() && !stack.getTagCompound().hasKey("tile")) {
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
    public static ItemStack makeItemStack (IBlockState blockState, int count, @Nonnull ItemStack matSide, @Nonnull ItemStack matTrim, @Nonnull ItemStack matFront) {
        Block block = blockState.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemCustomDrawers))
            return ItemStack.EMPTY;

        // No need to override, it is a new tag compound
        NBTTagCompound tag = setCompoundMaterials(matSide, matTrim, matFront, new NBTTagCompound(), false);

        ItemStack stack = new ItemStack(item, count, block.getMetaFromState(blockState));
        if (!tag.hasNoTags())
            stack.setTagCompound(tag);

        return stack;
    }

    /**
     * @param matSide The ItemStack to use has the side material
     * @param matTrim The ItemStack to use has the side material
     * @param matFront The ItemStack to use has the side material
     * @param compoundIn The NBTTagCompound to set the tags in. If no existing compound is needed, simply insert a new one.
     * @param override This will override any existing data in the provided tag, related to matSide, matTrim and matFront.
     *                 If this is false, if any of the given ItemStacks are empty, then the decoration specified by that ItemStack
     *                 will not be changed. Otherwise, if it is true, than that decoration will be set to none.
     */

    public static NBTTagCompound setCompoundMaterials (ItemStack matSide, ItemStack matTrim, ItemStack matFront, NBTTagCompound compoundIn, boolean override) {
        if (override){
            compoundIn.removeTag("MatS");
            compoundIn.removeTag("MatT");
            compoundIn.removeTag("MatF");
        }
        if (!matSide.isEmpty())
            compoundIn.setTag("MatS", getMaterialTag(matSide));

        if (!matTrim.isEmpty())
            compoundIn.setTag("MatT", getMaterialTag(matTrim));

        if (!matFront.isEmpty())
            compoundIn.setTag("MatF", getMaterialTag(matFront));

        return compoundIn;
    }

    private static NBTTagCompound getMaterialTag (@Nonnull ItemStack mat) {
        mat = mat.copy();
        mat.setCount(1);

        NBTTagCompound itag = new NBTTagCompound();
        mat.writeToNBT(itag);

        return itag;
    }
}
