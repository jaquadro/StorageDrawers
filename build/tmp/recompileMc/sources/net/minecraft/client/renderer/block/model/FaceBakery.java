package net.minecraft.client.renderer.block.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

@SideOnly(Side.CLIENT)
public class FaceBakery
{
    private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
    private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((Math.PI / 4D)) - 1.0F;
    private static final FaceBakery.Rotation[] UV_ROTATIONS = new FaceBakery.Rotation[ModelRotation.values().length * EnumFacing.values().length];
    private static final FaceBakery.Rotation UV_ROTATION_0 = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_)
        {
            return new BlockFaceUV(new float[] {p_188007_1_, p_188007_2_, p_188007_3_, p_188007_4_}, 0);
        }
    };
    private static final FaceBakery.Rotation UV_ROTATION_270 = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_)
        {
            return new BlockFaceUV(new float[] {p_188007_4_, 16.0F - p_188007_1_, p_188007_2_, 16.0F - p_188007_3_}, 270);
        }
    };
    private static final FaceBakery.Rotation UV_ROTATION_INVERSE = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_)
        {
            return new BlockFaceUV(new float[] {16.0F - p_188007_1_, 16.0F - p_188007_2_, 16.0F - p_188007_3_, 16.0F - p_188007_4_}, 0);
        }
    };
    private static final FaceBakery.Rotation UV_ROTATION_90 = new FaceBakery.Rotation()
    {
        BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_)
        {
            return new BlockFaceUV(new float[] {16.0F - p_188007_2_, p_188007_3_, 16.0F - p_188007_4_, p_188007_1_}, 90);
        }
    };

    public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ModelRotation modelRotationIn, @Nullable BlockPartRotation partRotation, boolean uvLocked, boolean shade)
    {
        return makeBakedQuad(posFrom, posTo, face, sprite, facing, (net.minecraftforge.common.model.ITransformation)modelRotationIn, partRotation, uvLocked, shade);
    }

    public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, net.minecraftforge.common.model.ITransformation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade)
    {
        BlockFaceUV blockfaceuv = face.blockFaceUV;

        if (uvLocked)
        {
            blockfaceuv = net.minecraftforge.client.ForgeHooksClient.applyUVLock(face.blockFaceUV, facing, modelRotationIn);
        }

        int[] aint = this.makeQuadVertexData(blockfaceuv, sprite, facing, this.getPositionsDiv16(posFrom, posTo), modelRotationIn, partRotation, false);
        EnumFacing enumfacing = getFacingFromVertexData(aint);

        if (partRotation == null)
        {
            this.applyFacing(aint, enumfacing);
        }

        net.minecraftforge.client.ForgeHooksClient.fillNormal(aint, enumfacing);
        return new BakedQuad(aint, face.tintIndex, enumfacing, sprite, shade, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
    }

    private BlockFaceUV applyUVLock(BlockFaceUV p_188010_1_, EnumFacing p_188010_2_, ModelRotation p_188010_3_)
    {
        return UV_ROTATIONS[getIndex(p_188010_3_, p_188010_2_)].rotateUV(p_188010_1_);
    }

    private int[] makeQuadVertexData(BlockFaceUV uvs, TextureAtlasSprite sprite, EnumFacing orientation, float[] p_188012_4_, ModelRotation rotationIn, @Nullable BlockPartRotation partRotation, boolean shade)
    {
        return makeQuadVertexData(uvs, sprite, orientation, p_188012_4_, (net.minecraftforge.common.model.ITransformation)rotationIn, partRotation, shade);
    }

    private int[] makeQuadVertexData(BlockFaceUV uvs, TextureAtlasSprite sprite, EnumFacing orientation, float[] p_188012_4_, net.minecraftforge.common.model.ITransformation rotationIn, BlockPartRotation partRotation, boolean shade)
    {
        int[] aint = new int[28];

        for (int i = 0; i < 4; ++i)
        {
            this.fillVertexData(aint, i, orientation, uvs, p_188012_4_, sprite, rotationIn, partRotation, shade);
        }

        return aint;
    }

    private int getFaceShadeColor(EnumFacing facing)
    {
        float f = this.getFaceBrightness(facing);
        int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
        return -16777216 | i << 16 | i << 8 | i;
    }

    private float getFaceBrightness(EnumFacing facing)
    {
        switch (facing)
        {
            case DOWN:
                return 0.5F;
            case UP:
                return 1.0F;
            case NORTH:
            case SOUTH:
                return 0.8F;
            case WEST:
            case EAST:
                return 0.6F;
            default:
                return 1.0F;
        }
    }

    private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2)
    {
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0F;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0F;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0F;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0F;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0F;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0F;
        return afloat;
    }

    private void fillVertexData(int[] p_188015_1_, int p_188015_2_, EnumFacing p_188015_3_, BlockFaceUV p_188015_4_, float[] p_188015_5_, TextureAtlasSprite p_188015_6_, ModelRotation p_188015_7_, @Nullable BlockPartRotation p_188015_8_, boolean p_188015_9_)
    {
        fillVertexData(p_188015_1_, p_188015_2_, p_188015_3_, p_188015_4_, p_188015_5_, p_188015_6_, (net.minecraftforge.common.model.ITransformation)p_188015_7_, p_188015_8_, p_188015_9_);
    }

    private void fillVertexData(int[] p_188015_1_, int p_188015_2_, EnumFacing p_188015_3_, BlockFaceUV p_188015_4_, float[] p_188015_5_, TextureAtlasSprite p_188015_6_, net.minecraftforge.common.model.ITransformation p_188015_7_, BlockPartRotation p_188015_8_, boolean p_188015_9_)
    {
        EnumFacing enumfacing = p_188015_7_.rotate(p_188015_3_);
        int i = p_188015_9_ ? this.getFaceShadeColor(enumfacing) : -1;
        EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = EnumFaceDirection.getFacing(p_188015_3_).getVertexInformation(p_188015_2_);
        Vector3f vector3f = new Vector3f(p_188015_5_[enumfacedirection$vertexinformation.xIndex], p_188015_5_[enumfacedirection$vertexinformation.yIndex], p_188015_5_[enumfacedirection$vertexinformation.zIndex]);
        this.rotatePart(vector3f, p_188015_8_);
        int j = this.rotateVertex(vector3f, p_188015_3_, p_188015_2_, p_188015_7_);
        this.storeVertexData(p_188015_1_, j, p_188015_2_, vector3f, i, p_188015_6_, p_188015_4_);
    }

    private void storeVertexData(int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV)
    {
        int i = storeIndex * 7;
        faceData[i] = Float.floatToRawIntBits(position.x);
        faceData[i + 1] = Float.floatToRawIntBits(position.y);
        faceData[i + 2] = Float.floatToRawIntBits(position.z);
        faceData[i + 3] = shadeColor;
        faceData[i + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.getVertexU(vertexIndex) * .999 + faceUV.getVertexU((vertexIndex + 2) % 4) * .001));
        faceData[i + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.getVertexV(vertexIndex) * .999 + faceUV.getVertexV((vertexIndex + 2) % 4) * .001));
    }

    private void rotatePart(Vector3f p_178407_1_, @Nullable BlockPartRotation partRotation)
    {
        if (partRotation != null)
        {
            Matrix4f matrix4f = this.getMatrixIdentity();
            Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);

            switch (partRotation.axis)
            {
                case X:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
                    vector3f.set(0.0F, 1.0F, 1.0F);
                    break;
                case Y:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
                    vector3f.set(1.0F, 0.0F, 1.0F);
                    break;
                case Z:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
                    vector3f.set(1.0F, 1.0F, 0.0F);
            }

            if (partRotation.rescale)
            {
                if (Math.abs(partRotation.angle) == 22.5F)
                {
                    vector3f.scale(SCALE_ROTATION_22_5);
                }
                else
                {
                    vector3f.scale(SCALE_ROTATION_GENERAL);
                }

                Vector3f.add(vector3f, new Vector3f(1.0F, 1.0F, 1.0F), vector3f);
            }
            else
            {
                vector3f.set(1.0F, 1.0F, 1.0F);
            }

            this.rotateScale(p_178407_1_, new Vector3f(partRotation.origin), matrix4f, vector3f);
        }
    }

    public int rotateVertex(Vector3f p_188011_1_, EnumFacing p_188011_2_, int p_188011_3_, ModelRotation p_188011_4_)
    {
        return rotateVertex(p_188011_1_, p_188011_2_, p_188011_3_, (net.minecraftforge.common.model.ITransformation)p_188011_4_);
    }

    public int rotateVertex(Vector3f p_188011_1_, EnumFacing p_188011_2_, int p_188011_3_, net.minecraftforge.common.model.ITransformation p_188011_4_)
    {
        if (p_188011_4_ == ModelRotation.X0_Y0)
        {
            return p_188011_3_;
        }
        else
        {
            net.minecraftforge.client.ForgeHooksClient.transform(p_188011_1_, p_188011_4_.getMatrix());
            return p_188011_4_.rotate(p_188011_2_, p_188011_3_);
        }
    }

    private void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale)
    {
        Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
        Matrix4f.transform(rotationMatrix, vector4f, vector4f);
        vector4f.x *= scale.x;
        vector4f.y *= scale.y;
        vector4f.z *= scale.z;
        position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
    }

    private Matrix4f getMatrixIdentity()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        return matrix4f;
    }

    public static EnumFacing getFacingFromVertexData(int[] faceData)
    {
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
        Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(faceData[7]), Float.intBitsToFloat(faceData[8]), Float.intBitsToFloat(faceData[9]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[14]), Float.intBitsToFloat(faceData[15]), Float.intBitsToFloat(faceData[16]));
        Vector3f vector3f3 = new Vector3f();
        Vector3f vector3f4 = new Vector3f();
        Vector3f vector3f5 = new Vector3f();
        Vector3f.sub(vector3f, vector3f1, vector3f3);
        Vector3f.sub(vector3f2, vector3f1, vector3f4);
        Vector3f.cross(vector3f4, vector3f3, vector3f5);
        float f = (float)Math.sqrt((double)(vector3f5.x * vector3f5.x + vector3f5.y * vector3f5.y + vector3f5.z * vector3f5.z));
        vector3f5.x /= f;
        vector3f5.y /= f;
        vector3f5.z /= f;
        EnumFacing enumfacing = null;
        float f1 = 0.0F;

        for (EnumFacing enumfacing1 : EnumFacing.values())
        {
            Vec3i vec3i = enumfacing1.getDirectionVec();
            Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            float f2 = Vector3f.dot(vector3f5, vector3f6);

            if (f2 >= 0.0F && f2 > f1)
            {
                f1 = f2;
                enumfacing = enumfacing1;
            }
        }

        if (enumfacing == null)
        {
            return EnumFacing.UP;
        }
        else
        {
            return enumfacing;
        }
    }

    private void applyFacing(int[] p_178408_1_, EnumFacing p_178408_2_)
    {
        int[] aint = new int[p_178408_1_.length];
        System.arraycopy(p_178408_1_, 0, aint, 0, p_178408_1_.length);
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;

        for (int i = 0; i < 4; ++i)
        {
            int j = 7 * i;
            float f = Float.intBitsToFloat(aint[j]);
            float f1 = Float.intBitsToFloat(aint[j + 1]);
            float f2 = Float.intBitsToFloat(aint[j + 2]);

            if (f < afloat[EnumFaceDirection.Constants.WEST_INDEX])
            {
                afloat[EnumFaceDirection.Constants.WEST_INDEX] = f;
            }

            if (f1 < afloat[EnumFaceDirection.Constants.DOWN_INDEX])
            {
                afloat[EnumFaceDirection.Constants.DOWN_INDEX] = f1;
            }

            if (f2 < afloat[EnumFaceDirection.Constants.NORTH_INDEX])
            {
                afloat[EnumFaceDirection.Constants.NORTH_INDEX] = f2;
            }

            if (f > afloat[EnumFaceDirection.Constants.EAST_INDEX])
            {
                afloat[EnumFaceDirection.Constants.EAST_INDEX] = f;
            }

            if (f1 > afloat[EnumFaceDirection.Constants.UP_INDEX])
            {
                afloat[EnumFaceDirection.Constants.UP_INDEX] = f1;
            }

            if (f2 > afloat[EnumFaceDirection.Constants.SOUTH_INDEX])
            {
                afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = f2;
            }
        }

        EnumFaceDirection enumfacedirection = EnumFaceDirection.getFacing(p_178408_2_);

        for (int i1 = 0; i1 < 4; ++i1)
        {
            int j1 = 7 * i1;
            EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = enumfacedirection.getVertexInformation(i1);
            float f8 = afloat[enumfacedirection$vertexinformation.xIndex];
            float f3 = afloat[enumfacedirection$vertexinformation.yIndex];
            float f4 = afloat[enumfacedirection$vertexinformation.zIndex];
            p_178408_1_[j1] = Float.floatToRawIntBits(f8);
            p_178408_1_[j1 + 1] = Float.floatToRawIntBits(f3);
            p_178408_1_[j1 + 2] = Float.floatToRawIntBits(f4);

            for (int k = 0; k < 4; ++k)
            {
                int l = 7 * k;
                float f5 = Float.intBitsToFloat(aint[l]);
                float f6 = Float.intBitsToFloat(aint[l + 1]);
                float f7 = Float.intBitsToFloat(aint[l + 2]);

                if (MathHelper.epsilonEquals(f8, f5) && MathHelper.epsilonEquals(f3, f6) && MathHelper.epsilonEquals(f4, f7))
                {
                    p_178408_1_[j1 + 4] = aint[l + 4];
                    p_178408_1_[j1 + 4 + 1] = aint[l + 4 + 1];
                }
            }
        }
    }

    private static void addUvRotation(ModelRotation p_188013_0_, EnumFacing p_188013_1_, FaceBakery.Rotation p_188013_2_)
    {
        UV_ROTATIONS[getIndex(p_188013_0_, p_188013_1_)] = p_188013_2_;
    }

    private static int getIndex(ModelRotation p_188014_0_, EnumFacing p_188014_1_)
    {
        return ModelRotation.values().length * p_188014_1_.ordinal() + p_188014_0_.ordinal();
    }

    static
    {
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y0, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.EAST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.WEST, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.DOWN, UV_ROTATION_0);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.SOUTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.NORTH, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.UP, UV_ROTATION_0);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.UP, UV_ROTATION_270);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.DOWN, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.NORTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.SOUTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.WEST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.UP, UV_ROTATION_270);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.DOWN, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.NORTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.SOUTH, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.EAST, UV_ROTATION_270);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X0_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y0, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.UP, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y180, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.EAST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.WEST, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.NORTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.SOUTH, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.DOWN, UV_ROTATION_INVERSE);
        addUvRotation(ModelRotation.X0_Y90, EnumFacing.UP, UV_ROTATION_90);
        addUvRotation(ModelRotation.X0_Y270, EnumFacing.DOWN, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y0, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.NORTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y90, EnumFacing.SOUTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y180, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X90_Y270, EnumFacing.EAST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y0, EnumFacing.WEST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X180_Y90, EnumFacing.DOWN, UV_ROTATION_90);
        addUvRotation(ModelRotation.X180_Y270, EnumFacing.UP, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y90, EnumFacing.WEST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y180, EnumFacing.WEST, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.NORTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.SOUTH, UV_ROTATION_90);
        addUvRotation(ModelRotation.X270_Y270, EnumFacing.WEST, UV_ROTATION_90);
    }

    @SideOnly(Side.CLIENT)
    abstract static class Rotation
        {
            private Rotation()
            {
            }

            public BlockFaceUV rotateUV(BlockFaceUV p_188006_1_)
            {
                float f = p_188006_1_.getVertexU(p_188006_1_.getVertexRotatedRev(0));
                float f1 = p_188006_1_.getVertexV(p_188006_1_.getVertexRotatedRev(0));
                float f2 = p_188006_1_.getVertexU(p_188006_1_.getVertexRotatedRev(2));
                float f3 = p_188006_1_.getVertexV(p_188006_1_.getVertexRotatedRev(2));
                return this.makeRotatedUV(f, f1, f2, f3);
            }

            abstract BlockFaceUV makeRotatedUV(float p_188007_1_, float p_188007_2_, float p_188007_3_, float p_188007_4_);
        }
}