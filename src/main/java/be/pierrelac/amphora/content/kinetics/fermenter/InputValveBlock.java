package be.pierrelac.amphora.content.kinetics.fermenter;

import be.pierrelac.amphora.content.core.ModCoreBlockEntities;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Input Valve - Allows fluid injection into fermentation chambers
 * Receives fluids from pipes and distributes to connected tanks
 */
public class InputValveBlock extends Block implements IBE<InputValveBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    private static final VoxelShape SHAPE = Block.box(2, 2, 2, 14, 14, 14);

    public InputValveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public Class<InputValveBlockEntity> getBlockEntityClass() {
        return InputValveBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends InputValveBlockEntity> getBlockEntityType() {
        return ModCoreBlockEntities.INPUT_VALVE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof InputValveBlockEntity valve) {
            // Display valve information
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "Input Valve - Buffer: " + valve.getFluidAmount() + "mB"
            ));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof InputValveBlockEntity valve) {
            valve.invalidateConnections();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
