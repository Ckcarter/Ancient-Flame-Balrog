package rem.ancient_flame_balrog.client.model;

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

public class AncientFlameBalrogModel<T extends AncientFlameBalrogEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(AncientFlameBalrog.MODID, "ancient_flame_balrog"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWingRoot;
    private final ModelPart leftWingMid;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingRoot;
    private final ModelPart rightWingMid;
    private final ModelPart rightWingTip;
    private final ModelPart tail;

    public AncientFlameBalrogModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftWingRoot = root.getChild("left_wing_root");
        this.leftWingMid = this.leftWingRoot.getChild("left_wing_mid");
        this.leftWingTip = this.leftWingMid.getChild("left_wing_tip");
        this.rightWingRoot = root.getChild("right_wing_root");
        this.rightWingMid = this.rightWingRoot.getChild("right_wing_mid");
        this.rightWingTip = this.rightWingMid.getChild("right_wing_tip");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 28)
                .addBox(-8.0F, -18.0F, -4.0F, 16.0F, 22.0F, 8.0F), PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-6.0F, -10.0F, -6.0F, 12.0F, 10.0F, 12.0F), PartPose.offset(0.0F, -4.0F, -1.0F));

        head.addOrReplaceChild("left_horn_base", CubeListBuilder.create().texOffs(48, 0)
                .addBox(0.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(5.0F, -8.0F, 0.0F, 0.0F, -0.25F, -0.55F));
        head.addOrReplaceChild("left_horn_mid", CubeListBuilder.create().texOffs(48, 8)
                .addBox(0.0F, -1.5F, -1.5F, 7.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(11.0F, -11.0F, 0.0F, 0.0F, -0.35F, -0.85F));
        head.addOrReplaceChild("left_horn_tip", CubeListBuilder.create().texOffs(48, 14)
                .addBox(0.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(16.0F, -16.0F, 0.0F, 0.0F, -0.45F, -1.15F));

        head.addOrReplaceChild("right_horn_base", CubeListBuilder.create().texOffs(48, 0).mirror()
                .addBox(-8.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(-5.0F, -8.0F, 0.0F, 0.0F, 0.25F, 0.55F));
        head.addOrReplaceChild("right_horn_mid", CubeListBuilder.create().texOffs(48, 8).mirror()
                .addBox(-7.0F, -1.5F, -1.5F, 7.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(-11.0F, -11.0F, 0.0F, 0.0F, 0.35F, 0.85F));
        head.addOrReplaceChild("right_horn_tip", CubeListBuilder.create().texOffs(48, 14).mirror()
                .addBox(-6.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(-16.0F, -16.0F, 0.0F, 0.0F, 0.45F, 1.15F));

        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(64, 28)
                .addBox(0.0F, -2.0F, -3.0F, 6.0F, 17.0F, 6.0F)
                .texOffs(64, 52).addBox(1.0F, 14.5F, -3.5F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.15F)), PartPose.offset(8.0F, -1.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 28).mirror()
                .addBox(-6.0F, -2.0F, -3.0F, 6.0F, 17.0F, 6.0F)
                .texOffs(64, 52).addBox(-6.0F, 14.5F, -3.5F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.15F)), PartPose.offset(-8.0F, -1.0F, 0.0F));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(88, 28)
                .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 22.0F, 6.0F), PartPose.offset(4.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(88, 28).mirror()
                .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 22.0F, 6.0F), PartPose.offset(-4.0F, 2.0F, 0.0F));

        PartDefinition leftWingRoot = root.addOrReplaceChild("left_wing_root", CubeListBuilder.create().texOffs(0, 64)
                .addBox(0.0F, -16.0F, 0.0F, 16.0F, 30.0F, 1.0F), PartPose.offsetAndRotation(6.0F, -2.0F, 5.0F, 0.12F, 0.52F, 0.18F));
        PartDefinition leftWingMid = leftWingRoot.addOrReplaceChild("left_wing_mid", CubeListBuilder.create().texOffs(34, 64)
                .addBox(0.0F, -14.0F, 0.0F, 18.0F, 28.0F, 1.0F), PartPose.offsetAndRotation(15.0F, -1.0F, 0.0F, 0.02F, 0.22F, -0.10F));
        leftWingMid.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(72, 64)
                .addBox(0.0F, -11.0F, 0.0F, 20.0F, 22.0F, 1.0F), PartPose.offsetAndRotation(17.0F, 1.0F, 0.0F, 0.0F, 0.18F, -0.16F));

        PartDefinition rightWingRoot = root.addOrReplaceChild("right_wing_root", CubeListBuilder.create().texOffs(0, 64).mirror()
                .addBox(-16.0F, -16.0F, 0.0F, 16.0F, 30.0F, 1.0F), PartPose.offsetAndRotation(-6.0F, -2.0F, 5.0F, 0.12F, -0.52F, -0.18F));
        PartDefinition rightWingMid = rightWingRoot.addOrReplaceChild("right_wing_mid", CubeListBuilder.create().texOffs(34, 64).mirror()
                .addBox(-18.0F, -14.0F, 0.0F, 18.0F, 28.0F, 1.0F), PartPose.offsetAndRotation(-15.0F, -1.0F, 0.0F, 0.02F, -0.22F, 0.10F));
        rightWingMid.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(72, 64).mirror()
                .addBox(-20.0F, -11.0F, 0.0F, 20.0F, 22.0F, 1.0F), PartPose.offsetAndRotation(-17.0F, 1.0F, 0.0F, 0.0F, -0.18F, 0.16F));

        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(40, 96)
                .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 20.0F), PartPose.offsetAndRotation(0.0F, 11.0F, 4.0F, 0.45F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.25F * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.25F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;

        float slowFlap = Mth.sin(ageInTicks * 0.12F) * 0.18F;
        float breath = Mth.sin(ageInTicks * 0.05F) * 0.08F;
        boolean angry = entity.isAggressive() || entity.getTarget() != null;

        this.leftWingRoot.xRot = 0.12F + breath;
        this.rightWingRoot.xRot = 0.12F + breath;
        this.leftWingRoot.yRot = (angry ? 0.72F : 0.52F) + slowFlap;
        this.rightWingRoot.yRot = (angry ? -0.72F : -0.52F) - slowFlap;
        this.leftWingRoot.zRot = (angry ? 0.32F : 0.18F) + slowFlap * 0.55F;
        this.rightWingRoot.zRot = (angry ? -0.32F : -0.18F) - slowFlap * 0.55F;

        this.leftWingMid.yRot = 0.22F + slowFlap * 0.65F;
        this.rightWingMid.yRot = -0.22F - slowFlap * 0.65F;
        this.leftWingTip.yRot = 0.18F + slowFlap * 0.45F;
        this.rightWingTip.yRot = -0.18F - slowFlap * 0.45F;

        this.tail.yRot = Mth.sin(ageInTicks * 0.08F) * 0.18F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float r, float g, float b, float a) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }
}
