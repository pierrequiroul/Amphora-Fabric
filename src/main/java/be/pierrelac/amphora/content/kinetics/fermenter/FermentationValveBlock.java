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
 * Fermentation Valve - Arms nearby Create Fluid Tanks for wine fermentation
 * Must be attached to the side of a Create Fluid Tank to function
 */
public class FermentationValveBlock extends KineticBlock implements IBE<FermentationValveBlockEntity> {
    
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    
    // Valve shape - compact design for side mounting
    private static final VoxelShape VALVE_SHAPE = Shapes.or(
        Block.box(4, 4, 0, 12, 12, 6),     // Main valve body
        Block.box(6, 6, 6, 10, 10, 16)     // Pipe connection
    );

    public FermentationValveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<FermentationValveBlockEntity> getBlockEntityClass() {
        return FermentationValveBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FermentationValveBlockEntity> getBlockEntityType() {
        return ModCoreBlockEntities.FERMENTATION_VALVE;
    }

    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return VALVE_SHAPE;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getOpposite() == state.getValue(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide) {
            if (worldIn.getBlockEntity(pos) instanceof FermentationValveBlockEntity valve) {
                // Gérer les différentes interactions
                valve.handlePlayerInteraction(player, player.isShiftKeyDown());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!worldIn.isClientSide && state.getBlock() != newState.getBlock()) {
            if (worldIn.getBlockEntity(pos) instanceof FermentationValveBlockEntity valve) {
                valve.disarmTanks();
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }
}
