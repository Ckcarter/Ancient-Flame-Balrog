package rem.ancient_flame_balrog.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FlamingBalrogSwordItem extends SwordItem {
    public FlamingBalrogSwordItem() {
        super(Tiers.NETHERITE, 9, -2.8F, new Item.Properties().stacksTo(1).fireResistant());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setSecondsOnFire(10);
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 0));
        attacker.level().addParticle(ParticleTypes.FLAME, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(), 0.0D, 0.08D, 0.0D);
        if (attacker instanceof Player player) {
            player.playSound(SoundEvents.FIRECHARGE_USE, 0.8F, 0.7F);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6Forged for the Ancient Flame Balrog"));
        tooltip.add(Component.literal("§cBurns, weakens, and slows enemies"));
    }
}
