package net.minecraft.command;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public interface ICommandManager
{
    /**
     * Attempt to execute a command. This method should return the number of times that the command was executed. If the
     * command does not exist or if the player does not have permission, 0 will be returned. A number greater than 1 can
     * be returned if a player selector is used.
     */
    int executeCommand(ICommandSender sender, String rawCommand);

    /**
     * Get a list of possible completion options for when the TAB key is pressed. This can be a list of commands if no
     * command is specified or a partial command is specified. It could also be a list of arguments for the command that
     * is specified.
     */
    List<String> getTabCompletions(ICommandSender sender, String input, @Nullable BlockPos pos);

    /**
     * Get a list of commands that the given command sender has access to execute.
     */
    List<ICommand> getPossibleCommands(ICommandSender sender);

    /**
     * Get a Map of all the name to command pairs stored in this command manager.
     */
    Map<String, ICommand> getCommands();
}