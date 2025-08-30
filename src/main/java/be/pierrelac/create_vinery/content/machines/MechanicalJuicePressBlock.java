package be.pierrelac.create_vinery.content.machines;

import be.pierrelac.create_vinery.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Bloc de la presse à jus mécanique
 * Suit les patterns standard de Create pour les machines cinématiques
 */
public class MechanicalJuicePressBlock extends KineticBlock implements IBE<MechanicalJuicePressBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Forme de collision du bloc - une presse occupant tout l'espace
    private static final VoxelShape SHAPE = Shapes.block();

    public MechanicalJuicePressBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        // La presse reçoit la puissance par le haut
        return face == Direction.UP;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        // Rotation verticale (axe Y)
        return Direction.Axis.Y;
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                InteractionHand hand, BlockHitResult hit) {
        // Interaction avec la presse (debug ou contrôle manuel si nécessaire)
        if (!level.isClientSide && player.isCreative()) {
            // Debug info pour les développeurs
            withBlockEntityDo(level, pos, be -> {
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "Juice Press - Running: " + be.isRunning() +
                        ", Speed: " + String.format("%.1f", be.getSpeed())
                    ),
                    true
                );
            });
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public Class<MechanicalJuicePressBlockEntity> getBlockEntityClass() {
        return MechanicalJuicePressBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalJuicePressBlockEntity> getBlockEntityType() {
        return ModBlockEntities.MECHANICAL_JUICE_PRESS;
    }

    /**
     * Vérifie si un basin est placé correctement sous la presse
     */
    public static boolean hasBasinBelow(Level level, BlockPos pos) {
        BlockPos basinPos = pos.below(2);
        return level.getBlockEntity(basinPos) instanceof com.simibubi.create.content.processing.basin.BasinBlockEntity;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                               BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        // Notifier la block entity si le basin change
        if (fromPos.equals(pos.below(2))) {
            withBlockEntityDo(level, pos, be -> {
                be.basinChecker.scheduleUpdate();
            });
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos,
                                 net.minecraft.world.level.pathfinder.PathComputationType type) {
        return false;
    }
}
