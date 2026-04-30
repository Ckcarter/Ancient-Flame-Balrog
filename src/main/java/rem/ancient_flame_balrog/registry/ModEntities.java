package rem.ancient_flame_balrog.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AncientFlameBalrog.MODID);

    public static final RegistryObject<EntityType<AncientFlameBalrogEntity>> ANCIENT_FLAME_BALROG =
            ENTITIES.register("ancient_flame_balrog", () ->
                    EntityType.Builder.of(AncientFlameBalrogEntity::new, MobCategory.MONSTER)
                            .sized(1.85F, 4.25F)
                            .fireImmune()
                            .clientTrackingRange(12)
                            .updateInterval(3)
                            .build("ancient_flame_balrog"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);

    }
}
