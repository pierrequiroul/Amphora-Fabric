package be.pierrelac.amphora.content.kinetics.juice_press;

import be.pierrelac.amphora.content.core.ModCoreBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


import javax.annotation.Nonnull;


/**
 * Bloc de pressoir à jus mécanique - Architecture simplifiée comme MechanicalMixer
 * Plus d'orientation horizontale, plus de logique de particules complexe
 */
public class MechanicalJuicePressBlock extends KineticBlock implements IBE<MechanicalJuicePressBlockEntity>, ICogWheel {

    public MechanicalJuicePressBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader worldIn, @Nonnull BlockPos pos) {
        return !BasinBlock.isBasin(worldIn, pos.below());
    }

    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        // Collision adaptative pour joueur comme le Mixer
        if (context instanceof EntityCollisionContext
            && ((EntityCollisionContext) context).getEntity() instanceof Player)
            return AllShapes.CASING_14PX.get(Direction.DOWN);

        return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return false; // Pas de shaft visible comme le Mixer
    }

    @Override
    public float getParticleTargetRadius() {
        return .85f; // Même valeurs que le Mixer
    }

    @Override
    public float getParticleInitialRadius() {
        return .75f; // Même valeurs que le Mixer
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }

    @Override
    public Class<MechanicalJuicePressBlockEntity> getBlockEntityClass() {
        return MechanicalJuicePressBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalJuicePressBlockEntity> getBlockEntityType() {
        return ModCoreBlockEntities.MECHANICAL_JUICE_PRESS;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
        return false;
    }
}
