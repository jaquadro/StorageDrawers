package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.chameleon.resources.IItemMeshMapper;
import com.jaquadro.minecraft.chameleon.resources.IItemVariantProvider;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ItemCompDrawers extends ItemBlock implements IItemMeshMapper, IItemVariantProvider
{
    public ItemCompDrawers (Block block) {
        super(block);
    }

    @Override
    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos);
        if (tile != null) {
            if (side != EnumFacing.UP && side != EnumFacing.DOWN)
                tile.setDirection(side.ordinal());

            int initCapacity = StorageDrawers.config.getBlockBaseStorage("compdrawers");
            tile.setDrawerCapacity(initCapacity);

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tile"))
                tile.readFromPortableNBT(stack.getTagCompound().getCompoundTag("tile"));

            tile.setIsSealed(false);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        ConfigManager config = StorageDrawers.config;

        int count = config.getBlockBaseStorage("compdrawers");
        list.add(I18n.translateToLocalFormatted("storageDrawers.drawers.description", count));

        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("tile")) {
            list.add(ChatFormatting.YELLOW + I18n.translateToLocal("storageDrawers.drawers.sealed"));
        }
    }

    @Override
    public List<ResourceLocation> getItemVariants () {
        ResourceLocation location = GameData.getItemRegistry().getNameForObject(this);
        List<ResourceLocation> variants = new ArrayList<ResourceLocation>();

        for (EnumCompDrawer type : EnumCompDrawer.values())
            variants.add(new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + '_' + type.getName()));

        return variants;
    }

    @Override
    public List<Pair<ItemStack, ModelResourceLocation>> getMeshMappings () {
        List<Pair<ItemStack, ModelResourceLocation>> mappings = new ArrayList<Pair<ItemStack, ModelResourceLocation>>();

        for (EnumCompDrawer type : EnumCompDrawer.values()) {
            ModelResourceLocation location = new ModelResourceLocation(StorageDrawers.MOD_ID + ":compDrawers_" + type.getName(), "inventory");
            mappings.add(Pair.of(new ItemStack(this, 1, type.getMetadata()), location));
        }

        return mappings;
    }
}
