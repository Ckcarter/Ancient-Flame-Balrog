package rem.ancient_flame_balrog.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Tracks whether the Ancient Flame Balrog has already been spawned.
 * Stored on overworld data storage, so it is once per whole save/world.
 */
public class BalrogBossSpawnData extends SavedData {
    private static final String DATA_NAME = "ancient_flame_balrog_one_time_spawn";
    private boolean bossSpawned;

    public static String dataName() {
        return DATA_NAME;
    }

    public boolean hasBossSpawned() {
        return bossSpawned;
    }

    public void markBossSpawned() {
        this.bossSpawned = true;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("BossSpawned", bossSpawned);
        return tag;
    }

    public static BalrogBossSpawnData load(CompoundTag tag) {
        BalrogBossSpawnData data = new BalrogBossSpawnData();
        data.bossSpawned = tag.getBoolean("BossSpawned");
        return data;
    }
}
