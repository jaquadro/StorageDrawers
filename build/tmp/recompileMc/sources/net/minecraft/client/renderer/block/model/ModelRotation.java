package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

@SideOnly(Side.CLIENT)
public enum ModelRotation implements net.minecraftforge.common.model.IModelState, net.minecraftforge.common.model.ITransformation
{
    X0_Y0(0, 0),
    X0_Y90(0, 90),
    X0_Y180(0, 180),
    X0_Y270(0, 270),
    X90_Y0(90, 0),
    X90_Y90(90, 90),
    X90_Y180(90, 180),
    X90_Y270(90, 270),
    X180_Y0(180, 0),
    X180_Y90(180, 90),
    X180_Y180(180, 180),
    X180_Y270(180, 270),
    X270_Y0(270, 0),
    X270_Y90(270, 90),
    X270_Y180(270, 180),
    X270_Y270(270, 270);

    private static final Map<Integer, ModelRotation> MAP_ROTATIONS = Maps.<Integer, ModelRotation>newHashMap();
    private final int combinedXY;
    private final Matrix4f matrix4d;
    private final int quartersX;
    private final int quartersY;

    private static int combineXY(int p_177521_0_, int p_177521_1_)
    {
        return p_177521_0_ * 360 + p_177521_1_;
    }

    private ModelRotation(int x, int y)
    {
        this.combinedXY = combineXY(x, y);
        this.matrix4d = new Matrix4f();
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        Matrix4f.rotate((float)(-x) * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
        this.quartersX = MathHelper.abs(x / 90);
        Matrix4f matrix4f1 = new Matrix4f();
        matrix4f1.setIdentity();
        Matrix4f.rotate((float)(-y) * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), matrix4f1, matrix4f1);
        this.quartersY = MathHelper.abs(y / 90);
        Matrix4f.mul(matrix4f1, matrix4f, this.matrix4d);
    }

    public Matrix4f getMatrix4d()
    {
        return this.matrix4d;
    }

    public EnumFacing rotateFace(EnumFacing facing)
    {
        EnumFacing enumfacing = facing;

        for (int i = 0; i < this.quartersX; ++i)
        {
            enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
        }

        if (enumfacing.getAxis() != EnumFacing.Axis.Y)
        {
            for (int j = 0; j < this.quartersY; ++j)
            {
                enumfacing = enumfacing.rotateAround(EnumFacing.Axis.Y);
            }
        }

        return enumfacing;
    }

    public int rotateVertex(EnumFacing facing, int vertexIndex)
    {
        int i = vertexIndex;

        if (facing.getAxis() == EnumFacing.Axis.X)
        {
            i = (vertexIndex + this.quartersX) % 4;
        }

        EnumFacing enumfacing = facing;

        for (int j = 0; j < this.quartersX; ++j)
        {
            enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
        }

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            i = (i + this.quartersY) % 4;
        }

        return i;
    }

    public static ModelRotation getModelRotation(int x, int y)
    {
        return (ModelRotation)MAP_ROTATIONS.get(Integer.valueOf(combineXY(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360))));
    }

    static
    {
        for (ModelRotation modelrotation : values())
        {
            MAP_ROTATIONS.put(Integer.valueOf(modelrotation.combinedXY), modelrotation);
        }
    }

    public com.google.common.base.Optional<net.minecraftforge.common.model.TRSRTransformation> apply(com.google.common.base.Optional<? extends net.minecraftforge.common.model.IModelPart> part) { return net.minecraftforge.client.ForgeHooksClient.applyTransform(getMatrix(), part); }
    public javax.vecmath.Matrix4f getMatrix() { return net.minecraftforge.client.ForgeHooksClient.getMatrix(this); }
    public EnumFacing rotate(EnumFacing facing) { return rotateFace(facing); }
    public int rotate(EnumFacing facing, int vertexIndex) { return rotateVertex(facing, vertexIndex); }
}