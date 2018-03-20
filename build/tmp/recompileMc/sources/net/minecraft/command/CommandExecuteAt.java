package net.minecraft.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class CommandExecuteAt extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "execute";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender)
    {
        return "commands.execute.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(final MinecraftServer server, final ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 5)
        {
            throw new WrongUsageException("commands.execute.usage", new Object[0]);
        }
        else
        {
            final Entity entity = getEntity(server, sender, args[0], Entity.class);
            final double d0 = parseDouble(entity.posX, args[1], false);
            final double d1 = parseDouble(entity.posY, args[2], false);
            final double d2 = parseDouble(entity.posZ, args[3], false);
            final BlockPos blockpos = new BlockPos(d0, d1, d2);
            int i = 4;

            if ("detect".equals(args[4]) && args.length > 10)
            {
                World world = entity.getEntityWorld();
                double d3 = parseDouble(d0, args[5], false);
                double d4 = parseDouble(d1, args[6], false);
                double d5 = parseDouble(d2, args[7], false);
                Block block = getBlockByText(sender, args[8]);
                int j = parseInt(args[9], -1, 15);
                BlockPos blockpos1 = new BlockPos(d3, d4, d5);

                if (!world.isBlockLoaded(blockpos1))
                {
                    throw new CommandException("commands.execute.failed", new Object[] {"detect", entity.getName()});
                }

                IBlockState iblockstate = world.getBlockState(blockpos1);

                if (iblockstate.getBlock() != block || j >= 0 && iblockstate.getBlock().getMetaFromState(iblockstate) != j)
                {
                    throw new CommandException("commands.execute.failed", new Object[] {"detect", entity.getName()});
                }

                i = 10;
            }

            String s = buildString(args, i);
            ICommandSender icommandsender = new ICommandSender()
            {
                /**
                 * Get the name of this object. For players this returns their username
                 */
                public String getName()
                {
                    return entity.getName();
                }
                /**
                 * Get the formatted ChatComponent that will be used for the sender's username in chat
                 */
                public ITextComponent getDisplayName()
                {
                    return entity.getDisplayName();
                }
                /**
                 * Send a chat message to the CommandSender
                 */
                public void sendMessage(ITextComponent component)
                {
                    sender.sendMessage(component);
                }
                /**
                 * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
                 */
                public boolean canUseCommand(int permLevel, String commandName)
                {
                    return sender.canUseCommand(permLevel, commandName);
                }
                /**
                 * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the coordinates 0, 0, 0
                 */
                public BlockPos getPosition()
                {
                    return blockpos;
                }
                /**
                 * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
                 * return 0.0D, 0.0D, 0.0D
                 */
                public Vec3d getPositionVector()
                {
                    return new Vec3d(d0, d1, d2);
                }
                /**
                 * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the overworld
                 */
                public World getEntityWorld()
                {
                    return entity.world;
                }
                /**
                 * Returns the entity associated with the command sender. MAY BE NULL!
                 */
                public Entity getCommandSenderEntity()
                {
                    return entity;
                }
                /**
                 * Returns true if the command sender should be sent feedback about executed commands
                 */
                public boolean sendCommandFeedback()
                {
                    return server == null || server.worlds[0].getGameRules().getBoolean("commandBlockOutput");
                }
                public void setCommandStat(CommandResultStats.Type type, int amount)
                {
                    entity.setCommandStat(type, amount);
                }
                /**
                 * Get the Minecraft server instance
                 */
                public MinecraftServer getServer()
                {
                    return entity.getServer();
                }
            };
            ICommandManager icommandmanager = server.getCommandManager();

            try
            {
                int k = icommandmanager.executeCommand(icommandsender, s);

                if (k < 1)
                {
                    throw new CommandException("commands.execute.allInvocationsFailed", new Object[] {s});
                }
            }
            catch (Throwable var24)
            {
                throw new CommandException("commands.execute.failed", new Object[] {s, entity.getName()});
            }
        }
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : (args.length > 1 && args.length <= 4 ? getTabCompletionCoordinate(args, 1, pos) : (args.length > 5 && args.length <= 8 && "detect".equals(args[4]) ? getTabCompletionCoordinate(args, 5, pos) : (args.length == 9 && "detect".equals(args[4]) ? getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys()) : Collections.<String>emptyList())));
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}