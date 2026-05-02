package rem.ancient_flame_balrog.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;
import rem.ancient_flame_balrog.block.entity.BalrogProximitySpawnerBlockEntity;

public class BalrogProximitySpawnerBlock extends BaseEntityBlock {
    public static final BooleanProperty USED = BooleanProperty.create("used");

    public BalrogProximitySpawnerBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(8.0F, 1200.0F)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> state.getValue(USED) ? 0 : 8)
                .sound(SoundType.NETHERITE_BLOCK)
                .pushReaction(PushReaction.BLOCK));
        this.registerDefaultState(this.stateDefinition.any().setValue(USED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(USED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(USED) ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction) {
        return state.getValue(USED) ? 15 : 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BalrogProximitySpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            net.minecraft.world.level.block.entity.BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }

        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (blockEntity instanceof BalrogProximitySpawnerBlockEntity spawner) {
                BalrogProximitySpawnerBlockEntity.serverTick((ServerLevel) tickLevel, tickPos, tickState, spawner);
            }
        };
    }
}
