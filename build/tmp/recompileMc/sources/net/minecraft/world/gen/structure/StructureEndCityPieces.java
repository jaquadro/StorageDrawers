package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class StructureEndCityPieces
{
    public static final TemplateManager MANAGER = new TemplateManager("structures");
    private static final PlacementSettings OVERWRITE = (new PlacementSettings()).setIgnoreEntities(true);
    private static final PlacementSettings INSERT = (new PlacementSettings()).setIgnoreEntities(true).setReplacedBlock(Blocks.AIR);
    private static final StructureEndCityPieces.IGenerator HOUSE_TOWER_GENERATOR = new StructureEndCityPieces.IGenerator()
    {
        public void init()
        {
        }
        public boolean generate(int p_186185_1_, StructureEndCityPieces.CityTemplate p_186185_2_, BlockPos p_186185_3_, List<StructureComponent> p_186185_4_, Random rand)
        {
            if (p_186185_1_ > 8)
            {
                return false;
            }
            else
            {
                Rotation rotation = p_186185_2_.placeSettings.getRotation();
                StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(p_186185_2_, p_186185_3_, "base_floor", rotation, true));
                int i = rand.nextInt(3);

                if (i == 0)
                {
                    StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 4, -1), "base_roof", rotation, true));
                }
                else if (i == 1)
                {
                    structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
                    structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 8, -1), "second_roof", rotation, false));
                    StructureEndCityPieces.recursiveChildren(StructureEndCityPieces.TOWER_GENERATOR, p_186185_1_ + 1, structureendcitypieces$citytemplate, (BlockPos)null, p_186185_4_, rand);
                }
                else if (i == 2)
                {
                    structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
                    structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 4, -1), "third_floor_c", rotation, false));
                    structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
                    StructureEndCityPieces.recursiveChildren(StructureEndCityPieces.TOWER_GENERATOR, p_186185_1_ + 1, structureendcitypieces$citytemplate, (BlockPos)null, p_186185_4_, rand);
                }

                return true;
            }
        }
    };
    private static final List<Tuple<Rotation, BlockPos>> TOWER_BRIDGES = Lists.<Tuple<Rotation, BlockPos>>newArrayList(new Tuple[] {new Tuple(Rotation.NONE, new BlockPos(1, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
    private static final StructureEndCityPieces.IGenerator TOWER_GENERATOR = new StructureEndCityPieces.IGenerator()
    {
        public void init()
        {
        }
        public boolean generate(int p_186185_1_, StructureEndCityPieces.CityTemplate p_186185_2_, BlockPos p_186185_3_, List<StructureComponent> p_186185_4_, Random rand)
        {
            Rotation rotation = p_186185_2_.placeSettings.getRotation();
            StructureEndCityPieces.CityTemplate lvt_7_1_ = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(p_186185_2_, new BlockPos(3 + rand.nextInt(2), -3, 3 + rand.nextInt(2)), "tower_base", rotation, true));
            lvt_7_1_ = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(lvt_7_1_, new BlockPos(0, 7, 0), "tower_piece", rotation, true));
            StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate1 = rand.nextInt(3) == 0 ? lvt_7_1_ : null;
            int i = 1 + rand.nextInt(3);

            for (int j = 0; j < i; ++j)
            {
                lvt_7_1_ = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(lvt_7_1_, new BlockPos(0, 4, 0), "tower_piece", rotation, true));

                if (j < i - 1 && rand.nextBoolean())
                {
                    structureendcitypieces$citytemplate1 = lvt_7_1_;
                }
            }

            if (structureendcitypieces$citytemplate1 != null)
            {
                for (Tuple<Rotation, BlockPos> tuple : StructureEndCityPieces.TOWER_BRIDGES)
                {
                    if (rand.nextBoolean())
                    {
                        StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate2 = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate1, (BlockPos)tuple.getSecond(), "bridge_end", rotation.add((Rotation)tuple.getFirst()), true));
                        StructureEndCityPieces.recursiveChildren(StructureEndCityPieces.TOWER_BRIDGE_GENERATOR, p_186185_1_ + 1, structureendcitypieces$citytemplate2, (BlockPos)null, p_186185_4_, rand);
                    }
                }

                StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(lvt_7_1_, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
            }
            else
            {
                if (p_186185_1_ != 7)
                {
                    return StructureEndCityPieces.recursiveChildren(StructureEndCityPieces.FAT_TOWER_GENERATOR, p_186185_1_ + 1, lvt_7_1_, (BlockPos)null, p_186185_4_, rand);
                }

                StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(lvt_7_1_, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
            }

            return true;
        }
    };
    private static final StructureEndCityPieces.IGenerator TOWER_BRIDGE_GENERATOR = new StructureEndCityPieces.IGenerator()
    {
        public boolean shipCreated;
        public void init()
        {
            this.shipCreated = false;
        }
        public boolean generate(int p_186185_1_, StructureEndCityPieces.CityTemplate p_186185_2_, BlockPos p_186185_3_, List<StructureComponent> p_186185_4_, Random rand)
        {
            Rotation rotation = p_186185_2_.placeSettings.getRotation();
            int i = rand.nextInt(4) + 1;
            StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(p_186185_2_, new BlockPos(0, 0, -4), "bridge_piece", rotation, true));
            structureendcitypieces$citytemplate.componentType = -1;
            int j = 0;

            for (int k = 0; k < i; ++k)
            {
                if (rand.nextBoolean())
                {
                    structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(0, j, -4), "bridge_piece", rotation, true));
                    j = 0;
                }
                else
                {
                    if (rand.nextBoolean())
                    {
                        structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(0, j, -4), "bridge_steep_stairs", rotation, true));
                    }
                    else
                    {
                        structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(0, j, -8), "bridge_gentle_stairs", rotation, true));
                    }

                    j = 4;
                }
            }

            if (!this.shipCreated && rand.nextInt(10 - p_186185_1_) == 0)
            {
                StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-8 + rand.nextInt(8), j, -70 + rand.nextInt(10)), "ship", rotation, true));
                this.shipCreated = true;
            }
            else if (!StructureEndCityPieces.recursiveChildren(StructureEndCityPieces.HOUSE_TOWER_GENERATOR, p_186185_1_ + 1, structureendcitypieces$citytemplate, new BlockPos(-3, j + 1, -11), p_186185_4_, rand))
            {
                return false;
            }

            structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(4, j, 0), "bridge_end", rotation.add(Rotation.CLOCKWISE_180), true));
            structureendcitypieces$citytemplate.componentType = -1;
            return true;
        }
    };
    private static final List<Tuple<Rotation, BlockPos>> FAT_TOWER_BRIDGES = Lists.<Tuple<Rotation, BlockPos>>newArrayList(new Tuple[] {new Tuple(Rotation.NONE, new BlockPos(4, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
    private static final StructureEndCityPieces.IGenerator FAT_TOWER_GENERATOR = new StructureEndCityPieces.IGenerator()
    {
        public void init()
        {
        }
        public boolean generate(int p_186185_1_, StructureEndCityPieces.CityTemplate p_186185_2_, BlockPos p_186185_3_, List<StructureComponent> p_186185_4_, Random rand)
        {
            Rotation rotation = p_186185_2_.placeSettings.getRotation();
            StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(p_186185_2_, new BlockPos(-3, 4, -3), "fat_tower_base", rotation, true));
            structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(0, 4, 0), "fat_tower_middle", rotation, true));

            for (int i = 0; i < 2 && rand.nextInt(3) != 0; ++i)
            {
                structureendcitypieces$citytemplate = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(0, 8, 0), "fat_tower_middle", rotation, true));

                for (Tuple<Rotation, BlockPos> tuple : StructureEndCityPieces.FAT_TOWER_BRIDGES)
                {
                    if (rand.nextBoolean())
                    {
                        StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate1 = StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, (BlockPos)tuple.getSecond(), "bridge_end", rotation.add((Rotation)tuple.getFirst()), true));
                        StructureEndCityPieces.recursiveChildren(StructureEndCityPieces.TOWER_BRIDGE_GENERATOR, p_186185_1_ + 1, structureendcitypieces$citytemplate1, (BlockPos)null, p_186185_4_, rand);
                    }
                }
            }

            StructureEndCityPieces.func_189935_b(p_186185_4_, StructureEndCityPieces.addPiece(structureendcitypieces$citytemplate, new BlockPos(-2, 8, -2), "fat_tower_top", rotation, true));
            return true;
        }
    };

    public static void registerPieces()
    {
        MapGenStructureIO.registerStructureComponent(StructureEndCityPieces.CityTemplate.class, "ECP");
    }

    private static StructureEndCityPieces.CityTemplate addPiece(StructureEndCityPieces.CityTemplate p_186189_0_, BlockPos p_186189_1_, String p_186189_2_, Rotation p_186189_3_, boolean p_186189_4_)
    {
        StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate = new StructureEndCityPieces.CityTemplate(p_186189_2_, p_186189_0_.templatePosition, p_186189_3_, p_186189_4_);
        BlockPos blockpos = p_186189_0_.template.calculateConnectedPos(p_186189_0_.placeSettings, p_186189_1_, structureendcitypieces$citytemplate.placeSettings, BlockPos.ORIGIN);
        structureendcitypieces$citytemplate.offset(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        return structureendcitypieces$citytemplate;
    }

    public static void beginHouseTower(BlockPos p_186190_0_, Rotation p_186190_1_, List<StructureComponent> p_186190_2_, Random p_186190_3_)
    {
        FAT_TOWER_GENERATOR.init();
        HOUSE_TOWER_GENERATOR.init();
        TOWER_BRIDGE_GENERATOR.init();
        TOWER_GENERATOR.init();
        StructureEndCityPieces.CityTemplate structureendcitypieces$citytemplate = func_189935_b(p_186190_2_, new StructureEndCityPieces.CityTemplate("base_floor", p_186190_0_, p_186190_1_, true));
        structureendcitypieces$citytemplate = func_189935_b(p_186190_2_, addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 0, -1), "second_floor", p_186190_1_, false));
        structureendcitypieces$citytemplate = func_189935_b(p_186190_2_, addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 4, -1), "third_floor", p_186190_1_, false));
        structureendcitypieces$citytemplate = func_189935_b(p_186190_2_, addPiece(structureendcitypieces$citytemplate, new BlockPos(-1, 8, -1), "third_roof", p_186190_1_, true));
        recursiveChildren(TOWER_GENERATOR, 1, structureendcitypieces$citytemplate, (BlockPos)null, p_186190_2_, p_186190_3_);
    }

    private static StructureEndCityPieces.CityTemplate func_189935_b(List<StructureComponent> p_189935_0_, StructureEndCityPieces.CityTemplate p_189935_1_)
    {
        p_189935_0_.add(p_189935_1_);
        return p_189935_1_;
    }

    private static boolean recursiveChildren(StructureEndCityPieces.IGenerator generator, int p_186187_1_, StructureEndCityPieces.CityTemplate p_186187_2_, BlockPos p_186187_3_, List<StructureComponent> p_186187_4_, Random p_186187_5_)
    {
        if (p_186187_1_ > 8)
        {
            return false;
        }
        else
        {
            List<StructureComponent> list = Lists.<StructureComponent>newArrayList();

            if (generator.generate(p_186187_1_, p_186187_2_, p_186187_3_, list, p_186187_5_))
            {
                boolean flag = false;
                int i = p_186187_5_.nextInt();

                for (StructureComponent structurecomponent : list)
                {
                    structurecomponent.componentType = i;
                    StructureComponent structurecomponent1 = StructureComponent.findIntersecting(p_186187_4_, structurecomponent.getBoundingBox());

                    if (structurecomponent1 != null && structurecomponent1.componentType != p_186187_2_.componentType)
                    {
                        flag = true;
                        break;
                    }
                }

                if (!flag)
                {
                    p_186187_4_.addAll(list);
                    return true;
                }
            }

            return false;
        }
    }

    public static class CityTemplate extends StructureComponentTemplate
        {
            private String pieceName;
            private Rotation rotation;
            private boolean overwrite;

            public CityTemplate()
            {
            }

            public CityTemplate(String pieceNameIn, BlockPos pos, Rotation rot, boolean p_i46634_4_)
            {
                super(0);
                this.pieceName = pieceNameIn;
                this.rotation = rot;
                this.overwrite = p_i46634_4_;
                this.loadAndSetup(pos);
            }

            private void loadAndSetup(BlockPos p_186180_1_)
            {
                Template template = StructureEndCityPieces.MANAGER.getTemplate((MinecraftServer)null, new ResourceLocation("endcity/" + this.pieceName));
                PlacementSettings placementsettings;

                if (this.overwrite)
                {
                    placementsettings = StructureEndCityPieces.OVERWRITE.copy().setRotation(this.rotation);
                }
                else
                {
                    placementsettings = StructureEndCityPieces.INSERT.copy().setRotation(this.rotation);
                }

                this.setup(template, p_186180_1_, placementsettings);
            }

            /**
             * (abstract) Helper method to write subclass data to NBT
             */
            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                tagCompound.setString("Template", this.pieceName);
                tagCompound.setString("Rot", this.rotation.name());
                tagCompound.setBoolean("OW", this.overwrite);
            }

            /**
             * (abstract) Helper method to read subclass data from NBT
             */
            protected void readStructureFromNBT(NBTTagCompound tagCompound)
            {
                super.readStructureFromNBT(tagCompound);
                this.pieceName = tagCompound.getString("Template");
                this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
                this.overwrite = tagCompound.getBoolean("OW");
                this.loadAndSetup(this.templatePosition);
            }

            protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, World p_186175_3_, Random p_186175_4_, StructureBoundingBox p_186175_5_)
            {
                if (p_186175_1_.startsWith("Chest"))
                {
                    BlockPos blockpos = p_186175_2_.down();

                    if (p_186175_5_.isVecInside(blockpos))
                    {
                        TileEntity tileentity = p_186175_3_.getTileEntity(blockpos);

                        if (tileentity instanceof TileEntityChest)
                        {
                            ((TileEntityChest)tileentity).setLootTable(LootTableList.CHESTS_END_CITY_TREASURE, p_186175_4_.nextLong());
                        }
                    }
                }
                else if (p_186175_1_.startsWith("Sentry"))
                {
                    EntityShulker entityshulker = new EntityShulker(p_186175_3_);
                    entityshulker.setPosition((double)p_186175_2_.getX() + 0.5D, (double)p_186175_2_.getY() + 0.5D, (double)p_186175_2_.getZ() + 0.5D);
                    entityshulker.setAttachmentPos(p_186175_2_);
                    p_186175_3_.spawnEntity(entityshulker);
                }
                else if (p_186175_1_.startsWith("Elytra"))
                {
                    EntityItemFrame entityitemframe = new EntityItemFrame(p_186175_3_, p_186175_2_, this.rotation.rotate(EnumFacing.SOUTH));
                    entityitemframe.setDisplayedItem(new ItemStack(Items.ELYTRA));
                    p_186175_3_.spawnEntity(entityitemframe);
                }
            }
        }

    interface IGenerator
    {
        void init();

        boolean generate(int p_186185_1_, StructureEndCityPieces.CityTemplate p_186185_2_, BlockPos p_186185_3_, List<StructureComponent> p_186185_4_, Random rand);
    }
}