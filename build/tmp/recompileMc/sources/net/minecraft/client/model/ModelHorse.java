package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHorse extends ModelBase
{
    private final ModelRenderer head;
    private final ModelRenderer upperMouth;
    private final ModelRenderer lowerMouth;
    private final ModelRenderer horseLeftEar;
    private final ModelRenderer horseRightEar;
    /** The left ear box for the mule model. */
    private final ModelRenderer muleLeftEar;
    /** The right ear box for the mule model. */
    private final ModelRenderer muleRightEar;
    private final ModelRenderer neck;
    /** The box for the horse's ropes on its face. */
    private final ModelRenderer horseFaceRopes;
    private final ModelRenderer mane;
    private final ModelRenderer body;
    private final ModelRenderer tailBase;
    private final ModelRenderer tailMiddle;
    private final ModelRenderer tailTip;
    private final ModelRenderer backLeftLeg;
    private final ModelRenderer backLeftShin;
    private final ModelRenderer backLeftHoof;
    private final ModelRenderer backRightLeg;
    private final ModelRenderer backRightShin;
    private final ModelRenderer backRightHoof;
    private final ModelRenderer frontLeftLeg;
    private final ModelRenderer frontLeftShin;
    private final ModelRenderer frontLeftHoof;
    private final ModelRenderer frontRightLeg;
    private final ModelRenderer frontRightShin;
    private final ModelRenderer frontRightHoof;
    /** The left chest box on the mule model. */
    private final ModelRenderer muleLeftChest;
    /** The right chest box on the mule model. */
    private final ModelRenderer muleRightChest;
    private final ModelRenderer horseSaddleBottom;
    private final ModelRenderer horseSaddleFront;
    private final ModelRenderer horseSaddleBack;
    private final ModelRenderer horseLeftSaddleRope;
    private final ModelRenderer horseLeftSaddleMetal;
    private final ModelRenderer horseRightSaddleRope;
    private final ModelRenderer horseRightSaddleMetal;
    /** The left metal connected to the horse's face ropes. */
    private final ModelRenderer horseLeftFaceMetal;
    /** The right metal connected to the horse's face ropes. */
    private final ModelRenderer horseRightFaceMetal;
    private final ModelRenderer horseLeftRein;
    private final ModelRenderer horseRightRein;

    public ModelHorse()
    {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.body = new ModelRenderer(this, 0, 34);
        this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
        this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
        this.tailBase = new ModelRenderer(this, 44, 0);
        this.tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
        this.tailBase.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.setBoxRotation(this.tailBase, -1.134464F, 0.0F, 0.0F);
        this.tailMiddle = new ModelRenderer(this, 38, 7);
        this.tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
        this.tailMiddle.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.setBoxRotation(this.tailMiddle, -1.134464F, 0.0F, 0.0F);
        this.tailTip = new ModelRenderer(this, 24, 3);
        this.tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
        this.tailTip.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.setBoxRotation(this.tailTip, -1.40215F, 0.0F, 0.0F);
        this.backLeftLeg = new ModelRenderer(this, 78, 29);
        this.backLeftLeg.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5);
        this.backLeftLeg.setRotationPoint(4.0F, 9.0F, 11.0F);
        this.backLeftShin = new ModelRenderer(this, 78, 43);
        this.backLeftShin.addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3);
        this.backLeftShin.setRotationPoint(4.0F, 16.0F, 11.0F);
        this.backLeftHoof = new ModelRenderer(this, 78, 51);
        this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4);
        this.backLeftHoof.setRotationPoint(4.0F, 16.0F, 11.0F);
        this.backRightLeg = new ModelRenderer(this, 96, 29);
        this.backRightLeg.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5);
        this.backRightLeg.setRotationPoint(-4.0F, 9.0F, 11.0F);
        this.backRightShin = new ModelRenderer(this, 96, 43);
        this.backRightShin.addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3);
        this.backRightShin.setRotationPoint(-4.0F, 16.0F, 11.0F);
        this.backRightHoof = new ModelRenderer(this, 96, 51);
        this.backRightHoof.addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4);
        this.backRightHoof.setRotationPoint(-4.0F, 16.0F, 11.0F);
        this.frontLeftLeg = new ModelRenderer(this, 44, 29);
        this.frontLeftLeg.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4);
        this.frontLeftLeg.setRotationPoint(4.0F, 9.0F, -8.0F);
        this.frontLeftShin = new ModelRenderer(this, 44, 41);
        this.frontLeftShin.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3);
        this.frontLeftShin.setRotationPoint(4.0F, 16.0F, -8.0F);
        this.frontLeftHoof = new ModelRenderer(this, 44, 51);
        this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4);
        this.frontLeftHoof.setRotationPoint(4.0F, 16.0F, -8.0F);
        this.frontRightLeg = new ModelRenderer(this, 60, 29);
        this.frontRightLeg.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4);
        this.frontRightLeg.setRotationPoint(-4.0F, 9.0F, -8.0F);
        this.frontRightShin = new ModelRenderer(this, 60, 41);
        this.frontRightShin.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3);
        this.frontRightShin.setRotationPoint(-4.0F, 16.0F, -8.0F);
        this.frontRightHoof = new ModelRenderer(this, 60, 51);
        this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4);
        this.frontRightHoof.setRotationPoint(-4.0F, 16.0F, -8.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
        this.head.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.head, 0.5235988F, 0.0F, 0.0F);
        this.upperMouth = new ModelRenderer(this, 24, 18);
        this.upperMouth.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6);
        this.upperMouth.setRotationPoint(0.0F, 3.95F, -10.0F);
        this.setBoxRotation(this.upperMouth, 0.5235988F, 0.0F, 0.0F);
        this.lowerMouth = new ModelRenderer(this, 24, 27);
        this.lowerMouth.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5);
        this.lowerMouth.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.lowerMouth, 0.5235988F, 0.0F, 0.0F);
        this.head.addChild(this.upperMouth);
        this.head.addChild(this.lowerMouth);
        this.horseLeftEar = new ModelRenderer(this, 0, 0);
        this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
        this.horseLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.horseLeftEar, 0.5235988F, 0.0F, 0.0F);
        this.horseRightEar = new ModelRenderer(this, 0, 0);
        this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
        this.horseRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.horseRightEar, 0.5235988F, 0.0F, 0.0F);
        this.muleLeftEar = new ModelRenderer(this, 0, 12);
        this.muleLeftEar.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1);
        this.muleLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.muleLeftEar, 0.5235988F, 0.0F, 0.2617994F);
        this.muleRightEar = new ModelRenderer(this, 0, 12);
        this.muleRightEar.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1);
        this.muleRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.muleRightEar, 0.5235988F, 0.0F, -0.2617994F);
        this.neck = new ModelRenderer(this, 0, 12);
        this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
        this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.neck, 0.5235988F, 0.0F, 0.0F);
        this.muleLeftChest = new ModelRenderer(this, 0, 34);
        this.muleLeftChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.muleLeftChest.setRotationPoint(-7.5F, 3.0F, 10.0F);
        this.setBoxRotation(this.muleLeftChest, 0.0F, ((float)Math.PI / 2F), 0.0F);
        this.muleRightChest = new ModelRenderer(this, 0, 47);
        this.muleRightChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
        this.muleRightChest.setRotationPoint(4.5F, 3.0F, 10.0F);
        this.setBoxRotation(this.muleRightChest, 0.0F, ((float)Math.PI / 2F), 0.0F);
        this.horseSaddleBottom = new ModelRenderer(this, 80, 0);
        this.horseSaddleBottom.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8);
        this.horseSaddleBottom.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.horseSaddleFront = new ModelRenderer(this, 106, 9);
        this.horseSaddleFront.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2);
        this.horseSaddleFront.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.horseSaddleBack = new ModelRenderer(this, 80, 9);
        this.horseSaddleBack.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2);
        this.horseSaddleBack.setRotationPoint(0.0F, 2.0F, 2.0F);
        this.horseLeftSaddleMetal = new ModelRenderer(this, 74, 0);
        this.horseLeftSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
        this.horseLeftSaddleMetal.setRotationPoint(5.0F, 3.0F, 2.0F);
        this.horseLeftSaddleRope = new ModelRenderer(this, 70, 0);
        this.horseLeftSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
        this.horseLeftSaddleRope.setRotationPoint(5.0F, 3.0F, 2.0F);
        this.horseRightSaddleMetal = new ModelRenderer(this, 74, 4);
        this.horseRightSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
        this.horseRightSaddleMetal.setRotationPoint(-5.0F, 3.0F, 2.0F);
        this.horseRightSaddleRope = new ModelRenderer(this, 80, 0);
        this.horseRightSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
        this.horseRightSaddleRope.setRotationPoint(-5.0F, 3.0F, 2.0F);
        this.horseLeftFaceMetal = new ModelRenderer(this, 74, 13);
        this.horseLeftFaceMetal.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2);
        this.horseLeftFaceMetal.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.horseLeftFaceMetal, 0.5235988F, 0.0F, 0.0F);
        this.horseRightFaceMetal = new ModelRenderer(this, 74, 13);
        this.horseRightFaceMetal.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2);
        this.horseRightFaceMetal.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.horseRightFaceMetal, 0.5235988F, 0.0F, 0.0F);
        this.horseLeftRein = new ModelRenderer(this, 44, 10);
        this.horseLeftRein.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.horseLeftRein.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.horseRightRein = new ModelRenderer(this, 44, 5);
        this.horseRightRein.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.horseRightRein.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.mane = new ModelRenderer(this, 58, 0);
        this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
        this.mane.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.mane, 0.5235988F, 0.0F, 0.0F);
        this.horseFaceRopes = new ModelRenderer(this, 80, 12);
        this.horseFaceRopes.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
        this.horseFaceRopes.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.setBoxRotation(this.horseFaceRopes, 0.5235988F, 0.0F, 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        EntityHorse entityhorse = (EntityHorse)entityIn;
        HorseType horsetype = entityhorse.getType();
        float f = entityhorse.getGrassEatingAmount(0.0F);
        boolean flag = entityhorse.isAdultHorse();
        boolean flag1 = flag && entityhorse.isHorseSaddled();
        boolean flag2 = flag && entityhorse.isChested();
        boolean flag3 = horsetype.hasMuleEars();
        float f1 = entityhorse.getHorseSize();
        boolean flag4 = entityhorse.isBeingRidden();

        if (flag1)
        {
            this.horseFaceRopes.render(scale);
            this.horseSaddleBottom.render(scale);
            this.horseSaddleFront.render(scale);
            this.horseSaddleBack.render(scale);
            this.horseLeftSaddleRope.render(scale);
            this.horseLeftSaddleMetal.render(scale);
            this.horseRightSaddleRope.render(scale);
            this.horseRightSaddleMetal.render(scale);
            this.horseLeftFaceMetal.render(scale);
            this.horseRightFaceMetal.render(scale);

            if (flag4)
            {
                this.horseLeftRein.render(scale);
                this.horseRightRein.render(scale);
            }
        }

        if (!flag)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(f1, 0.5F + f1 * 0.5F, f1);
            GlStateManager.translate(0.0F, 0.95F * (1.0F - f1), 0.0F);
        }

        this.backLeftLeg.render(scale);
        this.backLeftShin.render(scale);
        this.backLeftHoof.render(scale);
        this.backRightLeg.render(scale);
        this.backRightShin.render(scale);
        this.backRightHoof.render(scale);
        this.frontLeftLeg.render(scale);
        this.frontLeftShin.render(scale);
        this.frontLeftHoof.render(scale);
        this.frontRightLeg.render(scale);
        this.frontRightShin.render(scale);
        this.frontRightHoof.render(scale);

        if (!flag)
        {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(f1, f1, f1);
            GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
        }

        this.body.render(scale);
        this.tailBase.render(scale);
        this.tailMiddle.render(scale);
        this.tailTip.render(scale);
        this.neck.render(scale);
        this.mane.render(scale);

        if (!flag)
        {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            float f2 = 0.5F + f1 * f1 * 0.5F;
            GlStateManager.scale(f2, f2, f2);

            if (f <= 0.0F)
            {
                GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
            }
            else
            {
                GlStateManager.translate(0.0F, 0.9F * (1.0F - f1) * f + 1.35F * (1.0F - f1) * (1.0F - f), 0.15F * (1.0F - f1) * f);
            }
        }

        if (flag3)
        {
            this.muleLeftEar.render(scale);
            this.muleRightEar.render(scale);
        }
        else
        {
            this.horseLeftEar.render(scale);
            this.horseRightEar.render(scale);
        }

        this.head.render(scale);

        if (!flag)
        {
            GlStateManager.popMatrix();
        }

        if (flag2)
        {
            this.muleLeftChest.render(scale);
            this.muleRightChest.render(scale);
        }
    }

    /**
     * Sets the rotations for a ModelRenderer in the ModelHorse class.
     */
    private void setBoxRotation(ModelRenderer renderer, float rotateAngleX, float rotateAngleY, float rotateAngleZ)
    {
        renderer.rotateAngleX = rotateAngleX;
        renderer.rotateAngleY = rotateAngleY;
        renderer.rotateAngleZ = rotateAngleZ;
    }

    /**
     * Fixes and offsets a rotation in the ModelHorse class.
     */
    private float updateHorseRotation(float p_110683_1_, float p_110683_2_, float p_110683_3_)
    {
        float f;

        for (f = p_110683_2_ - p_110683_1_; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return p_110683_1_ + p_110683_3_ * f;
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime);
        float f = this.updateHorseRotation(entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset, partialTickTime);
        float f1 = this.updateHorseRotation(entitylivingbaseIn.prevRotationYawHead, entitylivingbaseIn.rotationYawHead, partialTickTime);
        float f2 = entitylivingbaseIn.prevRotationPitch + (entitylivingbaseIn.rotationPitch - entitylivingbaseIn.prevRotationPitch) * partialTickTime;
        float f3 = f1 - f;
        float f4 = f2 * 0.017453292F;

        if (f3 > 20.0F)
        {
            f3 = 20.0F;
        }

        if (f3 < -20.0F)
        {
            f3 = -20.0F;
        }

        if (p_78086_3_ > 0.2F)
        {
            f4 += MathHelper.cos(p_78086_2_ * 0.4F) * 0.15F * p_78086_3_;
        }

        EntityHorse entityhorse = (EntityHorse)entitylivingbaseIn;
        float f5 = entityhorse.getGrassEatingAmount(partialTickTime);
        float f6 = entityhorse.getRearingAmount(partialTickTime);
        float f7 = 1.0F - f6;
        float f8 = entityhorse.getMouthOpennessAngle(partialTickTime);
        boolean flag = entityhorse.tailCounter != 0;
        boolean flag1 = entityhorse.isHorseSaddled();
        boolean flag2 = entityhorse.isBeingRidden();
        float f9 = (float)entitylivingbaseIn.ticksExisted + partialTickTime;
        float f10 = MathHelper.cos(p_78086_2_ * 0.6662F + (float)Math.PI);
        float f11 = f10 * 0.8F * p_78086_3_;
        this.head.rotationPointY = 4.0F;
        this.head.rotationPointZ = -10.0F;
        this.tailBase.rotationPointY = 3.0F;
        this.tailMiddle.rotationPointZ = 14.0F;
        this.muleRightChest.rotationPointY = 3.0F;
        this.muleRightChest.rotationPointZ = 10.0F;
        this.body.rotateAngleX = 0.0F;
        this.head.rotateAngleX = 0.5235988F + f4;
        this.head.rotateAngleY = f3 * 0.017453292F;
        this.head.rotateAngleX = f6 * (0.2617994F + f4) + f5 * 2.18166F + (1.0F - Math.max(f6, f5)) * this.head.rotateAngleX;
        this.head.rotateAngleY = f6 * f3 * 0.017453292F + (1.0F - Math.max(f6, f5)) * this.head.rotateAngleY;
        this.head.rotationPointY = f6 * -6.0F + f5 * 11.0F + (1.0F - Math.max(f6, f5)) * this.head.rotationPointY;
        this.head.rotationPointZ = f6 * -1.0F + f5 * -10.0F + (1.0F - Math.max(f6, f5)) * this.head.rotationPointZ;
        this.tailBase.rotationPointY = f6 * 9.0F + f7 * this.tailBase.rotationPointY;
        this.tailMiddle.rotationPointZ = f6 * 18.0F + f7 * this.tailMiddle.rotationPointZ;
        this.muleRightChest.rotationPointY = f6 * 5.5F + f7 * this.muleRightChest.rotationPointY;
        this.muleRightChest.rotationPointZ = f6 * 15.0F + f7 * this.muleRightChest.rotationPointZ;
        this.body.rotateAngleX = f6 * -45.0F * 0.017453292F + f7 * this.body.rotateAngleX;
        this.horseLeftEar.rotationPointY = this.head.rotationPointY;
        this.horseRightEar.rotationPointY = this.head.rotationPointY;
        this.muleLeftEar.rotationPointY = this.head.rotationPointY;
        this.muleRightEar.rotationPointY = this.head.rotationPointY;
        this.neck.rotationPointY = this.head.rotationPointY;
        this.upperMouth.rotationPointY = 0.02F;
        this.lowerMouth.rotationPointY = 0.0F;
        this.mane.rotationPointY = this.head.rotationPointY;
        this.horseLeftEar.rotationPointZ = this.head.rotationPointZ;
        this.horseRightEar.rotationPointZ = this.head.rotationPointZ;
        this.muleLeftEar.rotationPointZ = this.head.rotationPointZ;
        this.muleRightEar.rotationPointZ = this.head.rotationPointZ;
        this.neck.rotationPointZ = this.head.rotationPointZ;
        this.upperMouth.rotationPointZ = 0.02F - f8;
        this.lowerMouth.rotationPointZ = f8;
        this.mane.rotationPointZ = this.head.rotationPointZ;
        this.horseLeftEar.rotateAngleX = this.head.rotateAngleX;
        this.horseRightEar.rotateAngleX = this.head.rotateAngleX;
        this.muleLeftEar.rotateAngleX = this.head.rotateAngleX;
        this.muleRightEar.rotateAngleX = this.head.rotateAngleX;
        this.neck.rotateAngleX = this.head.rotateAngleX;
        this.upperMouth.rotateAngleX = -0.09424778F * f8;
        this.lowerMouth.rotateAngleX = 0.15707964F * f8;
        this.mane.rotateAngleX = this.head.rotateAngleX;
        this.horseLeftEar.rotateAngleY = this.head.rotateAngleY;
        this.horseRightEar.rotateAngleY = this.head.rotateAngleY;
        this.muleLeftEar.rotateAngleY = this.head.rotateAngleY;
        this.muleRightEar.rotateAngleY = this.head.rotateAngleY;
        this.neck.rotateAngleY = this.head.rotateAngleY;
        this.upperMouth.rotateAngleY = 0.0F;
        this.lowerMouth.rotateAngleY = 0.0F;
        this.mane.rotateAngleY = this.head.rotateAngleY;
        this.muleLeftChest.rotateAngleX = f11 / 5.0F;
        this.muleRightChest.rotateAngleX = -f11 / 5.0F;
        float f12 = ((float)Math.PI / 2F);
        float f13 = ((float)Math.PI * 3F / 2F);
        float f14 = -1.0471976F;
        float f15 = 0.2617994F * f6;
        float f16 = MathHelper.cos(f9 * 0.6F + (float)Math.PI);
        this.frontLeftLeg.rotationPointY = -2.0F * f6 + 9.0F * f7;
        this.frontLeftLeg.rotationPointZ = -2.0F * f6 + -8.0F * f7;
        this.frontRightLeg.rotationPointY = this.frontLeftLeg.rotationPointY;
        this.frontRightLeg.rotationPointZ = this.frontLeftLeg.rotationPointZ;
        this.backLeftShin.rotationPointY = this.backLeftLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f15 + f7 * -f10 * 0.5F * p_78086_3_) * 7.0F;
        this.backLeftShin.rotationPointZ = this.backLeftLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f15 + f7 * -f10 * 0.5F * p_78086_3_) * 7.0F;
        this.backRightShin.rotationPointY = this.backRightLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f15 + f7 * f10 * 0.5F * p_78086_3_) * 7.0F;
        this.backRightShin.rotationPointZ = this.backRightLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f15 + f7 * f10 * 0.5F * p_78086_3_) * 7.0F;
        float f17 = (-1.0471976F + f16) * f6 + f11 * f7;
        float f18 = (-1.0471976F + -f16) * f6 + -f11 * f7;
        this.frontLeftShin.rotationPointY = this.frontLeftLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f17) * 7.0F;
        this.frontLeftShin.rotationPointZ = this.frontLeftLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f17) * 7.0F;
        this.frontRightShin.rotationPointY = this.frontRightLeg.rotationPointY + MathHelper.sin(((float)Math.PI / 2F) + f18) * 7.0F;
        this.frontRightShin.rotationPointZ = this.frontRightLeg.rotationPointZ + MathHelper.cos(((float)Math.PI * 3F / 2F) + f18) * 7.0F;
        this.backLeftLeg.rotateAngleX = f15 + -f10 * 0.5F * p_78086_3_ * f7;
        this.backLeftShin.rotateAngleX = -0.08726646F * f6 + (-f10 * 0.5F * p_78086_3_ - Math.max(0.0F, f10 * 0.5F * p_78086_3_)) * f7;
        this.backLeftHoof.rotateAngleX = this.backLeftShin.rotateAngleX;
        this.backRightLeg.rotateAngleX = f15 + f10 * 0.5F * p_78086_3_ * f7;
        this.backRightShin.rotateAngleX = -0.08726646F * f6 + (f10 * 0.5F * p_78086_3_ - Math.max(0.0F, -f10 * 0.5F * p_78086_3_)) * f7;
        this.backRightHoof.rotateAngleX = this.backRightShin.rotateAngleX;
        this.frontLeftLeg.rotateAngleX = f17;
        this.frontLeftShin.rotateAngleX = (this.frontLeftLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F + f16 * 0.2F)) * f6 + (f11 + Math.max(0.0F, f10 * 0.5F * p_78086_3_)) * f7;
        this.frontLeftHoof.rotateAngleX = this.frontLeftShin.rotateAngleX;
        this.frontRightLeg.rotateAngleX = f18;
        this.frontRightShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F - f16 * 0.2F)) * f6 + (-f11 + Math.max(0.0F, -f10 * 0.5F * p_78086_3_)) * f7;
        this.frontRightHoof.rotateAngleX = this.frontRightShin.rotateAngleX;
        this.backLeftHoof.rotationPointY = this.backLeftShin.rotationPointY;
        this.backLeftHoof.rotationPointZ = this.backLeftShin.rotationPointZ;
        this.backRightHoof.rotationPointY = this.backRightShin.rotationPointY;
        this.backRightHoof.rotationPointZ = this.backRightShin.rotationPointZ;
        this.frontLeftHoof.rotationPointY = this.frontLeftShin.rotationPointY;
        this.frontLeftHoof.rotationPointZ = this.frontLeftShin.rotationPointZ;
        this.frontRightHoof.rotationPointY = this.frontRightShin.rotationPointY;
        this.frontRightHoof.rotationPointZ = this.frontRightShin.rotationPointZ;

        if (flag1)
        {
            this.horseSaddleBottom.rotationPointY = f6 * 0.5F + f7 * 2.0F;
            this.horseSaddleBottom.rotationPointZ = f6 * 11.0F + f7 * 2.0F;
            this.horseSaddleFront.rotationPointY = this.horseSaddleBottom.rotationPointY;
            this.horseSaddleBack.rotationPointY = this.horseSaddleBottom.rotationPointY;
            this.horseLeftSaddleRope.rotationPointY = this.horseSaddleBottom.rotationPointY;
            this.horseRightSaddleRope.rotationPointY = this.horseSaddleBottom.rotationPointY;
            this.horseLeftSaddleMetal.rotationPointY = this.horseSaddleBottom.rotationPointY;
            this.horseRightSaddleMetal.rotationPointY = this.horseSaddleBottom.rotationPointY;
            this.muleLeftChest.rotationPointY = this.muleRightChest.rotationPointY;
            this.horseSaddleFront.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
            this.horseSaddleBack.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
            this.horseLeftSaddleRope.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
            this.horseRightSaddleRope.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
            this.horseLeftSaddleMetal.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
            this.horseRightSaddleMetal.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
            this.muleLeftChest.rotationPointZ = this.muleRightChest.rotationPointZ;
            this.horseSaddleBottom.rotateAngleX = this.body.rotateAngleX;
            this.horseSaddleFront.rotateAngleX = this.body.rotateAngleX;
            this.horseSaddleBack.rotateAngleX = this.body.rotateAngleX;
            this.horseLeftRein.rotationPointY = this.head.rotationPointY;
            this.horseRightRein.rotationPointY = this.head.rotationPointY;
            this.horseFaceRopes.rotationPointY = this.head.rotationPointY;
            this.horseLeftFaceMetal.rotationPointY = this.head.rotationPointY;
            this.horseRightFaceMetal.rotationPointY = this.head.rotationPointY;
            this.horseLeftRein.rotationPointZ = this.head.rotationPointZ;
            this.horseRightRein.rotationPointZ = this.head.rotationPointZ;
            this.horseFaceRopes.rotationPointZ = this.head.rotationPointZ;
            this.horseLeftFaceMetal.rotationPointZ = this.head.rotationPointZ;
            this.horseRightFaceMetal.rotationPointZ = this.head.rotationPointZ;
            this.horseLeftRein.rotateAngleX = f4;
            this.horseRightRein.rotateAngleX = f4;
            this.horseFaceRopes.rotateAngleX = this.head.rotateAngleX;
            this.horseLeftFaceMetal.rotateAngleX = this.head.rotateAngleX;
            this.horseRightFaceMetal.rotateAngleX = this.head.rotateAngleX;
            this.horseFaceRopes.rotateAngleY = this.head.rotateAngleY;
            this.horseLeftFaceMetal.rotateAngleY = this.head.rotateAngleY;
            this.horseLeftRein.rotateAngleY = this.head.rotateAngleY;
            this.horseRightFaceMetal.rotateAngleY = this.head.rotateAngleY;
            this.horseRightRein.rotateAngleY = this.head.rotateAngleY;

            if (flag2)
            {
                this.horseLeftSaddleRope.rotateAngleX = -1.0471976F;
                this.horseLeftSaddleMetal.rotateAngleX = -1.0471976F;
                this.horseRightSaddleRope.rotateAngleX = -1.0471976F;
                this.horseRightSaddleMetal.rotateAngleX = -1.0471976F;
                this.horseLeftSaddleRope.rotateAngleZ = 0.0F;
                this.horseLeftSaddleMetal.rotateAngleZ = 0.0F;
                this.horseRightSaddleRope.rotateAngleZ = 0.0F;
                this.horseRightSaddleMetal.rotateAngleZ = 0.0F;
            }
            else
            {
                this.horseLeftSaddleRope.rotateAngleX = f11 / 3.0F;
                this.horseLeftSaddleMetal.rotateAngleX = f11 / 3.0F;
                this.horseRightSaddleRope.rotateAngleX = f11 / 3.0F;
                this.horseRightSaddleMetal.rotateAngleX = f11 / 3.0F;
                this.horseLeftSaddleRope.rotateAngleZ = f11 / 5.0F;
                this.horseLeftSaddleMetal.rotateAngleZ = f11 / 5.0F;
                this.horseRightSaddleRope.rotateAngleZ = -f11 / 5.0F;
                this.horseRightSaddleMetal.rotateAngleZ = -f11 / 5.0F;
            }
        }

        f12 = -1.3089F + p_78086_3_ * 1.5F;

        if (f12 > 0.0F)
        {
            f12 = 0.0F;
        }

        if (flag)
        {
            this.tailBase.rotateAngleY = MathHelper.cos(f9 * 0.7F);
            f12 = 0.0F;
        }
        else
        {
            this.tailBase.rotateAngleY = 0.0F;
        }

        this.tailMiddle.rotateAngleY = this.tailBase.rotateAngleY;
        this.tailTip.rotateAngleY = this.tailBase.rotateAngleY;
        this.tailMiddle.rotationPointY = this.tailBase.rotationPointY;
        this.tailTip.rotationPointY = this.tailBase.rotationPointY;
        this.tailMiddle.rotationPointZ = this.tailBase.rotationPointZ;
        this.tailTip.rotationPointZ = this.tailBase.rotationPointZ;
        this.tailBase.rotateAngleX = f12;
        this.tailMiddle.rotateAngleX = f12;
        this.tailTip.rotateAngleX = -0.2618F + f12;
    }
}