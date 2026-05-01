package rem.ancient_flame_balrog.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
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
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart tail;

    public AncientFlameBalrogModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 28)
                .addBox(-8, -18, -4, 16, 22, 8), PartPose.offset(0, 14, 0));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-6, -10, -6, 12, 10, 12), PartPose.offset(0, -4, -1));

        head.addOrReplaceChild("left_horn_base", CubeListBuilder.create().texOffs(48, 0)
                .addBox(0, -2, -2, 8, 4, 4), PartPose.offsetAndRotation(5, -8, 0, 0.0F, -0.25F, -0.55F));
        head.addOrReplaceChild("left_horn_mid", CubeListBuilder.create().texOffs(48, 8)
                .addBox(0, -1.5F, -1.5F, 7, 3, 3), PartPose.offsetAndRotation(11, -11, 0, 0.0F, -0.35F, -0.85F));
        head.addOrReplaceChild("left_horn_tip", CubeListBuilder.create().texOffs(48, 14)
                .addBox(0, -1, -1, 6, 2, 2), PartPose.offsetAndRotation(16, -16, 0, 0.0F, -0.45F, -1.15F));

        head.addOrReplaceChild("right_horn_base", CubeListBuilder.create().texOffs(48, 0).mirror()
                .addBox(-8, -2, -2, 8, 4, 4), PartPose.offsetAndRotation(-5, -8, 0, 0.0F, 0.25F, 0.55F));
        head.addOrReplaceChild("right_horn_mid", CubeListBuilder.create().texOffs(48, 8).mirror()
                .addBox(-7, -1.5F, -1.5F, 7, 3, 3), PartPose.offsetAndRotation(-11, -11, 0, 0.0F, 0.35F, 0.85F));
        head.addOrReplaceChild("right_horn_tip", CubeListBuilder.create().texOffs(48, 14).mirror()
                .addBox(-6, -1, -1, 6, 2, 2), PartPose.offsetAndRotation(-16, -16, 0, 0.0F, 0.45F, 1.15F));

        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(64, 28)
                .addBox(0, -2, -3, 6, 24, 6), PartPose.offset(8, -1, 0));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 28).mirror()
                .addBox(-6, -2, -3, 6, 24, 6), PartPose.offset(-8, -1, 0));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(88, 28)
                .addBox(-3, 0, -3, 6, 22, 6), PartPose.offset(4, 2, 0));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(88, 28).mirror()
                .addBox(-3, 0, -3, 6, 22, 6), PartPose.offset(-4, 2, 0));

        root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 64)
                .addBox(0, -14, 0, 18, 28, 1), PartPose.offsetAndRotation(6, -1, 5, 0.1F, 0.55F, 0.2F));
        root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 64).mirror()
                .addBox(-18, -14, 0, 18, 28, 1), PartPose.offsetAndRotation(-6, -1, 5, 0.1F, -0.55F, -0.2F));

        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(40, 70)
                .addBox(-2, -2, 0, 4, 4, 18), PartPose.offsetAndRotation(0, 11, 4, 0.45F, 0, 0));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.6F * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.6F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;

        float flap = Mth.sin(ageInTicks * 0.18F) * 0.18F;
        this.leftWing.yRot = 0.55F + flap;
        this.rightWing.yRot = -0.55F - flap;
        this.tail.yRot = Mth.sin(ageInTicks * 0.08F) * 0.18F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float r, float g, float b, float a) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }
}
