package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

@SideOnly(Side.CLIENT)
public class Minecraft implements IThreadListener, ISnooperInfo
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation LOCATION_MOJANG_PNG = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean IS_RUNNING_ON_MAC = Util.getOSType() == Util.EnumOS.OSX;
    /** A 10MiB preallocation to ensure the heap is reasonably sized. */
    public static byte[] memoryReserve = new byte[10485760];
    private static final List<DisplayMode> MAC_DISPLAY_MODES = Lists.newArrayList(new DisplayMode[] {new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;
    /** The player's GameProfile properties */
    private final PropertyMap profileProperties;
    private ServerData currentServerData;
    /** The RenderEngine instance used by Minecraft */
    public TextureManager renderEngine;
    /** Set to 'this' in Minecraft constructor; used by some settings get methods */
    private static Minecraft theMinecraft;
    private final DataFixer dataFixer;
    @Nullable
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private final boolean enableGLErrorChecking = true;
    private boolean hasCrashed;
    /** Instance of CrashReport. */
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    /** True if the player is connected to a realms server */
    private boolean connectedToRealms;
    private final Timer timer = new Timer(20.0F);
    /** Instance of PlayerUsageSnooper. */
    private final Snooper usageSnooper = new Snooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient world;
    public RenderGlobal renderGlobal;
    private RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP player;
    @Nullable
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public ParticleManager effectRenderer;
    private final Session session;
    private boolean isGamePaused;
    /** The font renderer used for displaying and measuring text */
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;
    /** The GuiScreen that's being displayed at the moment. */
    @Nullable
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    public DebugRenderer debugRenderer;
    /** Mouse left click counter */
    private int leftClickCounter;
    /** Display width */
    private final int tempDisplayWidth;
    /** Display height */
    private final int tempDisplayHeight;
    /** Instance of IntegratedServer. */
    @Nullable
    private IntegratedServer theIntegratedServer;
    /** Gui achievement */
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;
    /** Skip render world */
    public boolean skipRenderWorld;
    /** The ray trace hit that the mouse is over. */
    public RayTraceResult objectMouseOver;
    /** The game settings that currently hold effect. */
    public GameSettings gameSettings;
    /** Mouse helper instance. */
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final String versionType;
    private final Proxy proxy;
    private ISaveFormat saveLoader;
    /**
     * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
     * the usage snooping.
     */
    private static int debugFPS;
    /** When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block. */
    private int rightClickDelayTimer;
    private String serverName;
    private int serverPort;
    /** Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus. */
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();
    /** Join player counter */
    private int joinPlayerCounter;
    /** The FrameTimer's instance */
    public final FrameTimer frameTimer = new FrameTimer();
    /** Time in nanoseconds of when the class is loaded */
    long startNanoTime = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    @Nullable
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;
    /** The profiler instance */
    public final Profiler mcProfiler = new Profiler();
    /** Keeps track of how long the debug crash keycombo (F3+C) has been pressed for, in order to crash after 10 seconds. */
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final MetadataSerializer metadataSerializer_ = new MetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.<IResourcePack>newArrayList();
    public final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private BlockColors blockColors;
    private ItemColors itemColors;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue < FutureTask<? >> scheduledTasks = Queues. < FutureTask<? >> newArrayDeque();
    private final Thread mcThread = Thread.currentThread();
    private ModelManager modelManager;
    /** The BlockRenderDispatcher instance that will be used based off gamesettings */
    private BlockRendererDispatcher blockRenderDispatcher;
    /** Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly. */
    volatile boolean running = true;
    /** String that shows the debug information */
    public String debug = "";
    public boolean renderChunksMany = true;
    /** Approximate time (in ms) of last update to debug string */
    private long debugUpdateTime = getSystemTime();
    /** holds the current fps */
    private int fpsCounter;
    private boolean actionKeyF3;
    long prevFrameTime = -1L;
    /** Profiler currently displayed in the debug screen pie chart */
    private String debugProfilerName = "root";

    public Minecraft(GameConfiguration gameConfig)
    {
        theMinecraft = this;
        this.mcDataDir = gameConfig.folderInfo.mcDataDir;
        this.fileAssets = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.versionType = gameConfig.gameInfo.versionType;
        this.twitchDetails = gameConfig.userInfo.userProperties;
        this.profileProperties = gameConfig.userInfo.profileProperties;
        this.mcDefaultResourcePack = new DefaultResourcePack(gameConfig.folderInfo.getAssetsIndex());
        this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        this.sessionService = (new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
        this.session = gameConfig.userInfo.session;
        LOGGER.info("Setting user: {}", new Object[] {this.session.getUsername()});
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = isJvm64bit();
        this.theIntegratedServer = null;

        if (gameConfig.serverInfo.serverName != null)
        {
            this.serverName = gameConfig.serverInfo.serverName;
            this.serverPort = gameConfig.serverInfo.serverPort;
        }

        ImageIO.setUseCache(false);
        Bootstrap.register();
        this.dataFixer = DataFixesManager.createFixer();
    }

    public void run()
    {
        this.running = true;

        try
        {
            this.init();
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
            crashreport.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
            return;
        }

        while (true)
        {
            try
            {
                while (this.running)
                {
                    if (!this.hasCrashed || this.crashReporter == null)
                    {
                        try
                        {
                            this.runGameLoop();
                        }
                        catch (OutOfMemoryError var10)
                        {
                            this.freeMemory();
                            this.displayGuiScreen(new GuiMemoryErrorScreen());
                            System.gc();
                        }
                    }
                    else
                    {
                        this.displayCrashReport(this.crashReporter);
                    }
                }
            }
            catch (MinecraftError var12)
            {
                break;
            }
            catch (ReportedException reportedexception)
            {
                this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
                this.freeMemory();
                LOGGER.fatal((String)"Reported exception thrown!", (Throwable)reportedexception);
                this.displayCrashReport(reportedexception.getCrashReport());
                break;
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
                this.freeMemory();
                LOGGER.fatal("Unreported exception thrown!", throwable1);
                this.displayCrashReport(crashreport1);
                break;
            }
            finally
            {
                this.shutdownMinecraftApplet();
            }

            return;
        }
    }

    /**
     * Starts the game: initializes the canvas, the title, the settings, etcetera.
     */
    private void init() throws LWJGLException, IOException
    {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
        this.startTimerHackThread();

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0)
        {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        LOGGER.info("LWJGL Version: {}", new Object[] {Sys.getVersion()});
        this.setWindowIcon();
        this.setInitialDisplayMode();
        this.createDisplay();
        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.registerMetadataSerializers();
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        net.minecraftforge.fml.client.FMLClientHandler.instance().beginMinecraftLoading(this, this.defaultResourcePacks, this.mcResourceManager);
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        net.minecraftforge.fml.client.SplashProgress.drawVanillaScreen(this.renderEngine);
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"), this.dataFixer);
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.OPEN_INVENTORY.setStatStringFormatter(new IStatStringFormat()
        {
            /**
             * Formats the strings based on 'IStatStringFormat' interface.
             */
            public String formatString(String str)
            {
                try
                {
                    return String.format(str, new Object[] {Minecraft.this.gameSettings.keyBindInventory.getDisplayName()});
                }
                catch (Exception exception)
                {
                    return "Error: " + exception.getLocalizedMessage();
                }
            }
        });
        this.mouseHelper = new MouseHelper();
        net.minecraftforge.fml.common.ProgressManager.ProgressBar bar= net.minecraftforge.fml.common.ProgressManager.push("Rendering Setup", 5, true);
        bar.step("GL Setup");
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.checkGLError("Startup");
        bar.step("Loading Texture Map");
        this.textureMapBlocks = new TextureMap("textures", true);
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTickableTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        bar.step("Loading Model Manager");
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.blockColors = BlockColors.init();
        this.itemColors = ItemColors.init(this.blockColors);
        bar.step("Loading Item Renderer");
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager, this.itemColors);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        bar.step("Loading Entity Renderer");
        net.minecraftforge.fml.client.SplashProgress.pause();
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.blockColors);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.guiAchievement = new GuiAchievement(this);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new ParticleManager(this.world, this.renderEngine);
        net.minecraftforge.fml.client.SplashProgress.resume();
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        net.minecraftforge.fml.client.FMLClientHandler.instance().finishMinecraftLoading();
        this.checkGLError("Post startup");
        this.ingameGUI = new net.minecraftforge.client.GuiIngameForge(this);

        if (this.serverName != null)
        {
            net.minecraftforge.fml.client.FMLClientHandler.instance().connectToServerAtStartup(this.serverName, this.serverPort);
        }
        else
        {
            this.displayGuiScreen(new GuiMainMenu());
        }

        net.minecraftforge.fml.client.SplashProgress.clearVanillaResources(renderEngine, mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);
        this.debugRenderer = new DebugRenderer(this);

        net.minecraftforge.fml.client.FMLClientHandler.instance().onInitializationComplete();
        if (this.gameSettings.fullScreen && !this.fullscreen)
        {
            this.toggleFullscreen();
        }

        try
        {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        }
        catch (OpenGLException var2)
        {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }

        this.renderGlobal.makeEntityOutlineShader();
    }

    private void registerMetadataSerializers()
    {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void createDisplay() throws LWJGLException
    {
        Display.setResizable(true);
        Display.setTitle("Minecraft 1.10.2");

        try
        {
            Display.create((new PixelFormat()).withDepthBits(24));
        }
        catch (LWJGLException lwjglexception)
        {
            LOGGER.error((String)"Couldn\'t set pixel format", (Throwable)lwjglexception);

            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException var3)
            {
                ;
            }

            if (this.fullscreen)
            {
                this.updateDisplayMode();
            }

            Display.create();
        }
    }

    private void setInitialDisplayMode() throws LWJGLException
    {
        if (this.fullscreen)
        {
            Display.setFullscreen(true);
            DisplayMode displaymode = Display.getDisplayMode();
            this.displayWidth = Math.max(1, displaymode.getWidth());
            this.displayHeight = Math.max(1, displaymode.getHeight());
        }
        else
        {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }
    }

    private void setWindowIcon()
    {
        Util.EnumOS util$enumos = Util.getOSType();

        if (util$enumos != Util.EnumOS.OSX)
        {
            InputStream inputstream = null;
            InputStream inputstream1 = null;

            try
            {
                inputstream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                inputstream1 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));

                if (inputstream != null && inputstream1 != null)
                {
                    Display.setIcon(new ByteBuffer[] {this.readImageToBuffer(inputstream), this.readImageToBuffer(inputstream1)});
                }
            }
            catch (IOException ioexception)
            {
                LOGGER.error((String)"Couldn\'t set icon", (Throwable)ioexception);
            }
            finally
            {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(inputstream1);
            }
        }
    }

    private static boolean isJvm64bit()
    {
        String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (String s : astring)
        {
            String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64"))
            {
                return true;
            }
        }

        return false;
    }

    public Framebuffer getFramebuffer()
    {
        return this.framebufferMc;
    }

    public String getVersion()
    {
        return this.launchedVersion;
    }

    public String getVersionType()
    {
        return this.versionType;
    }

    private void startTimerHackThread()
    {
        Thread thread = new Thread("Timer hack thread")
        {
            public void run()
            {
                while (Minecraft.this.running)
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
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport crash)
    {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    /**
     * Wrapper around displayCrashReportInternal
     */
    public void displayCrashReport(CrashReport crashReportIn)
    {
        File file1 = new File(getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());

        int retVal;
        if (crashReportIn.getFile() != null)
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            retVal = -1;
        }
        else if (crashReportIn.saveToFile(file2))
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            retVal = -1;
        }
        else
        {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            retVal = -2;
        }
        net.minecraftforge.fml.common.FMLCommonHandler.instance().handleExit(retVal);
    }

    public boolean isUnicode()
    {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources()
    {
        List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

        if (this.theIntegratedServer != null)
        {
            this.theIntegratedServer.reloadLootTables();
        }

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries())
        {
            list.add(resourcepackrepository$entry.getResourcePack());
        }

        if (this.mcResourcePackRepository.getResourcePackInstance() != null)
        {
            list.add(this.mcResourcePackRepository.getResourcePackInstance());
        }

        try
        {
            this.mcResourceManager.reloadResources(list);
        }
        catch (RuntimeException runtimeexception)
        {
            LOGGER.info((String)"Caught error stitching, removing all assigned resourcepacks", (Throwable)runtimeexception);
            list.clear();
            list.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.<ResourcePackRepository.Entry>emptyList());
            this.mcResourceManager.reloadResources(list);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.incompatibleResourcePacks.clear();
            this.gameSettings.saveOptions();
        }

        this.mcLanguageManager.parseLanguageMetadata(list);

        if (this.renderGlobal != null)
        {
            this.renderGlobal.loadRenderers();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[])null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

        for (int i : aint)
        {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    private void updateDisplayMode() throws LWJGLException
    {
        Set<DisplayMode> set = Sets.<DisplayMode>newHashSet();
        Collections.addAll(set, Display.getAvailableDisplayModes());
        DisplayMode displaymode = Display.getDesktopDisplayMode();

        if (!set.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX)
        {
            label53:

            for (DisplayMode displaymode1 : MAC_DISPLAY_MODES)
            {
                boolean flag = true;

                for (DisplayMode displaymode2 : set)
                {
                    if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() && displaymode2.getHeight() == displaymode1.getHeight())
                    {
                        flag = false;
                        break;
                    }
                }

                if (!flag)
                {
                    Iterator iterator = set.iterator();
                    DisplayMode displaymode3;

                    while (true)
                    {
                        if (!iterator.hasNext())
                        {
                            continue label53;
                        }

                        displaymode3 = (DisplayMode)iterator.next();

                        if (displaymode3.getBitsPerPixel() == 32 && displaymode3.getWidth() == displaymode1.getWidth() / 2 && displaymode3.getHeight() == displaymode1.getHeight() / 2)
                        {
                            break;
                        }
                    }

                    displaymode = displaymode3;
                }
            }
        }

        Display.setDisplayMode(displaymode);
        this.displayWidth = displaymode.getWidth();
        this.displayHeight = displaymode.getHeight();
    }

    public void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException
    {
        ScaledResolution scaledresolution = new ScaledResolution(this);
        int i = scaledresolution.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)scaledresolution.getScaledWidth(), (double)scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        InputStream inputstream = null;

        try
        {
            inputstream = this.mcDefaultResourcePack.getInputStream(LOCATION_MOJANG_PNG);
            this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstream)));
            textureManagerInstance.bindTexture(this.mojangLogo);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Unable to load logo: {}", new Object[] {LOCATION_MOJANG_PNG, ioexception});
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        vertexbuffer.pos(0.0D, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        vertexbuffer.pos((double)this.displayWidth, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        vertexbuffer.pos((double)this.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        vertexbuffer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int j = 256;
        int k = 256;
        this.draw((scaledresolution.getScaledWidth() - 256) / 2, (scaledresolution.getScaledHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        this.updateDisplay();
    }

    /**
     * Draw with the WorldRenderer
     */
    public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha)
    {
        VertexBuffer vertexbuffer = Tessellator.getInstance().getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        vertexbuffer.pos((double)posX, (double)(posY + height), 0.0D).tex((double)((float)texU * 0.00390625F), (double)((float)(texV + height) * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos((double)(posX + width), (double)(posY + height), 0.0D).tex((double)((float)(texU + width) * 0.00390625F), (double)((float)(texV + height) * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos((double)(posX + width), (double)posY, 0.0D).tex((double)((float)(texU + width) * 0.00390625F), (double)((float)texV * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos((double)posX, (double)posY, 0.0D).tex((double)((float)texU * 0.00390625F), (double)((float)texV * 0.00390625F)).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    /**
     * Returns the save loader that is currently being used
     */
    public ISaveFormat getSaveLoader()
    {
        return this.saveLoader;
    }

    /**
     * Sets the argument GuiScreen as the main (topmost visible) screen.
     */
    public void displayGuiScreen(@Nullable GuiScreen guiScreenIn)
    {
        if (guiScreenIn == null && this.world == null)
        {
            guiScreenIn = new GuiMainMenu();
        }
        else if (guiScreenIn == null && this.player.getHealth() <= 0.0F)
        {
            guiScreenIn = new GuiGameOver((ITextComponent)null);
        }

        GuiScreen old = this.currentScreen;
        net.minecraftforge.client.event.GuiOpenEvent event = new net.minecraftforge.client.event.GuiOpenEvent(guiScreenIn);

        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;

        guiScreenIn = event.getGui();
        if (old != null && guiScreenIn != old)
        {
            old.onGuiClosed();
        }

        if (guiScreenIn instanceof GuiMainMenu || guiScreenIn instanceof GuiMultiplayer)
        {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }

        this.currentScreen = (GuiScreen)guiScreenIn;

        if (guiScreenIn != null)
        {
            this.setIngameNotInFocus();
            KeyBinding.unPressAllKeys();

            while (Mouse.next())
            {
                ;
            }

            while (Keyboard.next())
            {
                ;
            }

            ScaledResolution scaledresolution = new ScaledResolution(this);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            ((GuiScreen)guiScreenIn).setWorldAndResolution(this, i, j);
            this.skipRenderWorld = false;
        }
        else
        {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    private void checkGLError(String message)
    {
        int i = GlStateManager.glGetError();

        if (i != 0)
        {
            String s = GLU.gluErrorString(i);
            LOGGER.error("########## GL ERROR ##########");
            LOGGER.error("@ {}", new Object[] {message});
            LOGGER.error("{}: {}", new Object[] {Integer.valueOf(i), s});
        }
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet()
    {
        try
        {
            LOGGER.info("Stopping!");

            try
            {
                this.loadWorld((WorldClient)null);
            }
            catch (Throwable var5)
            {
                ;
            }

            this.mcSoundHandler.unloadSounds();
        }
        finally
        {
            Display.destroy();

            if (!this.hasCrashed)
            {
                System.exit(0);
            }
        }

        System.gc();
    }

    /**
     * Called repeatedly from run()
     */
    private void runGameLoop() throws IOException
    {
        long i = System.nanoTime();
        this.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested())
        {
            this.shutdown();
        }

        if (this.isGamePaused && this.world != null)
        {
            float f = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = f;
        }
        else
        {
            this.timer.updateTimer();
        }

        this.mcProfiler.startSection("scheduledExecutables");

        synchronized (this.scheduledTasks)
        {
            while (!this.scheduledTasks.isEmpty())
            {
                Util.runTask((FutureTask)this.scheduledTasks.poll(), LOGGER);
            }
        }

        this.mcProfiler.endSection();
        long l = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int j = 0; j < this.timer.elapsedTicks; ++j)
        {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long i1 = System.nanoTime() - l;
        this.checkGLError("Pre render");
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.player, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GlStateManager.enableTexture2D();
        this.mcProfiler.endSection();

        if (!this.skipRenderWorld)
        {
            net.minecraftforge.fml.common.FMLCommonHandler.instance().onRenderTickStart(this.timer.renderPartialTicks);
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, i);
            this.mcProfiler.endSection();
            net.minecraftforge.fml.common.FMLCommonHandler.instance().onRenderTickEnd(this.timer.renderPartialTicks);
        }

        this.mcProfiler.endSection();

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI)
        {
            if (!this.mcProfiler.profilingEnabled)
            {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(i1);
        }
        else
        {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.updateAchievementWindow();
        this.framebufferMc.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
        GlStateManager.popMatrix();
        this.mcProfiler.startSection("root");
        this.updateDisplay();
        Thread.yield();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
        long k = System.nanoTime();
        this.frameTimer.addFrame(k - this.startNanoTime);
        this.startNanoTime = k;

        while (getSystemTime() >= this.debugUpdateTime + 1000L)
        {
            debugFPS = this.fpsCounter;
            this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", new Object[] {Integer.valueOf(debugFPS), Integer.valueOf(RenderChunk.renderChunksUpdated), RenderChunk.renderChunksUpdated == 1 ? "" : "s", (float)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : Integer.valueOf(this.gameSettings.limitFramerate), this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.useVbo() ? " vbo" : ""});
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();

            if (!this.usageSnooper.isSnooperRunning())
            {
                this.usageSnooper.startSnooper();
            }
        }

        if (this.isFramerateLimitBelowMax())
        {
            this.mcProfiler.startSection("fpslimit_wait");
            Display.sync(this.getLimitFramerate());
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();
    }

    public void updateDisplay()
    {
        this.mcProfiler.startSection("display_update");
        Display.update();
        this.mcProfiler.endSection();
        this.checkWindowResize();
    }

    protected void checkWindowResize()
    {
        if (!this.fullscreen && Display.wasResized())
        {
            int i = this.displayWidth;
            int j = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != i || this.displayHeight != j)
            {
                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate()
    {
        return this.world == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax()
    {
        return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory()
    {
        try
        {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        }
        catch (Throwable var3)
        {
            ;
        }

        try
        {
            System.gc();
            this.loadWorld((WorldClient)null);
        }
        catch (Throwable var2)
        {
            ;
        }

        System.gc();
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    private void updateDebugProfilerName(int keyCount)
    {
        List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);

        if (list != null && !list.isEmpty())
        {
            Profiler.Result profiler$result = (Profiler.Result)list.remove(0);

            if (keyCount == 0)
            {
                if (!profiler$result.profilerName.isEmpty())
                {
                    int i = this.debugProfilerName.lastIndexOf(46);

                    if (i >= 0)
                    {
                        this.debugProfilerName = this.debugProfilerName.substring(0, i);
                    }
                }
            }
            else
            {
                --keyCount;

                if (keyCount < list.size() && !"unspecified".equals(((Profiler.Result)list.get(keyCount)).profilerName))
                {
                    if (!this.debugProfilerName.isEmpty())
                    {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((Profiler.Result)list.get(keyCount)).profilerName;
                }
            }
        }
    }

    /**
     * Parameter appears to be unused
     */
    private void displayDebugInfo(long elapsedTicksTime)
    {
        if (this.mcProfiler.profilingEnabled)
        {
            List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result profiler$result = (Profiler.Result)list.remove(0);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, (double)this.displayWidth, (double)this.displayHeight, 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GlStateManager.glLineWidth(1.0F);
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            int i = 160;
            int j = this.displayWidth - 160 - 10;
            int k = this.displayHeight - 320;
            GlStateManager.enableBlend();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            vertexbuffer.pos((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            vertexbuffer.pos((double)((float)j - 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
            vertexbuffer.pos((double)((float)j + 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
            vertexbuffer.pos((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            double d0 = 0.0D;

            for (int l = 0; l < list.size(); ++l)
            {
                Profiler.Result profiler$result1 = (Profiler.Result)list.get(l);
                int i1 = MathHelper.floor(profiler$result1.usePercentage / 4.0D) + 1;
                vertexbuffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
                int j1 = profiler$result1.getColor();
                int k1 = j1 >> 16 & 255;
                int l1 = j1 >> 8 & 255;
                int i2 = j1 & 255;
                vertexbuffer.pos((double)j, (double)k, 0.0D).color(k1, l1, i2, 255).endVertex();

                for (int j2 = i1; j2 >= 0; --j2)
                {
                    float f = (float)((d0 + profiler$result1.usePercentage * (double)j2 / (double)i1) * (Math.PI * 2D) / 100.0D);
                    float f1 = MathHelper.sin(f) * 160.0F;
                    float f2 = MathHelper.cos(f) * 160.0F * 0.5F;
                    vertexbuffer.pos((double)((float)j + f1), (double)((float)k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
                }

                tessellator.draw();
                vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

                for (int i3 = i1; i3 >= 0; --i3)
                {
                    float f3 = (float)((d0 + profiler$result1.usePercentage * (double)i3 / (double)i1) * (Math.PI * 2D) / 100.0D);
                    float f4 = MathHelper.sin(f3) * 160.0F;
                    float f5 = MathHelper.cos(f3) * 160.0F * 0.5F;
                    vertexbuffer.pos((double)((float)j + f4), (double)((float)k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
                    vertexbuffer.pos((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
                }

                tessellator.draw();
                d0 += profiler$result1.usePercentage;
            }

            DecimalFormat decimalformat = new DecimalFormat("##0.00");
            GlStateManager.enableTexture2D();
            String s = "";

            if (!"unspecified".equals(profiler$result.profilerName))
            {
                s = s + "[0] ";
            }

            if (profiler$result.profilerName.isEmpty())
            {
                s = s + "ROOT ";
            }
            else
            {
                s = s + profiler$result.profilerName + ' ';
            }

            int l2 = 16777215;
            this.fontRendererObj.drawStringWithShadow(s, (float)(j - 160), (float)(k - 80 - 16), 16777215);
            s = decimalformat.format(profiler$result.totalUsePercentage) + "%";
            this.fontRendererObj.drawStringWithShadow(s, (float)(j + 160 - this.fontRendererObj.getStringWidth(s)), (float)(k - 80 - 16), 16777215);

            for (int k2 = 0; k2 < list.size(); ++k2)
            {
                Profiler.Result profiler$result2 = (Profiler.Result)list.get(k2);
                StringBuilder stringbuilder = new StringBuilder();

                if ("unspecified".equals(profiler$result2.profilerName))
                {
                    stringbuilder.append("[?] ");
                }
                else
                {
                    stringbuilder.append("[").append(k2 + 1).append("] ");
                }

                String s1 = stringbuilder.append(profiler$result2.profilerName).toString();
                this.fontRendererObj.drawStringWithShadow(s1, (float)(j - 160), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
                s1 = decimalformat.format(profiler$result2.usePercentage) + "%";
                this.fontRendererObj.drawStringWithShadow(s1, (float)(j + 160 - 50 - this.fontRendererObj.getStringWidth(s1)), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
                s1 = decimalformat.format(profiler$result2.totalUsePercentage) + "%";
                this.fontRendererObj.drawStringWithShadow(s1, (float)(j + 160 - this.fontRendererObj.getStringWidth(s1)), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
            }
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown()
    {
        this.running = false;
    }

    /**
     * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
     * currently displayed
     */
    public void setIngameFocus()
    {
        if (Display.isActive())
        {
            if (!this.inGameHasFocus)
            {
                if (!IS_RUNNING_ON_MAC)
                {
                    KeyBinding.updateKeyBindState();
                }

                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen)null);
                this.leftClickCounter = 10000;
            }
        }
    }

    /**
     * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
     */
    public void setIngameNotInFocus()
    {
        if (this.inGameHasFocus)
        {
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu()
    {
        if (this.currentScreen == null)
        {
            this.displayGuiScreen(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic())
            {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick)
    {
        if (!leftClick)
        {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0 && !this.player.isHandActive())
        {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();

                if (!this.world.isAirBlock(blockpos) && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit))
                {
                    this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver);
                    this.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void clickMouse()
    {
        if (this.leftClickCounter <= 0)
        {
            if (this.objectMouseOver == null)
            {
                LOGGER.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else if (!this.player.isRowingBoat())
            {
                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        this.playerController.attackEntity(this.player, this.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (!this.world.isAirBlock(blockpos))
                        {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:

                        if (this.playerController.isNotCreative())
                        {
                            this.leftClickCounter = 10;
                        }

                        this.player.resetCooldown();
                        net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(this.player, this.player.getHeldItemMainhand());
                }

                this.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    /**
     * Called when user clicked he's mouse right button (place)
     */
    @SuppressWarnings("incomplete-switch")
    private void rightClickMouse()
    {
        if (!this.playerController.getIsHittingBlock())
        {
            this.rightClickDelayTimer = 4;

            if (!this.player.isRowingBoat())
            {
                for (EnumHand enumhand : EnumHand.values())
                {
                    ItemStack itemstack = this.player.getHeldItem(enumhand);

                    if (this.objectMouseOver == null)
                    {
                        LOGGER.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
                    }
                    else
                    {
                        switch (this.objectMouseOver.typeOfHit)
                        {
                            case ENTITY:
                                if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit, this.objectMouseOver, this.player.getHeldItem(enumhand), enumhand) == EnumActionResult.SUCCESS)
                                {
                                    return;
                                }

                                if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit, this.player.getHeldItem(enumhand), enumhand) == EnumActionResult.SUCCESS)
                                {
                                    return;
                                }

                                break;
                            case BLOCK:
                                BlockPos blockpos = this.objectMouseOver.getBlockPos();

                                if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR)
                                {
                                    int i = itemstack != null ? itemstack.stackSize : 0;
                                    EnumActionResult enumactionresult = this.playerController.processRightClickBlock(this.player, this.world, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec, enumhand);

                                    if (enumactionresult == EnumActionResult.SUCCESS)
                                    {
                                        this.player.swingArm(enumhand);

                                        if (itemstack != null)
                                        {
                                            if (itemstack.stackSize == 0)
                                            {
                                                this.player.setHeldItem(enumhand, (ItemStack)null);
                                            }
                                            else if (itemstack.stackSize != i || this.playerController.isInCreativeMode())
                                            {
                                                this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                                            }
                                        }

                                        return;
                                    }
                                }
                        }
                    }

                    ItemStack itemstack1 = this.player.getHeldItem(enumhand);
                    if (itemstack1 == null && (this.objectMouseOver == null || this.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS)) net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, enumhand);
                    if (itemstack1 != null && this.playerController.processRightClick(this.player, this.world, itemstack1, enumhand) == EnumActionResult.SUCCESS)
                    {
                        this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen()
    {
        try
        {
            this.fullscreen = !this.fullscreen;
            this.gameSettings.fullScreen = this.fullscreen;

            if (this.fullscreen)
            {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }
            else
            {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null)
            {
                this.resize(this.displayWidth, this.displayHeight);
            }
            else
            {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.updateDisplay();
        }
        catch (Exception exception)
        {
            LOGGER.error((String)"Couldn\'t toggle fullscreen", (Throwable)exception);
        }
    }

    /**
     * Called to resize the current screen.
     */
    public void resize(int width, int height)
    {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);

        if (this.currentScreen != null)
        {
            ScaledResolution scaledresolution = new ScaledResolution(this);
            this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize()
    {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null)
        {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    /**
     * Return the musicTicker's instance
     */
    public MusicTicker getMusicTicker()
    {
        return this.mcMusicTicker;
    }

    /**
     * Runs the current tick.
     */
    public void runTick() throws IOException
    {
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPreClientTick();

        this.mcProfiler.startSection("gui");

        if (!this.isGamePaused)
        {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endSection();
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.startSection("gameMode");

        if (!this.isGamePaused && this.world != null)
        {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused)
        {
            this.renderEngine.tick();
        }

        if (this.currentScreen == null && this.player != null)
        {
            if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof GuiGameOver))
            {
                this.displayGuiScreen((GuiScreen)null);
            }
            else if (this.player.isPlayerSleeping() && this.world != null)
            {
                this.displayGuiScreen(new GuiSleepMP());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.player.isPlayerSleeping())
        {
            this.displayGuiScreen((GuiScreen)null);
        }

        if (this.currentScreen != null)
        {
            this.leftClickCounter = 10000;
        }

        if (this.currentScreen != null)
        {
            try
            {
                this.currentScreen.handleInput();
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.setDetail("Screen name", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }

            if (this.currentScreen != null)
            {
                try
                {
                    this.currentScreen.updateScreen();
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                    crashreportcategory1.setDetail("Screen name", new ICrashReportDetail<String>()
                    {
                        public String call() throws Exception
                        {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport1);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput)
        {
            this.mcProfiler.endStartSection("mouse");
            this.runTickMouse();

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }

            this.mcProfiler.endStartSection("keyboard");
            this.runTickKeyboard();
        }

        if (this.world != null)
        {
            if (this.player != null)
            {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30)
                {
                    this.joinPlayerCounter = 0;
                    this.world.joinEntityInSurroundings(this.player);
                }
                net.minecraftforge.fml.common.FMLCommonHandler.instance().fireMouseInput();
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
            {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused)
            {
                this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused)
            {
                if (this.world.getLastLightningBolt() > 0)
                {
                    this.world.setLastLightningBolt(this.world.getLastLightningBolt() - 1);
                }

                this.world.updateEntities();
            }
        }
        else if (this.entityRenderer.isShaderActive())
        {
            this.entityRenderer.stopUseShader();
        }

        if (!this.isGamePaused)
        {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.world != null)
        {
            if (!this.isGamePaused)
            {
                this.world.setAllowedSpawnTypes(this.world.getDifficulty() != EnumDifficulty.PEACEFUL, true);

                try
                {
                    this.world.tick();
                }
                catch (Throwable throwable2)
                {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                    if (this.world == null)
                    {
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                        crashreportcategory2.addCrashSection("Problem", "Level is null!");
                    }
                    else
                    {
                        this.world.addWorldInfoToCrashReport(crashreport2);
                    }

                    throw new ReportedException(crashreport2);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.world != null)
            {
                this.world.doVoidFogParticles(MathHelper.floor(this.player.posX), MathHelper.floor(this.player.posY), MathHelper.floor(this.player.posZ));
            }

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused)
            {
                this.effectRenderer.updateEffects();
            }
        }
        else if (this.myNetworkManager != null)
        {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPostClientTick();
        this.mcProfiler.endSection();
        this.systemTime = getSystemTime();
    }

    private void runTickKeyboard() throws IOException
    {
        while (Keyboard.next())
        {
            int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            if (this.debugCrashKeyPressTime > 0L)
            {
                if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L)
                {
                    throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                }

                if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
                {
                    this.debugCrashKeyPressTime = -1L;
                }
            }
            else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61))
            {
                this.actionKeyF3 = true;
                this.debugCrashKeyPressTime = getSystemTime();
            }

            this.dispatchKeypresses();

            if (this.currentScreen != null)
            {
                this.currentScreen.handleKeyboardInput();
            }

            boolean flag = Keyboard.getEventKeyState();

            if (flag)
            {
                if (i == 62 && this.entityRenderer != null)
                {
                    this.entityRenderer.switchUseShader();
                }

                boolean flag1 = false;

                if (this.currentScreen == null)
                {
                    if (i == 1)
                    {
                        this.displayInGameMenu();
                    }

                    flag1 = Keyboard.isKeyDown(61) && this.processKeyF3(i);
                    this.actionKeyF3 |= flag1;

                    if (i == 59)
                    {
                        this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                    }
                }

                if (flag1)
                {
                    KeyBinding.setKeyBindState(i, false);
                }
                else
                {
                    KeyBinding.setKeyBindState(i, true);
                    KeyBinding.onTick(i);
                }

                if (this.gameSettings.showDebugProfilerChart)
                {
                    if (i == 11)
                    {
                        this.updateDebugProfilerName(0);
                    }

                    for (int j = 0; j < 9; ++j)
                    {
                        if (i == 2 + j)
                        {
                            this.updateDebugProfilerName(j + 1);
                        }
                    }
                }
            }
            else
            {
                KeyBinding.setKeyBindState(i, false);

                if (i == 61)
                {
                    if (this.actionKeyF3)
                    {
                        this.actionKeyF3 = false;
                    }
                    else
                    {
                        this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                        this.gameSettings.showDebugProfilerChart = this.gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown();
                        this.gameSettings.showLagometer = this.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown();
                    }
                }
            }
            net.minecraftforge.fml.common.FMLCommonHandler.instance().fireKeyInput();
        }

        this.processKeyBinds();
    }

    private boolean processKeyF3(int p_184122_1_)
    {
        if (p_184122_1_ == 30)
        {
            this.renderGlobal.loadRenderers();
            this.debugChatMessage("Reloading all chunks", new Object[0]);
            return true;
        }
        else if (p_184122_1_ == 48)
        {
            boolean flag1 = !this.renderManager.isDebugBoundingBox();
            this.renderManager.setDebugBoundingBox(flag1);
            this.debugChatMessage("Hitboxes: {0}", new Object[] {flag1 ? "shown" : "hidden"});
            return true;
        }
        else if (p_184122_1_ == 32)
        {
            if (this.ingameGUI != null)
            {
                this.ingameGUI.getChatGUI().clearChatMessages();
            }

            return true;
        }
        else if (p_184122_1_ == 33)
        {
            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
            this.debugChatMessage("RenderDistance: {0}", new Object[] {Integer.valueOf(this.gameSettings.renderDistanceChunks)});
            return true;
        }
        else if (p_184122_1_ == 34)
        {
            boolean flag = this.debugRenderer.toggleDebugScreen();
            this.debugChatMessage("Chunk borders: {0}", new Object[] {flag ? "shown" : "hidden"});
            return true;
        }
        else if (p_184122_1_ == 35)
        {
            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
            this.debugChatMessage("Advanced tooltips: {0}", new Object[] {this.gameSettings.advancedItemTooltips ? "shown" : "hidden"});
            this.gameSettings.saveOptions();
            return true;
        }
        else if (p_184122_1_ == 49)
        {
            if (!this.player.canUseCommand(2, ""))
            {
                this.debugChatMessage("Unable to switch gamemode, no permission", new Object[0]);
            }
            else if (this.player.isCreative())
            {
                this.player.sendChatMessage("/gamemode spectator");
            }
            else if (this.player.isSpectator())
            {
                this.player.sendChatMessage("/gamemode creative");
            }

            return true;
        }
        else if (p_184122_1_ == 25)
        {
            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
            this.gameSettings.saveOptions();
            this.debugChatMessage("PauseOnLostFocus: {0}", new Object[] {this.gameSettings.pauseOnLostFocus ? "enabled" : "disabled"});
            return true;
        }
        else if (p_184122_1_ == 16)
        {
            this.debugChatMessage("Keybindings:", new Object[0]);
            GuiNewChat guinewchat = this.ingameGUI.getChatGUI();
            guinewchat.printChatMessage(new TextComponentString("F3 + A = Reload chunks"));
            guinewchat.printChatMessage(new TextComponentString("F3 + B = Show hitboxes"));
            guinewchat.printChatMessage(new TextComponentString("F3 + D = Clear chat"));
            guinewchat.printChatMessage(new TextComponentString("F3 + F = Cycle renderdistance (Shift to inverse)"));
            guinewchat.printChatMessage(new TextComponentString("F3 + G = Show chunk boundaries"));
            guinewchat.printChatMessage(new TextComponentString("F3 + H = Advanced tooltips"));
            guinewchat.printChatMessage(new TextComponentString("F3 + N = Cycle creative <-> spectator"));
            guinewchat.printChatMessage(new TextComponentString("F3 + P = Pause on lost focus"));
            guinewchat.printChatMessage(new TextComponentString("F3 + Q = Show this list"));
            guinewchat.printChatMessage(new TextComponentString("F3 + T = Reload resourcepacks"));
            return true;
        }
        else if (p_184122_1_ == 20)
        {
            this.refreshResources();
            this.debugChatMessage("Reloaded resourcepacks", new Object[0]);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void processKeyBinds()
    {
        for (; this.gameSettings.keyBindTogglePerspective.isPressed(); this.renderGlobal.setDisplayListEntitiesDirty())
        {
            ++this.gameSettings.thirdPersonView;

            if (this.gameSettings.thirdPersonView > 2)
            {
                this.gameSettings.thirdPersonView = 0;
            }

            if (this.gameSettings.thirdPersonView == 0)
            {
                this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
            }
            else if (this.gameSettings.thirdPersonView == 1)
            {
                this.entityRenderer.loadEntityShader((Entity)null);
            }
        }

        while (this.gameSettings.keyBindSmoothCamera.isPressed())
        {
            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
        }

        for (int i = 0; i < 9; ++i)
        {
            if (this.gameSettings.keyBindsHotbar[i].isPressed())
            {
                if (this.player.isSpectator())
                {
                    this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
                }
                else
                {
                    this.player.inventory.currentItem = i;
                }
            }
        }

        while (this.gameSettings.keyBindInventory.isPressed())
        {
            this.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.OPEN_INVENTORY_ACHIEVEMENT));

            if (this.playerController.isRidingHorse())
            {
                this.player.sendHorseInventory();
            }
            else
            {
                this.displayGuiScreen(new GuiInventory(this.player));
            }
        }

        while (this.gameSettings.keyBindSwapHands.isPressed())
        {
            if (!this.player.isSpectator())
            {
                this.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }

        while (this.gameSettings.keyBindDrop.isPressed())
        {
            if (!this.player.isSpectator())
            {
                this.player.dropItem(GuiScreen.isCtrlKeyDown());
            }
        }

        boolean flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

        if (flag)
        {
            while (this.gameSettings.keyBindChat.isPressed())
            {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed())
            {
                this.displayGuiScreen(new GuiChat("/"));
            }
        }

        if (this.player.isHandActive())
        {
            if (!this.gameSettings.keyBindUseItem.isKeyDown())
            {
                this.playerController.onStoppedUsingItem(this.player);
            }

            label472:

            while (true)
            {
                if (!this.gameSettings.keyBindAttack.isPressed())
                {
                    while (this.gameSettings.keyBindUseItem.isPressed())
                    {
                        ;
                    }

                    while (true)
                    {
                        if (this.gameSettings.keyBindPickBlock.isPressed())
                        {
                            continue;
                        }

                        break label472;
                    }
                }
            }
        }
        else
        {
            while (this.gameSettings.keyBindAttack.isPressed())
            {
                this.clickMouse();
            }

            while (this.gameSettings.keyBindUseItem.isPressed())
            {
                this.rightClickMouse();
            }

            while (this.gameSettings.keyBindPickBlock.isPressed())
            {
                this.middleClickMouse();
            }
        }

        if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive())
        {
            this.rightClickMouse();
        }

        this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
    }

    private void runTickMouse() throws IOException
    {
        while (Mouse.next())
        {
            if (net.minecraftforge.client.ForgeHooksClient.postMouseEvent()) continue;

            int i = Mouse.getEventButton();
            KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

            if (Mouse.getEventButtonState())
            {
                if (this.player.isSpectator() && i == 2)
                {
                    this.ingameGUI.getSpectatorGui().onMiddleClick();
                }
                else
                {
                    KeyBinding.onTick(i - 100);
                }
            }

            long j = getSystemTime() - this.systemTime;

            if (j <= 200L)
            {
                int k = Mouse.getEventDWheel();

                if (k != 0)
                {
                    if (this.player.isSpectator())
                    {
                        k = k < 0 ? -1 : 1;

                        if (this.ingameGUI.getSpectatorGui().isMenuActive())
                        {
                            this.ingameGUI.getSpectatorGui().onMouseScroll(-k);
                        }
                        else
                        {
                            float f = MathHelper.clamp(this.player.capabilities.getFlySpeed() + (float)k * 0.005F, 0.0F, 0.2F);
                            this.player.capabilities.setFlySpeed(f);
                        }
                    }
                    else
                    {
                        this.player.inventory.changeCurrentItem(k);
                    }
                }

                if (this.currentScreen == null)
                {
                    if (!this.inGameHasFocus && Mouse.getEventButtonState())
                    {
                        this.setIngameFocus();
                    }
                }
                else if (this.currentScreen != null)
                {
                    this.currentScreen.handleMouseInput();
                }
            }
        }
    }

    private void debugChatMessage(String p_184120_1_, Object... p_184120_2_)
    {
        this.ingameGUI.getChatGUI().printChatMessage((new TextComponentString("")).appendSibling((new TextComponentString("[Debug]: ")).setStyle((new Style()).setColor(TextFormatting.YELLOW).setBold(Boolean.valueOf(true)))).appendText(MessageFormat.format(p_184120_1_, p_184120_2_)));
    }

    /**
     * Arguments: World foldername,  World ingame name, WorldSettings
     */
    public void launchIntegratedServer(String folderName, String worldName, @Nullable WorldSettings worldSettingsIn)
    {
        net.minecraftforge.fml.client.FMLClientHandler.instance().startIntegratedServer(folderName, worldName, worldSettingsIn);
        this.loadWorld((WorldClient)null);
        System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();

        if (worldinfo == null && worldSettingsIn != null)
        {
            worldinfo = new WorldInfo(worldSettingsIn, folderName);
            isavehandler.saveWorldInfo(worldinfo);
        }

        if (worldSettingsIn == null)
        {
            worldSettingsIn = new WorldSettings(worldinfo);
        }

        try
        {
            YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.mcDataDir, MinecraftServer.USER_CACHE_FILE.getName()));
            TileEntitySkull.setProfileCache(playerprofilecache);
            TileEntitySkull.setSessionService(minecraftsessionservice);
            PlayerProfileCache.setOnlineMode(false);
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn, yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", folderName);
            crashreportcategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashreport);
        }

        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel", new Object[0]));

        while (!this.theIntegratedServer.serverIsInRunLoop())
        {
            if (!net.minecraftforge.fml.common.StartupQuery.check())
            {
                loadWorld(null);
                displayGuiScreen(null);
                return;
            }
            String s = this.theIntegratedServer.getUserMessage();

            if (s != null)
            {
                this.loadingScreen.displayLoadingString(I18n.format(s, new Object[0]));
            }
            else
            {
                this.loadingScreen.displayLoadingString("");
            }

            try
            {
                Thread.sleep(200L);
            }
            catch (InterruptedException var10)
            {
                ;
            }
        }

        this.displayGuiScreen(new GuiScreenWorking());
        SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, (GuiScreen)null));
        networkmanager.sendPacket(new C00Handshake(210, socketaddress.toString(), 0, EnumConnectionState.LOGIN, true));
        com.mojang.authlib.GameProfile gameProfile = this.getSession().getProfile();
        if (!this.getSession().hasCachedProperties())
        {
            gameProfile = sessionService.fillProfileProperties(gameProfile, true); //Forge: Fill profile properties upon game load. Fixes MC-52974.
            this.getSession().setProperties(gameProfile.getProperties());
        }
        networkmanager.sendPacket(new CPacketLoginStart(gameProfile));
        this.myNetworkManager = networkmanager;
    }

    /**
     * unloads the current world first
     */
    public void loadWorld(WorldClient worldClientIn)
    {
        this.loadWorld(worldClientIn, "");
    }

    /**
     * par2Str is displayed on the loading screen to the user unloads the current world first
     */
    public void loadWorld(@Nullable WorldClient worldClientIn, String loadingMessage)
    {
        if (world != null) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(world));

        if (worldClientIn == null)
        {
            NetHandlerPlayClient nethandlerplayclient = this.getConnection();

            if (nethandlerplayclient != null)
            {
                nethandlerplayclient.cleanup();
            }

            if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet())
            {
                this.theIntegratedServer.initiateShutdown();
                if (loadingScreen != null)
                {
                    this.loadingScreen.displayLoadingString(I18n.format("forge.client.shutdown.internal"));
                }
                while (!theIntegratedServer.isServerStopped())
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException ie) {}
                }
            }

            this.theIntegratedServer = null;
            this.guiAchievement.clearAchievements();
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
            this.playerController = null;
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null)
        {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }

        if (worldClientIn == null && this.world != null)
        {
            this.mcResourcePackRepository.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.setServerData((ServerData)null);
            this.integratedServerIsRunning = false;
            net.minecraftforge.fml.client.FMLClientHandler.instance().handleClientWorldClosing(this.world);
        }

        this.mcSoundHandler.stopSounds();
        this.world = worldClientIn;

        if (this.renderGlobal != null)
        {
            this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
        }

        if (this.effectRenderer != null)
        {
            this.effectRenderer.clearEffects(worldClientIn);
        }

        TileEntityRendererDispatcher.instance.setWorld(worldClientIn);

        if (worldClientIn != null)
        {
            if (!this.integratedServerIsRunning)
            {
                AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
                MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
                GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
                PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.mcDataDir, MinecraftServer.USER_CACHE_FILE.getName()));
                TileEntitySkull.setProfileCache(playerprofilecache);
                TileEntitySkull.setSessionService(minecraftsessionservice);
                PlayerProfileCache.setOnlineMode(false);
            }

            if (this.player == null)
            {
                this.player = this.playerController.createClientPlayer(worldClientIn, new StatisticsManager());
                this.playerController.flipPlayer(this.player);
            }

            this.player.preparePlayerToSpawn();
            worldClientIn.spawnEntity(this.player);
            this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.player);
            this.renderViewEntity = this.player;
        }
        else
        {
            this.saveLoader.flushCache();
            this.player = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    public void setDimensionAndSpawnPlayer(int dimension)
    {
        this.world.setInitialSpawnLocation();
        this.world.removeAllEntities();
        int i = 0;
        String s = null;

        if (this.player != null)
        {
            i = this.player.getEntityId();
            this.world.removeEntity(this.player);
            s = this.player.getServerBrand();
        }

        this.renderViewEntity = null;
        EntityPlayerSP entityplayersp = this.player;
        this.player = this.playerController.createClientPlayer(this.world, this.player == null ? new StatisticsManager() : this.player.getStatFileWriter());
        this.player.getDataManager().setEntryValues(entityplayersp.getDataManager().getAll());
        this.player.dimension = dimension;
        this.renderViewEntity = this.player;
        this.player.preparePlayerToSpawn();
        this.player.setServerBrand(s);
        this.world.spawnEntity(this.player);
        this.playerController.flipPlayer(this.player);
        this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.player.setEntityId(i);
        this.playerController.setPlayerCapabilities(this.player);
        this.player.setReducedDebug(entityplayersp.hasReducedDebug());

        if (this.currentScreen instanceof GuiGameOver)
        {
            this.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Gets whether this is a demo or not.
     */
    public final boolean isDemo()
    {
        return this.isDemo;
    }

    @Nullable
    public NetHandlerPlayClient getConnection()
    {
        return this.player == null ? null : this.player.connection;
    }

    public static boolean isGuiEnabled()
    {
        /** Set to 'this' in Minecraft constructor; used by some settings get methods */
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public static boolean isAmbientOcclusionEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    /**
     * Called when user clicked he's mouse middle button (pick block)
     */
    private void middleClickMouse()
    {
        if (this.objectMouseOver != null && this.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS)
        {
            net.minecraftforge.common.ForgeHooks.onPickBlock(this.objectMouseOver, this.player, this.world);
            // We delete this code wholly instead of commenting it out, to make sure we detect changes in it between MC versions
        }
    }

    public ItemStack storeTEInStack(ItemStack stack, TileEntity te)
    {
        NBTTagCompound nbttagcompound = te.writeToNBT(new NBTTagCompound());

        if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner"))
        {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            stack.setTagCompound(nbttagcompound3);
            return stack;
        }
        else
        {
            stack.setTagInfo("BlockEntityTag", nbttagcompound);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.appendTag(new NBTTagString("(+NBT)"));
            nbttagcompound1.setTag("Lore", nbttaglist);
            stack.setTagInfo("display", nbttagcompound1);
            return stack;
        }
    }

    /**
     * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
     */
    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash)
    {
        theCrash.getCategory().setDetail("Launched Version", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().setDetail("LWJGL", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().setDetail("OpenGL", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return GlStateManager.glGetString(7937) + " GL version " + GlStateManager.glGetString(7938) + ", " + GlStateManager.glGetString(7936);
            }
        });
        theCrash.getCategory().setDetail("GL Caps", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return OpenGlHelper.getLogText();
            }
        });
        theCrash.getCategory().setDetail("Using VBOs", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
        });
        theCrash.getCategory().setDetail("Is Modded", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                String s = ClientBrandRetriever.getClientModName();
                return !"vanilla".equals(s) ? "Definitely; Client brand changed to \'" + s + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
            }
        });
        theCrash.getCategory().setDetail("Type", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().setDetail("Resource Packs", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : Minecraft.this.gameSettings.resourcePacks)
                {
                    if (stringbuilder.length() > 0)
                    {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s);

                    if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(s))
                    {
                        stringbuilder.append(" (incompatible)");
                    }
                }

                return stringbuilder.toString();
            }
        });
        theCrash.getCategory().setDetail("Current Language", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().setDetail("Profiler Position", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().setDetail("CPU", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return OpenGlHelper.getCpu();
            }
        });

        if (this.world != null)
        {
            this.world.addWorldInfoToCrashReport(theCrash);
        }

        return theCrash;
    }

    /**
     * Return the singleton Minecraft instance for the game
     */
    public static Minecraft getMinecraft()
    {
        /** Set to 'this' in Minecraft constructor; used by some settings get methods */
        return theMinecraft;
    }

    public ListenableFuture<Object> scheduleResourcesRefresh()
    {
        return this.addScheduledTask(new Runnable()
        {
            public void run()
            {
                Minecraft.this.refreshResources();
            }
        });
    }

    public void addServerStatsToSnooper(Snooper playerSnooper)
    {
        playerSnooper.addClientStat("fps", Integer.valueOf(debugFPS));
        playerSnooper.addClientStat("vsync_enabled", Boolean.valueOf(this.gameSettings.enableVsync));
        playerSnooper.addClientStat("display_frequency", Integer.valueOf(Display.getDisplayMode().getFrequency()));
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", Long.valueOf((MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L));
        playerSnooper.addClientStat("current_action", this.getCurrentAction());
        playerSnooper.addClientStat("language", this.gameSettings.language == null ? "en_US" : this.gameSettings.language);
        String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", s);
        playerSnooper.addClientStat("subtitles", Boolean.valueOf(this.gameSettings.showSubtitles));
        playerSnooper.addClientStat("resource_packs", Integer.valueOf(this.mcResourcePackRepository.getRepositoryEntries().size()));
        int i = 0;

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries())
        {
            playerSnooper.addClientStat("resource_pack[" + i++ + "]", resourcepackrepository$entry.getResourcePackName());
        }

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null)
        {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    /**
     * Return the current action's name
     */
    private String getCurrentAction()
    {
        return this.theIntegratedServer != null ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer") : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
    }

    public void addServerTypeToSnooper(Snooper playerSnooper)
    {
        playerSnooper.addStatToSnooper("opengl_version", GlStateManager.glGetString(7938));
        playerSnooper.addStatToSnooper("opengl_vendor", GlStateManager.glGetString(7936));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities contextcapabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_arrays_of_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", Boolean.valueOf(contextcapabilities.GL_ARB_base_instance));
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", Boolean.valueOf(contextcapabilities.GL_ARB_blend_func_extended));
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_clear_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_color_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", Boolean.valueOf(contextcapabilities.GL_ARB_compatibility));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", Boolean.valueOf(contextcapabilities.GL_ARB_compressed_texture_pixel_storage));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_texture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_elements_base_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_indirect));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_attrib_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_uniform_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_layer_viewport));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_ARB_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", Boolean.valueOf(contextcapabilities.GL_ARB_gpu_shader5));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_pixel));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_instanced_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_alignment));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_range));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", Boolean.valueOf(contextcapabilities.GL_ARB_multisample));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", Boolean.valueOf(contextcapabilities.GL_ARB_multitexture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", Boolean.valueOf(contextcapabilities.GL_ARB_occlusion_query2));
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_pixel_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_seamless_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_stencil_export));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_texture_lod));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow_ambient));
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", Boolean.valueOf(contextcapabilities.GL_ARB_stencil_texturing));
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", Boolean.valueOf(contextcapabilities.GL_ARB_sync));
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_tessellation_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_border_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map_array));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_non_power_of_two));
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_uniform_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", Boolean.valueOf(contextcapabilities.GL_EXT_bindable_uniform));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_equation_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_func_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_minmax));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_subtract));
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_EXT_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_multisample));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_program_parameters));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", Boolean.valueOf(contextcapabilities.GL_EXT_multi_draw_arrays));
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", Boolean.valueOf(contextcapabilities.GL_EXT_packed_depth_stencil));
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", Boolean.valueOf(contextcapabilities.GL_EXT_paletted_texture));
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", Boolean.valueOf(contextcapabilities.GL_EXT_rescale_normal));
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", Boolean.valueOf(contextcapabilities.GL_EXT_separate_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", Boolean.valueOf(contextcapabilities.GL_EXT_shader_image_load_store));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", Boolean.valueOf(contextcapabilities.GL_EXT_shadow_funcs));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", Boolean.valueOf(contextcapabilities.GL_EXT_shared_texture_palette));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_clear_tag));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_two_side));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_wrap));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_3d));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_array));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_integer));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_lod_bias));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_weighting));
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", Integer.valueOf(GlStateManager.glGetInteger(35658)));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", Integer.valueOf(GlStateManager.glGetInteger(35657)));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", Integer.valueOf(GlStateManager.glGetInteger(34921)));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", Integer.valueOf(GlStateManager.glGetInteger(35660)));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GlStateManager.glGetInteger(34930)));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_array_texture_layers]", Integer.valueOf(GlStateManager.glGetInteger(35071)));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", Integer.valueOf(getGLMaximumTextureSize()));
        GameProfile gameprofile = this.session.getProfile();

        if (gameprofile != null && gameprofile.getId() != null)
        {
            playerSnooper.addStatToSnooper("uuid", Hashing.sha1().hashBytes(gameprofile.getId().toString().getBytes(Charsets.ISO_8859_1)).toString());
        }
    }

    /**
     * Used in the usage snooper.
     */
    public static int getGLMaximumTextureSize()
    {
        //Forge we redirect this to our code which caches the value before any splash screen stuff is done.
        return net.minecraftforge.fml.client.SplashProgress.getMaxTextureSize();
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return this.gameSettings.snooperEnabled;
    }

    /**
     * Set the current ServerData instance.
     */
    public void setServerData(ServerData serverDataIn)
    {
        this.currentServerData = serverDataIn;
    }

    @Nullable
    public ServerData getCurrentServerData()
    {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning()
    {
        return this.integratedServerIsRunning;
    }

    /**
     * Returns true if there is only one player playing, and the current server is the integrated one.
     */
    public boolean isSingleplayer()
    {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    /**
     * Returns the currently running integrated server
     */
    @Nullable
    public IntegratedServer getIntegratedServer()
    {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer()
    {
        if (theMinecraft != null)
        {
            IntegratedServer integratedserver = theMinecraft.getIntegratedServer();

            if (integratedserver != null)
            {
                integratedserver.stopServer();
            }
        }
    }

    /**
     * Returns the PlayerUsageSnooper instance.
     */
    public Snooper getPlayerUsageSnooper()
    {
        return this.usageSnooper;
    }

    /**
     * Gets the system time in milliseconds.
     */
    public static long getSystemTime()
    {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    /**
     * Returns whether we're in full screen or not.
     */
    public boolean isFullScreen()
    {
        return this.fullscreen;
    }

    public Session getSession()
    {
        return this.session;
    }

    /**
     * Return the player's GameProfile properties
     */
    public PropertyMap getProfileProperties()
    {
        if (this.profileProperties.isEmpty())
        {
            GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.profileProperties.putAll(gameprofile.getProperties());
        }

        return this.profileProperties;
    }

    public Proxy getProxy()
    {
        return this.proxy;
    }

    public TextureManager getTextureManager()
    {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager()
    {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository()
    {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager()
    {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks()
    {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit()
    {
        return this.jvm64bit;
    }

    public boolean isGamePaused()
    {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler()
    {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType getAmbientMusicType()
    {
        return this.player != null ? (this.player.world.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER : (this.player.world.provider instanceof WorldProviderEnd ? (this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END) : (this.player.capabilities.isCreativeMode && this.player.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME))) : MusicTicker.MusicType.MENU;
    }

    public void dispatchKeypresses()
    {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

        if (i != 0 && !Keyboard.isRepeatEvent())
        {
            if (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)
            {
                if (Keyboard.getEventKeyState())
                {
                    if (this.gameSettings.keyBindFullscreen.isActiveAndMatches(i))
                    {
                        this.toggleFullscreen();
                    }
                    else if (this.gameSettings.keyBindScreenshot.isActiveAndMatches(i))
                    {
                        this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
                    }
                }
                else if (this.currentScreen instanceof GuiControls) ((GuiControls)this.currentScreen).buttonId = null;
            }
        }
    }

    public MinecraftSessionService getSessionService()
    {
        return this.sessionService;
    }

    public SkinManager getSkinManager()
    {
        return this.skinManager;
    }

    @Nullable
    public Entity getRenderViewEntity()
    {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity)
    {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule)
    {
        Validate.notNull(callableToSchedule);

        if (this.isCallingFromMinecraftThread())
        {
            try
            {
                return Futures.<V>immediateFuture(callableToSchedule.call());
            }
            catch (Exception exception)
            {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
        else
        {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.<V>create(callableToSchedule);

            synchronized (this.scheduledTasks)
            {
                this.scheduledTasks.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule)
    {
        Validate.notNull(runnableToSchedule);
        return this.<Object>addScheduledTask(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread()
    {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher()
    {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager()
    {
        return this.renderManager;
    }

    public RenderItem getRenderItem()
    {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer()
    {
        return this.itemRenderer;
    }

    public static int getDebugFPS()
    {
        /**
         * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part
         * of the usage snooping.
         */
        return debugFPS;
    }

    /**
     * Return the FrameTimer's instance
     */
    public FrameTimer getFrameTimer()
    {
        return this.frameTimer;
    }

    /**
     * Return true if the player is connected to a realms server
     */
    public boolean isConnectedToRealms()
    {
        return this.connectedToRealms;
    }

    /**
     * Set if the player is connected to a realms server
     */
    public void setConnectedToRealms(boolean isConnected)
    {
        this.connectedToRealms = isConnected;
    }

    public DataFixer getDataFixer()
    {
        return this.dataFixer;
    }

    public float getRenderPartialTicks()
    {
        return this.timer.renderPartialTicks;
    }

    public BlockColors getBlockColors()
    {
        return this.blockColors;
    }

    // FORGE
    public ItemColors getItemColors()
    {
        return this.itemColors;
    }

    /**
     * Whether to use reduced debug info
     */
    public boolean isReducedDebug()
    {
        return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
    }
}