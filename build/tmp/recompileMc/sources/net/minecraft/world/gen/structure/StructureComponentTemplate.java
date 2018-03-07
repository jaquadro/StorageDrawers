package net.minecraft.world.gen.structure;

import java.util.Map;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public abstract class StructureComponentTemplate extends StructureComponent
{
    private static final PlacementSettings DEFAULT_PLACE_SETTINGS = new PlacementSettings();
    protected Template template;
    protected PlacementSettings placeSettings;
    protected BlockPos templatePosition;

    public StructureComponentTemplate()
    {
        this.placeSettings = DEFAULT_PLACE_SETTINGS.setIgnoreEntities(true).setReplacedBlock(Blocks.AIR);
    }

    public StructureComponentTemplate(int p_i46662_1_)
    {
        super(p_i46662_1_);
        this.placeSettings = DEFAULT_PLACE_SETTINGS.setIgnoreEntities(true).setReplacedBlock(Blocks.AIR);
    }

    protected void setup(Template p_186173_1_, BlockPos p_186173_2_, PlacementSettings p_186173_3_)
    {
        this.template = p_186173_1_;
        this.setCoordBaseMode(EnumFacing.NORTH);
        this.templatePosition = p_186173_2_;
        this.placeSettings = p_186173_3_;
        this.setBoundingBoxFromTemplate();
    }

    /**
     * (abstract) Helper method to write subclass data to NBT
     */
    protected void writeStructureToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setInteger("TPX", this.templatePosition.getX());
        tagCompound.setInteger("TPY", this.templatePosition.getY());
        tagCompound.setInteger("TPZ", this.templatePosition.getZ());
    }

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void readStructureFromNBT(NBTTagCompound tagCompound)
    {
        this.templatePosition = new BlockPos(tagCompound.getInteger("TPX"), tagCompound.getInteger("TPY"), tagCompound.getInteger("TPZ"));
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
    {
        this.placeSettings.setBoundingBox(structureBoundingBoxIn);
        this.template.addBlocksToWorld(worldIn, this.templatePosition, this.placeSettings);
        Map<BlockPos, String> map = this.template.getDataBlocks(this.templatePosition, this.placeSettings);

        for (BlockPos blockpos : map.keySet())
        {
            String s = (String)map.get(blockpos);
            this.handleDataMarker(s, blockpos, worldIn, randomIn, structureBoundingBoxIn);
        }

        return true;
    }

    protected abstract void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, World p_186175_3_, Random p_186175_4_, StructureBoundingBox p_186175_5_);

    private void setBoundingBoxFromTemplate()
    {
        Rotation rotation = this.placeSettings.getRotation();
        BlockPos blockpos = this.template.transformedSize(rotation);
        this.boundingBox = new StructureBoundingBox(0, 0, 0, blockpos.getX(), blockpos.getY() - 1, blockpos.getZ());

        switch (rotation)
        {
            case NONE:
            default:
                break;
            case CLOCKWISE_90:
                this.boundingBox.offset(-blockpos.getX(), 0, 0);
                break;
            case COUNTERCLOCKWISE_90:
                this.boundingBox.offset(0, 0, -blockpos.getZ());
                break;
            case CLOCKWISE_180:
                this.boundingBox.offset(-blockpos.getX(), 0, -blockpos.getZ());
        }

        this.boundingBox.offset(this.templatePosition.getX(), this.templatePosition.getY(), this.templatePosition.getZ());
    }

    public void offset(int x, int y, int z)
    {
        super.offset(x, y, z);
        this.templatePosition = this.templatePosition.add(x, y, z);
    }
}