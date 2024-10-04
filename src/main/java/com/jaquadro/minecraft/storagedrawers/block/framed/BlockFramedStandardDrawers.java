package com.jaquadro.minecraft.storagedrawers.block.framed;

import com.jaquadro.minecraft.storagedrawers.api.framing.FrameMaterial;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlock;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.api.storage.BlockType;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockFramedStandardDrawers extends BlockStandardDrawers implements IFramedBlock
{
    public BlockFramedStandardDrawers (int drawerCount, boolean halfDepth, int storageUnits, BlockBehaviour.Properties properties) {
        super(drawerCount, halfDepth, storageUnits, properties);
    }

    public BlockFramedStandardDrawers (int drawerCount, boolean halfDepth, BlockBehaviour.Properties properties) {
        super(drawerCount, halfDepth, properties);
    }

    @Override
    public BlockType retrimType () {
        return null;
    }

    public void setPlacedBy (@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);

        BlockEntityDrawers blockEntity = WorldUtils.getBlockEntity(world, pos, BlockEntityDrawers.class);
        if (blockEntity == null)
            return;

        CompoundTag tag = stack.getOrCreateTag();
        blockEntity.material().read(tag);
        blockEntity.setChanged();
    }

    @Override
    protected ItemStack getMainDrop (BlockState state, BlockEntityDrawers tile) {
        ItemStack drop = super.getMainDrop(state, tile);

        if (!tile.material().isEmpty()) {
            CompoundTag tag = drop.getOrCreateTag();
            tile.material().write(tag);
            drop.setTag(tag);
        }

        return drop;
    }

    @Override
    public IFramedBlockEntity getFramedBlockEntity (@NotNull Level world, @NotNull BlockPos pos) {
        return WorldUtils.getBlockEntity(world, pos, BlockEntityDrawers.class);
    }

    @Override
    public boolean supportsFrameMaterial (FrameMaterial material) {
        return true;
    }

    /*
    @Override
    @Nonnull
    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return ItemCustomDrawers.makeItemStack(state, 1, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);

        ItemStack drop = ItemCustomDrawers.makeItemStack(state, 1, tile.material().getSide(), tile.material().getTrim(), tile.material().getFront());
        if (drop.isEmpty())
            return ItemStack.EMPTY;

        NBTTagCompound data = drop.getTagCompound();
        if (data == null)
            data = new NBTTagCompound();

        if (tile.isSealed()) {
            NBTTagCompound tiledata = new NBTTagCompound();
            tile.writeToNBT(tiledata);
            data.setTag("tile", tiledata);
        }

        drop.setTagCompound(data);
        return drop;
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile != null && tile.material().getSide().isEmpty())
            return false;

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
    */
}
