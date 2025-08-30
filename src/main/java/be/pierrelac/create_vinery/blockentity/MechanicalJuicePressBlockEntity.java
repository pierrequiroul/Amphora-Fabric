package be.pierrelac.create_vinery.blockentity;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * BlockEntity pour la presse à jus mécanique avec animation synchronisée
 * Architecture alignée sur MechanicalMixer: cycle de 40 ticks en 3 phases
 */
public class MechanicalJuicePressBlockEntity extends BasinOperatingBlockEntity {

    private int runningTicks;
    private int processingTicks = -1;

    // Animation parameters - inspirés du MechanicalMixer
    public static final int CYCLE_TIME = 40;
    public static final int PRESS_PHASE_START = 15;
    public static final int PRESS_PHASE_END = 25;

    public MechanicalJuicePressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) return;

        if (runningTicks >= 40) {
            runningTicks = 0;

            // Finaliser le cycle de pressage
            runOneCycle();
            return;
        }

        float speed = Math.abs(getSpeed());
        if (speed == 0) return;

        runningTicks++;

        // Son de pressage au moment du contact
        if (runningTicks == PRESS_PHASE_START) {
            level.playSound(null, worldPosition, SoundEvents.HONEY_BLOCK_PLACE,
                SoundSource.BLOCKS, 0.35f, 1f);
        }

        if (speed > 20) {
            // Accélération x2 pendant la phase de pressage
            if (runningTicks >= PRESS_PHASE_START && runningTicks <= PRESS_PHASE_END) {
                runningTicks++;
            }
        }

        setChanged();
        sendData();
    }

    /**
     * Exécute un cycle de pressage complet
     */
    private void runOneCycle() {
        // Logique de production: consommer fruits, produire jus dans le basin
        // À implémenter: vérifier ingrédients, consommer, produire fluides
    }

    /**
     * Phase actuelle de l'animation (0=descente, 1=pressage, 2=remontée)
     */
    public int getCurrentPhase() {
        if (runningTicks < PRESS_PHASE_START) return 0; // Descente
        if (runningTicks <= PRESS_PHASE_END) return 1;  // Pressage
        return 2; // Remontée
    }

    /**
     * Offset Y de la vis pour l'animation
     */
    public float getRenderedHeadOffset(float partialTicks) {
        if (!isRunning()) return 0f;

        float ticks = runningTicks + partialTicks;
        int phase = getCurrentPhase();

        return switch (phase) {
            case 0 -> {
                // Descente (0-15 ticks)
                float descendProgress = ticks / PRESS_PHASE_START;
                yield -descendProgress * 6f/16f; // Descend de 6 pixels
            }
            case 1 ->
                // Pressage (15-25 ticks) - position basse maintenue
                -6f/16f;
            case 2 -> {
                // Remontée (25-40 ticks)
                float remonteeProgress = (ticks - PRESS_PHASE_END) / (CYCLE_TIME - PRESS_PHASE_END);
                yield -6f/16f + remonteeProgress * 6f/16f; // Remonte vers 0
            }
            default -> 0f;
        };
    }

    /**
     * Vitesse de rotation variable selon la phase
     */
    public float getAnimationSpeed(float partialTicks) {
        if (!isRunning()) return getSpeed() * 0.75f;

        int phase = getCurrentPhase();
        float baseSpeed = getSpeed() * 0.75f;

        return switch (phase) {
            case 0 -> baseSpeed * 1.5f; // Plus rapide en descente
            case 1 -> baseSpeed * 0.3f; // Très lent pendant le pressage
            case 2 -> baseSpeed * 1.2f; // Rapide en remontée
            default -> baseSpeed;
        };
    }

    @Override
    public AABB getRenderBoundingBox() {
        // Étendre la bounding box vers le bas pour inclure le basin
        return super.getRenderBoundingBox().expandTowards(0, -1.5, 0);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("RunningTicks", runningTicks);
        compound.putInt("ProcessingTicks", processingTicks);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        runningTicks = compound.getInt("RunningTicks");
        processingTicks = compound.getInt("ProcessingTicks");
    }

    // ===============================
    // Méthodes abstraites requises par BasinOperatingBlockEntity
    // ===============================

    @Override
    protected boolean isRunning() {
        return Math.abs(getSpeed()) > 0 && runningTicks > 0;
    }

    @Override
    protected void onBasinRemoved() {
        runningTicks = 0;
        processingTicks = -1;
        setChanged();
        sendData();
    }

    @Override
    protected Object getRecipeCacheKey() {
        return "juice_pressing_recipes";
    }

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        // Pour l'instant, accepter toutes les recettes
        // Plus tard: filtrer seulement les recettes de pressage de jus
        return true;
    }
}
