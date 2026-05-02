package rem.ancient_flame_balrog.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.block.entity.BalrogProximitySpawnerBlockEntity;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AncientFlameBalrog.MODID);

    public static final RegistryObject<BlockEntityType<BalrogProximitySpawnerBlockEntity>> BALROG_PROXIMITY_SPAWNER =
            BLOCK_ENTITIES.register("balrog_proximity_spawner",
                    () -> BlockEntityType.Builder.of(
                            BalrogProximitySpawnerBlockEntity::new,
                            ModBlocks.BALROG_PROXIMITY_SPAWNER.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
