package rem.ancient_flame_balrog.entity;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import rem.ancient_flame_balrog.registry.ModItems;

public class AncientFlameBalrogEntity extends Monster implements RangedAttackMob {
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    private int introTicks = 60;
    private int attackWindup = 0;
    private int attackCooldown = 120;
    private int queuedAttack = 0; // 1 ground slam, 2 fireball volley, 3 roar pressure
    private int footstepPulse = 0;
    private boolean ragePhaseStarted = false;
    private boolean introPlayed = false;

    public AncientFlameBalrogEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 1000;
        this.setPersistenceRequired();
        this.setNoGravity(false);
        this.setMaxUpStep(3.0F);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SHADOW_BLADE.get()));
        this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(3.0F, 8.5F);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 7.4F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1200.0D)
                .add(Attributes.ARMOR, 32.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 42.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.7D)
                .add(Attributes.MOVEMENT_SPEED, 0.350D)
                .add(Attributes.FOLLOW_RANGE, 96.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Strong chase behavior. The Balrog should always walk at its target.
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1.20D, 96.0F));

        // Only idle when it has nothing to attack.
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.65D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 64.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        this.setNoAi(false);
        forceBossPursuit();

        // Keep the Balrog armed with the custom flaming sword.
        if (!this.level().isClientSide && !this.getMainHandItem().is(ModItems.SHADOW_BLADE.get())) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SHADOW_BLADE.get()));
        }
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().x, Math.min(this.getDeltaMovement().y, -0.25D), this.getDeltaMovement().z);
        }

        if (this.level().isClientSide) {
            spawnFinalBalrogFlames();
        }
    }

    /**
     * Vanilla pathfinding can fail for very large boss mobs because their 3x8.5 hitbox
     * needs a huge clear path. This method gives the boss a direct walking push toward
     * the nearest survival/adventure player, so it still moves like a boss on normal terrain.
     */
    private void forceBossPursuit() {
        if (this.level().isClientSide || this.isNoAi()) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || (target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
            Player nearest = this.level().getNearestPlayer(this, 96.0D);
            if (nearest != null && !nearest.isCreative() && !nearest.isSpectator()) {
                this.setTarget(nearest);
                target = nearest;
            }
        }

        if (target == null || !target.isAlive()) {
            return;
        }

        faceTargetNow(target);

        double distanceSqr = this.distanceToSqr(target);
        if (distanceSqr <= this.getMeleeAttackRangeSqr(target)) {
            return;
        }

        faceTargetNow(target);
        this.getNavigation().moveTo(target, 1.25D);

        Vec3 toTarget = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (toTarget.lengthSqr() < 0.0001D) {
            return;
        }

        Vec3 direction = toTarget.normalize();
        double pushSpeed = this.ragePhaseStarted ? 0.16D : 0.13D;
        Vec3 current = this.getDeltaMovement();
        this.setDeltaMovement(
                current.x * 0.70D + direction.x * pushSpeed,
                current.y,
                current.z * 0.70D + direction.z * pushSpeed
        );
        this.hasImpulse = true;
    }

    /**
     * Force the Balrog to visually face its current target.
     * This rotates the whole boss body and syncs the head/body render yaw so the model
     * looks directly at the player instead of staring off to the side.
     */
    private void faceTargetNow(LivingEntity target) {
        if (target == null) {
            return;
        }

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        if (dx * dx + dz * dz < 0.0001D) {
            return;
        }

        float wantedYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);

        this.setYRot(wantedYaw);
        this.yRotO = wantedYaw;
        this.yBodyRot = wantedYaw;
        this.yBodyRotO = wantedYaw;
        this.yHeadRot = wantedYaw;
        this.yHeadRotO = wantedYaw;

        double dy = target.getEyeY() - this.getEyeY();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float wantedPitch = (float)(-(Math.toDegrees(Math.atan2(dy, horizontal))));
        this.setXRot(wantedPitch);
        this.xRotO = wantedPitch;

        this.getLookControl().setLookAt(target, 60.0F, 60.0F);
    }

    private void spawnFinalBalrogFlames() {
        boolean enraged = this.getHealth() <= this.getMaxHealth() * 0.50F;
        int flameCount = enraged ? 10 : 6;
        double width = Math.max(1.0D, this.getBbWidth());
        double height = Math.max(1.0D, this.getBbHeight());

        for (int i = 0; i < flameCount; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * width * 0.95D;
            double y = this.getY() + 0.45D + this.random.nextDouble() * height * 0.88D;
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * width * 0.95D;
            this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.025D + this.random.nextDouble() * 0.02D, 0.0D);

            if (i % 3 == 0) {
                this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.01D, 0.0D);
            }
        }

        for (int i = 0; i < (enraged ? 5 : 3); i++) {
            double side = this.random.nextBoolean() ? 1.0D : -1.0D;
            double x = this.getX() + side * (width * 0.55D + this.random.nextDouble() * width * 0.75D);
            double y = this.getY() + height * (0.62D + this.random.nextDouble() * 0.25D);
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * width * 0.85D;
            this.level().addParticle(ParticleTypes.FLAME, x, y, z, side * 0.015D, -0.01D, 0.0D);
        }

        if (this.tickCount % 2 == 0) {
            double wingY = this.getY() + height * 0.72D;
            double wingZ = this.getZ() + width * 0.35D;
            double wingSpan = width * 1.45D;
            this.level().addParticle(ParticleTypes.FLAME, this.getX() + wingSpan, wingY, wingZ, 0.0D, 0.02D, 0.0D);
            this.level().addParticle(ParticleTypes.FLAME, this.getX() - wingSpan, wingY, wingZ, 0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            return;
        }

        this.bossEvent.setName(this.getDisplayName());
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        playIntroOnce();
        tryStartRagePhase();
        tickCinematicCombat();
        slowStompPulse();
    }

    private void playIntroOnce() {
        if (introPlayed) {
            return;
        }
        introPlayed = true;
        this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 3.0F, 0.35F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 1.0D, this.getZ(), 35, 2.4D, 0.2D, 2.4D, 0.01D);
        }
    }

    private void tryStartRagePhase() {
        if (ragePhaseStarted || this.getHealth() > this.getMaxHealth() * 0.50F) {
            return;
        }

        ragePhaseStarted = true;
        this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 3.3F, 0.3F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 2.5D, this.getZ(), 60, 3.0D, 1.2D, 3.0D, 0.02D);
        }
    }

    private void tickCinematicCombat() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            queuedAttack = 0;
            attackWindup = 0;
            if (attackCooldown > 0) attackCooldown--;
            return;
        }

        if (introTicks > 0) {
            introTicks--;
            faceTargetNow(target);
            return;
        }

        if (attackWindup > 0) {
            attackWindup--;
            faceTargetNow(target);
            if (attackWindup == 0) {
                executeQueuedAttack(target);
                attackCooldown = ragePhaseStarted ? 95 : 125;
                queuedAttack = 0;
            }
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        double distanceSqr = this.distanceToSqr(target);
        if (distanceSqr <= 81.0D) {
            queuedAttack = 1; // ground slam
            attackWindup = ragePhaseStarted ? 24 : 34;
            telegraphAttack(ParticleTypes.ASH, 16);
            this.level().playSound(null, this.blockPosition(), SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.HOSTILE, 1.3F, 0.55F);
        } else if (distanceSqr <= 900.0D) {
            queuedAttack = 2; // fireball volley
            attackWindup = ragePhaseStarted ? 20 : 30;
            telegraphAttack(ParticleTypes.FLAME, 18);
            this.level().playSound(null, this.blockPosition(), SoundEvents.BLAZE_AMBIENT, SoundSource.HOSTILE, 1.2F, 0.45F);
        } else {
            queuedAttack = 3; // roar pressure
            attackWindup = 40;
            this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 0.4F);
        }
    }

    private void telegraphAttack(net.minecraft.core.particles.ParticleOptions particle, int count) {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, this.getX(), this.getY() + 0.25D, this.getZ(), count, 1.8D, 0.1D, 1.8D, 0.006D);
        }
    }

    private void executeQueuedAttack(LivingEntity target) {
        if (queuedAttack == 1) {
            groundSlam();
        } else if (queuedAttack == 2) {
            fireballVolley(target);
        } else if (queuedAttack == 3) {
            roarPressure();
        }
    }

    private void groundSlam() {
        this.level().playSound(null, this.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 2.0F, 0.45F);
        this.level().playSound(null, this.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1.8F, 0.55F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.2D, this.getZ(), 32, 3.6D, 0.1D, 3.6D, 0.03D);
            serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 1.0D, this.getZ(), 48, 4.0D, 0.5D, 4.0D, 0.08D);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 20, 3.0D, 0.2D, 3.0D, 0.01D);
        }

        AABB slamArea = this.getBoundingBox().inflate(ragePhaseStarted ? 9.0D : 7.0D, 3.0D, ragePhaseStarted ? 9.0D : 7.0D);
        for (LivingEntity victim : this.level().getEntitiesOfClass(LivingEntity.class, slamArea, this::canHurtBossTarget)) {
            victim.hurt(this.damageSources().mobAttack(this), ragePhaseStarted ? 24.0F : 18.0F);
            victim.setSecondsOnFire(ragePhaseStarted ? 12 : 8);
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            Vec3 push = victim.position().subtract(this.position()).normalize().scale(ragePhaseStarted ? 1.4D : 1.0D).add(0.0D, 0.55D, 0.0D);
            victim.setDeltaMovement(victim.getDeltaMovement().add(push));
            victim.hurtMarked = true;
        }
    }

    /**
     * Hurls a spread of large exploding fireballs at the target.
     * In rage phase, fires an extra fireball and uses a larger explosion power.
     */
    private void fireballVolley(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        this.level().playSound(null, this.blockPosition(), SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 2.5F, 0.5F);

        // Launch origin: chest height
        Vec3 origin = this.position().add(0.0D, this.getBbHeight() * 0.65D, 0.0D);
        Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);

        int count = ragePhaseStarted ? 4 : 2;
        int explosionPower = ragePhaseStarted ? 3 : 2;
        double spread = 0.18D;

        for (int i = 0; i < count; i++) {
            double offsetX = (this.random.nextDouble() - 0.5D) * spread * 2.0D;
            double offsetY = (this.random.nextDouble() - 0.5D) * spread;
            double offsetZ = (this.random.nextDouble() - 0.5D) * spread * 2.0D;

            Vec3 dir = targetPos.subtract(origin).normalize().add(offsetX, offsetY, offsetZ);

            LargeFireball fireball = new LargeFireball(
                    serverLevel, this,
                    dir.x, dir.y, dir.z,
                    explosionPower
            );
            fireball.setPos(origin.x, origin.y, origin.z);
            serverLevel.addFreshEntity(fireball);

            // Puff of fire particles at launch point
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    origin.x, origin.y, origin.z,
                    6, 0.3D, 0.3D, 0.3D, 0.05D);
        }
    }

    private void whipLash(LivingEntity target) {
        this.level().playSound(null, this.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1.5F, 0.62F);

        Vec3 start = this.position().add(0.0D, this.getBbHeight() * 0.62D, 0.0D);
        Vec3 end = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 direction = end.subtract(start).normalize();
        double range = ragePhaseStarted ? 22.0D : 18.0D;
        AABB lashBox = this.getBoundingBox().expandTowards(direction.scale(range)).inflate(2.0D);

        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 18; i++) {
                Vec3 point = start.add(direction.scale(i * (range / 18.0D)));
                serverLevel.sendParticles(ParticleTypes.FLAME, point.x, point.y, point.z, 1, 0.05D, 0.05D, 0.05D, 0.0D);
            }
        }

        for (LivingEntity victim : this.level().getEntitiesOfClass(LivingEntity.class, lashBox, this::canHurtBossTarget)) {
            Vec3 toVictim = victim.position().add(0.0D, victim.getBbHeight() * 0.5D, 0.0D).subtract(start);
            double along = toVictim.dot(direction);
            if (along < 0.0D || along > range) continue;
            double sideDistance = toVictim.subtract(direction.scale(along)).length();
            if (sideDistance > 2.6D) continue;

            victim.hurt(this.damageSources().mobAttack(this), ragePhaseStarted ? 18.0F : 13.0F);
            victim.setSecondsOnFire(ragePhaseStarted ? 8 : 5);
            Vec3 pull = this.position().subtract(victim.position()).normalize().scale(0.7D).add(0.0D, 0.15D, 0.0D);
            victim.setDeltaMovement(victim.getDeltaMovement().add(pull));
            victim.hurtMarked = true;
        }
    }

    private void roarPressure() {
        this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, ragePhaseStarted ? 3.0F : 2.2F, ragePhaseStarted ? 0.32F : 0.42F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 4.0D, this.getZ(), 45, 2.8D, 1.0D, 2.8D, 0.018D);
        }

        AABB roarArea = this.getBoundingBox().inflate(ragePhaseStarted ? 16.0D : 12.0D);
        for (LivingEntity victim : this.level().getEntitiesOfClass(LivingEntity.class, roarArea, this::canHurtBossTarget)) {
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
        }
    }

    private void slowStompPulse() {
        Vec3 movement = this.getDeltaMovement();
        double horizontalSpeedSqr = movement.x * movement.x + movement.z * movement.z;
        if (!this.onGround() || horizontalSpeedSqr < 0.0025D || footstepPulse > 0) {
            if (footstepPulse > 0) footstepPulse--;
            return;
        }

        footstepPulse = ragePhaseStarted ? 26 : 34;
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.05D, this.getZ(), 5, 1.4D, 0.03D, 1.4D, 0.004D);
        }
    }

    private boolean canHurtBossTarget(LivingEntity entity) {
        if (entity == this || entity instanceof AncientFlameBalrogEntity) {
            return false;
        }
        return !(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator());
    }

    public int getQueuedAttack() {
        return queuedAttack;
    }

    public int getAttackWindup() {
        return attackWindup;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Keep normal walking silent; cinematic stomp pulses are handled manually.
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit) {
            entity.setSecondsOnFire(ragePhaseStarted ? 10 : 6);
            if (entity instanceof LivingEntity living) {
            }
        }
        return hit;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        // Ranged behavior is handled by the cinematic fireball volley wind-up.
        if (this.attackCooldown <= 20 && this.attackWindup <= 0) {
            this.queuedAttack = 2;
            this.attackWindup = ragePhaseStarted ? 18 : 28;
        }
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity target) {
        return 64.0D;
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
