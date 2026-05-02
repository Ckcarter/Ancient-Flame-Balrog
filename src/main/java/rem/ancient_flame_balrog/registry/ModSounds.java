package rem.ancient_flame_balrog.registry;

import rem.ancient_flame_balrog.AncientFlameBalrog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AncientFlameBalrog.MODID);

    public static final RegistryObject<SoundEvent> BALROG_ROAR = registerSound("balrog_roar");
    public static final RegistryObject<SoundEvent> BALROG_WINGS = registerSound("balrog_wings");
    public static final RegistryObject<SoundEvent> BALROG_FIRE = registerSound("balrog_fire");
    public static final RegistryObject<SoundEvent> BALROG_BOSS_INTRO = registerSound("balrog_boss_intro");
    public static final RegistryObject<SoundEvent> BALROG_SLAM = registerSound("balrog_slam");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(AncientFlameBalrog.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
