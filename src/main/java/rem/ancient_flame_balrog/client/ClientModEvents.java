package rem.ancient_flame_balrog.client;

import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.client.model.AncientFlameBalrogModel;
import rem.ancient_flame_balrog.registry.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AncientFlameBalrog.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final ModelLayerLocation BALROG_LAYER =
            new ModelLayerLocation(new ResourceLocation(AncientFlameBalrog.MODID, "ancient_flame_balrog"), "main");

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BALROG_LAYER, AncientFlameBalrogModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ANCIENT_FLAME_BALROG.get(), AncientFlameBalrogRenderer::new);
    }
}
