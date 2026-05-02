package rem.ancient_flame_balrog.client.renderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.client.model.AncientFlameBalrogModel;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;

public class AncientFlameBalrogRenderer extends MobRenderer<AncientFlameBalrogEntity, AncientFlameBalrogModel<AncientFlameBalrogEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AncientFlameBalrog.MODID, "textures/entity/ancient_flame_balrog.png");

    public AncientFlameBalrogRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientFlameBalrogModel<>(context.bakeLayer(AncientFlameBalrogModel.LAYER_LOCATION)), 1.6F);
        
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
this.addLayer(new BalrogSwordLayer(this, context));
    }

    @Override
    public ResourceLocation getTextureLocation(AncientFlameBalrogEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(AncientFlameBalrogEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.8F, 2.8F, 2.8F);
    }

    // -------------------------------------------------------------------------
    // Held-item layer: renders the sword attached to the Balrog's right hand
    // -------------------------------------------------------------------------
    private static class BalrogSwordLayer extends RenderLayer<AncientFlameBalrogEntity, AncientFlameBalrogModel<AncientFlameBalrogEntity>> {

        private final net.minecraft.client.renderer.ItemInHandRenderer itemInHandRenderer;

        public BalrogSwordLayer(
                MobRenderer<AncientFlameBalrogEntity, AncientFlameBalrogModel<AncientFlameBalrogEntity>> renderer,
                EntityRendererProvider.Context context) {
            super(renderer);
            this.itemInHandRenderer = context.getItemInHandRenderer();
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                           AncientFlameBalrogEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

            ItemStack mainhand = entity.getMainHandItem();
            if (mainhand.isEmpty()) return;

            poseStack.pushPose();

            // Step into the right arm's local coordinate space
            ModelPart rightArm = this.getParentModel().rightArm;
            rightArm.translateAndRotate(poseStack);

            // The arm box runs from y=-2 to y=15 in arm-local space (17 units tall).
            // Translate down to the fist, then orient the sword blade pointing forward.
            poseStack.translate(0.0D, 1.0D, 0.0D);          // move to fist tip
            poseStack.mulPose(Axis.XP.rotationDegrees(-90F)); // blade points forward
            poseStack.mulPose(Axis.ZP.rotationDegrees(180F)); // right-hand grip
            poseStack.scale(0.6F, 0.6F, 0.6F);              // scale to giant hand

            itemInHandRenderer.renderItem(
                    entity, mainhand,
                    ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                    false, poseStack, bufferSource, packedLight);

            poseStack.popPose();
        }
    }
}
