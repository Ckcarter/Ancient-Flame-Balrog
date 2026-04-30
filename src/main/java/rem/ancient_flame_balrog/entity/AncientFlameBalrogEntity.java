package rem.ancient_flame_balrog.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;

import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class AncientFlameBalrogEntity extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.ancient_flame_balrog.ancient_flame_balrog"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private int roarCooldown = 160;

    public AncientFlameBalrogEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 250;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 450.0D)
                .add(Attributes.ATTACK_DAMAGE, 24.0D)
                .add(Attributes.ARMOR, 16.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.95D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BalrogFireballGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.05D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity target) {
            target.setSecondsOnFire(12);
            target.knockback(1.4D, this.getX() - target.getX(), this.getZ() - target.getZ());
            this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.45F, 0.8F);
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            if (--roarCooldown <= 0) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.6F, 0.55F);
                roarCooldown = 220 + this.random.nextInt(160);
            }
        }

        if (this.level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.FLAME, this.getRandomX(1.4D), this.getY() + 0.4D + this.random.nextDouble() * 3.8D, this.getRandomZ(1.4D), 0.0D, 0.04D, 0.0D);
            }
            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(1.6D), this.getY() + 3.4D, this.getRandomZ(1.6D), 0.0D, 0.035D, 0.0D);
            }
            if (this.random.nextInt(4) == 0) {
                this.level().addParticle(ParticleTypes.LAVA, this.getRandomX(1.1D), this.getY() + 1.2D, this.getRandomZ(1.1D), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable net.minecraft.nbt.CompoundTag tag) {
        this.setCustomName(Component.translatable("entity.ancient_flame_balrog.ancient_flame_balrog"));
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    public static boolean checkBalrogSpawnRules(EntityType<AncientFlameBalrogEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, reason, pos, random) && random.nextInt(80) == 0;
    }

    static class BalrogFireballGoal extends Goal {
        private final AncientFlameBalrogEntity balrog;
        private int attackStep;
        private int attackTime;

        public BalrogFireballGoal(AncientFlameBalrogEntity balrog) {
            this.balrog = balrog;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.balrog.getTarget();
            return target != null && target.isAlive() && this.balrog.distanceToSqr(target) > 36.0D;
        }

        @Override
        public void start() {
            this.attackStep = 0;
            this.attackTime = 0;
        }

        @Override
        public void stop() {
            this.attackStep = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.balrog.getTarget();
            if (target == null) return;

            double distance = this.balrog.distanceToSqr(target);
            this.balrog.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (distance < 225.0D) {
                this.balrog.getNavigation().stop();
            } else {
                this.balrog.getNavigation().moveTo(target, 0.85D);
            }

            if (--this.attackTime <= 0) {
                ++this.attackStep;
                if (this.attackStep == 1) {
                    this.attackTime = 20;
                } else if (this.attackStep <= 4) {
                    this.attackTime = 12;
                    shootFireball(target);
                } else {
                    this.attackTime = 80;
                    this.attackStep = 0;
                }
            }
        }

        private void shootFireball(LivingEntity target) {
            Level level = this.balrog.level();
            double dx = target.getX() - this.balrog.getX();
            double dy = target.getY(0.5D) - this.balrog.getY(0.6D);
            double dz = target.getZ() - this.balrog.getZ();

            SmallFireball fireball = new SmallFireball(level, this.balrog, dx, dy, dz);
            fireball.setPos(
                    this.balrog.getX() + Mth.sin(this.balrog.getYRot() * ((float)Math.PI / 180F)) * -1.3D,
                    this.balrog.getY(0.65D),
                    this.balrog.getZ() + Mth.cos(this.balrog.getYRot() * ((float)Math.PI / 180F)) * 1.3D
            );
            level.addFreshEntity(fireball);
            level.playSound(null, this.balrog.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.3F, 0.55F);
        }
    }
}
