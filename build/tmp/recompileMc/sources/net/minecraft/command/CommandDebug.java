package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase
{
    private static final Logger LOGGER = LogManager.getLogger();
    /** The time (in milliseconds) that profiling was started */
    private long profileStartTime;
    /** The tick number that profiling was started on */
    private int profileStartTick;

    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "debug";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender)
    {
        return "commands.debug.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.debug.usage", new Object[0]);
        }
        else
        {
            if ("start".equals(args[0]))
            {
                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                notifyCommandListener(sender, this, "commands.debug.start", new Object[0]);
                server.enableProfiling();
                this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
                this.profileStartTick = server.getTickCounter();
            }
            else
            {
                if (!"stop".equals(args[0]))
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                if (!server.theProfiler.profilingEnabled)
                {
                    throw new CommandException("commands.debug.notStarted", new Object[0]);
                }

                long i = MinecraftServer.getCurrentTimeMillis();
                int j = server.getTickCounter();
                long k = i - this.profileStartTime;
                int l = j - this.profileStartTick;
                this.saveProfilerResults(k, l, server);
                server.theProfiler.profilingEnabled = false;
                notifyCommandListener(sender, this, "commands.debug.stop", new Object[] {Float.valueOf((float)k / 1000.0F), Integer.valueOf(l)});
            }
        }
    }

    private void saveProfilerResults(long timeSpan, int tickSpan, MinecraftServer server)
    {
        File file1 = new File(server.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
        file1.getParentFile().mkdirs();
        FileWriter filewriter = null;

        try
        {
            filewriter = new FileWriter(file1);
            filewriter.write(this.getProfilerResults(timeSpan, tickSpan, server));
        }
        catch (Throwable throwable)
        {
            IOUtils.closeQuietly((Writer)filewriter);
            LOGGER.error("Could not save profiler results to {}", new Object[] {file1, throwable});
        } finally { IOUtils.closeQuietly(filewriter); } // FORGE: Fix MC-103399
    }

    private String getProfilerResults(long timeSpan, int tickSpan, MinecraftServer server)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time span: ").append(timeSpan).append(" ms\n");
        stringbuilder.append("Tick span: ").append(tickSpan).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format("%.2f", new Object[] {Float.valueOf((float)tickSpan / ((float)timeSpan / 1000.0F))})).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendProfilerResults(0, "root", stringbuilder, server);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringbuilder.toString();
    }

    private void appendProfilerResults(int p_184895_1_, String sectionName, StringBuilder builder, MinecraftServer server)
    {
        List<Profiler.Result> list = server.theProfiler.getProfilingData(sectionName);

        if (list != null && list.size() >= 3)
        {
            for (int i = 1; i < list.size(); ++i)
            {
                Profiler.Result profiler$result = (Profiler.Result)list.get(i);
                builder.append(String.format("[%02d] ", new Object[] {Integer.valueOf(p_184895_1_)}));

                for (int j = 0; j < p_184895_1_; ++j)
                {
                    builder.append("|   ");
                }

                builder.append(profiler$result.profilerName).append(" - ").append(String.format("%.2f", new Object[] {Double.valueOf(profiler$result.usePercentage)})).append("%/").append(String.format("%.2f", new Object[] {Double.valueOf(profiler$result.totalUsePercentage)})).append("%\n");

                if (!"unspecified".equals(profiler$result.profilerName))
                {
                    try
                    {
                        this.appendProfilerResults(p_184895_1_ + 1, sectionName + "." + profiler$result.profilerName, builder, server);
                    }
                    catch (Exception exception)
                    {
                        builder.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
                    }
                }
            }
        }
    }

    /**
     * Get a random witty comment
     */
    private static String getWittyComment()
    {
        String[] astring = new String[] {"Shiny numbers!", "Am I not running fast enough? :(", "I\'m working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it\'ll have more motivation to work faster! Poor server."};

        try
        {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"start", "stop"}): Collections.<String>emptyList();
    }
}