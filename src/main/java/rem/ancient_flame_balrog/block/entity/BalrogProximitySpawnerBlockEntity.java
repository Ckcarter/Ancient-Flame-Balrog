package rem.ancient_flame_balrog.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import rem.ancient_flame_balrog.block.BalrogProximitySpawnerBlock;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;
import rem.ancient_flame_balrog.entity.ModEntities;
import rem.ancient_flame_balrog.registry.ModBlockEntities;
import rem.ancient_flame_balrog.world.BalrogBossSpawnData;

public class BalrogProximitySpawnerBlockEntity extends BlockEntity {
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final double PLAYER_RANGE = 12.0D;

    private int tickCounter = 0;
    private boolean used = false;

    public BalrogProximitySpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BALROG_PROXIMITY_SPAWNER.get(), pos, state);
        this.used = state.hasProperty(BalrogProximitySpawnerBlock.USED) && state.getValue(BalrogProximitySpawnerBlock.USED);
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, BalrogProximitySpawnerBlockEntity spawner) {
        if (spawner.used || state.getValue(BalrogProximitySpawnerBlock.USED)) {
            return;
        }

        spawner.tickCounter++;
        if (spawner.tickCounter < CHECK_INTERVAL_TICKS) {
            return;
        }
        spawner.tickCounter = 0;

        Player nearbyPlayer = level.getNearestPlayer(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                PLAYER_RANGE,
                false
        );

        if (nearbyPlayer == null) {
            return;
        }

        ServerLevel overworld = level.getServer().overworld();
        BalrogBossSpawnData spawnData = overworld.getDataStorage().computeIfAbsent(
                BalrogBossSpawnData::load,
                BalrogBossSpawnData::new,
                BalrogBossSpawnData.dataName()
        );

        if (spawnData.hasBossSpawned()) {
            spawner.markUsed(level, pos, state);
            nearbyPlayer.displayClientMessage(
                    Component.literal("The Ancient Flame Balrog has already been summoned in this world."),
                    true
            );
            return;
        }

        BlockPos spawnPos = findSpawnPos(level, pos);

        AncientFlameBalrogEntity balrog = ModEntities.ANCIENT_FLAME_BALROG.get().create(level);
        if (balrog == null) {
            return;
        }

        balrog.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F,
                0.0F
        );

        balrog.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.EVENT,
                null,
                null
        );

        if (level.addFreshEntity(balrog)) {
            spawnData.markBossSpawned();
            spawner.markUsed(level, pos, state);
            nearbyPlayer.displayClientMessage(
                    Component.literal("The Ancient Flame Balrog awakens!"),
                    true
            );
        }
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos spawnerPos) {
        // Spawn a few blocks above/in front of the spawner if possible.
        BlockPos preferred = spawnerPos.above();

        if (level.getBlockState(preferred).isAir() && level.getBlockState(preferred.above()).isAir()) {
            return preferred;
        }

        for (int radius = 1; radius <= 5; radius++) {
            for (BlockPos check : BlockPos.betweenClosed(
                    spawnerPos.offset(-radius, 0, -radius),
                    spawnerPos.offset(radius, 3, radius)
            )) {
                BlockPos candidate = check.immutable();
                if (level.getBlockState(candidate).isAir()
                        && level.getBlockState(candidate.above()).isAir()
                        && !level.getBlockState(candidate.below()).isAir()) {
                    return candidate;
                }
            }
        }

        return spawnerPos.above();
    }

    private void markUsed(ServerLevel level, BlockPos pos, BlockState state) {
        this.used = true;
        setChanged();

        if (state.hasProperty(BalrogProximitySpawnerBlock.USED)) {
            level.setBlock(pos, state.setValue(BalrogProximitySpawnerBlock.USED, true), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Used", used);
        tag.putInt("TickCounter", tickCounter);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        used = tag.getBoolean("Used");
        tickCounter = tag.getInt("TickCounter");
    }
}
