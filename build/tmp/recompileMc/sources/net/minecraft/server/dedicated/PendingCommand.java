package net.minecraft.server.dedicated;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class PendingCommand
{
    /** The command string. */
    public final String command;
    public final ICommandSender sender;

    public PendingCommand(String input, ICommandSender sender)
    {
        this.command = input;
        this.sender = sender;
    }
}