package rem.ancient_flame_balrog.client;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.client.model.AncientFlameBalrogModel;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;

public class AncientFlameBalrogRenderer extends MobRenderer<AncientFlameBalrogEntity, AncientFlameBalrogModel<AncientFlameBalrogEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AncientFlameBalrog.MODID, "textures/entity/ancient_flame_balrog.png");

    public AncientFlameBalrogRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientFlameBalrogModel<>(context.bakeLayer(AncientFlameBalrogModel.LAYER_LOCATION)), 1.6F);
    
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
}

    @Override
    public ResourceLocation getTextureLocation(AncientFlameBalrogEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(AncientFlameBalrogEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.8F, 2.8F, 2.8F);
    }
}
