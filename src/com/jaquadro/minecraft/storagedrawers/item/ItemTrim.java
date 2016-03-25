package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.base.Function;
import com.jaquadro.minecraft.chameleon.resources.IItemMeshMapper;
import com.jaquadro.minecraft.chameleon.resources.IItemVariantProvider;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemTrim extends ItemMultiTexture implements IItemMeshMapper, IItemVariantProvider
{
    public ItemTrim (Block block) {
        super(block, block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                return BlockPlanks.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
            }
        });
    }

    protected ItemTrim (Block block, Function function) {
        super(block, block, function);
    }

    @Override
    public boolean doesSneakBypassUse (World world, BlockPos pos, EntityPlayer player) {
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block instanceof BlockDrawers && ((BlockDrawers) block).retrimType() != null)
            return true;

        return false;
    }

    @Override
    public List<ResourceLocation> getItemVariants () {
        ResourceLocation location = GameData.getItemRegistry().getNameForObject(this);
        List<ResourceLocation> variants = new ArrayList<ResourceLocation>();

        for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values())
            variants.add(new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + '_' + woodType.getName()));

        return variants;
    }

    @Override
    public List<Pair<ItemStack, ModelResourceLocation>> getMeshMappings () {
        List<Pair<ItemStack, ModelResourceLocation>> mappings = new ArrayList<Pair<ItemStack, ModelResourceLocation>>();

        for (BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values()) {
            IBlockState state = block.getDefaultState().withProperty(BlockTrim.VARIANT, woodType);
            ModelResourceLocation location = new ModelResourceLocation(ModBlocks.getQualifiedName(ModBlocks.trim) + '_' + woodType.getName(), "inventory");
            mappings.add(Pair.of(new ItemStack(this, 1, block.getMetaFromState(state)), location));
        }

        return mappings;
    }
}
