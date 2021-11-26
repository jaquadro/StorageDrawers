package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.List;

public class ItemKey extends Item
{
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final Multimap<Attribute, AttributeModifier> modifiers;

    public ItemKey(Item.Properties properties) {
        super(properties);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)2, AttributeModifier.Operation.ADDITION));
        modifiers = builder.build();
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TextComponent("").append(getDescription()).withStyle(ChatFormatting.GRAY));
    }

    @OnlyIn(Dist.CLIENT)
    protected Component getDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".desc");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers (EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? modifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public InteractionResult useOn (UseOnContext context) {
        BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
        if (tile == null)
            return InteractionResult.PASS;

        IDrawerAttributes attrs = tile.getCapability(DRAWER_ATTRIBUTES_CAPABILITY, null).orElse(EmptyDrawerAttributes.EMPTY);
        if (!(attrs instanceof IDrawerAttributesModifiable))
            return InteractionResult.PASS;

        handleDrawerAttributes((IDrawerAttributesModifiable)attrs);

        return InteractionResult.SUCCESS;
    }


    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) { }
}
