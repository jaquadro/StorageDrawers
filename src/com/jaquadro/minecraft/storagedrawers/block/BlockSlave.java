package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSlave extends BlockContainer implements INetworked
{
    public BlockSlave (String registryName, String blockName) {
        super(Material.ROCK);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setUnlocalizedName(blockName);
        setRegistryName(registryName);
        setSoundType(SoundType.STONE);
    }

    @Override
    public EnumBlockRenderType getRenderType (IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public void toggle (World world, BlockPos pos, EntityPlayer player, EnumKeyType keyType) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null)
            return;

        BlockPos controllerPos = tile.getControllerPos();
        if (controllerPos == null)
            return;

        Block block = world.getBlockState(controllerPos).getBlock();
        if (block instanceof BlockContainer) {
            BlockController controller = (BlockController)block;
            controller.toggle(world, controllerPos, player, keyType);
        }
    }

    @Override
    public TileEntitySlave createNewTileEntity (World world, int meta) {
        return new TileEntitySlave();
    }

    public TileEntitySlave getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntitySlave) ? (TileEntitySlave) tile : null;
    }

    public TileEntitySlave getTileEntitySafe (World world, BlockPos pos) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createNewTileEntity(world, 0);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }
}
