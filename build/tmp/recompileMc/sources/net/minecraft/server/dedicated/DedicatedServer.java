package net.minecraft.server.dedicated;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerEula;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.CryptManager;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.SERVER)
public class DedicatedServer extends MinecraftServer implements IServer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern RESOURCE_PACK_SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    public final List<PendingCommand> pendingCommandList = Collections.<PendingCommand>synchronizedList(Lists.<PendingCommand>newArrayList());
    private RConThreadQuery theRConThreadQuery;
    private final RConConsoleSource rconConsoleSource = new RConConsoleSource(this);
    private RConThreadMain theRConThreadMain;
    private PropertyManager settings;
    private ServerEula eula;
    private boolean canSpawnStructures;
    private GameType gameType;
    private boolean guiIsEnabled;
    public static boolean allowPlayerLogins = false;

    public DedicatedServer(File anvilFileIn, DataFixer dataFixerIn, YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn)
    {
        super(anvilFileIn, Proxy.NO_PROXY, dataFixerIn, authServiceIn, sessionServiceIn, profileRepoIn, profileCacheIn);
        Thread thread = new Thread("Server Infinisleeper")
        {
            {
                this.setDaemon(true);
                this.start();
            }
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(2147483647L);
                    }
                    catch (InterruptedException var2)
                    {
                        ;
                    }
                }
            }
        };
    }

    /**
     * Initialises the server and starts it.
     */
    public boolean init() throws IOException
    {
        Thread thread = new Thread("Server console handler")
        {
            public void run()
            {
                if (net.minecraftforge.server.console.TerminalHandler.handleCommands(DedicatedServer.this)) return;
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
                String s4;

                try
                {
                    while (!DedicatedServer.this.isServerStopped() && DedicatedServer.this.isServerRunning() && (s4 = bufferedreader.readLine()) != null)
                    {
                        DedicatedServer.this.addPendingCommand(s4, DedicatedServer.this);
                    }
                }
                catch (IOException ioexception1)
                {
                    DedicatedServer.LOGGER.error((String)"Exception handling console input", (Throwable)ioexception1);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        LOGGER.info("Starting minecraft server version 1.10.2");

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        net.minecraftforge.fml.common.FMLCommonHandler.instance().onServerStart(this);

        LOGGER.info("Loading properties");
        this.settings = new PropertyManager(new File("server.properties"));
        this.eula = new ServerEula(new File("eula.txt"));

        if (!this.eula.hasAcceptedEULA())
        {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            this.eula.createEULAFile();
            return false;
        }
        else
        {
            if (this.isSinglePlayer())
            {
                this.setHostname("127.0.0.1");
            }
            else
            {
                this.setOnlineMode(this.settings.getBooleanProperty("online-mode", true));
                this.setHostname(this.settings.getStringProperty("server-ip", ""));
            }

            this.setCanSpawnAnimals(this.settings.getBooleanProperty("spawn-animals", true));
            this.setCanSpawnNPCs(this.settings.getBooleanProperty("spawn-npcs", true));
            this.setAllowPvp(this.settings.getBooleanProperty("pvp", true));
            this.setAllowFlight(this.settings.getBooleanProperty("allow-flight", false));
            this.setResourcePack(this.settings.getStringProperty("resource-pack", ""), this.loadResourcePackSHA());
            this.setMOTD(this.settings.getStringProperty("motd", "A Minecraft Server"));
            this.setForceGamemode(this.settings.getBooleanProperty("force-gamemode", false));
            this.setPlayerIdleTimeout(this.settings.getIntProperty("player-idle-timeout", 0));

            if (this.settings.getIntProperty("difficulty", 1) < 0)
            {
                this.settings.setProperty("difficulty", Integer.valueOf(0));
            }
            else if (this.settings.getIntProperty("difficulty", 1) > 3)
            {
                this.settings.setProperty("difficulty", Integer.valueOf(3));
            }

            this.canSpawnStructures = this.settings.getBooleanProperty("generate-structures", true);
            int i = this.settings.getIntProperty("gamemode", GameType.SURVIVAL.getID());
            this.gameType = WorldSettings.getGameTypeById(i);
            LOGGER.info("Default game type: {}", new Object[] {this.gameType});
            InetAddress inetaddress = null;

            if (!this.getServerHostname().isEmpty())
            {
                inetaddress = InetAddress.getByName(this.getServerHostname());
            }

            if (this.getServerPort() < 0)
            {
                this.setServerPort(this.settings.getIntProperty("server-port", 25565));
            }

            LOGGER.info("Generating keypair");
            this.setKeyPair(CryptManager.generateKeyPair());
            LOGGER.info("Starting Minecraft server on {}:{}", new Object[] {this.getServerHostname().isEmpty() ? "*" : this.getServerHostname(), Integer.valueOf(this.getServerPort())});

            try
            {
                this.getNetworkSystem().addLanEndpoint(inetaddress, this.getServerPort());
            }
            catch (IOException ioexception)
            {
                LOGGER.warn("**** FAILED TO BIND TO PORT!");
                LOGGER.warn("The exception was: {}", new Object[] {ioexception.toString()});
                LOGGER.warn("Perhaps a server is already running on that port?");
                return false;
            }

            if (!this.isServerInOnlineMode())
            {
                LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
                LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
                LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
                LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
            }

            if (this.convertFiles())
            {
                this.getPlayerProfileCache().save();
            }

            if (!PreYggdrasilConverter.tryConvert(this.settings))
            {
                return false;
            }
            else
            {
                net.minecraftforge.fml.common.FMLCommonHandler.instance().onServerStarted();
                this.setPlayerList(new DedicatedPlayerList(this));
                long j = System.nanoTime();

                if (this.getFolderName() == null)
                {
                    this.setFolderName(this.settings.getStringProperty("level-name", "world"));
                }

                String s = this.settings.getStringProperty("level-seed", "");
                String s1 = this.settings.getStringProperty("level-type", "DEFAULT");
                String s2 = this.settings.getStringProperty("generator-settings", "");
                long k = (new Random()).nextLong();

                if (!s.isEmpty())
                {
                    try
                    {
                        long l = Long.parseLong(s);

                        if (l != 0L)
                        {
                            k = l;
                        }
                    }
                    catch (NumberFormatException var16)
                    {
                        k = (long)s.hashCode();
                    }
                }

                WorldType worldtype = WorldType.parseWorldType(s1);

                if (worldtype == null)
                {
                    worldtype = WorldType.DEFAULT;
                }

                this.isAnnouncingPlayerAchievements();
                this.isCommandBlockEnabled();
                this.getOpPermissionLevel();
                this.isSnooperEnabled();
                this.getNetworkCompressionThreshold();
                this.setBuildLimit(this.settings.getIntProperty("max-build-height", 256));
                this.setBuildLimit((this.getBuildLimit() + 8) / 16 * 16);
                this.setBuildLimit(MathHelper.clamp(this.getBuildLimit(), 64, 256));
                this.settings.setProperty("max-build-height", Integer.valueOf(this.getBuildLimit()));
                TileEntitySkull.setProfileCache(this.getPlayerProfileCache());
                TileEntitySkull.setSessionService(this.getMinecraftSessionService());
                PlayerProfileCache.setOnlineMode(this.isServerInOnlineMode());
                if (!net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerAboutToStart(this)) return false;
                LOGGER.info("Preparing level \"{}\"", new Object[] {this.getFolderName()});
                this.loadAllWorlds(this.getFolderName(), this.getFolderName(), k, worldtype, s2);
                long i1 = System.nanoTime() - j;
                String s3 = String.format("%.3fs", new Object[] {Double.valueOf((double)i1 / 1.0E9D)});
                LOGGER.info("Done ({})! For help, type \"help\" or \"?\"", new Object[] {s3});

                if (this.settings.getBooleanProperty("enable-query", false))
                {
                    LOGGER.info("Starting GS4 status listener");
                    this.theRConThreadQuery = new RConThreadQuery(this);
                    this.theRConThreadQuery.startThread();
                }

                if (this.settings.getBooleanProperty("enable-rcon", false))
                {
                    LOGGER.info("Starting remote control listener");
                    this.theRConThreadMain = new RConThreadMain(this);
                    this.theRConThreadMain.startThread();
                }

                if (this.getMaxTickTime() > 0L)
                {
                    Thread thread1 = new Thread(new ServerHangWatchdog(this));
                    thread1.setName("Server Watchdog");
                    thread1.setDaemon(true);
                    thread1.start();
                }

                return net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerStarting(this);
            }
        }
    }

    public String loadResourcePackSHA()
    {
        if (this.settings.hasProperty("resource-pack-hash"))
        {
            if (this.settings.hasProperty("resource-pack-sha1"))
            {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
            else
            {
                LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
                this.settings.getStringProperty("resource-pack-sha1", this.settings.getStringProperty("resource-pack-hash", ""));
                this.settings.removeProperty("resource-pack-hash");
            }
        }

        String s = this.settings.getStringProperty("resource-pack-sha1", "");

        if (!s.isEmpty() && !RESOURCE_PACK_SHA1_PATTERN.matcher(s).matches())
        {
            LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }

        if (!this.settings.getStringProperty("resource-pack", "").isEmpty() && s.isEmpty())
        {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }

        return s;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(GameType gameMode)
    {
        super.setGameType(gameMode);
        this.gameType = gameMode;
    }

    public boolean canStructuresSpawn()
    {
        return this.canSpawnStructures;
    }

    public GameType getGameType()
    {
        return this.gameType;
    }

    /**
     * Get the server's difficulty
     */
    public EnumDifficulty getDifficulty()
    {
        return EnumDifficulty.getDifficultyEnum(this.settings.getIntProperty("difficulty", EnumDifficulty.NORMAL.getDifficultyId()));
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.settings.getBooleanProperty("hardcore", false);
    }

    /**
     * Called on exit from the main run() loop.
     */
    public void finalTick(CrashReport report)
    {
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        report = super.addServerInfoToCrashReport(report);
        report.getCategory().setDetail("Is Modded", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                String s = DedicatedServer.this.getServerModName();
                return !"vanilla".equals(s) ? "Definitely; Server brand changed to \'" + s + "\'" : "Unknown (can\'t tell)";
            }
        });
        report.getCategory().setDetail("Type", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return "Dedicated Server (map_server.txt)";
            }
        });
        return report;
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    public void systemExitNow()
    {
        System.exit(0);
    }

    public void updateTimeLightAndEntities()
    {
        super.updateTimeLightAndEntities();
        this.executePendingCommands();
    }

    public boolean getAllowNether()
    {
        return this.settings.getBooleanProperty("allow-nether", true);
    }

    public boolean allowSpawnMonsters()
    {
        return this.settings.getBooleanProperty("spawn-monsters", true);
    }

    public void addServerStatsToSnooper(Snooper playerSnooper)
    {
        playerSnooper.addClientStat("whitelist_enabled", Boolean.valueOf(this.getPlayerList().isWhiteListEnabled()));
        playerSnooper.addClientStat("whitelist_count", Integer.valueOf(this.getPlayerList().getWhitelistedPlayerNames().length));
        super.addServerStatsToSnooper(playerSnooper);
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return this.settings.getBooleanProperty("snooper-enabled", true);
    }

    public void addPendingCommand(String input, ICommandSender sender)
    {
        this.pendingCommandList.add(new PendingCommand(input, sender));
    }

    public void executePendingCommands()
    {
        while (!this.pendingCommandList.isEmpty())
        {
            PendingCommand pendingcommand = (PendingCommand)this.pendingCommandList.remove(0);
            this.getCommandManager().executeCommand(pendingcommand.sender, pendingcommand.command);
        }
    }

    public boolean isDedicatedServer()
    {
        return true;
    }

    /**
     * Get if native transport should be used. Native transport means linux server performance improvements and
     * optimized packet sending/receiving on linux
     */
    public boolean shouldUseNativeTransport()
    {
        return this.settings.getBooleanProperty("use-native-transport", true);
    }

    public DedicatedPlayerList getPlayerList()
    {
        return (DedicatedPlayerList)super.getPlayerList();
    }

    /**
     * Gets an integer property. If it does not exist, set it to the specified value.
     */
    public int getIntProperty(String key, int defaultValue)
    {
        return this.settings.getIntProperty(key, defaultValue);
    }

    /**
     * Gets a string property. If it does not exist, set it to the specified value.
     */
    public String getStringProperty(String key, String defaultValue)
    {
        return this.settings.getStringProperty(key, defaultValue);
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified value.
     */
    public boolean getBooleanProperty(String key, boolean defaultValue)
    {
        return this.settings.getBooleanProperty(key, defaultValue);
    }

    /**
     * Saves an Object with the given property name.
     */
    public void setProperty(String key, Object value)
    {
        this.settings.setProperty(key, value);
    }

    /**
     * Saves all of the server properties to the properties file.
     */
    public void saveProperties()
    {
        this.settings.saveProperties();
    }

    /**
     * Returns the filename where server properties are stored
     */
    public String getSettingsFilename()
    {
        File file1 = this.settings.getPropertiesFile();
        return file1 != null ? file1.getAbsolutePath() : "No settings file";
    }

    /**
     * Returns the server's hostname.
     */
    public String getHostname()
    {
        return this.getServerHostname();
    }

    /**
     * Never used, but "getServerPort" is already taken.
     */
    public int getPort()
    {
        return this.getServerPort();
    }

    /**
     * Returns the server message of the day
     */
    public String getMotd()
    {
        return this.getMOTD();
    }

    public void setGuiEnabled()
    {
        MinecraftServerGui.createServerGui(this);
        this.guiIsEnabled = true;
    }

    public boolean getGuiEnabled()
    {
        return this.guiIsEnabled;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(GameType type, boolean allowCheats)
    {
        return "";
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled()
    {
        return this.settings.getBooleanProperty("enable-command-block", false);
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize()
    {
        return this.settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
    }

    public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        if (worldIn.provider.getDimension() != 0)
        {
            return false;
        }
        else if (this.getPlayerList().getOppedPlayers().isEmpty())
        {
            return false;
        }
        else if (this.getPlayerList().canSendCommands(playerIn.getGameProfile()))
        {
            return false;
        }
        else if (this.getSpawnProtectionSize() <= 0)
        {
            return false;
        }
        else
        {
            BlockPos blockpos = worldIn.getSpawnPoint();
            int i = MathHelper.abs(pos.getX() - blockpos.getX());
            int j = MathHelper.abs(pos.getZ() - blockpos.getZ());
            int k = Math.max(i, j);
            return k <= this.getSpawnProtectionSize();
        }
    }

    public int getOpPermissionLevel()
    {
        return this.settings.getIntProperty("op-permission-level", 4);
    }

    public void setPlayerIdleTimeout(int idleTimeout)
    {
        super.setPlayerIdleTimeout(idleTimeout);
        this.settings.setProperty("player-idle-timeout", Integer.valueOf(idleTimeout));
        this.saveProperties();
    }

    /**
     * Get if RCON command events should be broadcast to ops
     */
    public boolean shouldBroadcastRconToOps()
    {
        return this.settings.getBooleanProperty("broadcast-rcon-to-ops", true);
    }

    /**
     * Get if console command events should be broadcast to ops
     */
    public boolean shouldBroadcastConsoleToOps()
    {
        return this.settings.getBooleanProperty("broadcast-console-to-ops", true);
    }

    public boolean isAnnouncingPlayerAchievements()
    {
        return this.settings.getBooleanProperty("announce-player-achievements", true);
    }

    public int getMaxWorldSize()
    {
        int i = this.settings.getIntProperty("max-world-size", super.getMaxWorldSize());

        if (i < 1)
        {
            i = 1;
        }
        else if (i > super.getMaxWorldSize())
        {
            i = super.getMaxWorldSize();
        }

        return i;
    }

    /**
     * The compression treshold. If the packet is larger than the specified amount of bytes, it will be compressed
     */
    public int getNetworkCompressionThreshold()
    {
        return this.settings.getIntProperty("network-compression-threshold", super.getNetworkCompressionThreshold());
    }

    //Forge: Enable formated text for colors in console.
    @Override public void sendMessage(net.minecraft.util.text.ITextComponent message) { LOGGER.info(message.getFormattedText()); }

    protected boolean convertFiles() throws IOException
    {
        boolean flag = false;

        for (int i = 0; !flag && i <= 2; ++i)
        {
            if (i > 0)
            {
                LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag = PreYggdrasilConverter.convertUserBanlist(this);
        }

        boolean flag1 = false;

        for (int j = 0; !flag1 && j <= 2; ++j)
        {
            if (j > 0)
            {
                LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag1 = PreYggdrasilConverter.convertIpBanlist(this);
        }

        boolean flag2 = false;

        for (int k = 0; !flag2 && k <= 2; ++k)
        {
            if (k > 0)
            {
                LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag2 = PreYggdrasilConverter.convertOplist(this);
        }

        boolean flag3 = false;

        for (int l = 0; !flag3 && l <= 2; ++l)
        {
            if (l > 0)
            {
                LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag3 = PreYggdrasilConverter.convertWhitelist(this);
        }

        boolean flag4 = false;

        for (int i1 = 0; !flag4 && i1 <= 2; ++i1)
        {
            if (i1 > 0)
            {
                LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag4 = PreYggdrasilConverter.convertSaveFiles(this, this.settings);
        }

        return flag || flag1 || flag2 || flag3 || flag4;
    }

    private void sleepFiveSeconds()
    {
        try
        {
            Thread.sleep(5000L);
        }
        catch (InterruptedException var2)
        {
            ;
        }
    }

    public long getMaxTickTime()
    {
        return this.settings.getLongProperty("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
    }

    /**
     * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    public String getPlugins()
    {
        return "";
    }

    /**
     * Handle a command received by an RCon instance
     */
    public String handleRConCommand(String command)
    {
        this.rconConsoleSource.resetLog();
        this.commandManager.executeCommand(this.rconConsoleSource, command);
        return this.rconConsoleSource.getLogContents();
    }
}