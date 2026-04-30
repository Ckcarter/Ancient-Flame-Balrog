package rem.ancient_flame_balrog.registry;

import rem.ancient_flame_balrog.AncientFlameBalrog;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AncientFlameBalrog.MODID);

    public static final RegistryObject<Item> ANCIENT_FLAME_BALROG_SPAWN_EGG =
            ITEMS.register("ancient_flame_balrog_spawn_egg", () ->
                    new ForgeSpawnEggItem(ModEntities.ANCIENT_FLAME_BALROG, 0x100707, 0xff5a00, new Item.Properties()));

    public static final RegistryObject<Item> BALROG_HORN =
            ITEMS.register("balrog_horn", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FIRE_HEART =
            ITEMS.register("fire_heart", () -> new Item(new Item.Properties().fireResistant()));
}
