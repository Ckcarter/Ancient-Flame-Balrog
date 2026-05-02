package rem.ancient_flame_balrog.client.model;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.model.ArmedModel;
import com.mojang.math.Axis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;

public class AncientFlameBalrogModel<T extends AncientFlameBalrogEntity> extends EntityModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(AncientFlameBalrog.MODID, "ancient_flame_balrog"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    public final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWingRoot;
    private final ModelPart rightWingRoot;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingTip;

    public AncientFlameBalrogModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftWingRoot = this.body.getChild("left_wing_root");
        this.rightWingRoot = this.body.getChild("right_wing_root");
        this.leftWingTip = this.leftWingRoot.getChild("left_wing_tip");
        this.rightWingTip = this.rightWingRoot.getChild("right_wing_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Balrog-inspired silhouette: huge hunched shoulders/chest, narrow waist, thick ground-planted legs.
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        // narrow lower torso/waist
                        .texOffs(0, 34).addBox(-5.0F, -9.0F, -3.8F, 10.0F, 13.0F, 7.0F, new CubeDeformation(0.15F))
                        // massive upper chest, pushed forward and up for a hunched demon shape
                        .texOffs(0, 54).addBox(-9.5F, -22.0F, -5.8F, 19.0F, 15.0F, 10.0F, new CubeDeformation(0.25F))
                        // raised shoulder/back mass where the wings connect
                        .texOffs(58, 54).addBox(-11.0F, -20.0F, 1.6F, 22.0F, 8.0F, 5.5F, new CubeDeformation(0.2F))
                        // small forward rib/abdomen plate
                        .texOffs(58, 68).addBox(-6.0F, -12.0F, -6.4F, 12.0F, 7.0F, 2.5F, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.10F, 0.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        // heavier brow and lower forward face
                        .texOffs(0, 0).addBox(-5.5F, -8.5F, -6.8F, 11.0F, 9.0F, 10.5F, new CubeDeformation(0.15F))
                        .texOffs(44, 18).addBox(-6.0F, -6.8F, -7.6F, 12.0F, 3.2F, 4.0F, new CubeDeformation(0.1F))
                        .texOffs(0, 20).addBox(-3.8F, -1.0F, -7.8F, 7.6F, 4.0F, 4.5F, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(0.0F, -5.0F, -5.2F, 0.22F, 0.0F, 0.0F));

        // Long swept horns built from several segments so they look curved instead of like straight sticks.
        head.addOrReplaceChild("left_horn_base", CubeListBuilder.create().texOffs(48, 0)
                        .addBox(0.0F, -2.4F, -2.2F, 7.5F, 4.6F, 4.4F, new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(4.7F, -7.5F, -0.6F, -0.10F, -0.30F, -0.58F));
        head.addOrReplaceChild("left_horn_mid", CubeListBuilder.create().texOffs(48, 8)
                        .addBox(0.0F, -1.8F, -1.7F, 7.5F, 3.4F, 3.4F, new CubeDeformation(-0.10F)),
                PartPose.offsetAndRotation(10.2F, -11.4F, -1.2F, -0.18F, -0.42F, -0.96F));
        head.addOrReplaceChild("left_horn_tip", CubeListBuilder.create().texOffs(48, 14)
                        .addBox(0.0F, -1.1F, -1.1F, 6.5F, 2.1F, 2.1F, new CubeDeformation(-0.18F)),
                PartPose.offsetAndRotation(14.8F, -16.8F, -1.6F, -0.25F, -0.56F, -1.32F));

        head.addOrReplaceChild("right_horn_base", CubeListBuilder.create().texOffs(48, 0).mirror()
                        .addBox(-7.5F, -2.4F, -2.2F, 7.5F, 4.6F, 4.4F, new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(-4.7F, -7.5F, -0.6F, -0.10F, 0.30F, 0.58F));
        head.addOrReplaceChild("right_horn_mid", CubeListBuilder.create().texOffs(48, 8).mirror()
                        .addBox(-7.5F, -1.8F, -1.7F, 7.5F, 3.4F, 3.4F, new CubeDeformation(-0.10F)),
                PartPose.offsetAndRotation(-10.2F, -11.4F, -1.2F, -0.18F, 0.42F, 0.96F));
        head.addOrReplaceChild("right_horn_tip", CubeListBuilder.create().texOffs(48, 14).mirror()
                        .addBox(-6.5F, -1.1F, -1.1F, 6.5F, 2.1F, 2.1F, new CubeDeformation(-0.18F)),
                PartPose.offsetAndRotation(-14.8F, -16.8F, -1.6F, -0.25F, 0.56F, 1.32F));

        // Shorter, thicker arms: closer to the hulking Balrog look without dragging below the feet.
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(64, 28)
                        .addBox(0.0F, -2.0F, -3.4F, 6.8F, 14.2F, 6.8F, new CubeDeformation(0.18F))
                        .texOffs(64, 52).addBox(0.8F, 10.8F, -4.0F, 5.8F, 4.4F, 8.0F, new CubeDeformation(0.18F)),
                PartPose.offsetAndRotation(9.2F, -3.2F, -0.2F, 0.10F, 0.0F, -0.10F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 28).mirror()
                        .addBox(-6.8F, -2.0F, -3.4F, 6.8F, 14.2F, 6.8F, new CubeDeformation(0.18F))
                        .texOffs(64, 52).addBox(-6.6F, 10.8F, -4.0F, 5.8F, 4.4F, 8.0F, new CubeDeformation(0.18F)),
                PartPose.offsetAndRotation(-9.2F, -3.2F, -0.2F, 0.10F, 0.0F, 0.10F));

        // Thick legs under a smaller waist so it stays grounded and walks on the floor.
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(88, 28)
                        .addBox(-3.3F, 0.0F, -3.3F, 6.6F, 22.0F, 6.6F, new CubeDeformation(0.05F))
                        .texOffs(88, 58).addBox(-4.0F, 17.5F, -5.2F, 8.0F, 4.5F, 8.2F, new CubeDeformation(0.05F)),
                PartPose.offset(3.8F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(88, 28).mirror()
                        .addBox(-3.3F, 0.0F, -3.3F, 6.6F, 22.0F, 6.6F, new CubeDeformation(0.05F))
                        .texOffs(88, 58).addBox(-4.0F, 17.5F, -5.2F, 8.0F, 4.5F, 8.2F, new CubeDeformation(0.05F)),
                PartPose.offset(-3.8F, 2.0F, 0.0F));

        // Large connected bat wings: shoulder sockets, bones, fingers, and broad membrane panels.
        PartDefinition leftWingRoot = body.addOrReplaceChild("left_wing_root",
                CubeListBuilder.create()
                        .texOffs(0, 82).addBox(-1.8F, -3.0F, -1.8F, 5.8F, 6.8F, 5.8F, new CubeDeformation(0.16F))
                        .texOffs(24, 82).addBox(2.0F, -0.9F, 0.0F, 16.0F, 1.8F, 1.8F)
                        .texOffs(0, 96).addBox(1.2F, 1.0F, 0.55F, 17.2F, 11.0F, 0.65F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(8.0F, -17.0F, 5.8F, 0.04F, -0.24F, -0.46F));

        leftWingRoot.addOrReplaceChild("left_wing_elbow",
                CubeListBuilder.create()
                        .texOffs(66, 82).addBox(-0.75F, -1.25F, -0.25F, 2.6F, 2.6F, 2.6F, new CubeDeformation(0.05F))
                        .texOffs(76, 82).addBox(1.0F, -0.75F, 0.0F, 16.5F, 1.5F, 1.5F)
                        .texOffs(76, 88).addBox(1.8F, 3.0F, 0.35F, 15.5F, 1.0F, 1.0F)
                        .texOffs(76, 92).addBox(1.4F, 7.1F, 0.35F, 14.0F, 0.9F, 0.9F)
                        .texOffs(76, 96).addBox(1.0F, 11.4F, 0.35F, 12.0F, 0.8F, 0.8F)
                        .texOffs(38, 98).addBox(1.0F, 1.2F, 0.65F, 16.5F, 5.2F, 0.55F, new CubeDeformation(0.01F))
                        .texOffs(38, 106).addBox(1.0F, 5.8F, 0.65F, 15.0F, 5.2F, 0.55F, new CubeDeformation(0.01F))
                        .texOffs(38, 114).addBox(1.0F, 10.3F, 0.65F, 12.8F, 4.8F, 0.55F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(17.2F, 0.0F, 0.1F, 0.02F, -0.05F, -0.38F));

        leftWingRoot.addOrReplaceChild("left_wing_tip",
                CubeListBuilder.create()
                        .texOffs(106, 82).addBox(0.0F, -0.6F, 0.0F, 12.0F, 1.2F, 1.2F)
                        .texOffs(106, 88).addBox(9.8F, -0.9F, -0.2F, 4.2F, 1.8F, 1.8F, new CubeDeformation(-0.18F))
                        .texOffs(86, 100).addBox(0.0F, 0.8F, 0.55F, 10.8F, 10.2F, 0.5F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(33.0F, 0.2F, 0.2F, 0.0F, -0.06F, -0.62F));

        PartDefinition rightWingRoot = body.addOrReplaceChild("right_wing_root",
                CubeListBuilder.create().mirror()
                        .texOffs(0, 82).addBox(-4.0F, -3.0F, -1.8F, 5.8F, 6.8F, 5.8F, new CubeDeformation(0.16F))
                        .texOffs(24, 82).addBox(-18.0F, -0.9F, 0.0F, 16.0F, 1.8F, 1.8F)
                        .texOffs(0, 96).addBox(-18.4F, 1.0F, 0.55F, 17.2F, 11.0F, 0.65F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(-8.0F, -17.0F, 5.8F, 0.04F, 0.24F, 0.46F));

        rightWingRoot.addOrReplaceChild("right_wing_elbow",
                CubeListBuilder.create().mirror()
                        .texOffs(66, 82).addBox(-1.85F, -1.25F, -0.25F, 2.6F, 2.6F, 2.6F, new CubeDeformation(0.05F))
                        .texOffs(76, 82).addBox(-17.5F, -0.75F, 0.0F, 16.5F, 1.5F, 1.5F)
                        .texOffs(76, 88).addBox(-17.3F, 3.0F, 0.35F, 15.5F, 1.0F, 1.0F)
                        .texOffs(76, 92).addBox(-15.4F, 7.1F, 0.35F, 14.0F, 0.9F, 0.9F)
                        .texOffs(76, 96).addBox(-13.0F, 11.4F, 0.35F, 12.0F, 0.8F, 0.8F)
                        .texOffs(38, 98).addBox(-17.5F, 1.2F, 0.65F, 16.5F, 5.2F, 0.55F, new CubeDeformation(0.01F))
                        .texOffs(38, 106).addBox(-16.0F, 5.8F, 0.65F, 15.0F, 5.2F, 0.55F, new CubeDeformation(0.01F))
                        .texOffs(38, 114).addBox(-13.8F, 10.3F, 0.65F, 12.8F, 4.8F, 0.55F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(-17.2F, 0.0F, 0.1F, 0.02F, 0.05F, 0.38F));

        rightWingRoot.addOrReplaceChild("right_wing_tip",
                CubeListBuilder.create().mirror()
                        .texOffs(106, 82).addBox(-12.0F, -0.6F, 0.0F, 12.0F, 1.2F, 1.2F)
                        .texOffs(106, 88).addBox(-14.0F, -0.9F, -0.2F, 4.2F, 1.8F, 1.8F, new CubeDeformation(-0.18F))
                        .texOffs(86, 100).addBox(-10.8F, 0.8F, 0.55F, 10.8F, 10.2F, 0.5F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(-33.0F, 0.2F, 0.2F, 0.0F, 0.06F, 0.62F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = 0.22F + headPitch * Mth.DEG_TO_RAD;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.25F * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.25F * limbSwingAmount;

        // Both arms raise during fireball volley windup (queuedAttack == 2)
        int queued = entity.getQueuedAttack();
        int windup = entity.getAttackWindup();
        if (queued == 2 && windup > 0) {
            float progress = Math.min(1.0F, windup / 30.0F);
            float raise = -Mth.PI * 0.6F * progress;
            this.rightArm.xRot = raise;
            this.leftArm.xRot = raise;
            this.rightArm.zRot = -0.2F * progress;
            this.leftArm.zRot = 0.2F * progress;
        }

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;


        // Realistic wing motion: slow breathing flap, stronger combat spread, tips lag behind root for a natural bat-wing feel.
        float idleFlap = Mth.sin(ageInTicks * 0.08F) * 0.08F;
        float tipLag = Mth.sin(ageInTicks * 0.08F - 0.65F) * 0.10F;
        float walkLift = Mth.cos(limbSwing * 0.35F) * 0.035F * limbSwingAmount;
        float combatSpread = entity.isAggressive() ? 0.32F : 0.0F;

        this.leftWingRoot.xRot = 0.04F + walkLift;
        this.rightWingRoot.xRot = 0.04F + walkLift;

        this.leftWingRoot.yRot = -0.24F - combatSpread * 0.25F;
        this.rightWingRoot.yRot = 0.24F + combatSpread * 0.25F;

        this.leftWingRoot.zRot = -0.46F - combatSpread + idleFlap;
        this.rightWingRoot.zRot = 0.46F + combatSpread - idleFlap;

        this.leftWingTip.zRot = -0.62F - combatSpread * 0.55F + tipLag;
        this.rightWingTip.zRot = 0.62F + combatSpread * 0.55F - tipLag;

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float r, float g, float b, float a) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        ModelPart hand = arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;

        this.root.translateAndRotate(poseStack);
        hand.translateAndRotate(poseStack);

        // FORCE the item into the palm and orient like a sword grip
        if (arm == HumanoidArm.RIGHT) {
            poseStack.translate(-0.0D, -0.50D, -0.0D);
            poseStack.mulPose(Axis.XP.rotationDegrees(0.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(0.0F));
        } else {
            poseStack.translate(0.15D, 0.85D, -0.10D);
            poseStack.mulPose(Axis.XP.rotationDegrees(-95.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-5.0F));
        }

        // Scale for big Balrog hands
        poseStack.scale(2.6F, 2.6F, 2.6F);


    }
}
