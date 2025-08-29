package be.pierrelac.create_vinery.blocks;

import be.pierrelac.create_vinery.blockentity.MechanicalJuicePressBlockEntity;
import be.pierrelac.create_vinery.blockentity.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
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
 * Bloc de pressoir à jus mécanique avec animations cinématiques
 */
public class MechanicalJuicePressBlock extends KineticBlock implements IBE<MechanicalJuicePressBlockEntity> {
    
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    // Forme de collision du bloc (16x16x16 complet pour l'instant)
    private static final VoxelShape SHAPE = Shapes.block();
    
    public MechanicalJuicePressBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }
    
    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader worldIn, @Nonnull BlockPos pos) {
        // Même logique que MechanicalMixer et MechanicalPress : ne peut pas être placé directement au-dessus d'un bassin
        // Cela force le placement avec 1 bloc d'offset au-dessus du bassin
        return !BasinBlock.isBasin(worldIn, pos.below());
    }
    
    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y; // Rotation verticale pour le volant
    }
    
    @Override
    @Nonnull
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, 
                                @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        // Laisser le comportement par défaut - pas de logique de placement custom ici
        return InteractionResult.PASS;
    }
    
    @Override
    public Class<MechanicalJuicePressBlockEntity> getBlockEntityClass() {
        return MechanicalJuicePressBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends MechanicalJuicePressBlockEntity> getBlockEntityType() {
        return ModBlockEntities.MECHANICAL_JUICE_PRESS;
    }
}
