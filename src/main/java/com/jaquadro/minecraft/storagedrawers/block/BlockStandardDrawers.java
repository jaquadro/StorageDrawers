package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockStandardDrawers extends BlockDrawers
{
    //public static final EnumProperty<EnumBasicDrawer> BLOCK = EnumProperty.create("block", EnumBasicDrawer.class);


    //@SideOnly(Side.CLIENT)
    //private StatusModelData[] statusInfo;

    public BlockStandardDrawers (int drawerCount, boolean halfDepth, Block.Properties properties) {
        super(drawerCount, halfDepth, properties);
    }

    /*@Override
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
    }*/

    @Override
    @SuppressWarnings("deprecation")
    public boolean causesSuffocation (BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    protected int getDrawerSlot (Direction side, double hitX, double hitY, double hitZ) {
        if (getDrawerCount() == 1)
            return 0;
        if (getDrawerCount() == 2)
            return hitTop(hitY) ? 0 : 1;

        if (hitLeft(side, hitX, hitZ))
            return hitTop(hitY) ? 0 : 1;
        else
            return hitTop(hitY) ? 2 : 3;
    }

    @Override
    public TileEntityDrawers createTileEntity (BlockState state, IBlockReader world) {
        return TileEntityDrawersStandard.createEntity(getDrawerCount());
    }
}
