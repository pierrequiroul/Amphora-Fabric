package be.pierrelac.amphora.content.kinetics.fermenter;

import be.pierrelac.amphora.content.core.ModCoreBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

/**
 * Fermentation Base - Central controller for fermentation chambers
 * Manages the fermentation process and communicates with input/output valves
 */
public class FermentationBaseBlock extends KineticBlock implements IBE<FermentationBaseBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    public FermentationBaseBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    public Class<FermentationBaseBlockEntity> getBlockEntityClass() {
        return FermentationBaseBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FermentationBaseBlockEntity> getBlockEntityType() {
        return ModCoreBlockEntities.FERMENTATION_BASE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof FermentationBaseBlockEntity base) {
            // Debug information or GUI opening logic
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "Fermentation Base - Tanks: " + base.getTankGroupSize() + 
                " | Active: " + base.isActive()
            ));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof FermentationBaseBlockEntity base) {
            base.disarmTankGroup();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
