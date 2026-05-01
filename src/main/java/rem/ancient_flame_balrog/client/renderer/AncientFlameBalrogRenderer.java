package rem.ancient_flame_balrog.client.renderer;



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
    }

    @Override
    public ResourceLocation getTextureLocation(AncientFlameBalrogEntity entity) {
        return TEXTURE;
    }
}
