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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
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
    public boolean removedByPlayer (World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(world, pos, player, true);
    }

    @Override
    public void harvestBlock (World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        super.harvestBlock(worldIn, player, pos, state, te);
        worldIn.setBlockToAir(pos);
    }

    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getBlock().getMetaFromState(state));
    }

    @Override
    public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack dropStack = getMainDrop(world, pos, state);

        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(dropStack);

        return drops;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values())
            list.add(new ItemStack(item, 1, type.getMetadata()));
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


