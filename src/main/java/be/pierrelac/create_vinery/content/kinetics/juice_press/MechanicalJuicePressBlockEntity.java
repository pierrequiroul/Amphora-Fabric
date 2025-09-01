package be.pierrelac.create_vinery.content.kinetics.juice_press;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * BlockEntity pour la presse à jus mécanique - Architecture simplifiée comme MechanicalMixer
 */
public class MechanicalJuicePressBlockEntity extends BasinOperatingBlockEntity {

    // Variables d'animation simplifiées comme le Mixer
    public int runningTicks;
    public int processingTicks;
    public boolean running;

    public MechanicalJuicePressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    // Animation similaire au Mixer mais adaptée pour un pressage vertical
    public float getRenderedScrewOffset(float partialTicks) {
        int localTick;
        float offset = 0;
        if (running) {
            if (runningTicks < 20) {
                localTick = runningTicks;
                float num = (localTick + partialTicks) / 20f;
                num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
                offset = num - .5f;
            } else if (runningTicks <= 20) {
                offset = 1; // Position basse maintenue
            } else {
                localTick = 40 - runningTicks;
                float num = (localTick - partialTicks) / 20f;
                num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
                offset = num - .5f;
            }
        }
        return offset + 7 / 16f; // Ajustement de position comme le Mixer
    }

    public float getRenderedHandleRotationSpeed(float partialTicks) {
        float speed = getSpeed();
        if (running) {
            if (runningTicks < 15) {
                return speed;
            }
            if (runningTicks <= 20) {
                return speed * 2; // Vitesse double pendant le pressage
            }
            return speed;
        }
        return speed / 2;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expandTowards(0, -1.5, 0);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("Running", running);
        compound.putInt("RunningTicks", runningTicks);
        compound.putInt("ProcessingTicks", processingTicks);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        running = compound.getBoolean("Running");
        runningTicks = compound.getInt("RunningTicks");
        processingTicks = compound.getInt("ProcessingTicks");
    }

    @Override
    protected void onBasinRemoved() {
        running = false;
        runningTicks = 0;
        processingTicks = -1;
        setChanged();
        sendData();
    }

	@Override
	protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
		return false;
	}

	@Override
	protected Object getRecipeCacheKey() {
		return null;
	}

	@Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide)
            return;

        if (runningTicks >= 40) {
            running = false;
            runningTicks = 0;
            // Finaliser le processus ici - appeler la logique de recette
            applyRecipeOutputs();
            return;
        }

        if (running) {
            runningTicks++;

            // Son pendant le pressage
            if (runningTicks == 15) {
                level.playSound(null, worldPosition, SoundEvents.HONEY_BLOCK_PLACE,
                    SoundSource.BLOCKS, 0.35f, 1f);
            }

            setChanged();
            sendData();
            return;
        }

        // Vérifier si on peut démarrer un processus SEULEMENT s'il y a une recette valide
        if (canProcessInBasin()) {
            running = true;
            runningTicks = 0;
            setChanged();
            sendData();
        }
    }

    private boolean canProcessInBasin() {
        if (!getBasin().isPresent() || !isSpeedRequirementFulfilled()) {
            return false;
        }

        // Vérifier qu'il y a une recette valide dans le bassin
        return hasValidRecipe();
    }

    private boolean hasValidRecipe() {
        // TODO: Implémenter la vérification de recette réelle
        // Pour l'instant, retourner false pour éviter le démarrage automatique
        return false;
    }

    private void applyRecipeOutputs() {
        // TODO: Implémenter l'application des résultats de recette
        // Consommer les ingrédients et produire les résultats
    }

	@Override
	public boolean isRunning() {
		return running;
	}
}
