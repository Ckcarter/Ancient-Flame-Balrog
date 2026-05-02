package rem.ancient_flame_balrog.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.block.BalrogProximitySpawnerBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AncientFlameBalrog.MODID);

    public static final RegistryObject<Block> BALROG_PROXIMITY_SPAWNER =
            BLOCKS.register("balrog_proximity_spawner", BalrogProximitySpawnerBlock::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
