package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.List;

public class BlockDrawers extends BlockContainer implements IExtendedBlockClickHandler, INetworked
{
    public static final PropertyEnum BLOCK = PropertyEnum.create("block", EnumBasicDrawer.class);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);

    //private static final ResourceLocation blockConfig = new ResourceLocation(StorageDrawers.MOD_ID + ":textures/blocks/block_config.mcmeta");

    public BlockDrawers (String blockName) {
        this(Material.wood, blockName);
    }

    protected BlockDrawers (Material material, String blockName) {
        super(material);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setStepSound(Block.soundTypeWood);
        setUnlocalizedName(blockName);

        initDefaultState();
    }

    protected void initDefaultState () {
        setDefaultState(blockState.getBaseState().withProperty(BLOCK, EnumBasicDrawer.FULL2));
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK));
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    protected int getDrawerCount (IBlockState state) {
        if (state != null) {
            EnumBasicDrawer info = (EnumBasicDrawer) state.getValue(BLOCK);
            if (info != null)
                return info.getDrawerCount();
        }

        return 0;
    }

    protected boolean isHalfDepth (IBlockState state) {
        if (state != null) {
            EnumBasicDrawer info = (EnumBasicDrawer) state.getValue(BLOCK);
            if (info != null)
                return info.isHalfDepth();
        }

        return false;
    }

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    public int getRenderType () {
        return 3;
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess blockAccess, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        if (tile == null) {
            setBlockBounds(0, 0, 0, 1, 1, 1);
            return;
        }

        float depth = isHalfDepth(blockAccess.getBlockState(pos)) ? .5f : 1;
        switch (tile.getDirection()) {
            case 2:
                setBlockBounds(0, 0, 1 - depth, 1, 1, 1);
                break;
            case 3:
                setBlockBounds(0, 0, 0, 1, 1, depth);
                break;
            case 4:
                setBlockBounds(1 - depth, 0, 0, 1, 1, 1);
                break;
            case 5:
                setBlockBounds(0, 0, 0, depth, 1, 1);
                break;
        }
    }

    @Override
    public void setBlockBoundsForItemRender () {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public void addCollisionBoxesToList (World world, BlockPos pos, IBlockState state, AxisAlignedBB aabb, List list, Entity entity) {
        setBlockBoundsBasedOnState(world, pos);
        super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
    }

    @Override
    public void onBlockAdded (World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            Block blockNorth = world.getBlockState(pos.north()).getBlock();
            Block blockSouth = world.getBlockState(pos.south()).getBlock();
            Block blockWest = world.getBlockState(pos.west()).getBlock();
            Block blockEast = world.getBlockState(pos.east()).getBlock();

            EnumFacing facing = (EnumFacing)state.getValue(FACING);

            if (facing == EnumFacing.NORTH && blockNorth.isFullBlock() && !blockSouth.isFullBlock())
                facing = EnumFacing.SOUTH;
            if (facing == EnumFacing.SOUTH && blockSouth.isFullBlock() && !blockNorth.isFullBlock())
                facing = EnumFacing.NORTH;
            if (facing == EnumFacing.WEST && blockWest.isFullBlock() && !blockEast.isFullBlock())
                facing = EnumFacing.EAST;
            if (facing == EnumFacing.EAST && blockEast.isFullBlock() && !blockWest.isFullBlock())
                facing = EnumFacing.WEST;

            TileEntityDrawers tile = getTileEntitySafe(world, pos);
            tile.setDirection(facing.ordinal());
            tile.markDirty();
        }

        super.onBlockAdded(world, pos, state);
    }

    @Override
    public IBlockState onBlockPlaced (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(BLOCK, EnumBasicDrawer.byMetadata(meta));
    }

    @Override
    public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        EnumFacing facing = entity.getHorizontalFacing().getOpposite();

        TileEntityDrawers tile = getTileEntitySafe(world, pos);
        tile.setDirection(facing.ordinal());
        tile.markDirty();
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);
        ItemStack item = player.inventory.getCurrentItem();

        if (StorageDrawers.config.cache.debugTrace) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockActivated");
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item.toString());
        }

        if (item != null && item.getItem() != null) {
            if (item.getItem() == ModItems.upgradeStorage  && item.getItemDamage() != tileDrawers.getStorageLevel()) {
                tileDrawers.setStorageLevel(item.getItemDamage());
                world.markBlockForUpdate(pos);

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgradeStatus && item.getItemDamage() != tileDrawers.getStatusLevel()) {
                tileDrawers.setStatusLevel(item.getItemDamage());
                world.markBlockForUpdate(pos);

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgradeVoid && !tileDrawers.isVoid()) {
                tileDrawers.setVoid(true);
                world.markBlockForUpdate(pos);

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.drawerKey) {
                tileDrawers.setIsLocked(!tileDrawers.isLocked());
                world.markBlockForUpdate(pos);

                return true;
            }
        }

        if (tileDrawers.getDirection() != side.ordinal())
            return false;

        int slot = getDrawerSlot(getDrawerCount(state), side.ordinal(), hitX, hitY, hitZ);
        int countAdded = tileDrawers.interactPutItemsIntoSlot(slot, player);

        if (countAdded > 0)
            world.markBlockForUpdate(pos);

        return true;
    }

    protected int getDrawerSlot (int drawerCount, int side, float hitX, float hitY, float hitZ) {
        if (drawerCount == 1)
            return 0;
        if (drawerCount == 2)
            return hitTop(hitY) ? 0 : 1;

        if (hitLeft(side, hitX, hitZ))
            return hitTop(hitY) ? 0 : 1;
        else
            return hitTop(hitY) ? 2 : 3;
    }

    protected boolean hitTop (float hitY) {
        return hitY > .5;
    }

    protected boolean hitLeft (int side, float hitX, float hitZ) {
        switch (side) {
            case 2:
                return hitX > .5;
            case 3:
                return hitX < .5;
            case 4:
                return hitZ < .5;
            case 5:
                return hitZ > .5;
            default:
                return true;
        }
    }

    @Override
    public void onBlockClicked (World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            MovingObjectPosition posn = Minecraft.getMinecraft().objectMouseOver;
            BlockPos posb = posn.getBlockPos();
            float hitX = (float)(posn.hitVec.xCoord - posb.getX());
            float hitY = (float)(posn.hitVec.yCoord - posb.getY());
            float hitZ = (float)(posn.hitVec.zCoord - posb.getZ());

            StorageDrawers.network.sendToServer(new BlockClickMessage(pos.getX(), pos.getY(), pos.getZ(), posn.sideHit.ordinal(), hitX, hitY, hitZ, StorageDrawers.config.cache.invertShift));

            if (StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockClicked with " + posn.toString());
        }
    }

    @Override
    public void onBlockClicked (final World world, final BlockPos pos, final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final boolean invertShift) {
        if (world.isRemote)
            return;

        ((WorldServer)world).addScheduledTask(new Runnable()
        {
            @Override
            public void run () {
                BlockDrawers.this.onBlockClickedAsync(world, pos, player, side, hitX, hitY, hitZ, invertShift);
            }
        });
    }

    private void onBlockClickedAsync (World world, BlockPos pos, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, boolean invertShift) {
        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "IExtendedBlockClickHandler.onBlockClicked");

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);
        if (tileDrawers.getDirection() != side.ordinal())
            return;

        int slot = getDrawerSlot(getDrawerCount(world.getBlockState(pos)), side.ordinal(), hitX, hitY, hitZ);
        IDrawer drawer = tileDrawers.getDrawer(slot);

        ItemStack item = null;
        if (player.isSneaking() != invertShift)
            item = tileDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = tileDrawers.takeItemsFromSlot(slot, 1);

        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item.toString());

        if (item != null && item.stackSize > 0) {
            dropItemStack(world, pos, player, item);
            world.markBlockForUpdate(pos);
        }
    }

    @Override
    public boolean isSideSolid (IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (isHalfDepth(world.getBlockState(pos)))
            return false;

        if (side == EnumFacing.DOWN) {
            Block blockUnder = world.getBlockState(pos.down()).getBlock();
            if (blockUnder instanceof BlockChest || blockUnder instanceof BlockEnderChest)
                return false;
        }

        if (side.ordinal() != getTileEntity(world, pos).getDirection())
            return true;

        return false;
    }

    private void dropItemStack (World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
        EntityItem entity = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
        world.spawnEntityInWorld(entity);
    }

    @Override
    public boolean removedByPlayer (World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (world.isRemote && player.capabilities.isCreativeMode) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            MovingObjectPosition posn = Minecraft.getMinecraft().objectMouseOver;

            if (tile.getDirection() == posn.sideHit.ordinal()) {
                onBlockClicked(world, pos, player);
                return false;
            }
        }

        return super.removedByPlayer(world, pos, player, willHarvest);
    }

    @Override
    public void breakBlock (World world, BlockPos pos, IBlockState state) {
        TileEntityDrawers tile = getTileEntity(world, pos);

        if (tile != null) {
            if (tile.getStorageLevel() > 1)
                spawnAsEntity(world, pos, new ItemStack(ModItems.upgradeStorage, 1, tile.getStorageLevel()));
            if (tile.getStatusLevel() > 0)
                spawnAsEntity(world, pos, new ItemStack(ModItems.upgradeStatus, 1, tile.getStatusLevel()));
            if (tile.isVoid())
                spawnAsEntity(world, pos, new ItemStack(ModItems.upgradeVoid));

            InventoryHelper.dropInventoryItems(world, pos, tile);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public TileEntityDrawers createNewTileEntity (World world, int meta) {
        return new TileEntityDrawersStandard();
    }

    public TileEntityDrawers getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntityDrawers) ? (TileEntityDrawers) tile : null;
    }

    public TileEntityDrawers getTileEntitySafe (World world, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createNewTileEntity(world, 0);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects (World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        TileEntity tile = worldObj.getTileEntity(target.getBlockPos());
        if (tile instanceof TileEntityDrawers) {
            if (((TileEntityDrawers) tile).getDirection() == target.sideHit.ordinal())
                return true;
        }

        return super.addHitEffects(worldObj, target, effectRenderer);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects (World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDrawers)
        return super.addDestroyEffects(world, x, y, z, meta, effectRenderer);
    }*/

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
                ItemStack stack = new ItemStack(item, 1, type.getMetadata());

                NBTTagCompound data = new NBTTagCompound();
                data.setString("material", material.getName());
                stack.setTagCompound(data);

                list.add(stack);
            }
        }
        /*if (StorageDrawers.config.cache.creativeTabVanillaWoods) {
            BlockPlanks.EnumType[] plankTypes = BlockPlanks.EnumType.values();
            for (int i = 0; i < plankTypes.length; i++)
                list.add(new ItemStack(item, 1, plankTypes[i].getMetadata()));
        }
        else
            list.add(new ItemStack(item, 1, BlockPlanks.EnumType.values()[0].getMetadata()));*/
    }

    @Override
    public IBlockState getStateForEntityRender (IBlockState state) {
        return getDefaultState();
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {
        IBlockState state = getDefaultState().withProperty(BLOCK, EnumBasicDrawer.byMetadata(meta));
        return state;
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        int meta = ((EnumBasicDrawer)state.getValue(BLOCK)).getMetadata();
        return meta;
    }

    @Override
    protected BlockState createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { BLOCK, VARIANT, FACING }, new IUnlistedProperty[0]);
    }

    @Override
    public IBlockState getActualState (IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            TileEntityDrawers tile = getTileEntity(world, pos);
            EnumFacing facing = EnumFacing.getFront(tile.getDirection());
            if (facing.getAxis() == EnumFacing.Axis.Y)
                facing = EnumFacing.NORTH;

            BlockPlanks.EnumType woodType = translateMaterial(tile.getMaterialOrDefault());

            return ((IExtendedBlockState) state).withProperty(BLOCK, state.getValue(BLOCK)).withProperty(FACING, facing).withProperty(VARIANT, woodType);
        }
        return state;
    }

    private BlockPlanks.EnumType translateMaterial (String materal) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            if (materal.equals(type.getName()))
                return type;
        }

        return BlockPlanks.EnumType.OAK;
    }

    /*
    @SideOnly(Side.CLIENT)
    public IIcon getOverlayIcon (IBlockAccess blockAccess, int x, int y, int z, int side, int level) {
        if (level == 0)
            return null;

        return getIcon(blockAccess, x, y, z, side, level);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getOverlayIconTrim (int level) {
        if (level == 0)
            return null;

        return iconOverlayTrim[level];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIndicatorIcon (int drawerCount, boolean on) {
        int onIndex = on ? 1 : 0;
        switch (drawerCount) {
            case 1: return iconIndicator1[onIndex];
            case 2: return iconIndicator2[onIndex];
            case 4: return iconIndicator4[onIndex];
        }

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        String[] subtex = BlockWood.field_150096_a;

        iconSide = new IIcon[subtex.length];
        iconSideH = new IIcon[subtex.length];
        iconSideV = new IIcon[subtex.length];
        iconFront1 = new IIcon[subtex.length];
        iconFront2 = new IIcon[subtex.length];
        iconFront4 = new IIcon[subtex.length];
        iconTrim = new IIcon[subtex.length];

        for (int i = 0; i < subtex.length; i++) {
            iconFront1[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_front_1");
            iconFront2[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_front_2");
            iconFront4[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_front_4");
            iconSide[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_side");
            iconSideV[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_side_v");
            iconSideH[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_side_h");
            iconTrim[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_trim");
        }

        String[] overlays = new String[] { null, null, "iron", "gold", "obsidian", "diamond", "emerald" };

        iconOverlay = new IIcon[overlays.length];
        iconOverlayH = new IIcon[overlays.length];
        iconOverlayV = new IIcon[overlays.length];
        iconOverlayTrim = new IIcon[overlays.length];

        for (int i = 2; i < overlays.length; i++) {
            iconOverlay[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i]);
            iconOverlayV[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i] + "_v");
            iconOverlayH[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i] + "_h");
            iconOverlayTrim[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i] + "_trim");
        }

        iconIndicator1 = new IIcon[2];
        iconIndicator2 = new IIcon[2];
        iconIndicator4 = new IIcon[2];

        iconIndicator1[0] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_1_off");
        iconIndicator1[1] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_1_on");
        iconIndicator2[0] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_2_off");
        iconIndicator2[1] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_2_on");
        iconIndicator4[0] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_4_off");
        iconIndicator4[1] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_4_on");

        //iconLockFace = register.registerIcon(StorageDrawers.MOD_ID + ":lock_face");

        loadBlockConfig();
    }

    private void loadBlockConfig () {
        try {
            IResource configResource = Minecraft.getMinecraft().getResourceManager().getResource(blockConfig);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(configResource.getInputStream()));
                JsonObject root = (new JsonParser()).parse(reader).getAsJsonObject();

                JsonObject entry = root.getAsJsonObject(getUnlocalizedName().substring(5));
                if (entry != null) {
                    if (entry.has("trimWidth"))
                        trimWidth = entry.get("trimWidth").getAsFloat();
                    if (entry.has("trimDepth"))
                        trimDepth = entry.get("trimDepth").getAsFloat();
                    if (entry.has("indStart"))
                        indStart = entry.get("indStart").getAsFloat();
                    if (entry.has("indEnd"))
                        indEnd = entry.get("indEnd").getAsFloat();
                    if (entry.has("indSteps"))
                        indSteps = entry.get("indSteps").getAsInt();
                }
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
