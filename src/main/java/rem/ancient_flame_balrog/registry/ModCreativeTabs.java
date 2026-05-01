package rem.ancient_flame_balrog.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import rem.ancient_flame_balrog.AncientFlameBalrog;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AncientFlameBalrog.MODID);

    public static final RegistryObject<CreativeModeTab> BALROG_TAB =
            CREATIVE_MODE_TABS.register("balrog_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ancient_flame_balrog"))
                    .icon(() -> ModItems.ANCIENT_FLAME_BALROG_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.ANCIENT_FLAME_BALROG_SPAWN_EGG.get());
                        output.accept(ModItems.BALROG_HORN.get());
                        output.accept(ModItems.FIRE_HEART.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
