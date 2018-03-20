package net.minecraft.command;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public interface ICommandSender
{
    /**
     * Get the name of this object. For players this returns their username
     */
    String getName();

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    ITextComponent getDisplayName();

    /**
     * Send a chat message to the CommandSender
     */
    void sendMessage(ITextComponent component);

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    boolean canUseCommand(int permLevel, String commandName);

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the coordinates 0, 0, 0
     */
    BlockPos getPosition();

    /**
     * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return 0.0D,
     * 0.0D, 0.0D
     */
    Vec3d getPositionVector();

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the overworld
     */
    World getEntityWorld();

    /**
     * Returns the entity associated with the command sender. MAY BE NULL!
     */
    @Nullable
    Entity getCommandSenderEntity();

    /**
     * Returns true if the command sender should be sent feedback about executed commands
     */
    boolean sendCommandFeedback();

    void setCommandStat(CommandResultStats.Type type, int amount);

    /**
     * Get the Minecraft server instance
     */
    @Nullable
    MinecraftServer getServer();
}