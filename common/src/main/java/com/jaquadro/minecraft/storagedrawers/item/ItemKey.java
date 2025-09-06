package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.capabilities.Capabilities;
import com.jaquadro.minecraft.storagedrawers.util.ComponentUtil;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemKey extends Item
{
    private final Multimap<Attribute, AttributeModifier> modifiers;

    public ItemKey(Item.Properties properties) {
        super(properties);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)2, AttributeModifier.Operation.ADDITION));
        modifiers = builder.build();
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player) {
        return !player.isCreative();
    }

    @Override
    public void appendHoverText (@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ComponentUtil.appendSplitDescription(tooltip, this);

        if (!isEnabled())
            tooltip.add(Component.translatable("itemConfig.storagedrawers.disabled_tool")
                .withStyle(ChatFormatting.YELLOW));
    }

    @NotNull
    public Component getDescription() {
        return isEnabled()
            ? Component.translatable(this.getDescriptionId() + ".desc") : Component.empty();
    }

    // TODO: Forge Extension
    //@Override
    //public Multimap<Attribute, AttributeModifier> getAttributeModifiers (EquipmentSlot slot, ItemStack stack) {
    //    return slot == EquipmentSlot.MAINHAND ? modifiers : super.getAttributeModifiers(slot, stack);
    //}

    @Override
    @NotNull
    public InteractionResult useOn (UseOnContext context) {
        if (!isEnabled())
            return InteractionResult.PASS;

        BlockEntity blockEntity = WorldUtils.getBlockEntity(context.getLevel(), context.getClickedPos(), BlockEntity.class);
        if (blockEntity == null)
            return InteractionResult.PASS;

        IDrawerAttributes attrs = Capabilities.DRAWER_ATTRIBUTES.getCapability(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (attrs == null)
            attrs = EmptyDrawerAttributes.EMPTY;

        if (!(attrs instanceof IDrawerAttributesModifiable))
            return InteractionResult.PASS;

        handleDrawerAttributes((IDrawerAttributesModifiable)attrs, context);

        if (context.getPlayer() != null)
            context.getPlayer().getCooldowns().addCooldown(this, 5);

        return InteractionResult.SUCCESS;
    }

    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs, UseOnContext context) { }

    public boolean isEnabled () {
        return true;
    }
}
