package rem.ancient_flame_balrog.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ShadowBladeItem extends SwordItem {
    public ShadowBladeItem() {
        super(Tiers.NETHERITE, 7, -2.4F, new Item.Properties().stacksTo(1).fireResistant());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide || !selected || !(entity instanceof LivingEntity living)) {
            return;
        }

        Vec3 look = living.getLookAngle();
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        if (right.lengthSqr() < 0.001D) {
            right = new Vec3(1, 0, 0);
        }

        float swing = living.attackAnim;
        double arc = Math.sin(swing * Math.PI);

        Vec3 blade = living.position()
                .add(0, living.getBbHeight() * 0.62D, 0)
                .add(right.scale(0.42D + arc * 0.16D))
                .add(look.scale(0.55D + arc * 0.45D))
                .add(0, arc * 0.22D, 0);

        for (int i = 0; i < 2; i++) {
            level.addParticle(
                    ParticleTypes.PORTAL,
                    blade.x + (level.random.nextDouble() - 0.5D) * 0.12D,
                    blade.y + (level.random.nextDouble() - 0.5D) * 0.12D,
                    blade.z + (level.random.nextDouble() - 0.5D) * 0.12D,
                    (level.random.nextDouble() - 0.5D) * 0.04D,
                    0.02D,
                    (level.random.nextDouble() - 0.5D) * 0.04D
            );
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 45, 0));

        if (attacker.level().isClientSide) {
            for (int i = 0; i < 12; i++) {
                attacker.level().addParticle(ParticleTypes.PORTAL,
                        target.getX(),
                        target.getY() + target.getBbHeight() * 0.55D,
                        target.getZ(),
                        (attacker.level().random.nextDouble() - 0.5D) * 0.35D,
                        attacker.level().random.nextDouble() * 0.15D,
                        (attacker.level().random.nextDouble() - 0.5D) * 0.35D);
            }
        }

        if (attacker instanceof Player player) {
            player.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.45F, 0.65F);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5A blade wrapped in living shadow"));
        tooltip.add(Component.literal("§8Slows and blinds enemies"));
    }
}
