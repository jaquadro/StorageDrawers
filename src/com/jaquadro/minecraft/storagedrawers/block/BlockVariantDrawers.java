package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class BlockVariantDrawers extends BlockStandardDrawers
{
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);

    public BlockVariantDrawers (String blockName) {
        super(blockName);
    }

    @Override
    protected void initDefaultState () {
        super.initDefaultState();
        setDefaultState(getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.OAK));
    }

    @Override
    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        ItemStack drop = super.getMainDrop(world, pos, state);

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return drop;

        NBTTagCompound data = drop.getTagCompound();
        if (data == null)
            data = new NBTTagCompound();

        BlockPlanks.EnumType material = translateMaterial(tile.getMaterialOrDefault());
        data.setString("material", material.getName());

        drop.setTagCompound(data);
        return drop;
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
                ItemStack stack = new ItemStack(item, 1, type.getMetadata());

                NBTTagCompound data = new NBTTagCompound();
                data.setString("material", material.getName());
                stack.setTagCompound(data);

                if (StorageDrawers.config.cache.creativeTabVanillaWoods || material == BlockPlanks.EnumType.OAK)
                    list.add(stack);
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { BLOCK, VARIANT, FACING }, new IUnlistedProperty[] { STATE_MODEL });
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState (IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return state;

        return super.getActualState(state, worldIn, pos)
            .withProperty(VARIANT, translateMaterial(tile.getMaterialOrDefault()));
    }

    @Override
    public boolean retrimBlock (World world, BlockPos pos, ItemStack prototype) {
        if (retrimType() == null)
            return false;

        IBlockState curState = getActualState(world.getBlockState(pos), world, pos);
        if (curState == null || !(curState.getBlock() instanceof BlockDrawers))
            return false;

        Block protoBlock = Block.getBlockFromItem(prototype.getItem());
        int protoMeta = prototype.getItemDamage();

        IBlockState newState = protoBlock.getStateFromMeta(protoMeta);
        if (newState == null || !(newState.getBlock() instanceof BlockTrim))
            return false;

        BlockPlanks.EnumType curVariant = curState.getValue(VARIANT);
        BlockPlanks.EnumType newVariant = newState.getValue(VARIANT);
        if (curVariant == newVariant)
            return false;

        TileEntityDrawers tile = getTileEntity(world, pos);
        tile.setMaterial(newVariant.getName());

        world.setBlockState(pos, curState.withProperty(VARIANT, newVariant));

        return true;
    }

    private BlockPlanks.EnumType translateMaterial (String materal) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            if (materal.equals(type.getName()))
                return type;
        }

        return BlockPlanks.EnumType.OAK;
    }
}
