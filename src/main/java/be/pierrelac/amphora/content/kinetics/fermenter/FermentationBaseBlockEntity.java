package be.pierrelac.amphora.content.kinetics.fermenter;

import be.pierrelac.amphora.content.core.ModCoreBlockEntities;
import be.pierrelac.amphora.content.kinetics.fermenter.recipe.EnhancedRecipeMatcher;
import be.pierrelac.amphora.content.kinetics.fermenter.recipe.FermentationRecipeCreate;
import be.pierrelac.amphora.content.kinetics.fermenter.recipe.JsonRecipeManager;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Fermentation Base Block Entity - Central controller for fermentation chambers
 * Manages tank group detection, valve coordination, and fermentation processing
 */
public class FermentationBaseBlockEntity extends KineticBlockEntity {

    private Set<BlockPos> detectedTanks = new HashSet<>();
    private Set<BlockPos> inputValves = new HashSet<>();
    private Set<BlockPos> outputValves = new HashSet<>();
    private boolean tankGroupValid = false;
    private int lastTankCount = 0;
    private int processingTicks = 0;
    private boolean isProcessing = false;

    public FermentationBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (level.isClientSide) return;
        
        // Check tank group every 20 ticks (1 second)
        if (level.getGameTime() % 20 == 0) {
            detectTankGroup();
        }
        
        // Process fermentation if conditions are met
        if (tankGroupValid && hasMinimumSpeed()) {
            processRecipes();
        }
    }

    /**
     * Detects connected tank groups and valves around this base
     */
    private void detectTankGroup() {
        detectedTanks.clear();
        inputValves.clear();
        outputValves.clear();
        
        // Search in a 7x7x7 area around the base
        AABB searchArea = new AABB(worldPosition).inflate(3);
        
        for (int x = (int) searchArea.minX; x <= searchArea.maxX; x++) {
            for (int y = (int) searchArea.minY; y <= searchArea.maxY; y++) {
                for (int z = (int) searchArea.minZ; z <= searchArea.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    
                    // Check for Create fluid tanks
                    if (state.getBlock().getDescriptionId().contains("fluid_tank")) {
                        detectedTanks.add(pos);
                    }
                    // Check for input valves
                    else if (state.getBlock() instanceof InputValveBlock) {
                        inputValves.add(pos);
                    }
                    // Check for output valves
                    else if (state.getBlock() instanceof OutputValveBlock) {
                        outputValves.add(pos);
                    }
                }
            }
        }
        
        // Update tank group validity
        boolean wasValid = tankGroupValid;
        tankGroupValid = detectedTanks.size() >= 1 && !inputValves.isEmpty() && !outputValves.isEmpty();
        
        if (tankGroupValid != wasValid) {
            setChanged();
        }
        
        lastTankCount = detectedTanks.size();
    }

    /**
     * Process fermentation recipes using the enhanced matcher
     */
    private void processRecipes() {
        if (!tankGroupValid) return;
        
        // TODO: Implement actual fluid processing with input/output valves
        // This would involve:
        // 1. Getting fluids from input valves
        // 2. Checking recipes with enhanced matcher
        // 3. Processing fluids in tanks
        // 4. Outputting to output valves
        
        if (isProcessing) {
            processingTicks++;
            
            // Example processing logic (placeholder)
            if (processingTicks >= 200) { // 10 seconds at 20 TPS
                isProcessing = false;
                processingTicks = 0;
                // Complete recipe processing
            }
        }
    }

    /**
     * Check if the base has minimum rotational speed for operation
     */
    private boolean hasMinimumSpeed() {
        return Math.abs(getSpeed()) >= 16; // Minimum 16 RPM like other Create machines
    }

    /**
     * Start a fermentation process
     */
    public void startFermentation(FermentationRecipeCreate recipe) {
        if (tankGroupValid && !isProcessing) {
            isProcessing = true;
            processingTicks = 0;
            setChanged();
        }
    }

    /**
     * Stop fermentation and disarm tank group
     */
    public void disarmTankGroup() {
        tankGroupValid = false;
        isProcessing = false;
        processingTicks = 0;
        detectedTanks.clear();
        inputValves.clear();
        outputValves.clear();
        setChanged();
    }

    // Getters for information display
    public int getTankGroupSize() {
        return detectedTanks.size();
    }

    public boolean isActive() {
        return tankGroupValid && hasMinimumSpeed();
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public Set<BlockPos> getDetectedTanks() {
        return new HashSet<>(detectedTanks);
    }

    public Set<BlockPos> getInputValves() {
        return new HashSet<>(inputValves);
    }

    public Set<BlockPos> getOutputValves() {
        return new HashSet<>(outputValves);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tankGroupValid = tag.getBoolean("TankGroupValid");
        isProcessing = tag.getBoolean("IsProcessing");
        processingTicks = tag.getInt("ProcessingTicks");
        lastTankCount = tag.getInt("LastTankCount");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("TankGroupValid", tankGroupValid);
        tag.putBoolean("IsProcessing", isProcessing);
        tag.putInt("ProcessingTicks", processingTicks);
        tag.putInt("LastTankCount", lastTankCount);
    }

    @Override
    public boolean addToGoggleTooltip(List<net.minecraft.network.chat.Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        
        tooltip.add(net.minecraft.network.chat.Component.literal("Tank Group: " + (tankGroupValid ? "Valid" : "Invalid")));
        tooltip.add(net.minecraft.network.chat.Component.literal("Tanks: " + detectedTanks.size()));
        tooltip.add(net.minecraft.network.chat.Component.literal("Input Valves: " + inputValves.size()));
        tooltip.add(net.minecraft.network.chat.Component.literal("Output Valves: " + outputValves.size()));
        
        if (isProcessing) {
            tooltip.add(net.minecraft.network.chat.Component.literal("Processing: " + processingTicks + " ticks"));
        }
        
        return true;
    }
}
