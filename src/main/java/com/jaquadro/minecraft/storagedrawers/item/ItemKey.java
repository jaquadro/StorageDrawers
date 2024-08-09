package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemKey extends Item
{
    public ItemKey(Item.Properties properties) {
        super(properties.attributes(createAttributes()));
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                    BASE_ATTACK_DAMAGE_ID,
                    2,
                    AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player) {
        return !player.isCreative();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("").append(getDescription()).withStyle(ChatFormatting.GRAY));
    }

    @OnlyIn(Dist.CLIENT)
    @NotNull
    public Component getDescription() {
        return Component.translatable(this.getDescriptionId() + ".desc");
    }

    @Override
    @NotNull
    public InteractionResult useOn (UseOnContext context) {
        BlockEntity blockEntity = WorldUtils.getBlockEntity(context.getLevel(), context.getClickedPos(), BlockEntity.class);
        if (blockEntity == null)
            return InteractionResult.PASS;

        IDrawerAttributes attrs = blockEntity.getLevel().getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        if (attrs == null)
            attrs = EmptyDrawerAttributes.EMPTY;

        if (!(attrs instanceof IDrawerAttributesModifiable))
            return InteractionResult.PASS;

        handleDrawerAttributes((IDrawerAttributesModifiable)attrs);

        return InteractionResult.SUCCESS;
    }


    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) { }
}
