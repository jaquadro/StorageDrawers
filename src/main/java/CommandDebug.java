/*package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityItemRepository;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Arrays;

public class CommandDebug extends CommandBase
{
    @Override
    public String getName () {
        return StorageDrawers.MOD_ID;
    }

    @Override
    public String getUsage (ICommandSender sender) {
        return "commands.storagedrawers.usage";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            return;

        if (args[0].equals("simulate")) {
            Entity e = sender.getCommandSenderEntity();
            if (e instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP) e;
                simulateOp(server, mp, Arrays.copyOfRange(args, 1, args.length));
            }
        }
    }

    private void simulateOp (MinecraftServer server, EntityPlayerMP player, String[] args) {
        if (args.length < 2)
            return;

        ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
        if (item.isEmpty()) {
            sendMessage(player, "Need item in hand to perform simulation");
            return;
        }

        RayTraceResult rayResult = net.minecraftforge.common.ForgeHooks.rayTraceEyes(player, player.interactionManager.getBlockReachDistance() + 1);
        if (rayResult == null) {
            sendMessage(player, "Not facing a block");
            return;
        }

        BlockPos pos = rayResult.getBlockPos();
        World world = server.getWorld(player.dimension);
        if (world == null)
            return;

        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            sendMessage(player, "Target block not a suitable inventory");
            return;
        }

        if (args[0].equals("itemrepo")) {
            IItemRepository repo = tile.getCapability(CapabilityItemRepository.ITEM_REPOSITORY_CAPABILITY, null);
            if (repo == null) {
                sendMessage(player, "Target block does not have the IItemRepository capability");
                return;
            }

            if (args[1].equals("insert")) {
                if (args.length > 2) {
                    item = item.copy();
                    item.setCount(Integer.parseInt(args[2]));
                }

                sendMessage(player, "Simulate inserting " + item.toString() + " into tile " + tile.getClass().getSimpleName() + " at " + pos.toString());
                ItemStack remainder = repo.insertItem(item, true);

                sendMessage(player, "Count = " + item.getCount() + "; Accepted = " +
                    (item.getCount() - remainder.getCount()) + "; Remainder = " + remainder.getCount());
            }
        }
        else if (args[0].equals("itemhandler")) {
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler == null) {
                sendMessage(player, "Target block does not have the IItemHandler capability");
                return;
            }

            if (args[1].equals("insert")) {
                if (args.length > 2) {
                    item = item.copy();
                    item.setCount(Integer.parseInt(args[2]));
                }

                int slotCount = handler.getSlots();
                int matchCount = 0;

                sendMessage(player, "Simulate inserting " + item.toString() + " into tile " + tile.getClass().getSimpleName() + " at " + pos.toString());

                for (int i = 0; i < slotCount; i++) {
                    ItemStack remainder = handler.insertItem(i, item, true);
                    if (item.getCount() > remainder.getCount()) {
                        matchCount++;
                        sendMessage(player, "Slot " + i + ": Count = " + item.getCount() + "; Accepted = " +
                            (item.getCount() - remainder.getCount()) + "; Remainder = " + remainder.getCount());
                    }
                }

                sendMessage(player, "Stack full or partial acceptance in " + matchCount + " slots");
            }
        }
    }

    private void sendMessage (ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }
}
*/