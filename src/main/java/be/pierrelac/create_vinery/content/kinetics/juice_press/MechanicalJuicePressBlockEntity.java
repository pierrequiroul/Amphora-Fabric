package be.pierrelac.create_vinery.content.kinetics.juice_press;

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
 * Architecture séparée: kinétique vs processus
 */
public class MechanicalJuicePressBlockEntity extends BasinOperatingBlockEntity {

    // ===============================
    // Process cycle variables (separate from kinetics)
    // ===============================
    private boolean running = false;           // true only during an active cycle
    private int cycleTicks = 0;               // 0..BASE_CYCLE

    // Constants for process cycle
    private static final int BASE_CYCLE = 40;
    public static final int PRESS_PHASE_START = 15;
    public static final int PRESS_PHASE_END = 25;

    // Constants for screw mechanics
    private static final float STROKE = 6f/16f;    // Maximum screw travel in blocks
    private static final float PITCH = 2f/16f;     // Thread pitch for handle rotation

    // Legacy field for compatibility
    private int processingTicks = -1;

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

        // Check if we can run a process cycle
        boolean speedOk = super.isSpeedRequirementFulfilled();
        boolean canProcess = speedOk && canProcessInBasin();

        if (!canProcess) {
            // Stop the cycle if conditions aren't met
            running = false;
            cycleTicks = 0;
            setChanged();
            sendData();
            return;
        }

        // Start or continue the pressing cycle
        if (!running) {
            running = true;
            cycleTicks = 0;
        }

        // Advance cycle based on speed (faster RPM = faster cycle)
        float speed = Math.abs(getSpeed());
        int cycleIncrement = 1;
        if (speed > 20) {
            cycleIncrement = 2; // Faster at high speeds
        }

        cycleTicks += cycleIncrement;

        // Play sound during pressing phase
        if (cycleTicks == PRESS_PHASE_START) {
            level.playSound(null, worldPosition, SoundEvents.HONEY_BLOCK_PLACE,
                SoundSource.BLOCKS, 0.35f, 1f);
        }

        // Complete the cycle
        if (cycleTicks >= BASE_CYCLE) {
            runOneCycle();
            running = false;
            cycleTicks = 0;
        }

        setChanged();
        sendData();
    }

    // ===============================
    // Process cycle methods
    // ===============================

    /**
     * Check if basin has valid recipe ingredients
     */
    private boolean canProcessInBasin() {
        // TODO: Implement proper recipe checking with basin contents
        // For now, always return true if basin exists
        return getBasin().isPresent();
    }

    /**
     * Execute one complete pressing cycle
     */
    private void runOneCycle() {
        // TODO: Implement recipe processing logic
        // - Consume ingredients from basin
        // - Produce juice fluid in basin
    }

    // ===============================
    // Animation API methods
    // ===============================

    /**
     * Returns true only when actively running a press cycle
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Get current cycle phase (0.0 to 1.0)
     */
    public float getCyclePhase(float pt) {
        return running ? ((cycleTicks + pt) / (float) BASE_CYCLE) : 0f;
    }

    /**
     * Map phase to down/pause/up profile (same as mixer's profile)
     * Return 0..1 travel fraction (0 = top, 1 = bottom)
     */
    public float getScrewProgress(float pt) {
        if (!running) return 0f;
        float p = getCyclePhase(pt);
        if (p <= 0.5f) return p * 2f;        // 0..0.5 = going down
        return (1f - p) * 2f;                 // 0.5..1 = going up
    }

    /**
     * Actual Y offset for the screw partial
     */
    public float getScrewYOffset(float pt) {
        return -getScrewProgress(pt) * STROKE;
    }

    /**
     * Handle angle derived strictly from screw travel (freeze when !running)
     */
    public float getHandleAngleDeg(float pt) {
        if (!running) return 0f;
        float screwTravel = getScrewProgress(pt) * STROKE;
        return (screwTravel / PITCH) * 360f;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expandTowards(0, -1.5, 0);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("Running", running);
        compound.putInt("CycleTicks", cycleTicks);
        compound.putInt("ProcessingTicks", processingTicks);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        running = compound.getBoolean("Running");
        cycleTicks = compound.getInt("CycleTicks");
        processingTicks = compound.getInt("ProcessingTicks");
    }

    // ===============================
    // BasinOperatingBlockEntity required methods
    // ===============================

    @Override
    protected void onBasinRemoved() {
        running = false;
        cycleTicks = 0;
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
        // TODO: Filter only juice pressing recipes
        return true;
    }
}
