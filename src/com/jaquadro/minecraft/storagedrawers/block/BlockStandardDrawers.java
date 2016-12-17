package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockStandardDrawers extends BlockDrawers
{
    public static final PropertyEnum<EnumBasicDrawer> BLOCK = PropertyEnum.create("block", EnumBasicDrawer.class);


    @SideOnly(Side.CLIENT)
    private StatusModelData[] statusInfo;

    public BlockStandardDrawers (String blockName) {
        super(Material.WOOD, blockName);
    }

    @Override
    protected void initDefaultState () {
        super.initDefaultState();
        setDefaultState(getDefaultState().withProperty(BLOCK, EnumBasicDrawer.FULL2));
    }

    @Override
    public int getDrawerCount (IBlockState state) {
        if (state != null && state.getBlock() instanceof BlockDrawers) {
            EnumBasicDrawer info = state.getValue(BLOCK);
            if (info != null)
                return info.getDrawerCount();
        }

        return 0;
    }

    @Override
    public boolean isHalfDepth (IBlockState state) {
        if (state != null && state.getBlock() instanceof BlockDrawers) {
            EnumBasicDrawer info = state.getValue(BLOCK);
            if (info != null)
                return info.isHalfDepth();
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initDynamic () {
        statusInfo = new StatusModelData[EnumBasicDrawer.values().length];
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            ResourceLocation location = new ResourceLocation(StorageDrawers.MOD_ID + ":models/dynamic/basicDrawers_" + type.getName() + ".json");
            statusInfo[type.getMetadata()] = new StatusModelData(type.getDrawerCount(), location);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public StatusModelData getStatusInfo (IBlockState state) {
        if (state != null) {
            EnumBasicDrawer info = state.getValue(BLOCK);
            if (info != null)
                return statusInfo[info.getMetadata()];
        }

        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube (IBlockState state) {
        return isOpaqueCube(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube (IBlockState state) {
        try {
            switch (state.getValue(BLOCK)) {
                case FULL1:
                case FULL2:
                case FULL4:
                    return true;
                default:
                    return false;
            }
        }
        catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean doesSideBlockRendering (IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        switch (state.getValue(BLOCK)) {
            case FULL1:
            case FULL2:
            case FULL4:
                return true;
            default:
                TileEntityDrawers tile = getTileEntity(world, pos);
                return (tile != null && tile.getDirection() == face.getOpposite().getIndex());
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered (IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        switch (blockState.getValue(BLOCK)) {
            case FULL1:
            case FULL2:
            case FULL4:
                return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
            default:
                TileEntityDrawers tile = getTileEntity(blockAccess, pos);
                if (tile != null && tile.getDirection() == side.getIndex())
                    return true;
                return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
        }
    }

    @Override
    public boolean causesSuffocation () {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(BLOCK, EnumBasicDrawer.byMetadata(meta));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta (int meta) {
        return getDefaultState().withProperty(BLOCK, EnumBasicDrawer.byMetadata(meta));
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return state.getValue(BLOCK).getMetadata();
    }

    @Override
    protected int getDrawerSlot (int drawerCount, int side, float hitX, float hitY, float hitZ) {
        if (drawerCount == 1)
            return 0;
        if (drawerCount == 2)
            return hitTop(hitY) ? 0 : 1;

        if (hitLeft(side, hitX, hitZ))
            return hitTop(hitY) ? 0 : 1;
        else
            return hitTop(hitY) ? 2 : 3;
    }

    @Override
    public TileEntityDrawers createNewTileEntity (World world, int meta) {
        return new TileEntityDrawersStandard();
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            ItemStack stack = new ItemStack(item, 1, type.getMetadata());

            NBTTagCompound data = new NBTTagCompound();
            data.setString("material", BlockPlanks.EnumType.OAK.getName());
            stack.setTagCompound(data);
        }
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { BLOCK, FACING }, new IUnlistedProperty[] { STATE_MODEL });
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState (IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos).withProperty(BLOCK, state.getValue(BLOCK));
    }
}
