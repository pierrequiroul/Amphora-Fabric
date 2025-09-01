package be.pierrelac.create_vinery.content.kinetics.juice_press;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.VecHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * BlockEntity pour la presse à jus mécanique - Compatible Fabric 1.20.1
 * Inspiré du MechanicalMixer de Create
 */
public class MechanicalJuicePressBlockEntity extends BasinOperatingBlockEntity {

    private static final Object juicePressRecipesKey = new Object();

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

                    // Son pendant le pressage
                    level.playSound(null, worldPosition, SoundEvents.HONEY_BLOCK_PLACE,
                        SoundSource.BLOCKS, 0.35f, speed < 65 ? .75f : 1.5f);

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
        return recipe instanceof JuicePressRecipe;
    }

    @Override
    protected Object getRecipeCacheKey() {
        return juicePressRecipesKey;
    }

    @Override
    protected List<Recipe<?>> getMatchingRecipes() {
        List<Recipe<?>> matchingRecipes = super.getMatchingRecipes();

        Optional<BasinBlockEntity> basin = getBasin();
        if (!basin.isPresent())
            return matchingRecipes;

        BasinBlockEntity basinBlockEntity = basin.get();
        Storage<ItemVariant> availableItems = basinBlockEntity.getItemStorage(null);
        if (availableItems == null)
            return matchingRecipes;

        System.out.println("[JuicePress Debug] Checking for juice press recipes...");
        System.out.println("[JuicePress Debug] Items in basin:");

        try (Transaction t = TransferUtil.getTransaction()) {
            for (StorageView<ItemVariant> view : availableItems.nonEmptyViews()) {
                ItemStack stack = view.getResource().toStack((int) view.getAmount());
                System.out.println("  - " + stack.getItem() + " x" + stack.getCount());
            }
        }

        return matchingRecipes;
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
}
