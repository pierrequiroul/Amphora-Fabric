package be.pierrelac.amphora.content.kinetics.juice_press;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.content.core.ModCoreRecipeTypes;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

/**
 * BlockEntity pour la presse à jus mécanique - Compatible Fabric 1.20.1
 * Inspiré du MechanicalMixer de Create
 */
public class MechanicalJuicePressBlockEntity extends BasinOperatingBlockEntity {

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
    protected void read(CompoundTag compound, boolean clientPacket) {
        running = compound.getBoolean("Running");
        runningTicks = compound.getInt("Ticks");
        processingTicks = compound.getInt("ProcessingTicks");
        super.read(compound, clientPacket);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putBoolean("Running", running);
        compound.putInt("Ticks", runningTicks);
        compound.putInt("ProcessingTicks", processingTicks);
        super.write(compound, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();

        if (runningTicks >= 40) {
            running = false;
            runningTicks = 0;
            basinChecker.scheduleUpdate();
            return;
        }

        float speed = Math.abs(getSpeed());
        if (running && level != null) {
            if ((!level.isClientSide || isVirtual()) && runningTicks == 20) {
                if (processingTicks < 0) {
                    float recipeSpeed = 1;
                    if (currentRecipe instanceof JuicePressRecipe) {
                        int t = ((JuicePressRecipe) currentRecipe).getProcessingDuration();
                        if (t != 0)
                            recipeSpeed = t / 100f;
                    }

                    processingTicks = Mth.clamp((Mth.log2((int) (512 / speed))) * Mth.ceil(recipeSpeed * 15) + 1, 1, 512);

                    // TODO: Ajouter un son pendant le pressage (problème de null safety à résoudre)

                } else {
                    processingTicks--;
                    if (processingTicks == 0) {
                        runningTicks++;
                        processingTicks = -1;
                        applyBasinRecipe();
                        sendData();
                    }
                }
            }

            if (runningTicks != 20)
                runningTicks++;
        }
    }

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        boolean isJuicePress = recipe instanceof JuicePressRecipe;
        Amphora.LOGGER.info("Matching static filters - Recipe: {} (type: {}), isJuicePress: {}", 
            recipe.getId(), recipe.getType(), isJuicePress);
        return isJuicePress;
    }

    @Override
    protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
        if (recipe == null || !(recipe instanceof JuicePressRecipe)) {
            Amphora.LOGGER.info("Basin match failed - Recipe: {} is not JuicePressRecipe", 
                recipe != null ? recipe.getId() : "null");
            return false;
        }
        
        Optional<BasinBlockEntity> basin = getBasin();
        if (!basin.isPresent()) {
            Amphora.LOGGER.info("Basin match failed - No basin found");
            return false;
        }

        // Utiliser le système standard de Create pour le matching
        boolean matchResult = super.matchBasinRecipe(recipe);
        Amphora.LOGGER.info("Basin recipe match for {} - Result: {}", 
            recipe.getId(), matchResult);
            
        return matchResult;
    }

    @Override
    protected Object getRecipeCacheKey() {
        Object key = ModCoreRecipeTypes.JUICE_PRESSING;
        Amphora.LOGGER.info("Recipe cache key requested: {} (recipe type: {})", 
            key, ModCoreRecipeTypes.JUICE_PRESSING.getType());
        return key;
    }

    @Override
    protected List<Recipe<?>> getMatchingRecipes() {
        // Utiliser le système standard de Create qui gère déjà tout correctement
        List<Recipe<?>> recipes = super.getMatchingRecipes();
        Amphora.LOGGER.info("Found {} matching recipes for juice pressing", recipes.size());
        for (Recipe<?> recipe : recipes) {
            Amphora.LOGGER.info("  - Recipe: {} (type: {})", recipe.getId(), recipe.getType());
        }
        return recipes;
    }

    @Override
    protected void onBasinRemoved() {
        if (!running)
            return;
        runningTicks = 40;
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void startProcessingBasin() {
        Amphora.LOGGER.info("Starting juice pressing basin process!");
        if (running && runningTicks <= 20)
            return;
        super.startProcessingBasin();
        running = true;
        runningTicks = 0;
        processingTicks = -1;
    }
}
