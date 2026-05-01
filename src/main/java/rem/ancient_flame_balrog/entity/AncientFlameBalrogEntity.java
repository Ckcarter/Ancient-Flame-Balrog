package rem.ancient_flame_balrog.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AncientFlameBalrogEntity extends Monster implements RangedAttackMob {
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    private int groundSlamCooldown = 80;
    private int fireRainCooldown = 120;
    private int roarCooldown = 160;
    private int legendaryStompCooldown = 20;
    private boolean ragePhaseStarted = false;

    public AncientFlameBalrogEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 750;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.ARMOR, 30.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 18.0D)
                .add(Attributes.ATTACK_DAMAGE, 36.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.4D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 96.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 28, 42.0F));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.22D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.85D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 40.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        spawnLegendaryFlameAura();
        spawnWalkingFireTrail();

        if (this.level().isClientSide) {
            return;
        }

        this.bossEvent.setName(this.getDisplayName());
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        tickCooldowns();
        burnNearbyEnemies();
        tryStartRagePhase();
        legendaryStompWhileWalking();

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double distanceSqr = this.distanceToSqr(target);

            if (this.groundSlamCooldown <= 0 && distanceSqr <= 100.0D) {
                groundSlam();
                this.groundSlamCooldown = ragePhaseStarted ? 85 : 115;
            }

            if (this.fireRainCooldown <= 0 && distanceSqr <= 1600.0D) {
                fireRain(target);
                this.fireRainCooldown = ragePhaseStarted ? 95 : 135;
            }
        }

        if (this.roarCooldown <= 0) {
            roar();
            this.roarCooldown = ragePhaseStarted ? 150 : 220;
        }
    }

    private void spawnLegendaryFlameAura() {
        if (!this.level().isClientSide || this.tickCount % 2 != 0) {
            return;
        }

        int count = ragePhaseStarted ? 18 : 10;
        for (int i = 0; i < count; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.25D;
            double y = this.getY() + this.random.nextDouble() * this.getBbHeight();
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.25D;
            this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.04D, 0.0D);
            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.025D, 0.0D);
            }
            if (ragePhaseStarted && this.random.nextInt(5) == 0) {
                this.level().addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.02D, 0.0D);
            }
        }
    }

    private void spawnWalkingFireTrail() {
        Vec3 movement = this.getDeltaMovement();
        double horizontalSpeedSqr = movement.x * movement.x + movement.z * movement.z;

        if (!this.onGround() || horizontalSpeedSqr < 0.0025D || this.tickCount % 3 != 0) {
            return;
        }

        double backX = this.getX() - movement.x * 6.0D;
        double backZ = this.getZ() - movement.z * 6.0D;
        double y = this.getY() + 0.08D;

        if (this.level().isClientSide) {
            for (int i = 0; i < 10; i++) {
                double spreadX = (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.1D;
                double spreadZ = (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.1D;
                this.level().addParticle(ParticleTypes.FLAME, backX + spreadX, y, backZ + spreadZ, 0.0D, 0.05D, 0.0D);
                if (this.random.nextBoolean()) {
                    this.level().addParticle(ParticleTypes.SMOKE, backX + spreadX, y + 0.05D, backZ + spreadZ, 0.0D, 0.02D, 0.0D);
                }
            }
            return;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, backX, y, backZ, ragePhaseStarted ? 22 : 14, this.getBbWidth() * 0.55D, 0.06D, this.getBbWidth() * 0.55D, 0.025D);
            serverLevel.sendParticles(ParticleTypes.SMOKE, backX, y + 0.05D, backZ, ragePhaseStarted ? 10 : 5, this.getBbWidth() * 0.45D, 0.05D, this.getBbWidth() * 0.45D, 0.012D);
            if (this.random.nextInt(ragePhaseStarted ? 2 : 4) == 0) {
                serverLevel.sendParticles(ParticleTypes.LAVA, backX, y + 0.05D, backZ, 2, this.getBbWidth() * 0.35D, 0.03D, this.getBbWidth() * 0.35D, 0.01D);
            }
        }
    }

    private void tickCooldowns() {
        if (this.groundSlamCooldown > 0) this.groundSlamCooldown--;
        if (this.fireRainCooldown > 0) this.fireRainCooldown--;
        if (this.roarCooldown > 0) this.roarCooldown--;
        if (this.legendaryStompCooldown > 0) this.legendaryStompCooldown--;
    }

    private void burnNearbyEnemies() {
        AABB aura = this.getBoundingBox().inflate(ragePhaseStarted ? 8.0D : 6.0D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aura, this::canHurtWithBossAura)) {
            if (this.tickCount % (ragePhaseStarted ? 20 : 30) == 0) {
                target.setSecondsOnFire(ragePhaseStarted ? 9 : 6);
                target.hurt(this.damageSources().onFire(), ragePhaseStarted ? 4.0F : 2.5F);
            }
        }
    }

    private boolean canHurtWithBossAura(LivingEntity entity) {
        if (entity == this || entity instanceof AncientFlameBalrogEntity) {
            return false;
        }
        return !(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator());
    }

    private void tryStartRagePhase() {
        if (ragePhaseStarted || this.getHealth() > this.getMaxHealth() * 0.50F) {
            return;
        }

        ragePhaseStarted = true;
        this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 3.0F, 0.35F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 2.0D, this.getZ(), 8, 2.5D, 1.5D, 2.5D, 0.02D);
            serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 2.0D, this.getZ(), 80, 3.0D, 2.0D, 3.0D, 0.05D);
        }
    }

    private void legendaryStompWhileWalking() {
        Vec3 movement = this.getDeltaMovement();
        double horizontalSpeedSqr = movement.x * movement.x + movement.z * movement.z;
        if (!this.onGround() || horizontalSpeedSqr < 0.003D || this.legendaryStompCooldown > 0) {
            return;
        }

        this.legendaryStompCooldown = ragePhaseStarted ? 12 : 18;
        this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.45F, 0.5F);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.1D, this.getZ(), 6, 1.6D, 0.05D, 1.6D, 0.01D);
        }

        AABB stompArea = this.getBoundingBox().inflate(3.5D, 1.2D, 3.5D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, stompArea, this::canHurtWithBossAura)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 0));
        }
    }

    private void groundSlam() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.2F, 0.55F);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.4D, this.getZ(), 3, 1.0D, 0.25D, 1.0D, 0.01D);
        serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 0.2D, this.getZ(), ragePhaseStarted ? 150 : 100, 5.5D, 0.35D, 5.5D, 0.04D);
        serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 0.2D, this.getZ(), ragePhaseStarted ? 60 : 40, 4.0D, 0.25D, 4.0D, 0.03D);

        AABB slamArea = this.getBoundingBox().inflate(ragePhaseStarted ? 10.0D : 8.0D, 3.0D, ragePhaseStarted ? 10.0D : 8.0D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, slamArea, this::canHurtWithBossAura)) {
            target.hurt(this.damageSources().mobAttack(this), ragePhaseStarted ? 18.0F : 14.0F);
            target.setSecondsOnFire(ragePhaseStarted ? 12 : 8);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));

            Vec3 push = target.position().subtract(this.position()).normalize().scale(ragePhaseStarted ? 1.7D : 1.3D).add(0.0D, ragePhaseStarted ? 0.9D : 0.7D, 0.0D);
            target.setDeltaMovement(target.getDeltaMovement().add(push));
            target.hurtMarked = true;
        }
    }

    private void fireRain(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        this.level().playSound(null, this.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.8F, 0.5F);

        int fireballs = ragePhaseStarted ? 10 : 6;
        for (int i = 0; i < fireballs; i++) {
            double offsetX = (this.random.nextDouble() - 0.5D) * (ragePhaseStarted ? 14.0D : 10.0D);
            double offsetZ = (this.random.nextDouble() - 0.5D) * (ragePhaseStarted ? 14.0D : 10.0D);
            double startX = target.getX() + offsetX;
            double startY = target.getY() + 9.0D + this.random.nextDouble() * 5.0D;
            double startZ = target.getZ() + offsetZ;

            double dx = target.getX() - startX;
            double dy = target.getY(0.5D) - startY;
            double dz = target.getZ() - startZ;

            LargeFireball fireball = new LargeFireball(this.level(), this, dx, dy, dz, ragePhaseStarted ? 2 : 1);
            fireball.setPos(startX, startY, startZ);
            this.level().addFreshEntity(fireball);
        }

        serverLevel.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 1.0D, target.getZ(), 50, 3.0D, 1.3D, 3.0D, 0.025D);
    }

    private void roar() {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, ragePhaseStarted ? 3.0F : 2.2F, ragePhaseStarted ? 0.35F : 0.45F);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 3.2D, this.getZ(), ragePhaseStarted ? 90 : 55, 2.8D, 1.5D, 2.8D, 0.025D);
        }

        AABB roarArea = this.getBoundingBox().inflate(12.0D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, roarArea, this::canHurtWithBossAura)) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 70, 0));
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit) {
            entity.setSecondsOnFire(ragePhaseStarted ? 14 : 10);
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
            }
        }
        return hit;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.5D) - this.getY(0.5D);
        double dz = target.getZ() - this.getZ();
        LargeFireball fireball = new LargeFireball(this.level(), this, dx, dy, dz, ragePhaseStarted ? 5 : 3);
        fireball.setPos(this.getX(), this.getY(0.75D), this.getZ());
        this.level().addFreshEntity(fireball);
        this.level().playSound(null, this.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.4F, 0.65F);
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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurt(source, amount);
    }
}
