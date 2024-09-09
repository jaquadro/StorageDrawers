package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IDrawerCapability;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;

public class Capabilities
{
    public static final IDrawerCapability<IDrawerAttributes> DRAWER_ATTRIBUTES
        = new Wrapper<>("drawer_attributes", IDrawerAttributes.class, Void.TYPE);
    public static final IDrawerCapability<IDrawerGroup> DRAWER_GROUP
        = new Wrapper<>("drawer_group", IDrawerGroup.class, Void.TYPE);
    public static final IDrawerCapability<IItemRepository> ITEM_REPOSITORY
        = new Wrapper<>("item_respository", IItemRepository.class, Void.TYPE);
    public static final IDrawerCapability<IItemHandler> ITEM_HANDLER
        = new Wrapper<>(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK);

    static class Wrapper<T, C> implements IDrawerCapability<T> {
        final BlockCapability<T, C> nativeCapability;

        Wrapper(String name, Class<T> clazz, Class<C> ctx) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name);
            nativeCapability = BlockCapability.create(location, clazz, ctx);
        }

        Wrapper(BlockCapability<T, C> nativeCapability) {
            this.nativeCapability = nativeCapability;
        }

        @Override
        public T getCapability (Level level, BlockPos pos) {
            return level.getCapability(nativeCapability, pos, null, null, null);
        }
    }
}
