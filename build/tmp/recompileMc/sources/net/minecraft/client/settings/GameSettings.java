package net.minecraft.client.settings;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

@SideOnly(Side.CLIENT)
public class GameSettings
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Type TYPE_LIST_STRING = new ParameterizedType()
    {
        public Type[] getActualTypeArguments()
        {
            return new Type[] {String.class};
        }
        public Type getRawType()
        {
            return List.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };
    public static final Splitter COLON_SPLITTER = Splitter.on(':');
    /** GUI scale values */
    private static final String[] GUISCALES = new String[] {"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[] {"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[] {"options.ao.off", "options.ao.min", "options.ao.max"};
    private static final String[] CLOUDS_TYPES = new String[] {"options.off", "options.graphics.fast", "options.graphics.fancy"};
    private static final String[] ATTACK_INDICATORS = new String[] {"options.off", "options.attack.crosshair", "options.attack.hotbar"};
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    /** Clouds flag */
    public int clouds = 2;
    public boolean fancyGraphics = true;
    /** Smooth Lighting */
    public int ambientOcclusion = 2;
    public List<String> resourcePacks = Lists.<String>newArrayList();
    public List<String> incompatibleResourcePacks = Lists.<String>newArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0F;
    public boolean snooperEnabled = true;
    public boolean fullScreen;
    public boolean enableVsync = true;
    public boolean useVbo = true;
    public boolean reducedDebugInfo;
    public boolean hideServerAddress;
    /** Whether to show advanced information on item tooltips, toggled by F3+H */
    public boolean advancedItemTooltips;
    /** Whether to pause when the game loses focus, toggled by F3+P */
    public boolean pauseOnLostFocus = true;
    private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
    public boolean touchscreen;
    public EnumHandSide mainHand = EnumHandSide.RIGHT;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public float chatScale = 1.0F;
    public float chatWidth = 1.0F;
    public float chatHeightUnfocused = 0.44366196F;
    public float chatHeightFocused = 1.0F;
    public boolean showInventoryAchievementHint = true;
    public int mipmapLevels = 4;
    private final Map<SoundCategory, Float> soundLevels = Maps.newEnumMap(SoundCategory.class);
    public boolean useNativeTransport = true;
    public boolean entityShadows = true;
    public int attackIndicator = 1;
    public boolean enableWeakAttacks;
    public boolean showSubtitles;
    public boolean realmsNotifications = true;
    public boolean autoJump = true;
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
    public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
    public KeyBinding keyBindSwapHands = new KeyBinding("key.swapHands", 33, "key.categories.inventory");
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.inventory");
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
    public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
    public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
    public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
    public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
    public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
    public KeyBinding[] keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    /** true if debug info should be displayed instead of version */
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean showLagometer;
    /** The lastServer string. */
    public String lastServer;
    /** Smooth Camera Toggle */
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;
    /** GUI scale */
    public int guiScale;
    /** Determines amount of particles. 0 = All, 1 = Decreased, 2 = Minimal */
    public int particleSetting;
    /** Game settings language */
    public String language;
    public boolean forceUnicodeFont;

    public GameSettings(Minecraft mcIn, File optionsFileIn)
    {
        setForgeKeybindProperties();
        this.keyBindings = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
        this.mc = mcIn;
        this.optionsFile = new File(optionsFileIn, "options.txt");

        if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L)
        {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
        }
        else
        {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
        }

        this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
        this.loadOptions();
    }

    public GameSettings()
    {
        setForgeKeybindProperties();
        this.keyBindings = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
    }

    /**
     * Gets the display name for a key.
     */
    public static String getKeyDisplayString(int key)
    {
        return key < 0 ? I18n.format("key.mouseButton", new Object[] {Integer.valueOf(key + 101)}): (key < 256 ? Keyboard.getKeyName(key) : String.format("%c", new Object[] {Character.valueOf((char)(key - 256))}).toUpperCase());
    }

    /**
     * Returns whether the specified key binding is currently being pressed.
     */
    public static boolean isKeyDown(KeyBinding key)
    {
        int i = key.getKeyCode();
        return i != 0 && i < 256 ? (i < 0 ? Mouse.isButtonDown(i + 100) : Keyboard.isKeyDown(i)) : false;
    }

    /**
     * Sets a key binding and then saves all settings.
     */
    public void setOptionKeyBinding(KeyBinding key, int keyCode)
    {
        key.setKeyCode(keyCode);
        this.saveOptions();
    }

    /**
     * If the specified option is controlled by a slider (float value), this will set the float value.
     */
    public void setOptionFloatValue(GameSettings.Options settingsOption, float value)
    {
        if (settingsOption == GameSettings.Options.SENSITIVITY)
        {
            this.mouseSensitivity = value;
        }

        if (settingsOption == GameSettings.Options.FOV)
        {
            this.fovSetting = value;
        }

        if (settingsOption == GameSettings.Options.GAMMA)
        {
            this.gammaSetting = value;
        }

        if (settingsOption == GameSettings.Options.FRAMERATE_LIMIT)
        {
            this.limitFramerate = (int)value;
        }

        if (settingsOption == GameSettings.Options.CHAT_OPACITY)
        {
            this.chatOpacity = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED)
        {
            this.chatHeightFocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED)
        {
            this.chatHeightUnfocused = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_WIDTH)
        {
            this.chatWidth = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_SCALE)
        {
            this.chatScale = value;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.MIPMAP_LEVELS)
        {
            int i = this.mipmapLevels;
            this.mipmapLevels = (int)value;

            if ((float)i != value)
            {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
                this.needsResourceRefresh = true; // FORGE: fix for MC-64581 very laggy mipmap slider
            }
        }

        if (settingsOption == GameSettings.Options.RENDER_DISTANCE)
        {
            this.renderDistanceChunks = (int)value;
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
    }

    /**
     * For non-float options. Toggles the option on/off, or cycles through the list i.e. render distances.
     */
    public void setOptionValue(GameSettings.Options settingsOption, int value)
    {
        if (settingsOption == GameSettings.Options.RENDER_DISTANCE)
        {
            this.setOptionFloatValue(settingsOption, MathHelper.clamp((float)(this.renderDistanceChunks + value), settingsOption.getValueMin(), settingsOption.getValueMax()));
        }

        if (settingsOption == GameSettings.Options.MAIN_HAND)
        {
            this.mainHand = this.mainHand.opposite();
        }

        if (settingsOption == GameSettings.Options.INVERT_MOUSE)
        {
            this.invertMouse = !this.invertMouse;
        }

        if (settingsOption == GameSettings.Options.GUI_SCALE)
        {
            this.guiScale = this.guiScale + value & 3;
        }

        if (settingsOption == GameSettings.Options.PARTICLES)
        {
            this.particleSetting = (this.particleSetting + value) % 3;
        }

        if (settingsOption == GameSettings.Options.VIEW_BOBBING)
        {
            this.viewBobbing = !this.viewBobbing;
        }

        if (settingsOption == GameSettings.Options.RENDER_CLOUDS)
        {
            this.clouds = (this.clouds + value) % 3;
        }

        if (settingsOption == GameSettings.Options.FORCE_UNICODE_FONT)
        {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }

        if (settingsOption == GameSettings.Options.FBO_ENABLE)
        {
            this.fboEnable = !this.fboEnable;
        }

        if (settingsOption == GameSettings.Options.ANAGLYPH)
        {
            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }

        if (settingsOption == GameSettings.Options.GRAPHICS)
        {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.CHAT_VISIBILITY)
        {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + value) % 3);
        }

        if (settingsOption == GameSettings.Options.CHAT_COLOR)
        {
            this.chatColours = !this.chatColours;
        }

        if (settingsOption == GameSettings.Options.CHAT_LINKS)
        {
            this.chatLinks = !this.chatLinks;
        }

        if (settingsOption == GameSettings.Options.CHAT_LINKS_PROMPT)
        {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (settingsOption == GameSettings.Options.SNOOPER_ENABLED)
        {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (settingsOption == GameSettings.Options.TOUCHSCREEN)
        {
            this.touchscreen = !this.touchscreen;
        }

        if (settingsOption == GameSettings.Options.USE_FULLSCREEN)
        {
            this.fullScreen = !this.fullScreen;

            if (this.mc.isFullScreen() != this.fullScreen)
            {
                this.mc.toggleFullscreen();
            }
        }

        if (settingsOption == GameSettings.Options.ENABLE_VSYNC)
        {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }

        if (settingsOption == GameSettings.Options.USE_VBO)
        {
            this.useVbo = !this.useVbo;
            this.mc.renderGlobal.loadRenderers();
        }

        if (settingsOption == GameSettings.Options.REDUCED_DEBUG_INFO)
        {
            this.reducedDebugInfo = !this.reducedDebugInfo;
        }

        if (settingsOption == GameSettings.Options.ENTITY_SHADOWS)
        {
            this.entityShadows = !this.entityShadows;
        }

        if (settingsOption == GameSettings.Options.ATTACK_INDICATOR)
        {
            this.attackIndicator = (this.attackIndicator + value) % 3;
        }

        if (settingsOption == GameSettings.Options.SHOW_SUBTITLES)
        {
            this.showSubtitles = !this.showSubtitles;
        }

        if (settingsOption == GameSettings.Options.REALMS_NOTIFICATIONS)
        {
            this.realmsNotifications = !this.realmsNotifications;
        }

        if (settingsOption == GameSettings.Options.AUTO_JUMP)
        {
            this.autoJump = !this.autoJump;
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(GameSettings.Options settingOption)
    {
        return settingOption == GameSettings.Options.FOV ? this.fovSetting : (settingOption == GameSettings.Options.GAMMA ? this.gammaSetting : (settingOption == GameSettings.Options.SATURATION ? this.saturation : (settingOption == GameSettings.Options.SENSITIVITY ? this.mouseSensitivity : (settingOption == GameSettings.Options.CHAT_OPACITY ? this.chatOpacity : (settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (settingOption == GameSettings.Options.CHAT_SCALE ? this.chatScale : (settingOption == GameSettings.Options.CHAT_WIDTH ? this.chatWidth : (settingOption == GameSettings.Options.FRAMERATE_LIMIT ? (float)this.limitFramerate : (settingOption == GameSettings.Options.MIPMAP_LEVELS ? (float)this.mipmapLevels : (settingOption == GameSettings.Options.RENDER_DISTANCE ? (float)this.renderDistanceChunks : 0.0F)))))))))));
    }

    public boolean getOptionOrdinalValue(GameSettings.Options settingOption)
    {
        switch (settingOption)
        {
            case INVERT_MOUSE:
                return this.invertMouse;
            case VIEW_BOBBING:
                return this.viewBobbing;
            case ANAGLYPH:
                return this.anaglyph;
            case FBO_ENABLE:
                return this.fboEnable;
            case CHAT_COLOR:
                return this.chatColours;
            case CHAT_LINKS:
                return this.chatLinks;
            case CHAT_LINKS_PROMPT:
                return this.chatLinksPrompt;
            case SNOOPER_ENABLED:
                return this.snooperEnabled;
            case USE_FULLSCREEN:
                return this.fullScreen;
            case ENABLE_VSYNC:
                return this.enableVsync;
            case USE_VBO:
                return this.useVbo;
            case TOUCHSCREEN:
                return this.touchscreen;
            case FORCE_UNICODE_FONT:
                return this.forceUnicodeFont;
            case REDUCED_DEBUG_INFO:
                return this.reducedDebugInfo;
            case ENTITY_SHADOWS:
                return this.entityShadows;
            case SHOW_SUBTITLES:
                return this.showSubtitles;
            case REALMS_NOTIFICATIONS:
                return this.realmsNotifications;
            case ENABLE_WEAK_ATTACKS:
                return this.enableWeakAttacks;
            case AUTO_JUMP:
                return this.autoJump;
            default:
                return false;
        }
    }

    /**
     * Returns the translation of the given index in the given String array. If the index is smaller than 0 or greater
     * than/equal to the length of the String array, it is changed to 0.
     */
    private static String getTranslation(String[] strArray, int index)
    {
        if (index < 0 || index >= strArray.length)
        {
            index = 0;
        }

        return I18n.format(strArray[index], new Object[0]);
    }

    /**
     * Gets a key binding.
     */
    public String getKeyBinding(GameSettings.Options settingOption)
    {
        String s = I18n.format(settingOption.getEnumString(), new Object[0]) + ": ";

        if (settingOption.getEnumFloat())
        {
            float f1 = this.getOptionFloatValue(settingOption);
            float f = settingOption.normalizeValue(f1);
            return settingOption == GameSettings.Options.SENSITIVITY ? (f == 0.0F ? s + I18n.format("options.sensitivity.min", new Object[0]) : (f == 1.0F ? s + I18n.format("options.sensitivity.max", new Object[0]) : s + (int)(f * 200.0F) + "%")) : (settingOption == GameSettings.Options.FOV ? (f1 == 70.0F ? s + I18n.format("options.fov.min", new Object[0]) : (f1 == 110.0F ? s + I18n.format("options.fov.max", new Object[0]) : s + (int)f1)) : (settingOption == GameSettings.Options.FRAMERATE_LIMIT ? (f1 == settingOption.valueMax ? s + I18n.format("options.framerateLimit.max", new Object[0]) : s + (int)f1 + " fps") : (settingOption == GameSettings.Options.RENDER_CLOUDS ? (f1 == settingOption.valueMin ? s + I18n.format("options.cloudHeight.min", new Object[0]) : s + ((int)f1 + 128)) : (settingOption == GameSettings.Options.GAMMA ? (f == 0.0F ? s + I18n.format("options.gamma.min", new Object[0]) : (f == 1.0F ? s + I18n.format("options.gamma.max", new Object[0]) : s + "+" + (int)(f * 100.0F) + "%")) : (settingOption == GameSettings.Options.SATURATION ? s + (int)(f * 400.0F) + "%" : (settingOption == GameSettings.Options.CHAT_OPACITY ? s + (int)(f * 90.0F + 10.0F) + "%" : (settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? s + GuiNewChat.calculateChatboxHeight(f) + "px" : (settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? s + GuiNewChat.calculateChatboxHeight(f) + "px" : (settingOption == GameSettings.Options.CHAT_WIDTH ? s + GuiNewChat.calculateChatboxWidth(f) + "px" : (settingOption == GameSettings.Options.RENDER_DISTANCE ? s + (int)f1 + " chunks" : (settingOption == GameSettings.Options.MIPMAP_LEVELS ? (f1 == 0.0F ? s + I18n.format("options.off", new Object[0]) : s + (int)f1) : (f == 0.0F ? s + I18n.format("options.off", new Object[0]) : s + (int)(f * 100.0F) + "%"))))))))))));
        }
        else if (settingOption.getEnumBoolean())
        {
            boolean flag = this.getOptionOrdinalValue(settingOption);
            return flag ? s + I18n.format("options.on", new Object[0]) : s + I18n.format("options.off", new Object[0]);
        }
        else if (settingOption == GameSettings.Options.MAIN_HAND)
        {
            return s + this.mainHand;
        }
        else if (settingOption == GameSettings.Options.GUI_SCALE)
        {
            return s + getTranslation(GUISCALES, this.guiScale);
        }
        else if (settingOption == GameSettings.Options.CHAT_VISIBILITY)
        {
            return s + I18n.format(this.chatVisibility.getResourceKey(), new Object[0]);
        }
        else if (settingOption == GameSettings.Options.PARTICLES)
        {
            return s + getTranslation(PARTICLES, this.particleSetting);
        }
        else if (settingOption == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            return s + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
        }
        else if (settingOption == GameSettings.Options.RENDER_CLOUDS)
        {
            return s + getTranslation(CLOUDS_TYPES, this.clouds);
        }
        else if (settingOption == GameSettings.Options.GRAPHICS)
        {
            if (this.fancyGraphics)
            {
                return s + I18n.format("options.graphics.fancy", new Object[0]);
            }
            else
            {
                String s1 = "options.graphics.fast";
                return s + I18n.format("options.graphics.fast", new Object[0]);
            }
        }
        else
        {
            return settingOption == GameSettings.Options.ATTACK_INDICATOR ? s + getTranslation(ATTACK_INDICATORS, this.attackIndicator) : s;
        }
    }

    /**
     * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
     */
    public void loadOptions()
    {
        try
        {
            if (!this.optionsFile.exists())
            {
                return;
            }

            this.soundLevels.clear();
            List<String> list = IOUtils.readLines((InputStream)(new FileInputStream(this.optionsFile)));
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            for (String s : list)
            {
                try
                {
                    Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
                    nbttagcompound.setString((String)iterator.next(), (String)iterator.next());
                }
                catch (Exception var10)
                {
                    LOGGER.warn("Skipping bad option: {}", new Object[] {s});
                }
            }

            nbttagcompound = this.dataFix(nbttagcompound);

            for (String s1 : nbttagcompound.getKeySet())
            {
                String s2 = nbttagcompound.getString(s1);

                try
                {
                    if ("mouseSensitivity".equals(s1))
                    {
                        this.mouseSensitivity = this.parseFloat(s2);
                    }

                    if ("fov".equals(s1))
                    {
                        this.fovSetting = this.parseFloat(s2) * 40.0F + 70.0F;
                    }

                    if ("gamma".equals(s1))
                    {
                        this.gammaSetting = this.parseFloat(s2);
                    }

                    if ("saturation".equals(s1))
                    {
                        this.saturation = this.parseFloat(s2);
                    }

                    if ("invertYMouse".equals(s1))
                    {
                        this.invertMouse = "true".equals(s2);
                    }

                    if ("renderDistance".equals(s1))
                    {
                        this.renderDistanceChunks = Integer.parseInt(s2);
                    }

                    if ("guiScale".equals(s1))
                    {
                        this.guiScale = Integer.parseInt(s2);
                    }

                    if ("particles".equals(s1))
                    {
                        this.particleSetting = Integer.parseInt(s2);
                    }

                    if ("bobView".equals(s1))
                    {
                        this.viewBobbing = "true".equals(s2);
                    }

                    if ("anaglyph3d".equals(s1))
                    {
                        this.anaglyph = "true".equals(s2);
                    }

                    if ("maxFps".equals(s1))
                    {
                        this.limitFramerate = Integer.parseInt(s2);
                    }

                    if ("fboEnable".equals(s1))
                    {
                        this.fboEnable = "true".equals(s2);
                    }

                    if ("difficulty".equals(s1))
                    {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(s2));
                    }

                    if ("fancyGraphics".equals(s1))
                    {
                        this.fancyGraphics = "true".equals(s2);
                    }

                    if ("ao".equals(s1))
                    {
                        if ("true".equals(s2))
                        {
                            this.ambientOcclusion = 2;
                        }
                        else if ("false".equals(s2))
                        {
                            this.ambientOcclusion = 0;
                        }
                        else
                        {
                            this.ambientOcclusion = Integer.parseInt(s2);
                        }
                    }

                    if ("renderClouds".equals(s1))
                    {
                        if ("true".equals(s2))
                        {
                            this.clouds = 2;
                        }
                        else if ("false".equals(s2))
                        {
                            this.clouds = 0;
                        }
                        else if ("fast".equals(s2))
                        {
                            this.clouds = 1;
                        }
                    }

                    if ("attackIndicator".equals(s1))
                    {
                        if ("0".equals(s2))
                        {
                            this.attackIndicator = 0;
                        }
                        else if ("1".equals(s2))
                        {
                            this.attackIndicator = 1;
                        }
                        else if ("2".equals(s2))
                        {
                            this.attackIndicator = 2;
                        }
                    }

                    if ("resourcePacks".equals(s1))
                    {
                        this.resourcePacks = (List)GSON.fromJson(s2, TYPE_LIST_STRING);

                        if (this.resourcePacks == null)
                        {
                            this.resourcePacks = Lists.<String>newArrayList();
                        }
                    }

                    if ("incompatibleResourcePacks".equals(s1))
                    {
                        this.incompatibleResourcePacks = (List)GSON.fromJson(s2, TYPE_LIST_STRING);

                        if (this.incompatibleResourcePacks == null)
                        {
                            this.incompatibleResourcePacks = Lists.<String>newArrayList();
                        }
                    }

                    if ("lastServer".equals(s1))
                    {
                        this.lastServer = s2;
                    }

                    if ("lang".equals(s1))
                    {
                        this.language = s2;
                    }

                    if ("chatVisibility".equals(s1))
                    {
                        this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(s2));
                    }

                    if ("chatColors".equals(s1))
                    {
                        this.chatColours = "true".equals(s2);
                    }

                    if ("chatLinks".equals(s1))
                    {
                        this.chatLinks = "true".equals(s2);
                    }

                    if ("chatLinksPrompt".equals(s1))
                    {
                        this.chatLinksPrompt = "true".equals(s2);
                    }

                    if ("chatOpacity".equals(s1))
                    {
                        this.chatOpacity = this.parseFloat(s2);
                    }

                    if ("snooperEnabled".equals(s1))
                    {
                        this.snooperEnabled = "true".equals(s2);
                    }

                    if ("fullscreen".equals(s1))
                    {
                        this.fullScreen = "true".equals(s2);
                    }

                    if ("enableVsync".equals(s1))
                    {
                        this.enableVsync = "true".equals(s2);
                    }

                    if ("useVbo".equals(s1))
                    {
                        this.useVbo = "true".equals(s2);
                    }

                    if ("hideServerAddress".equals(s1))
                    {
                        this.hideServerAddress = "true".equals(s2);
                    }

                    if ("advancedItemTooltips".equals(s1))
                    {
                        this.advancedItemTooltips = "true".equals(s2);
                    }

                    if ("pauseOnLostFocus".equals(s1))
                    {
                        this.pauseOnLostFocus = "true".equals(s2);
                    }

                    if ("touchscreen".equals(s1))
                    {
                        this.touchscreen = "true".equals(s2);
                    }

                    if ("overrideHeight".equals(s1))
                    {
                        this.overrideHeight = Integer.parseInt(s2);
                    }

                    if ("overrideWidth".equals(s1))
                    {
                        this.overrideWidth = Integer.parseInt(s2);
                    }

                    if ("heldItemTooltips".equals(s1))
                    {
                        this.heldItemTooltips = "true".equals(s2);
                    }

                    if ("chatHeightFocused".equals(s1))
                    {
                        this.chatHeightFocused = this.parseFloat(s2);
                    }

                    if ("chatHeightUnfocused".equals(s1))
                    {
                        this.chatHeightUnfocused = this.parseFloat(s2);
                    }

                    if ("chatScale".equals(s1))
                    {
                        this.chatScale = this.parseFloat(s2);
                    }

                    if ("chatWidth".equals(s1))
                    {
                        this.chatWidth = this.parseFloat(s2);
                    }

                    if ("showInventoryAchievementHint".equals(s1))
                    {
                        this.showInventoryAchievementHint = "true".equals(s2);
                    }

                    if ("mipmapLevels".equals(s1))
                    {
                        this.mipmapLevels = Integer.parseInt(s2);
                    }

                    if ("forceUnicodeFont".equals(s1))
                    {
                        this.forceUnicodeFont = "true".equals(s2);
                    }

                    if ("reducedDebugInfo".equals(s1))
                    {
                        this.reducedDebugInfo = "true".equals(s2);
                    }

                    if ("useNativeTransport".equals(s1))
                    {
                        this.useNativeTransport = "true".equals(s2);
                    }

                    if ("entityShadows".equals(s1))
                    {
                        this.entityShadows = "true".equals(s2);
                    }

                    if ("mainHand".equals(s1))
                    {
                        this.mainHand = "left".equals(s2) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
                    }

                    if ("showSubtitles".equals(s1))
                    {
                        this.showSubtitles = "true".equals(s2);
                    }

                    if ("realmsNotifications".equals(s1))
                    {
                        this.realmsNotifications = "true".equals(s2);
                    }

                    if ("enableWeakAttacks".equals(s1))
                    {
                        this.enableWeakAttacks = "true".equals(s2);
                    }

                    if ("autoJump".equals(s1))
                    {
                        this.autoJump = "true".equals(s2);
                    }

                    for (KeyBinding keybinding : this.keyBindings)
                    {
                        if (s1.equals("key_" + keybinding.getKeyDescription()))
                        {
                            if (s2.indexOf(':') != -1)
                            {
                                String[] t = s2.split(":");
                                keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.valueFromString(t[1]), Integer.parseInt(t[0]));
                            } else
                            keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, Integer.parseInt(s2));
                        }
                    }

                    for (SoundCategory soundcategory : SoundCategory.values())
                    {
                        if (s1.equals("soundCategory_" + soundcategory.getName()))
                        {
                            this.soundLevels.put(soundcategory, Float.valueOf(this.parseFloat(s2)));
                        }
                    }

                    for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
                    {
                        if (s1.equals("modelPart_" + enumplayermodelparts.getPartName()))
                        {
                            this.setModelPartEnabled(enumplayermodelparts, "true".equals(s2));
                        }
                    }
                }
                catch (Exception var11)
                {
                    LOGGER.warn("Skipping bad option: {}:{}", new Object[] {s1, s2});
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        }
        catch (Exception exception)
        {
            LOGGER.error((String)"Failed to load options", (Throwable)exception);
        }
    }

    private NBTTagCompound dataFix(NBTTagCompound p_189988_1_)
    {
        int i = 0;

        try
        {
            i = Integer.parseInt(p_189988_1_.getString("version"));
        }
        catch (RuntimeException var4)
        {
            ;
        }

        return this.mc.getDataFixer().process(FixTypes.OPTIONS, p_189988_1_, i);
    }

    /**
     * Parses a string into a float.
     */
    private float parseFloat(String str)
    {
        return "true".equals(str) ? 1.0F : ("false".equals(str) ? 0.0F : Float.parseFloat(str));
    }

    /**
     * Saves the options to the options file.
     */
    public void saveOptions()
    {
        if (net.minecraftforge.fml.client.FMLClientHandler.instance().isLoading()) return;
        PrintWriter printwriter = null;

        try
        {
            printwriter = new PrintWriter(new FileWriter(this.optionsFile));
            printwriter.println("version:512");
            printwriter.println("invertYMouse:" + this.invertMouse);
            printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printwriter.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
            printwriter.println("gamma:" + this.gammaSetting);
            printwriter.println("saturation:" + this.saturation);
            printwriter.println("renderDistance:" + this.renderDistanceChunks);
            printwriter.println("guiScale:" + this.guiScale);
            printwriter.println("particles:" + this.particleSetting);
            printwriter.println("bobView:" + this.viewBobbing);
            printwriter.println("anaglyph3d:" + this.anaglyph);
            printwriter.println("maxFps:" + this.limitFramerate);
            printwriter.println("fboEnable:" + this.fboEnable);
            printwriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + this.fancyGraphics);
            printwriter.println("ao:" + this.ambientOcclusion);

            switch (this.clouds)
            {
                case 0:
                    printwriter.println("renderClouds:false");
                    break;
                case 1:
                    printwriter.println("renderClouds:fast");
                    break;
                case 2:
                    printwriter.println("renderClouds:true");
            }

            printwriter.println("resourcePacks:" + GSON.toJson((Object)this.resourcePacks));
            printwriter.println("incompatibleResourcePacks:" + GSON.toJson((Object)this.incompatibleResourcePacks));
            printwriter.println("lastServer:" + this.lastServer);
            printwriter.println("lang:" + this.language);
            printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + this.chatColours);
            printwriter.println("chatLinks:" + this.chatLinks);
            printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
            printwriter.println("chatOpacity:" + this.chatOpacity);
            printwriter.println("snooperEnabled:" + this.snooperEnabled);
            printwriter.println("fullscreen:" + this.fullScreen);
            printwriter.println("enableVsync:" + this.enableVsync);
            printwriter.println("useVbo:" + this.useVbo);
            printwriter.println("hideServerAddress:" + this.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printwriter.println("touchscreen:" + this.touchscreen);
            printwriter.println("overrideWidth:" + this.overrideWidth);
            printwriter.println("overrideHeight:" + this.overrideHeight);
            printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printwriter.println("chatScale:" + this.chatScale);
            printwriter.println("chatWidth:" + this.chatWidth);
            printwriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            printwriter.println("mipmapLevels:" + this.mipmapLevels);
            printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
            printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
            printwriter.println("useNativeTransport:" + this.useNativeTransport);
            printwriter.println("entityShadows:" + this.entityShadows);
            printwriter.println("mainHand:" + (this.mainHand == EnumHandSide.LEFT ? "left" : "right"));
            printwriter.println("attackIndicator:" + this.attackIndicator);
            printwriter.println("showSubtitles:" + this.showSubtitles);
            printwriter.println("realmsNotifications:" + this.realmsNotifications);
            printwriter.println("enableWeakAttacks:" + this.enableWeakAttacks);
            printwriter.println("autoJump:" + this.autoJump);

            for (KeyBinding keybinding : this.keyBindings)
            {
                String keyString = "key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode();
                printwriter.println(keybinding.getKeyModifier() != net.minecraftforge.client.settings.KeyModifier.NONE ? keyString + ":" + keybinding.getKeyModifier() : keyString);
            }

            for (SoundCategory soundcategory : SoundCategory.values())
            {
                printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
            }

            for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
            {
                printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":" + this.setModelParts.contains(enumplayermodelparts));
            }
        }
        catch (Exception exception)
        {
            LOGGER.error((String)"Failed to save options", (Throwable)exception);
        }
        finally
        {
            IOUtils.closeQuietly((Writer)printwriter);
        }

        this.sendSettingsToServer();
    }

    public float getSoundLevel(SoundCategory category)
    {
        return this.soundLevels.containsKey(category) ? ((Float)this.soundLevels.get(category)).floatValue() : 1.0F;
    }

    public void setSoundLevel(SoundCategory category, float volume)
    {
        this.mc.getSoundHandler().setSoundLevel(category, volume);
        this.soundLevels.put(category, Float.valueOf(volume));
    }

    /**
     * Send a client info packet with settings information to the server
     */
    public void sendSettingsToServer()
    {
        if (this.mc.player != null)
        {
            int i = 0;

            for (EnumPlayerModelParts enumplayermodelparts : this.setModelParts)
            {
                i |= enumplayermodelparts.getPartMask();
            }

            this.mc.player.connection.sendPacket(new CPacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i, this.mainHand));
        }
    }

    public Set<EnumPlayerModelParts> getModelParts()
    {
        return ImmutableSet.copyOf(this.setModelParts);
    }

    public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enable)
    {
        if (enable)
        {
            this.setModelParts.add(modelPart);
        }
        else
        {
            this.setModelParts.remove(modelPart);
        }

        this.sendSettingsToServer();
    }

    public void switchModelPartEnabled(EnumPlayerModelParts modelPart)
    {
        if (this.getModelParts().contains(modelPart))
        {
            this.setModelParts.remove(modelPart);
        }
        else
        {
            this.setModelParts.add(modelPart);
        }

        this.sendSettingsToServer();
    }

    /**
     * Return true if the clouds should be rendered
     */
    public int shouldRenderClouds()
    {
        return this.renderDistanceChunks >= 4 ? this.clouds : 0;
    }

    /**
     * Return true if the client connect to a server using the native transport system
     */
    public boolean isUsingNativeTransport()
    {
        return this.useNativeTransport;
    }

    @SideOnly(Side.CLIENT)
    public static enum Options
    {
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
        GAMMA("options.gamma", true, false),
        SATURATION("options.saturation", true, false),
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
        FBO_ENABLE("options.fboEnable", false, true),
        RENDER_CLOUDS("options.renderClouds", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, false),
        GUI_SCALE("options.guiScale", false, false),
        PARTICLES("options.particles", false, false),
        CHAT_VISIBILITY("options.chat.visibility", false, false),
        CHAT_COLOR("options.chat.color", false, true),
        CHAT_LINKS("options.chat.links", false, true),
        CHAT_OPACITY("options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
        SNOOPER_ENABLED("options.snooper", false, true),
        USE_FULLSCREEN("options.fullscreen", false, true),
        ENABLE_VSYNC("options.vsync", false, true),
        USE_VBO("options.vbo", false, true),
        TOUCHSCREEN("options.touchscreen", false, true),
        CHAT_SCALE("options.chat.scale", true, false),
        CHAT_WIDTH("options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
        REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
        ENTITY_SHADOWS("options.entityShadows", false, true),
        MAIN_HAND("options.mainHand", false, false),
        ATTACK_INDICATOR("options.attackIndicator", false, false),
        ENABLE_WEAK_ATTACKS("options.enableWeakAttacks", false, true),
        SHOW_SUBTITLES("options.showSubtitles", false, true),
        REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
        AUTO_JUMP("options.autoJump", false, true);

        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        private float valueMin;
        private float valueMax;

        public static GameSettings.Options getEnumOptions(int ordinal)
        {
            for (GameSettings.Options gamesettings$options : values())
            {
                if (gamesettings$options.returnEnumOrdinal() == ordinal)
                {
                    return gamesettings$options;
                }
            }

            return null;
        }

        private Options(String str, boolean isFloat, boolean isBoolean)
        {
            this(str, isFloat, isBoolean, 0.0F, 1.0F, 0.0F);
        }

        private Options(String str, boolean isFloat, boolean isBoolean, float valMin, float valMax, float valStep)
        {
            this.enumString = str;
            this.enumFloat = isFloat;
            this.enumBoolean = isBoolean;
            this.valueMin = valMin;
            this.valueMax = valMax;
            this.valueStep = valStep;
        }

        public boolean getEnumFloat()
        {
            return this.enumFloat;
        }

        public boolean getEnumBoolean()
        {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal()
        {
            return this.ordinal();
        }

        public String getEnumString()
        {
            return this.enumString;
        }

        public float getValueMin()
        {
            return this.valueMin;
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public void setValueMax(float value)
        {
            this.valueMax = value;
        }

        public float normalizeValue(float value)
        {
            return MathHelper.clamp((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float value)
        {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp(value, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float value)
        {
            value = this.snapToStep(value);
            return MathHelper.clamp(value, this.valueMin, this.valueMax);
        }

        private float snapToStep(float value)
        {
            if (this.valueStep > 0.0F)
            {
                value = this.valueStep * (float)Math.round(value / this.valueStep);
            }

            return value;
        }
    }

    /******* Forge Start ***********/
    private void setForgeKeybindProperties() {
        net.minecraftforge.client.settings.KeyConflictContext inGame = net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
        keyBindForward.setKeyConflictContext(inGame);
        keyBindLeft.setKeyConflictContext(inGame);
        keyBindBack.setKeyConflictContext(inGame);
        keyBindRight.setKeyConflictContext(inGame);
        keyBindJump.setKeyConflictContext(inGame);
        keyBindSneak.setKeyConflictContext(inGame);
        keyBindSprint.setKeyConflictContext(inGame);
        keyBindAttack.setKeyConflictContext(inGame);
        keyBindChat.setKeyConflictContext(inGame);
        keyBindPlayerList.setKeyConflictContext(inGame);
        keyBindCommand.setKeyConflictContext(inGame);
        keyBindTogglePerspective.setKeyConflictContext(inGame);
        keyBindSmoothCamera.setKeyConflictContext(inGame);
        keyBindSwapHands.setKeyConflictContext(inGame);
    }

    // FORGE: fix for MC-64581 very laggy mipmap slider
    private boolean needsResourceRefresh = false;
    public void onGuiClosed()
    {
        if (needsResourceRefresh)
        {
            this.mc.scheduleResourcesRefresh();
            this.needsResourceRefresh = false;
        }
    }
    /******* Forge End ***********/
}