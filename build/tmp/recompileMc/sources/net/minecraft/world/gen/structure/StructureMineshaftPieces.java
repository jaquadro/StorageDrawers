package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class StructureMineshaftPieces
{
    public static void registerStructurePieces()
    {
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
    }

    private static StructureMineshaftPieces.Peice func_189940_a(List<StructureComponent> p_189940_0_, Random p_189940_1_, int p_189940_2_, int p_189940_3_, int p_189940_4_, @Nullable EnumFacing p_189940_5_, int p_189940_6_, MapGenMineshaft.Type p_189940_7_)
    {
        int i = p_189940_1_.nextInt(100);

        if (i >= 80)
        {
            StructureBoundingBox structureboundingbox = StructureMineshaftPieces.Cross.findCrossing(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);

            if (structureboundingbox != null)
            {
                return new StructureMineshaftPieces.Cross(p_189940_6_, p_189940_1_, structureboundingbox, p_189940_5_, p_189940_7_);
            }
        }
        else if (i >= 70)
        {
            StructureBoundingBox structureboundingbox1 = StructureMineshaftPieces.Stairs.findStairs(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);

            if (structureboundingbox1 != null)
            {
                return new StructureMineshaftPieces.Stairs(p_189940_6_, p_189940_1_, structureboundingbox1, p_189940_5_, p_189940_7_);
            }
        }
        else
        {
            StructureBoundingBox structureboundingbox2 = StructureMineshaftPieces.Corridor.findCorridorSize(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);

            if (structureboundingbox2 != null)
            {
                return new StructureMineshaftPieces.Corridor(p_189940_6_, p_189940_1_, structureboundingbox2, p_189940_5_, p_189940_7_);
            }
        }

        return null;
    }

    private static StructureMineshaftPieces.Peice func_189938_b(StructureComponent p_189938_0_, List<StructureComponent> p_189938_1_, Random p_189938_2_, int p_189938_3_, int p_189938_4_, int p_189938_5_, EnumFacing p_189938_6_, int p_189938_7_)
    {
        if (p_189938_7_ > 8)
        {
            return null;
        }
        else if (Math.abs(p_189938_3_ - p_189938_0_.getBoundingBox().minX) <= 80 && Math.abs(p_189938_5_ - p_189938_0_.getBoundingBox().minZ) <= 80)
        {
            MapGenMineshaft.Type mapgenmineshaft$type = ((StructureMineshaftPieces.Peice)p_189938_0_).mineShaftType;
            StructureMineshaftPieces.Peice structuremineshaftpieces$peice = func_189940_a(p_189938_1_, p_189938_2_, p_189938_3_, p_189938_4_, p_189938_5_, p_189938_6_, p_189938_7_ + 1, mapgenmineshaft$type);

            if (structuremineshaftpieces$peice != null)
            {
                p_189938_1_.add(structuremineshaftpieces$peice);
                structuremineshaftpieces$peice.buildComponent(p_189938_0_, p_189938_1_, p_189938_2_);
            }

            return structuremineshaftpieces$peice;
        }
        else
        {
            return null;
        }
    }

    public static class Corridor extends StructureMineshaftPieces.Peice
        {
            private boolean hasRails;
            private boolean hasSpiders;
            private boolean spawnerPlaced;
            /** A count of the different sections of this mine. The space between ceiling supports. */
            private int sectionCount;

            public Corridor()
            {
            }

            /**
             * (abstract) Helper method to write subclass data to NBT
             */
            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                tagCompound.setBoolean("hr", this.hasRails);
                tagCompound.setBoolean("sc", this.hasSpiders);
                tagCompound.setBoolean("hps", this.spawnerPlaced);
                tagCompound.setInteger("Num", this.sectionCount);
            }

            /**
             * (abstract) Helper method to read subclass data from NBT
             */
            protected void readStructureFromNBT(NBTTagCompound tagCompound)
            {
                super.readStructureFromNBT(tagCompound);
                this.hasRails = tagCompound.getBoolean("hr");
                this.hasSpiders = tagCompound.getBoolean("sc");
                this.spawnerPlaced = tagCompound.getBoolean("hps");
                this.sectionCount = tagCompound.getInteger("Num");
            }

            public Corridor(int p_i47140_1_, Random p_i47140_2_, StructureBoundingBox p_i47140_3_, EnumFacing p_i47140_4_, MapGenMineshaft.Type p_i47140_5_)
            {
                super(p_i47140_1_, p_i47140_5_);
                this.setCoordBaseMode(p_i47140_4_);
                this.boundingBox = p_i47140_3_;
                this.hasRails = p_i47140_2_.nextInt(3) == 0;
                this.hasSpiders = !this.hasRails && p_i47140_2_.nextInt(23) == 0;

                if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z)
                {
                    this.sectionCount = p_i47140_3_.getZSize() / 5;
                }
                else
                {
                    this.sectionCount = p_i47140_3_.getXSize() / 5;
                }
            }

            public static StructureBoundingBox findCorridorSize(List<StructureComponent> p_175814_0_, Random rand, int x, int y, int z, EnumFacing facing)
            {
                StructureBoundingBox structureboundingbox = new StructureBoundingBox(x, y, z, x, y + 2, z);
                int i;

                for (i = rand.nextInt(3) + 2; i > 0; --i)
                {
                    int j = i * 5;

                    switch (facing)
                    {
                        case NORTH:
                        default:
                            structureboundingbox.maxX = x + 2;
                            structureboundingbox.minZ = z - (j - 1);
                            break;
                        case SOUTH:
                            structureboundingbox.maxX = x + 2;
                            structureboundingbox.maxZ = z + (j - 1);
                            break;
                        case WEST:
                            structureboundingbox.minX = x - (j - 1);
                            structureboundingbox.maxZ = z + 2;
                            break;
                        case EAST:
                            structureboundingbox.maxX = x + (j - 1);
                            structureboundingbox.maxZ = z + 2;
                    }

                    if (StructureComponent.findIntersecting(p_175814_0_, structureboundingbox) == null)
                    {
                        break;
                    }
                }

                return i > 0 ? structureboundingbox : null;
            }

            /**
             * Initiates construction of the Structure Component picked, at the current Location of StructGen
             */
            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int i = this.getComponentType();
                int j = rand.nextInt(4);
                EnumFacing enumfacing = this.getCoordBaseMode();

                if (enumfacing != null)
                {
                    switch (enumfacing)
                    {
                        case NORTH:
                        default:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, enumfacing, i);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.WEST, i);
                            }
                            else
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.EAST, i);
                            }

                            break;
                        case SOUTH:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, enumfacing, i);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.WEST, i);
                            }
                            else
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.EAST, i);
                            }

                            break;
                        case WEST:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, enumfacing, i);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                            }
                            else
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                            }

                            break;
                        case EAST:

                            if (j <= 1)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, enumfacing, i);
                            }
                            else if (j == 2)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                            }
                            else
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                            }
                    }
                }

                if (i < 8)
                {
                    if (enumfacing != EnumFacing.NORTH && enumfacing != EnumFacing.SOUTH)
                    {
                        for (int i1 = this.boundingBox.minX + 3; i1 + 3 <= this.boundingBox.maxX; i1 += 5)
                        {
                            int j1 = rand.nextInt(5);

                            if (j1 == 0)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i + 1);
                            }
                            else if (j1 == 1)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, i1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i + 1);
                            }
                        }
                    }
                    else
                    {
                        for (int k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5)
                        {
                            int l = rand.nextInt(5);

                            if (l == 0)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, k, EnumFacing.WEST, i + 1);
                            }
                            else if (l == 1)
                            {
                                StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, k, EnumFacing.EAST, i + 1);
                            }
                        }
                    }
                }
            }

            /**
             * Adds chest to the structure and sets its contents
             */
            protected boolean generateChest(World worldIn, StructureBoundingBox structurebb, Random randomIn, int x, int y, int z, ResourceLocation loot)
            {
                BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

                if (structurebb.isVecInside(blockpos) && worldIn.getBlockState(blockpos).getMaterial() == Material.AIR && worldIn.getBlockState(blockpos.down()).getMaterial() != Material.AIR)
                {
                    IBlockState iblockstate = Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, randomIn.nextBoolean() ? BlockRailBase.EnumRailDirection.NORTH_SOUTH : BlockRailBase.EnumRailDirection.EAST_WEST);
                    this.setBlockState(worldIn, iblockstate, x, y, z, structurebb);
                    EntityMinecartChest entityminecartchest = new EntityMinecartChest(worldIn, (double)((float)blockpos.getX() + 0.5F), (double)((float)blockpos.getY() + 0.5F), (double)((float)blockpos.getZ() + 0.5F));
                    entityminecartchest.setLootTable(loot, randomIn.nextLong());
                    worldIn.spawnEntity(entityminecartchest);
                    return true;
                }
                else
                {
                    return false;
                }
            }

            /**
             * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
             * Mineshafts at the end, it adds Fences...
             */
            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                    int i = 0;
                    int j = 2;
                    int k = 0;
                    int l = 2;
                    int i1 = this.sectionCount * 5 - 1;
                    IBlockState iblockstate = this.func_189917_F_();
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, i1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                    this.func_189914_a(worldIn, structureBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, i1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false, 0);

                    if (this.hasSpiders)
                    {
                        this.func_189914_a(worldIn, structureBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, i1, Blocks.WEB.getDefaultState(), Blocks.AIR.getDefaultState(), false, 8);
                    }

                    for (int j1 = 0; j1 < this.sectionCount; ++j1)
                    {
                        int k1 = 2 + j1 * 5;
                        this.func_189921_a(worldIn, structureBoundingBoxIn, 0, 0, k1, 2, 2, randomIn);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, k1 - 1);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, k1 - 1);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, k1 + 1);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, k1 + 1);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, k1 - 2);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, k1 - 2);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, k1 + 2);
                        this.func_189922_a(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, k1 + 2);

                        if (randomIn.nextInt(100) == 0)
                        {
                            this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 2, 0, k1 - 1, LootTableList.CHESTS_ABANDONED_MINESHAFT);
                        }

                        if (randomIn.nextInt(100) == 0)
                        {
                            this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 0, 0, k1 + 1, LootTableList.CHESTS_ABANDONED_MINESHAFT);
                        }

                        if (this.hasSpiders && !this.spawnerPlaced)
                        {
                            int l1 = this.getYWithOffset(0);
                            int i2 = k1 - 1 + randomIn.nextInt(3);
                            int j2 = this.getXWithOffset(1, i2);
                            int k2 = this.getZWithOffset(1, i2);
                            BlockPos blockpos = new BlockPos(j2, l1, k2);

                            if (structureBoundingBoxIn.isVecInside(blockpos) && this.func_189916_b(worldIn, 1, 0, i2, structureBoundingBoxIn) < 8)
                            {
                                this.spawnerPlaced = true;
                                worldIn.setBlockState(blockpos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
                                TileEntity tileentity = worldIn.getTileEntity(blockpos);

                                if (tileentity instanceof TileEntityMobSpawner)
                                {
                                    ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().setEntityName("CaveSpider");
                                }
                            }
                        }
                    }

                    for (int l2 = 0; l2 <= 2; ++l2)
                    {
                        for (int i3 = 0; i3 <= i1; ++i3)
                        {
                            int k3 = -1;
                            IBlockState iblockstate3 = this.getBlockStateFromPos(worldIn, l2, -1, i3, structureBoundingBoxIn);

                            if (iblockstate3.getMaterial() == Material.AIR && this.func_189916_b(worldIn, l2, -1, i3, structureBoundingBoxIn) < 8)
                            {
                                int l3 = -1;
                                this.setBlockState(worldIn, iblockstate, l2, -1, i3, structureBoundingBoxIn);
                            }
                        }
                    }

                    if (this.hasRails)
                    {
                        IBlockState iblockstate1 = Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);

                        for (int j3 = 0; j3 <= i1; ++j3)
                        {
                            IBlockState iblockstate2 = this.getBlockStateFromPos(worldIn, 1, -1, j3, structureBoundingBoxIn);

                            if (iblockstate2.getMaterial() != Material.AIR && iblockstate2.isFullBlock())
                            {
                                float f = this.func_189916_b(worldIn, 1, 0, j3, structureBoundingBoxIn) > 8 ? 0.9F : 0.7F;
                                this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, f, 1, 0, j3, iblockstate1);
                            }
                        }
                    }

                    return true;
                }
            }

            private void func_189921_a(World p_189921_1_, StructureBoundingBox p_189921_2_, int p_189921_3_, int p_189921_4_, int p_189921_5_, int p_189921_6_, int p_189921_7_, Random p_189921_8_)
            {
                if (this.func_189918_a(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_7_, p_189921_6_, p_189921_5_))
                {
                    IBlockState iblockstate = this.func_189917_F_();
                    IBlockState iblockstate1 = this.func_189919_b();
                    IBlockState iblockstate2 = Blocks.AIR.getDefaultState();
                    this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_4_, p_189921_5_, p_189921_3_, p_189921_6_ - 1, p_189921_5_, iblockstate1, iblockstate2, false);
                    this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_4_, p_189921_5_, p_189921_7_, p_189921_6_ - 1, p_189921_5_, iblockstate1, iblockstate2, false);

                    if (p_189921_8_.nextInt(4) == 0)
                    {
                        this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_3_, p_189921_6_, p_189921_5_, iblockstate, iblockstate2, false);
                        this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, iblockstate, iblockstate2, false);
                    }
                    else
                    {
                        this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, iblockstate, iblockstate2, false);
                        this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ - 1, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
                        this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ + 1, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
                    }
                }
            }

            private void func_189922_a(World p_189922_1_, StructureBoundingBox p_189922_2_, Random p_189922_3_, float p_189922_4_, int p_189922_5_, int p_189922_6_, int p_189922_7_)
            {
                if (this.func_189916_b(p_189922_1_, p_189922_5_, p_189922_6_, p_189922_7_, p_189922_2_) < 8)
                {
                    this.randomlyPlaceBlock(p_189922_1_, p_189922_2_, p_189922_3_, p_189922_4_, p_189922_5_, p_189922_6_, p_189922_7_, Blocks.WEB.getDefaultState());
                }
            }
        }

    public static class Cross extends StructureMineshaftPieces.Peice
        {
            private EnumFacing corridorDirection;
            private boolean isMultipleFloors;

            public Cross()
            {
            }

            /**
             * (abstract) Helper method to write subclass data to NBT
             */
            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                tagCompound.setBoolean("tf", this.isMultipleFloors);
                tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
            }

            /**
             * (abstract) Helper method to read subclass data from NBT
             */
            protected void readStructureFromNBT(NBTTagCompound tagCompound)
            {
                super.readStructureFromNBT(tagCompound);
                this.isMultipleFloors = tagCompound.getBoolean("tf");
                this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
            }

            public Cross(int p_i47139_1_, Random p_i47139_2_, StructureBoundingBox p_i47139_3_, @Nullable EnumFacing p_i47139_4_, MapGenMineshaft.Type p_i47139_5_)
            {
                super(p_i47139_1_, p_i47139_5_);
                this.corridorDirection = p_i47139_4_;
                this.boundingBox = p_i47139_3_;
                this.isMultipleFloors = p_i47139_3_.getYSize() > 3;
            }

            public static StructureBoundingBox findCrossing(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing)
            {
                StructureBoundingBox structureboundingbox = new StructureBoundingBox(x, y, z, x, y + 2, z);

                if (rand.nextInt(4) == 0)
                {
                    structureboundingbox.maxY += 4;
                }

                switch (facing)
                {
                    case NORTH:
                    default:
                        structureboundingbox.minX = x - 1;
                        structureboundingbox.maxX = x + 3;
                        structureboundingbox.minZ = z - 4;
                        break;
                    case SOUTH:
                        structureboundingbox.minX = x - 1;
                        structureboundingbox.maxX = x + 3;
                        structureboundingbox.maxZ = z + 3 + 1;
                        break;
                    case WEST:
                        structureboundingbox.minX = x - 4;
                        structureboundingbox.minZ = z - 1;
                        structureboundingbox.maxZ = z + 3;
                        break;
                    case EAST:
                        structureboundingbox.maxX = x + 3 + 1;
                        structureboundingbox.minZ = z - 1;
                        structureboundingbox.maxZ = z + 3;
                }

                return StructureComponent.findIntersecting(listIn, structureboundingbox) != null ? null : structureboundingbox;
            }

            /**
             * Initiates construction of the Structure Component picked, at the current Location of StructGen
             */
            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int i = this.getComponentType();

                switch (this.corridorDirection)
                {
                    case NORTH:
                    default:
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                        break;
                    case SOUTH:
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                        break;
                    case WEST:
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                        break;
                    case EAST:
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                }

                if (this.isMultipleFloors)
                {
                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                    }

                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                    }

                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                    }

                    if (rand.nextBoolean())
                    {
                        StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                    }
                }
            }

            /**
             * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
             * Mineshafts at the end, it adds Fences...
             */
            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                    IBlockState iblockstate = this.func_189917_F_();

                    if (this.isMultipleFloors)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                    }
                    else
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                    }

                    this.func_189923_b(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
                    this.func_189923_b(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
                    this.func_189923_b(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
                    this.func_189923_b(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);

                    for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i)
                    {
                        for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j)
                        {
                            if (this.getBlockStateFromPos(worldIn, i, this.boundingBox.minY - 1, j, structureBoundingBoxIn).getMaterial() == Material.AIR && this.func_189916_b(worldIn, i, this.boundingBox.minY - 1, j, structureBoundingBoxIn) < 8)
                            {
                                this.setBlockState(worldIn, iblockstate, i, this.boundingBox.minY - 1, j, structureBoundingBoxIn);
                            }
                        }
                    }

                    return true;
                }
            }

            private void func_189923_b(World p_189923_1_, StructureBoundingBox p_189923_2_, int p_189923_3_, int p_189923_4_, int p_189923_5_, int p_189923_6_)
            {
                if (this.getBlockStateFromPos(p_189923_1_, p_189923_3_, p_189923_6_ + 1, p_189923_5_, p_189923_2_).getMaterial() != Material.AIR)
                {
                    this.fillWithBlocks(p_189923_1_, p_189923_2_, p_189923_3_, p_189923_4_, p_189923_5_, p_189923_3_, p_189923_6_, p_189923_5_, this.func_189917_F_(), Blocks.AIR.getDefaultState(), false);
                }
            }
        }

    abstract static class Peice extends StructureComponent
        {
            protected MapGenMineshaft.Type mineShaftType;

            public Peice()
            {
            }

            public Peice(int p_i47138_1_, MapGenMineshaft.Type p_i47138_2_)
            {
                super(p_i47138_1_);
                this.mineShaftType = p_i47138_2_;
            }

            /**
             * (abstract) Helper method to write subclass data to NBT
             */
            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                tagCompound.setInteger("MST", this.mineShaftType.ordinal());
            }

            /**
             * (abstract) Helper method to read subclass data from NBT
             */
            protected void readStructureFromNBT(NBTTagCompound tagCompound)
            {
                this.mineShaftType = MapGenMineshaft.Type.byId(tagCompound.getInteger("MST"));
            }

            protected IBlockState func_189917_F_()
            {
                switch (this.mineShaftType)
                {
                    case NORMAL:
                    default:
                        return Blocks.PLANKS.getDefaultState();
                    case MESA:
                        return Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK);
                }
            }

            protected IBlockState func_189919_b()
            {
                switch (this.mineShaftType)
                {
                    case NORMAL:
                    default:
                        return Blocks.OAK_FENCE.getDefaultState();
                    case MESA:
                        return Blocks.DARK_OAK_FENCE.getDefaultState();
                }
            }

            protected boolean func_189918_a(World p_189918_1_, StructureBoundingBox p_189918_2_, int p_189918_3_, int p_189918_4_, int p_189918_5_, int p_189918_6_)
            {
                for (int i = p_189918_3_; i <= p_189918_4_; ++i)
                {
                    if (this.getBlockStateFromPos(p_189918_1_, i, p_189918_5_ + 1, p_189918_6_, p_189918_2_).getMaterial() == Material.AIR)
                    {
                        return false;
                    }
                }

                return true;
            }
        }

    public static class Room extends StructureMineshaftPieces.Peice
        {
            /** List of other Mineshaft components linked to this room. */
            private final List<StructureBoundingBox> roomsLinkedToTheRoom = Lists.<StructureBoundingBox>newLinkedList();

            public Room()
            {
            }

            public Room(int p_i47137_1_, Random p_i47137_2_, int p_i47137_3_, int p_i47137_4_, MapGenMineshaft.Type p_i47137_5_)
            {
                super(p_i47137_1_, p_i47137_5_);
                this.mineShaftType = p_i47137_5_;
                this.boundingBox = new StructureBoundingBox(p_i47137_3_, 50, p_i47137_4_, p_i47137_3_ + 7 + p_i47137_2_.nextInt(6), 54 + p_i47137_2_.nextInt(6), p_i47137_4_ + 7 + p_i47137_2_.nextInt(6));
            }

            /**
             * Initiates construction of the Structure Component picked, at the current Location of StructGen
             */
            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int i = this.getComponentType();
                int k = this.boundingBox.getYSize() - 3 - 1;

                if (k <= 0)
                {
                    k = 1;
                }

                int l;

                for (int j = 0; j < this.boundingBox.getXSize(); j = l + 4)
                {
                    l = j + rand.nextInt(this.boundingBox.getXSize());

                    if (l + 3 > this.boundingBox.getXSize())
                    {
                        break;
                    }

                    StructureMineshaftPieces.Peice structuremineshaftpieces$peice = StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + l, this.boundingBox.minY + rand.nextInt(k) + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);

                    if (structuremineshaftpieces$peice != null)
                    {
                        StructureBoundingBox structureboundingbox = structuremineshaftpieces$peice.getBoundingBox();
                        this.roomsLinkedToTheRoom.add(new StructureBoundingBox(structureboundingbox.minX, structureboundingbox.minY, this.boundingBox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, this.boundingBox.minZ + 1));
                    }
                }

                for (int i1 = 0; i1 < this.boundingBox.getXSize(); i1 = l + 4)
                {
                    l = i1 + rand.nextInt(this.boundingBox.getXSize());

                    if (l + 3 > this.boundingBox.getXSize())
                    {
                        break;
                    }

                    StructureMineshaftPieces.Peice structuremineshaftpieces$peice1 = StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX + l, this.boundingBox.minY + rand.nextInt(k) + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);

                    if (structuremineshaftpieces$peice1 != null)
                    {
                        StructureBoundingBox structureboundingbox1 = structuremineshaftpieces$peice1.getBoundingBox();
                        this.roomsLinkedToTheRoom.add(new StructureBoundingBox(structureboundingbox1.minX, structureboundingbox1.minY, this.boundingBox.maxZ - 1, structureboundingbox1.maxX, structureboundingbox1.maxY, this.boundingBox.maxZ));
                    }
                }

                for (int j1 = 0; j1 < this.boundingBox.getZSize(); j1 = l + 4)
                {
                    l = j1 + rand.nextInt(this.boundingBox.getZSize());

                    if (l + 3 > this.boundingBox.getZSize())
                    {
                        break;
                    }

                    StructureMineshaftPieces.Peice structuremineshaftpieces$peice2 = StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(k) + 1, this.boundingBox.minZ + l, EnumFacing.WEST, i);

                    if (structuremineshaftpieces$peice2 != null)
                    {
                        StructureBoundingBox structureboundingbox2 = structuremineshaftpieces$peice2.getBoundingBox();
                        this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.minX, structureboundingbox2.minY, structureboundingbox2.minZ, this.boundingBox.minX + 1, structureboundingbox2.maxY, structureboundingbox2.maxZ));
                    }
                }

                for (int k1 = 0; k1 < this.boundingBox.getZSize(); k1 = l + 4)
                {
                    l = k1 + rand.nextInt(this.boundingBox.getZSize());

                    if (l + 3 > this.boundingBox.getZSize())
                    {
                        break;
                    }

                    StructureComponent structurecomponent = StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(k) + 1, this.boundingBox.minZ + l, EnumFacing.EAST, i);

                    if (structurecomponent != null)
                    {
                        StructureBoundingBox structureboundingbox3 = structurecomponent.getBoundingBox();
                        this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.maxX - 1, structureboundingbox3.minY, structureboundingbox3.minZ, this.boundingBox.maxX, structureboundingbox3.maxY, structureboundingbox3.maxZ));
                    }
                }
            }

            /**
             * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
             * Mineshafts at the end, it adds Fences...
             */
            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.DIRT.getDefaultState(), Blocks.AIR.getDefaultState(), true);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

                    for (StructureBoundingBox structureboundingbox : this.roomsLinkedToTheRoom)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, structureboundingbox.minX, structureboundingbox.maxY - 2, structureboundingbox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, structureboundingbox.maxZ, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                    }

                    this.randomlyRareFillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.AIR.getDefaultState(), false);
                    return true;
                }
            }

            public void offset(int x, int y, int z)
            {
                super.offset(x, y, z);

                for (StructureBoundingBox structureboundingbox : this.roomsLinkedToTheRoom)
                {
                    structureboundingbox.offset(x, y, z);
                }
            }

            /**
             * (abstract) Helper method to write subclass data to NBT
             */
            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                NBTTagList nbttaglist = new NBTTagList();

                for (StructureBoundingBox structureboundingbox : this.roomsLinkedToTheRoom)
                {
                    nbttaglist.appendTag(structureboundingbox.toNBTTagIntArray());
                }

                tagCompound.setTag("Entrances", nbttaglist);
            }

            /**
             * (abstract) Helper method to read subclass data from NBT
             */
            protected void readStructureFromNBT(NBTTagCompound tagCompound)
            {
                super.readStructureFromNBT(tagCompound);
                NBTTagList nbttaglist = tagCompound.getTagList("Entrances", 11);

                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(nbttaglist.getIntArrayAt(i)));
                }
            }
        }

    public static class Stairs extends StructureMineshaftPieces.Peice
        {
            public Stairs()
            {
            }

            public Stairs(int p_i47136_1_, Random p_i47136_2_, StructureBoundingBox p_i47136_3_, EnumFacing p_i47136_4_, MapGenMineshaft.Type p_i47136_5_)
            {
                super(p_i47136_1_, p_i47136_5_);
                this.setCoordBaseMode(p_i47136_4_);
                this.boundingBox = p_i47136_3_;
            }

            /**
             * (abstract) Helper method to write subclass data to NBT
             */
            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
            }

            /**
             * (abstract) Helper method to read subclass data from NBT
             */
            protected void readStructureFromNBT(NBTTagCompound tagCompound)
            {
                super.readStructureFromNBT(tagCompound);
            }

            public static StructureBoundingBox findStairs(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing)
            {
                StructureBoundingBox structureboundingbox = new StructureBoundingBox(x, y - 5, z, x, y + 2, z);

                switch (facing)
                {
                    case NORTH:
                    default:
                        structureboundingbox.maxX = x + 2;
                        structureboundingbox.minZ = z - 8;
                        break;
                    case SOUTH:
                        structureboundingbox.maxX = x + 2;
                        structureboundingbox.maxZ = z + 8;
                        break;
                    case WEST:
                        structureboundingbox.minX = x - 8;
                        structureboundingbox.maxZ = z + 2;
                        break;
                    case EAST:
                        structureboundingbox.maxX = x + 8;
                        structureboundingbox.maxZ = z + 2;
                }

                return StructureComponent.findIntersecting(listIn, structureboundingbox) != null ? null : structureboundingbox;
            }

            /**
             * Initiates construction of the Structure Component picked, at the current Location of StructGen
             */
            public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
            {
                int i = this.getComponentType();
                EnumFacing enumfacing = this.getCoordBaseMode();

                if (enumfacing != null)
                {
                    switch (enumfacing)
                    {
                        case NORTH:
                        default:
                            StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                            break;
                        case SOUTH:
                            StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                            break;
                        case WEST:
                            StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, i);
                            break;
                        case EAST:
                            StructureMineshaftPieces.func_189938_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, i);
                    }
                }
            }

            /**
             * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
             * Mineshafts at the end, it adds Fences...
             */
            public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
            {
                if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
                {
                    return false;
                }
                else
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

                    for (int i = 0; i < 5; ++i)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                    }

                    return true;
                }
            }
        }
}