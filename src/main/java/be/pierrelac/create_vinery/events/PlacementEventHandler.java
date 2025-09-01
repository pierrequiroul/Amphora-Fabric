package be.pierrelac.create_vinery.events;

import be.pierrelac.create_vinery.ModBlocks;
import be.pierrelac.create_vinery.content.kinetics.juice_press.MechanicalJuicePressBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Gestionnaire d'événements pour le placement intelligent du pressoir à jus
 */
public class PlacementEventHandler {

    /**
     * Enregistre les gestionnaires d'événements pour le placement intelligent
     */
    public static void registerEvents() {
        UseBlockCallback.EVENT.register(PlacementEventHandler::onUseBlock);
    }

    /**
     * Gestionnaire pour l'utilisation d'un bloc avec un item
     */
    private static InteractionResult onUseBlock(Player player, Level world,
                                               net.minecraft.world.InteractionHand hand,
                                               BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        ItemStack heldItem = player.getItemInHand(hand);

        // Vérifier si on clique sur un bassin avec un pressoir à jus
        if (BasinBlock.isBasin(world, pos) &&
            heldItem.getItem() == ModBlocks.MECHANICAL_JUICE_PRESS.asItem()) {

            // Position cible : 2 blocs au-dessus du bassin
            BlockPos targetPos = pos.above(2);

            // Vérifier si la position est libre
            if (world.getBlockState(targetPos).canBeReplaced()) {
                BlockState newState = ModBlocks.MECHANICAL_JUICE_PRESS.defaultBlockState()
						.setValue(BlockStateProperties.HORIZONTAL_FACING, player.getDirection().getOpposite());

                // Vérifier si le bloc peut survivre à cette position
                if (newState.canSurvive(world, targetPos)) {
                    if (!world.isClientSide) {
                        world.setBlock(targetPos, newState, 11);
                        if (!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}
