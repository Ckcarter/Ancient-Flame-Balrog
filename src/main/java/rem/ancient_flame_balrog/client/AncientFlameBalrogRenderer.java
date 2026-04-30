package rem.ancient_flame_balrog.client;

import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.client.model.AncientFlameBalrogModel;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AncientFlameBalrogRenderer extends MobRenderer<AncientFlameBalrogEntity, AncientFlameBalrogModel<AncientFlameBalrogEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AncientFlameBalrog.MODID, "textures/entity/ancient_flame_balrog.png");

    public AncientFlameBalrogRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientFlameBalrogModel<>(context.bakeLayer(ClientModEvents.BALROG_LAYER)), 1.25F);
    }

    @Override
    public ResourceLocation getTextureLocation(AncientFlameBalrogEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(AncientFlameBalrogEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.15F, 1.15F, 1.15F);
    }
}
