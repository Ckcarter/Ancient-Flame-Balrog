package rem.ancient_flame_balrog.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import rem.ancient_flame_balrog.entity.AncientFlameBalrogEntity;
import rem.ancient_flame_balrog.entity.ModEntities;
import rem.ancient_flame_balrog.world.BalrogBossSpawnData;

import java.util.List;

public class OneTimeBalrogSpawnerItem extends Item {
    public OneTimeBalrogSpawnerItem() {
        super(new Item.Properties().stacksTo(1).fireResistant());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;

        ServerLevel overworld = serverLevel.getServer().overworld();

        BalrogBossSpawnData spawnData = overworld.getDataStorage().computeIfAbsent(
                BalrogBossSpawnData::load,
                BalrogBossSpawnData::new,
                BalrogBossSpawnData.dataName()
        );

        if (spawnData.hasBossSpawned()) {
            if (player != null) {
                player.displayClientMessage(
                        Component.literal("The Ancient Flame Balrog has already been summoned in this world.")
                                .withStyle(ChatFormatting.RED),
                        true
                );
            }
            return InteractionResult.FAIL;
        }

        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());

        AncientFlameBalrogEntity balrog = ModEntities.ANCIENT_FLAME_BALROG.get().create(serverLevel);
        if (balrog == null) return InteractionResult.FAIL;

        balrog.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360.0F,
                0.0F
        );

        balrog.finalizeSpawn(
                serverLevel,
                serverLevel.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.EVENT,
                null,
                null
        );

        if (!serverLevel.addFreshEntity(balrog)) return InteractionResult.FAIL;

        spawnData.markBossSpawned();

        if (player != null) {
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }

            player.displayClientMessage(
                    Component.literal("The Ancient Flame Balrog has been summoned!")
                            .withStyle(ChatFormatting.GOLD),
                    true
            );
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Summons the Ancient Flame Balrog once per world.").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("After it works, this world is permanently marked as used.").withStyle(ChatFormatting.DARK_RED));
    }
}
