package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.capabilities.Capabilities;
import com.jaquadro.minecraft.storagedrawers.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemKey extends Item
{
    public ItemKey (Properties properties) {
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
    public void appendHoverText (ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("").append(getDescription()).withStyle(ChatFormatting.GRAY));
    }

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

        IDrawerAttributes attrs = Capabilities.DRAWER_ATTRIBUTES.getCapability(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (attrs == null)
            attrs = EmptyDrawerAttributes.EMPTY;

        if (!(attrs instanceof IDrawerAttributesModifiable))
            return InteractionResult.PASS;

        handleDrawerAttributes((IDrawerAttributesModifiable)attrs);

        return InteractionResult.SUCCESS;
    }


    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) { }
}
