package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BlockTrim extends Block implements INetworked
{
    public String[] getResourceVariants () {
        String[] variants = new String[BlockPlanks.EnumType.values().length];
        int index = 0;

        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values())
            variants[index++] = '_' + type.getName();

        return variants;
    }

    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);

    public BlockTrim (String name) {
        super(Material.wood);

        setUnlocalizedName(name);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setStepSound(Block.soundTypeWood);

        setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK));
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values())
            list.add(new ItemStack(item, 1, type.getMetadata()));
    }

    @Override
    public int damageDropped (IBlockState state) {
        return ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {
        return getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    protected BlockState createBlockState () {
        return new BlockState(this, VARIANT);
    }
}
