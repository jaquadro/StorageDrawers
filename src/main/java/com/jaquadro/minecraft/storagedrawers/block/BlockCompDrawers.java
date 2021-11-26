package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockCompDrawers extends BlockDrawers implements INetworked
{
    public static final EnumProperty<EnumCompDrawer> SLOTS = EnumProperty.create("slots", EnumCompDrawer.class);

    //@SideOnly(Side.CLIENT)
    //private StatusModelData statusInfo;

    public BlockCompDrawers (int storageUnits, BlockBehaviour.Properties properties) {
        super(3, false, storageUnits, properties);
        this.registerDefaultState(defaultBlockState()
            .setValue(SLOTS, EnumCompDrawer.OPEN1));
    }

    public BlockCompDrawers (BlockBehaviour.Properties properties) {
        this(32, properties);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SLOTS);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void initDynamic () {
        ResourceLocation location = new ResourceLocation(StorageDrawers.MOD_ID + ":models/dynamic/compDrawers.json");
        statusInfo = new StatusModelData(3, location);
    }*/

    /*@Override
    public StatusModelData getStatusInfo (IBlockState state) {
        return statusInfo;
    }*/

    @Override
    protected int getDrawerSlot (Direction side, Vec3 hit) {
        if (hitTop(hit.y))
            return 0;

        if (hitLeft(side, hit.x, hit.z))
            return 1;
        else
            return 2;
    }

    @Override
    public void setPlacedBy (Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile != null) {
            IDrawerGroup group = tile.getGroup();
            for (int i = group.getDrawerCount() - 1; i >= 0; i--) {
                if (!group.getDrawer(i).isEmpty()) {
                    world.setBlock(pos, state.setValue(SLOTS, EnumCompDrawer.byOpenSlots(i + 1)), 3);
                    break;
                }
            }
        }
    }

    /*@Override
    public IBlockState getStateForPlacement (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState();
    }*/

    /*@Override
    public BlockType retrimType () {
        return null;
    }*/

    @Override
    public TileEntityDrawers createTileEntity (BlockState state, BlockGetter world) {
        return new TileEntityDrawersComp.Slot3();
    }

    /*@Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { SLOTS, FACING }, new IUnlistedProperty[] { STATE_MODEL });
    }*/
}
