package com.jaquadro.minecraft.storagedrawers.client.model.register;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.model.FramingTableModel;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.*;

public class FramingTableRegister
{
    private static final Set<IBlockState> blockStates = new HashSet<IBlockState>();
    private static final Set<ModelResourceLocation> resourceLocations = new HashSet<ModelResourceLocation>();
    private static final Set<ModelResourceLocation> itemResourceLocations = new HashSet<ModelResourceLocation>();
    private static final Map<EnumFacing, Map<Boolean, ModelResourceLocation>> stateMap = new HashMap<EnumFacing, Map<Boolean, ModelResourceLocation>>();

    public static void initalize (IRegistry<ModelResourceLocation, IBakedModel> modelRegistry) {
        initializeResourceLocations();

        for (IBlockState state : blockStates)
            modelRegistry.putObject(getResourceLocation(state), new FramingTableModel(state));
    }

    public static void initializeResourceLocations () {
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            Map<Boolean, ModelResourceLocation> sideMap = new HashMap<Boolean, ModelResourceLocation>();
            stateMap.put(dir, sideMap);

            for (Boolean side : new Boolean[] { false, true }) {
                String key = StorageDrawers.MOD_ID + ":framingTable#facing=" + dir + ",right=" + side;
                ModelResourceLocation location = new ModelResourceLocation(key);

                resourceLocations.add(location);
                blockStates.add(ModBlocks.framingTable.getDefaultState().withProperty(BlockFramingTable.FACING, dir).withProperty(BlockFramingTable.RIGHT_SIDE, side));
                sideMap.put(side, location);
            }
        }

        String key = StorageDrawers.MOD_ID + ":framingTable#inventory";
        ModelResourceLocation location = new ModelResourceLocation(key);

        itemResourceLocations.add(location);
    }

    private static ModelResourceLocation getResourceLocation (IBlockState state) {
        ResourceLocation loc = GameData.getBlockRegistry().getNameForObject(state.getBlock());

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<IProperty, Comparable> entry : state.getProperties().entrySet()) {
           builder.append(entry.getKey().getName() + '=' + entry.getValue().toString() + ',');
        }
        builder.deleteCharAt(builder.length() - 1);

        return new ModelResourceLocation(loc, builder.toString());
    }
}
