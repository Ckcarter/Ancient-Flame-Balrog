package rem.ancient_flame_balrog.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import rem.ancient_flame_balrog.AncientFlameBalrog;
import rem.ancient_flame_balrog.entity.ModEntities;
import rem.ancient_flame_balrog.item.FlamingBalrogSwordItem;
import rem.ancient_flame_balrog.item.ShadowBladeItem;
import rem.ancient_flame_balrog.item.OneTimeBalrogSpawnerItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AncientFlameBalrog.MODID);

    public static final RegistryObject<Item> ANCIENT_FLAME_BALROG_SPAWN_EGG =
            ITEMS.register("ancient_flame_balrog_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.ANCIENT_FLAME_BALROG, 0x1b0b08, 0xff5a00, new Item.Properties()));

    public static final RegistryObject<Item> BALROG_HORN =
            ITEMS.register("balrog_horn", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FIRE_HEART =
            ITEMS.register("fire_heart", () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> FLAMING_BALROG_SWORD =
            ITEMS.register("flaming_balrog_sword", FlamingBalrogSwordItem::new);

    public static final RegistryObject<Item> SHADOW_BLADE =
            ITEMS.register("shadow_blade", ShadowBladeItem::new);

//    public static final RegistryObject<Item> ONE_TIME_BALROG_SPAWNER =
//            ITEMS.register("one_time_balrog_spawner", OneTimeBalrogSpawnerItem::new);

    public static final RegistryObject<Item> BALROG_PROXIMITY_SPAWNER =
            ITEMS.register("balrog_proximity_spawner",
                    () -> new BlockItem(ModBlocks.BALROG_PROXIMITY_SPAWNER.get(),
                            new Item.Properties().fireResistant()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
