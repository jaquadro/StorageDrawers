package net.minecraft.command.server;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CommandTeleport extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "teleport";
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
        return "commands.teleport.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException("commands.teleport.usage", new Object[0]);
        }
        else
        {
            Entity entity = getEntity(server, sender, args[0]);

            if (entity.world != null)
            {
                int i = 4096;
                Vec3d vec3d = sender.getPositionVector();
                int j = 1;
                CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(vec3d.xCoord, args[j++], true);
                CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(vec3d.yCoord, args[j++], -4096, 4096, false);
                CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(vec3d.zCoord, args[j++], true);
                Entity entity1 = sender.getCommandSenderEntity() == null ? entity : sender.getCommandSenderEntity();
                CommandBase.CoordinateArg commandbase$coordinatearg3 = parseCoordinate(args.length > j ? (double)entity1.rotationYaw : (double)entity.rotationYaw, args.length > j ? args[j] : "~", false);
                ++j;
                CommandBase.CoordinateArg commandbase$coordinatearg4 = parseCoordinate(args.length > j ? (double)entity1.rotationPitch : (double)entity.rotationPitch, args.length > j ? args[j] : "~", false);
                doTeleport(entity, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, commandbase$coordinatearg3, commandbase$coordinatearg4);
                notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {entity.getName(), Double.valueOf(commandbase$coordinatearg.getResult()), Double.valueOf(commandbase$coordinatearg1.getResult()), Double.valueOf(commandbase$coordinatearg2.getResult())});
            }
        }
    }

    /**
     * Perform the actual teleport
     */
    private static void doTeleport(Entity p_189862_0_, CommandBase.CoordinateArg p_189862_1_, CommandBase.CoordinateArg p_189862_2_, CommandBase.CoordinateArg p_189862_3_, CommandBase.CoordinateArg p_189862_4_, CommandBase.CoordinateArg p_189862_5_)
    {
        if (p_189862_0_ instanceof EntityPlayerMP)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);
            float f = (float)p_189862_4_.getAmount();

            if (p_189862_4_.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
            }
            else
            {
                f = MathHelper.wrapDegrees(f);
            }

            float f1 = (float)p_189862_5_.getAmount();

            if (p_189862_5_.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
            }
            else
            {
                f1 = MathHelper.wrapDegrees(f1);
            }

            p_189862_0_.dismountRidingEntity();
            ((EntityPlayerMP)p_189862_0_).connection.setPlayerLocation(p_189862_1_.getResult(), p_189862_2_.getResult(), p_189862_3_.getResult(), f, f1, set);
            p_189862_0_.setRotationYawHead(f);
        }
        else
        {
            float f2 = (float)MathHelper.wrapDegrees(p_189862_4_.getResult());
            float f3 = (float)MathHelper.wrapDegrees(p_189862_5_.getResult());
            f3 = MathHelper.clamp(f3, -90.0F, 90.0F);
            p_189862_0_.setLocationAndAngles(p_189862_1_.getResult(), p_189862_2_.getResult(), p_189862_3_.getResult(), f2, f3);
            p_189862_0_.setRotationYawHead(f2);
        }

        if (!(p_189862_0_ instanceof EntityLivingBase) || !((EntityLivingBase)p_189862_0_).isElytraFlying())
        {
            p_189862_0_.motionY = 0.0D;
            p_189862_0_.onGround = true;
        }
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : (args.length > 1 && args.length <= 4 ? getTabCompletionCoordinate(args, 1, pos) : Collections.<String>emptyList());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}