package net.minecraft.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.exception.ExceptionUtils;

public abstract class CommandBase implements ICommand
{
    private static ICommandListener commandListener;

    /**
     * Convert a JsonParseException into a user-friendly exception
     */
    protected static SyntaxErrorException toSyntaxException(JsonParseException e)
    {
        Throwable throwable = ExceptionUtils.getRootCause(e);
        String s = "";

        if (throwable != null)
        {
            s = throwable.getMessage();

            if (s.contains("setLenient"))
            {
                s = s.substring(s.indexOf("to accept ") + 10);
            }
        }

        return new SyntaxErrorException("commands.tellraw.jsonException", new Object[] {s});
    }

    protected static NBTTagCompound entityToNBT(Entity theEntity)
    {
        NBTTagCompound nbttagcompound = theEntity.writeToNBT(new NBTTagCompound());

        if (theEntity instanceof EntityPlayer)
        {
            ItemStack itemstack = ((EntityPlayer)theEntity).inventory.getCurrentItem();

            if (itemstack != null && itemstack.getItem() != null)
            {
                nbttagcompound.setTag("SelectedItem", itemstack.writeToNBT(new NBTTagCompound()));
            }
        }

        return nbttagcompound;
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    /**
     * Get a list of aliases for this command. <b>Never return null!</b>
     */
    public List<String> getAliases()
    {
        return Collections.<String>emptyList();
    }

    /**
     * Check if the given ICommandSender has permission to execute this command
     */
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return Collections.<String>emptyList();
    }

    public static int parseInt(String input) throws NumberInvalidException
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
        }
    }

    public static int parseInt(String input, int min) throws NumberInvalidException
    {
        return parseInt(input, min, Integer.MAX_VALUE);
    }

    public static int parseInt(String input, int min, int max) throws NumberInvalidException
    {
        int i = parseInt(input);

        if (i < min)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Integer.valueOf(i), Integer.valueOf(min)});
        }
        else if (i > max)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Integer.valueOf(i), Integer.valueOf(max)});
        }
        else
        {
            return i;
        }
    }

    public static long parseLong(String input) throws NumberInvalidException
    {
        try
        {
            return Long.parseLong(input);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
        }
    }

    public static long parseLong(String input, long min, long max) throws NumberInvalidException
    {
        long i = parseLong(input);

        if (i < min)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Long.valueOf(i), Long.valueOf(min)});
        }
        else if (i > max)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Long.valueOf(i), Long.valueOf(max)});
        }
        else
        {
            return i;
        }
    }

    public static BlockPos parseBlockPos(ICommandSender sender, String[] args, int startIndex, boolean centerBlock) throws NumberInvalidException
    {
        BlockPos blockpos = sender.getPosition();
        return new BlockPos(parseDouble((double)blockpos.getX(), args[startIndex], -30000000, 30000000, centerBlock), parseDouble((double)blockpos.getY(), args[startIndex + 1], 0, 256, false), parseDouble((double)blockpos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock));
    }

    public static double parseDouble(String input) throws NumberInvalidException
    {
        try
        {
            double d0 = Double.parseDouble(input);

            if (!Doubles.isFinite(d0))
            {
                throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
            }
            else
            {
                return d0;
            }
        }
        catch (NumberFormatException var3)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {input});
        }
    }

    public static double parseDouble(String input, double min) throws NumberInvalidException
    {
        return parseDouble(input, min, Double.MAX_VALUE);
    }

    public static double parseDouble(String input, double min, double max) throws NumberInvalidException
    {
        double d0 = parseDouble(input);

        if (d0 < min)
        {
            throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d0), Double.valueOf(min)});
        }
        else if (d0 > max)
        {
            throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d0), Double.valueOf(max)});
        }
        else
        {
            return d0;
        }
    }

    public static boolean parseBoolean(String input) throws CommandException
    {
        if (!"true".equals(input) && !"1".equals(input))
        {
            if (!"false".equals(input) && !"0".equals(input))
            {
                throw new CommandException("commands.generic.boolean.invalid", new Object[] {input});
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns the given ICommandSender as a EntityPlayer or throw an exception.
     */
    public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender sender) throws PlayerNotFoundException
    {
        if (sender instanceof EntityPlayerMP)
        {
            return (EntityPlayerMP)sender;
        }
        else
        {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
        }
    }

    public static EntityPlayerMP getPlayer(MinecraftServer server, ICommandSender sender, String target) throws PlayerNotFoundException
    {
        EntityPlayerMP entityplayermp = EntitySelector.matchOnePlayer(sender, target);

        if (entityplayermp == null)
        {
            try
            {
                entityplayermp = server.getPlayerList().getPlayerByUUID(UUID.fromString(target));
            }
            catch (IllegalArgumentException var5)
            {
                ;
            }
        }

        if (entityplayermp == null)
        {
            entityplayermp = server.getPlayerList().getPlayerByUsername(target);
        }

        if (entityplayermp == null)
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return entityplayermp;
        }
    }

    public static Entity getEntity(MinecraftServer server, ICommandSender sender, String target) throws EntityNotFoundException
    {
        return getEntity(server, sender, target, Entity.class);
    }

    public static <T extends Entity> T getEntity(MinecraftServer server, ICommandSender sender, String target, Class <? extends T > targetClass) throws EntityNotFoundException
    {
        Entity entity = EntitySelector.matchOneEntity(sender, target, targetClass);

        if (entity == null)
        {
            entity = server.getPlayerList().getPlayerByUsername(target);
        }

        if (entity == null)
        {
            try
            {
                UUID uuid = UUID.fromString(target);
                entity = server.getEntityFromUuid(uuid);

                if (entity == null)
                {
                    entity = server.getPlayerList().getPlayerByUUID(uuid);
                }
            }
            catch (IllegalArgumentException var6)
            {
                throw new EntityNotFoundException("commands.generic.entity.invalidUuid", new Object[0]);
            }
        }

        if (entity != null && targetClass.isAssignableFrom(entity.getClass()))
        {
            return (T)entity;
        }
        else
        {
            throw new EntityNotFoundException();
        }
    }

    public static List<Entity> getEntityList(MinecraftServer server, ICommandSender sender, String target) throws EntityNotFoundException
    {
        return (List<Entity>)(EntitySelector.hasArguments(target) ? EntitySelector.matchEntities(sender, target, Entity.class) : Lists.newArrayList(new Entity[] {getEntity(server, sender, target)}));
    }

    public static String getPlayerName(MinecraftServer server, ICommandSender sender, String target) throws PlayerNotFoundException
    {
        try
        {
            return getPlayer(server, sender, target).getName();
        }
        catch (PlayerNotFoundException playernotfoundexception)
        {
            if (target != null && !target.startsWith("@"))
            {
                return target;
            }
            else
            {
                throw playernotfoundexception;
            }
        }
    }

    public static String getEntityName(MinecraftServer server, ICommandSender sender, String target) throws EntityNotFoundException
    {
        try
        {
            return getPlayer(server, sender, target).getName();
        }
        catch (PlayerNotFoundException var6)
        {
            try
            {
                return getEntity(server, sender, target).getCachedUniqueIdString();
            }
            catch (EntityNotFoundException entitynotfoundexception)
            {
                if (target != null && !target.startsWith("@"))
                {
                    return target;
                }
                else
                {
                    throw entitynotfoundexception;
                }
            }
        }
    }

    public static ITextComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index) throws CommandException, PlayerNotFoundException
    {
        return getChatComponentFromNthArg(sender, args, index, false);
    }

    public static ITextComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index, boolean p_147176_3_) throws PlayerNotFoundException
    {
        ITextComponent itextcomponent = new TextComponentString("");

        for (int i = index; i < args.length; ++i)
        {
            if (i > index)
            {
                itextcomponent.appendText(" ");
            }

            ITextComponent itextcomponent1 = net.minecraftforge.common.ForgeHooks.newChatWithLinks(args[i]); // Forge: links for messages

            if (p_147176_3_)
            {
                ITextComponent itextcomponent2 = EntitySelector.matchEntitiesToTextComponent(sender, args[i]);

                if (itextcomponent2 == null)
                {
                    if (EntitySelector.hasArguments(args[i]))
                    {
                        throw new PlayerNotFoundException();
                    }
                }
                else
                {
                    itextcomponent1 = itextcomponent2;
                }
            }

            itextcomponent.appendSibling(itextcomponent1);
        }

        return itextcomponent;
    }

    /**
     * Builds a string starting at startPos
     */
    public static String buildString(String[] args, int startPos)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = startPos; i < args.length; ++i)
        {
            if (i > startPos)
            {
                stringbuilder.append(" ");
            }

            String s = args[i];
            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static CommandBase.CoordinateArg parseCoordinate(double base, String selectorArg, boolean centerBlock) throws NumberInvalidException
    {
        return parseCoordinate(base, selectorArg, -30000000, 30000000, centerBlock);
    }

    public static CommandBase.CoordinateArg parseCoordinate(double base, String selectorArg, int min, int max, boolean centerBlock) throws NumberInvalidException
    {
        boolean flag = selectorArg.startsWith("~");

        if (flag && Double.isNaN(base))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(base)});
        }
        else
        {
            double d0 = 0.0D;

            if (!flag || selectorArg.length() > 1)
            {
                boolean flag1 = selectorArg.contains(".");

                if (flag)
                {
                    selectorArg = selectorArg.substring(1);
                }

                d0 += parseDouble(selectorArg);

                if (!flag1 && !flag && centerBlock)
                {
                    d0 += 0.5D;
                }
            }

            double d1 = d0 + (flag ? base : 0.0D);

            if (min != 0 || max != 0)
            {
                if (d1 < (double)min)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d1), Integer.valueOf(min)});
                }

                if (d1 > (double)max)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d1), Integer.valueOf(max)});
                }
            }

            return new CommandBase.CoordinateArg(d1, d0, flag);
        }
    }

    public static double parseDouble(double base, String input, boolean centerBlock) throws NumberInvalidException
    {
        return parseDouble(base, input, -30000000, 30000000, centerBlock);
    }

    public static double parseDouble(double base, String input, int min, int max, boolean centerBlock) throws NumberInvalidException
    {
        boolean flag = input.startsWith("~");

        if (flag && Double.isNaN(base))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(base)});
        }
        else
        {
            double d0 = flag ? base : 0.0D;

            if (!flag || input.length() > 1)
            {
                boolean flag1 = input.contains(".");

                if (flag)
                {
                    input = input.substring(1);
                }

                d0 += parseDouble(input);

                if (!flag1 && !flag && centerBlock)
                {
                    d0 += 0.5D;
                }
            }

            if (min != 0 || max != 0)
            {
                if (d0 < (double)min)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d0), Integer.valueOf(min)});
                }

                if (d0 > (double)max)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d0), Integer.valueOf(max)});
                }
            }

            return d0;
        }
    }

    /**
     * Gets the Item specified by the given text string.  First checks the item registry, then tries by parsing the
     * string as an integer ID (deprecated).  Warns the sender if we matched by parsing the ID.  Throws if the item
     * wasn't found.  Returns the item if it was found.
     */
    public static Item getItemByText(ICommandSender sender, String id) throws NumberInvalidException
    {
        ResourceLocation resourcelocation = new ResourceLocation(id);
        Item item = (Item)Item.REGISTRY.getObject(resourcelocation);

        if (item == null)
        {
            throw new NumberInvalidException("commands.give.item.notFound", new Object[] {resourcelocation});
        }
        else
        {
            return item;
        }
    }

    /**
     * Gets the Block specified by the given text string.  First checks the block registry, then tries by parsing the
     * string as an integer ID (deprecated).  Warns the sender if we matched by parsing the ID.  Throws if the block
     * wasn't found.  Returns the block if it was found.
     */
    public static Block getBlockByText(ICommandSender sender, String id) throws NumberInvalidException
    {
        ResourceLocation resourcelocation = new ResourceLocation(id);

        if (!Block.REGISTRY.containsKey(resourcelocation))
        {
            throw new NumberInvalidException("commands.give.block.notFound", new Object[] {resourcelocation});
        }
        else
        {
            Block block = (Block)Block.REGISTRY.getObject(resourcelocation);

            if (block == null)
            {
                throw new NumberInvalidException("commands.give.block.notFound", new Object[] {resourcelocation});
            }
            else
            {
                return block;
            }
        }
    }

    /**
     * Creates a linguistic series joining the input objects together.  Examples: 1) {} --> "",  2) {"Steve"} -->
     * "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil and Mark"
     */
    public static String joinNiceString(Object[] elements)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < elements.length; ++i)
        {
            String s = elements[i].toString();

            if (i > 0)
            {
                if (i == elements.length - 1)
                {
                    stringbuilder.append(" and ");
                }
                else
                {
                    stringbuilder.append(", ");
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static ITextComponent join(List<ITextComponent> components)
    {
        ITextComponent itextcomponent = new TextComponentString("");

        for (int i = 0; i < components.size(); ++i)
        {
            if (i > 0)
            {
                if (i == components.size() - 1)
                {
                    itextcomponent.appendText(" and ");
                }
                else if (i > 0)
                {
                    itextcomponent.appendText(", ");
                }
            }

            itextcomponent.appendSibling((ITextComponent)components.get(i));
        }

        return itextcomponent;
    }

    /**
     * Creates a linguistic series joining together the elements of the given collection.  Examples: 1) {} --> "",  2)
     * {"Steve"} --> "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil
     * and Mark"
     */
    public static String joinNiceStringFromCollection(Collection<String> strings)
    {
        return joinNiceString(strings.toArray(new String[strings.size()]));
    }

    public static List<String> getTabCompletionCoordinate(String[] inputArgs, int index, @Nullable BlockPos pos)
    {
        if (pos == null)
        {
            return Lists.newArrayList(new String[] {"~"});
        }
        else
        {
            int i = inputArgs.length - 1;
            String s;

            if (i == index)
            {
                s = Integer.toString(pos.getX());
            }
            else if (i == index + 1)
            {
                s = Integer.toString(pos.getY());
            }
            else
            {
                if (i != index + 2)
                {
                    return Collections.<String>emptyList();
                }

                s = Integer.toString(pos.getZ());
            }

            return Lists.newArrayList(new String[] {s});
        }
    }

    @Nullable
    public static List<String> getTabCompletionCoordinateXZ(String[] inputArgs, int index, @Nullable BlockPos lookedPos)
    {
        if (lookedPos == null)
        {
            return Lists.newArrayList(new String[] {"~"});
        }
        else
        {
            int i = inputArgs.length - 1;
            String s;

            if (i == index)
            {
                s = Integer.toString(lookedPos.getX());
            }
            else
            {
                if (i != index + 1)
                {
                    return null;
                }

                s = Integer.toString(lookedPos.getZ());
            }

            return Lists.newArrayList(new String[] {s});
        }
    }

    /**
     * Returns true if the given substring is exactly equal to the start of the given string (case insensitive).
     */
    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }

    /**
     * Returns a List of strings (chosen from the given strings) which the last word in the given string array is a
     * beginning-match for. (Tab completion).
     */
    public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities)
    {
        return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
    }

    public static List<String> getListOfStringsMatchingLastWord(String[] inputArgs, Collection<?> possibleCompletions)
    {
        String s = inputArgs[inputArgs.length - 1];
        List<String> list = Lists.<String>newArrayList();

        if (!possibleCompletions.isEmpty())
        {
            for (String s1 : Iterables.transform(possibleCompletions, Functions.toStringFunction()))
            {
                if (doesStringStartWith(s, s1))
                {
                    list.add(s1);
                }
            }

            if (list.isEmpty())
            {
                for (Object object : possibleCompletions)
                {
                    if (object instanceof ResourceLocation && doesStringStartWith(s, ((ResourceLocation)object).getResourcePath()))
                    {
                        list.add(String.valueOf(object));
                    }
                }
            }
        }

        return list;
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    public static void notifyCommandListener(ICommandSender sender, ICommand command, String translationKey, Object... translationArgs)
    {
        notifyCommandListener(sender, command, 0, translationKey, translationArgs);
    }

    public static void notifyCommandListener(ICommandSender sender, ICommand command, int flags, String translationKey, Object... translationArgs)
    {
        if (commandListener != null)
        {
            commandListener.notifyListener(sender, command, flags, translationKey, translationArgs);
        }
    }

    /**
     * Sets the command listener responsable for notifying server operators when asked to by commands
     */
    public static void setCommandListener(ICommandListener listener)
    {
        commandListener = listener;
    }

    public int compareTo(ICommand p_compareTo_1_)
    {
        return this.getName().compareTo(p_compareTo_1_.getName());
    }

    public static class CoordinateArg
        {
            private final double result;
            private final double amount;
            private final boolean isRelative;

            protected CoordinateArg(double resultIn, double amountIn, boolean relative)
            {
                this.result = resultIn;
                this.amount = amountIn;
                this.isRelative = relative;
            }

            public double getResult()
            {
                return this.result;
            }

            public double getAmount()
            {
                return this.amount;
            }

            public boolean isRelative()
            {
                return this.isRelative;
            }
        }
}