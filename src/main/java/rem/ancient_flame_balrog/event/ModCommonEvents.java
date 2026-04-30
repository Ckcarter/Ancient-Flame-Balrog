package rem.ancient_flame_balrog.event;

import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;
import rem.ancient_flame_balrog.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AncientFlameBalrog.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ANCIENT_FLAME_BALROG.get(), AncientFlameBalrogEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntities.ANCIENT_FLAME_BALROG.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AncientFlameBalrogEntity::checkBalrogSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }
}
