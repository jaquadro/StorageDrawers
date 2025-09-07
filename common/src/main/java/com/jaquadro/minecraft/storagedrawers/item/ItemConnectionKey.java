package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ConnectionMode;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class ItemConnectionKey extends ItemKey
{
    private final ConnectionMode mode;
    private static final Map<ConnectionMode, ItemConnectionKey> modeLookup = new HashMap<>();

    public ItemConnectionKey (ConnectionMode mode, Properties properties) {
        super(properties);
        this.mode = mode;
        modeLookup.put(mode, this);
    }

    public ItemConnectionKey getNextKey () {
        return modeLookup.getOrDefault(mode.getNextMode(), this);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs, UseOnContext context) {
        Direction dir = context.getClickedFace();
        if (dir != Direction.DOWN && dir != Direction.UP) {
            Direction blockDir = Direction.NORTH;
            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            if (state.hasProperty(HorizontalDirectionalBlock.FACING))
                blockDir = state.getValue(HorizontalDirectionalBlock.FACING).getOpposite();

            dir = Direction.fromYRot(dir.toYRot() - blockDir.toYRot());
        }
        ConnectionMode curMode = attrs.getSidedConnectionMode(dir);

        if (mode == ConnectionMode.DEFAULT)
            attrs.setSidedConnectionMode(dir, curMode.getNextMode());
        else {
            if (mode == curMode)
                attrs.setSidedConnectionMode(dir, ConnectionMode.DEFAULT);
            else
                attrs.setSidedConnectionMode(dir, mode);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use (Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!ModCommonConfig.INSTANCE.TOOLS.connectionKey.enable.get())
            return InteractionResultHolder.pass(stack);

        if (!player.isShiftKeyDown())
            return InteractionResultHolder.pass(stack);

        ItemStack nextKey = new ItemStack(getNextKey(), 1);
        nextKey.setTag(stack.getTag());

        return InteractionResultHolder.success(nextKey);
    }

    @Override
    public boolean isEnabled () {
        return ModCommonConfig.INSTANCE.TOOLS.connectionKey.enable.get();
    }
}
