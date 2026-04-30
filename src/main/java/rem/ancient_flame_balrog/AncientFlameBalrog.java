package rem.ancient_flame_balrog;

import rem.ancient_flame_balrog.registry.ModCreativeTabs;
import rem.ancient_flame_balrog.registry.ModEntities;
import rem.ancient_flame_balrog.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AncientFlameBalrog.MODID)
public class AncientFlameBalrog {
    public static final String MODID = "ancient_flame_balrog";

    public AncientFlameBalrog() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
