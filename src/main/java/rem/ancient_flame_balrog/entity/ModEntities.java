package rem.ancient_flame_balrog.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import rem.ancient_flame_balrog.AncientFlameBalrog;

public class ModEntities {
    public static final ResourceLocation ANCIENT_FLAME_BALROG_ID =
            new ResourceLocation(AncientFlameBalrog.MODID, "ancient_flame_balrog");

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AncientFlameBalrog.MODID);

    public static final RegistryObject<EntityType<AncientFlameBalrogEntity>> ANCIENT_FLAME_BALROG =
            ENTITY_TYPES.register("ancient_flame_balrog", () -> EntityType.Builder
                    .of(AncientFlameBalrogEntity::new, MobCategory.MONSTER)
                    .sized(2.2F, 4.8F)
                    .fireImmune()
                    .clientTrackingRange(12)
                    .updateInterval(3)
                    .build(ANCIENT_FLAME_BALROG_ID.toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
