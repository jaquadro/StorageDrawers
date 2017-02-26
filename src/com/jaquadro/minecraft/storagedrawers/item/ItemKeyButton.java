package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.chameleon.resources.IItemMeshMapper;
import com.jaquadro.minecraft.chameleon.resources.IItemVariantProvider;
import com.jaquadro.minecraft.storagedrawers.block.BlockKeyButton;
import com.jaquadro.minecraft.storagedrawers.block.EnumKeyType;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemKeyButton extends ItemMultiTexture implements IItemMeshMapper, IItemVariantProvider
{
    public ItemKeyButton (Block block) {
        super(block, block, new Mapper() {
            @Override
            @Nonnull
            public String apply (@Nonnull ItemStack input) {
                return EnumKeyType.byMetadata(input.getMetadata()).getName();
            }
        });
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName (@Nonnull ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + EnumKeyType.byMetadata(itemStack.getMetadata()).getName();
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    public List<Pair<ItemStack, ModelResourceLocation>> getMeshMappings () {
        List<Pair<ItemStack, ModelResourceLocation>> mappings = new ArrayList<Pair<ItemStack, ModelResourceLocation>>();

        for (EnumKeyType keyType : EnumKeyType.values()) {
            IBlockState state = block.getDefaultState().withProperty(BlockKeyButton.VARIANT, keyType);
            ModelResourceLocation location = new ModelResourceLocation(ModBlocks.keyButton.getRegistryName().toString() + '_' + keyType.getName(), "inventory");
            mappings.add(Pair.of(new ItemStack(this, 1, block.getMetaFromState(state)), location));
        }

        return mappings;
    }

    @Override
    public List<ResourceLocation> getItemVariants () {
        ResourceLocation location = GameData.getItemRegistry().getNameForObject(this);
        List<ResourceLocation> variants = new ArrayList<ResourceLocation>();

        for (EnumKeyType keyType : EnumKeyType.values())
            variants.add(new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + '_' + keyType.getName()));

        return variants;
    }
}
