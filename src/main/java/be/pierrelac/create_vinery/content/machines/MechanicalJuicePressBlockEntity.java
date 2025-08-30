package be.pierrelac.create_vinery.content.machines;

import be.pierrelac.create_vinery.ModBlockEntities;
import be.pierrelac.create_vinery.content.machines.recipes.JuicePressRecipe;
import be.pierrelac.create_vinery.CreateVinery;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * BlockEntity pour la presse à jus mécanique
 * Implémente les patterns standard de Create pour les machines basin-operating
 */
public class MechanicalJuicePressBlockEntity extends BasinOperatingBlockEntity {

    private int runningTicks;
    private int processingTicks;
    private boolean running;

    // Animation de la vis
    public float pressingProgress = 0f;
    public float lastPressingProgress = 0f;

    // Constantes pour l'animation
    private static final int PROCESSING_TIME = 160; // Temps de base en ticks
    private static final float STROKE = 6f/16f; // Course verticale de la vis (6px)
    private static final float PITCH = 2f/16f;  // Pas de vis (2px par tour)

    public MechanicalJuicePressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MECHANICAL_JUICE_PRESS, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null) return;

        // Animation côté client
        if (level.isClientSide) {
            updateClientAnimation();
            return;
        }

        // Logique serveur
        if (running) {
            processingTicks++;

            // Calcul du temps de traitement basé sur la vitesse RPM
            float speedFactor = Math.max(0.1f, Math.abs(getSpeed()) / 256f);
            int adjustedProcessingTime = Math.max(20, (int)(PROCESSING_TIME / speedFactor));

            if (processingTicks >= adjustedProcessingTime) {
                finishProcessing();
            }

            // Effets de particules pendant le traitement
            if (processingTicks % 8 == 0) {
                spawnProcessingParticles();
            }
        }
    }

    private void updateClientAnimation() {
        lastPressingProgress = pressingProgress;

        if (running && getSpeed() != 0) {
            // Animation basée sur la vitesse de rotation
            float speed = Math.abs(getSpeed());
            float animationSpeed = speed / 256f; // Normaliser la vitesse

            // Mouvement de vis avec rotation et descente
            pressingProgress += animationSpeed * 0.1f;

            // Cycle d'animation : descente puis remontée
            if (pressingProgress > 1f) {
                pressingProgress = 0f;
            }
        }
    }

    @Override
    protected boolean isRunning() {
        return running;
    }

    @Override
    public void startProcessingBasin() {
        if (running) return;

        running = true;
        runningTicks = 0;
        processingTicks = 0;
        pressingProgress = 0f;

        setChanged();
        CreateVinery.LOGGER.debug("Starting juice pressing process");
    }

    private void finishProcessing() {
        if (!running) return;

        running = false;
        processingTicks = 0;
        pressingProgress = 0f;

        // Appliquer la recette
        applyBasinRecipe();

        setChanged();
        CreateVinery.LOGGER.debug("Finished juice pressing process");
    }

    @Override
    protected void onBasinRemoved() {
        running = false;
        processingTicks = 0;
        pressingProgress = 0f;
        setChanged();
    }

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        // Accepter seulement les recettes de pressage de jus
        return recipe instanceof JuicePressRecipe;
    }

    @Override
    protected Object getRecipeCacheKey() {
        return JuicePressRecipe.TYPE;
    }

    @Override
    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        // TODO: Ajouter un advancement pour le pressage de jus
        return Optional.empty();
    }

    /**
     * Génère des particules pendant le processus de pressage
     */
    private void spawnProcessingParticles() {
        if (level == null || level.isClientSide) return;

        getBasin().ifPresent(basin -> {
            // Particules d'items en cours de traitement
            if (!basin.getInputInventory().isEmpty()) {
                Vec3 center = VecHelper.getCenterOf(basin.getBlockPos());
                Vec3 offset = new Vec3(
                    (level.random.nextFloat() - 0.5f) * 0.5f,
                    0.2f + level.random.nextFloat() * 0.3f,
                    (level.random.nextFloat() - 0.5f) * 0.5f
                );

                level.addParticle(
                    new ItemParticleOption(ParticleTypes.ITEM, basin.getInputInventory().getItem(0)),
                    center.x + offset.x,
                    center.y + offset.y,
                    center.z + offset.z,
                    0, 0, 0
                );
            }
        });
    }

    /**
     * Obtient la progression actuelle du pressage (0.0 à 1.0)
     */
    public float getPressingProgress(float partialTicks) {
        if (!running) return 0f;

        float progress = Mth.lerp(partialTicks, lastPressingProgress, pressingProgress);
        return Mth.clamp(progress, 0f, 1f);
    }

    /**
     * Calcule la position de la vis pour l'animation
     */
    public float getScrewPosition(float partialTicks) {
        float progress = getPressingProgress(partialTicks);
        float verticalOffset = -progress * STROKE;
        return verticalOffset;
    }

    /**
     * Calcule la rotation de la vis pour l'animation
     */
    public float getScrewRotation(float partialTicks) {
        if (getSpeed() == 0) return 0f;

        float progress = getPressingProgress(partialTicks);
        float rotationSpeed = getSpeed() / 16f;
        return progress * rotationSpeed * 360f;
    }

    /**
     * Offset Y de la vis (descente linéaire) - pour compatibilité avec le renderer
     */
    public float getScrewYOffset(float partialTicks) {
        return getScrewPosition(partialTicks);
    }

    /**
     * Angle de rotation du volant en degrés (lié à la descente par le pas de vis)
     */
    public float getHandleAngleDeg(float partialTicks) {
        if (getSpeed() == 0) return 0f;

        float progress = getPressingProgress(partialTicks);
        float turns = (progress * STROKE) / PITCH;
        return (turns * 360f) % 360f;
    }

    /**
     * Angle de rotation de la cogwheel basé sur la vitesse cinétique
     * La cogwheel doit tourner dès qu'il y a de la puissance, même sans pressage
     */
    public float getCogwheelAngleDeg(float partialTicks) {
        if (level == null) return 0f;

        float speed = getSpeed();
        if (speed == 0) return 0f;

        // La cogwheel tourne en continu dès qu'il y a de la puissance
        // Contrairement aux autres animations qui sont liées au processus de pressage
        if (level.isClientSide) {
            long time = level.getGameTime();
            float angle = (time + partialTicks) * speed * 0.75f;
            return angle % 360f;
        }

        return 0f;
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("Running", running);
        compound.putInt("ProcessingTicks", processingTicks);
        compound.putFloat("PressingProgress", pressingProgress);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        running = compound.getBoolean("Running");
        processingTicks = compound.getInt("ProcessingTicks");
        pressingProgress = compound.getFloat("PressingProgress");
    }
}
