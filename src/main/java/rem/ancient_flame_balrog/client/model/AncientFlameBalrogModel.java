package rem.ancient_flame_balrog.client.model;

import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class AncientFlameBalrogModel<T extends AncientFlameBalrogEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public AncientFlameBalrogModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");
        this.rightLeg = body.getChild("right_leg");
        this.leftLeg = body.getChild("left_leg");
        this.rightWing = body.getChild("right_wing");
        this.leftWing = body.getChild("left_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0F, -25.0F, -4.0F, 16.0F, 25.0F, 8.0F)
                .texOffs(48, 0).addBox(-10.0F, -21.0F, -5.0F, 20.0F, 10.0F, 10.0F),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 34).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(40, 34).addBox(-9.0F, -12.0F, -2.0F, 5.0F, 5.0F, 5.0F)
                .texOffs(60, 34).addBox(4.0F, -12.0F, -2.0F, 5.0F, 5.0F, 5.0F)
                .texOffs(80, 34).addBox(-2.0F, -6.0F, -9.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, -25.0F, -1.0F));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 55).addBox(-5.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F),
                PartPose.offset(-10.0F, -21.0F, 0.0F));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(24, 55).addBox(-1.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F),
                PartPose.offset(10.0F, -21.0F, 0.0F));

        body.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(48, 55).addBox(-3.5F, 0.0F, -3.0F, 7.0F, 18.0F, 6.0F),
                PartPose.offset(-4.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(74, 55).addBox(-3.5F, 0.0F, -3.0F, 7.0F, 18.0F, 6.0F),
                PartPose.offset(4.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("right_wing", CubeListBuilder.create()
                .texOffs(98, 0).addBox(-17.0F, -3.0F, 0.0F, 17.0F, 22.0F, 1.0F),
                PartPose.offsetAndRotation(-6.0F, -21.0F, 4.0F, 0.15F, 0.55F, -0.25F));

        body.addOrReplaceChild("left_wing", CubeListBuilder.create()
                .texOffs(98, 24).addBox(0.0F, -3.0F, 0.0F, 17.0F, 22.0F, 1.0F),
                PartPose.offsetAndRotation(6.0F, -21.0F, 4.0F, 0.15F, -0.55F, 0.25F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 0.9F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.9F * limbSwingAmount;
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.7F * limbSwingAmount - 0.2F;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount - 0.2F;
        float wingFlap = Mth.sin(ageInTicks * 0.08F) * 0.08F;
        this.rightWing.yRot = 0.55F + wingFlap;
        this.leftWing.yRot = -0.55F - wingFlap;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
