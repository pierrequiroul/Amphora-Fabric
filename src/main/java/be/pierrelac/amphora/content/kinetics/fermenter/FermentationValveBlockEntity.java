package be.pierrelac.amphora.content.kinetics.fermenter;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.content.kinetics.fermenter.scanner.TankGroupScanner;
import be.pierrelac.amphora.content.kinetics.fermenter.scanner.TankGroup;
import be.pierrelac.amphora.content.kinetics.fermenter.scanner.OptimizedTankScanner;
import be.pierrelac.amphora.content.kinetics.fermenter.processor.QuantumFermentationProcessor;
import be.pierrelac.amphora.content.kinetics.fermenter.debug.FermentationDebugger;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * Fermentation Valve Block Entity - Système quantique de fermentation optimisé
 * Utilise le scanner optimisé par carrés (1×1, 2×2, 3×3) pour détecter les groupes de tanks
 * et traite la fermentation par quantums de 100mB avec cooldown de scan
 */
public class FermentationValveBlockEntity extends KineticBlockEntity {
    
    // Configuration
    private static final int SCAN_INTERVAL = 100; // Rescanner toutes les 5 secondes  
    private static final float MIN_RPM_REQUIRED = 16.0f; // RPM minimum pour fonctionner
    
    // État du système
    private TankGroup currentTankGroup;
    private QuantumFermentationProcessor processor;
    private OptimizedTankScanner optimizedScanner; // Scanner optimisé par carrés
    private TankGroupScanner legacyScanner; // Scanner BFS legacy pour fallback
    private FermentationDebugger debugger;
    
    // Timing et contrôle
    private int scanCooldown = 0;
    private int lastRpm = 0;
    private boolean wasActive = false;
    private boolean useOptimizedScanning = true; // Toggle pour basculer entre les méthodes
    
    public FermentationValveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.optimizedScanner = new OptimizedTankScanner(null, pos); // Level sera set plus tard
        this.legacyScanner = new TankGroupScanner(null, pos); // Scanner BFS en fallback
        this.debugger = new FermentationDebugger(this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // Pas de comportements spéciaux pour l'instant
    }

    @Override
    public void tick() {
        super.tick();
        
        if (level == null || level.isClientSide) return;
        
        // Initialiser les scanners avec le level si nécessaire
        if (optimizedScanner.getLevel() == null) {
            Direction facing = getBlockState().getValue(FermentationValveBlock.FACING);
            optimizedScanner = new OptimizedTankScanner(level, worldPosition, facing);
        }
        if (legacyScanner.getLevel() == null) {
            legacyScanner = new TankGroupScanner(level, worldPosition);
        }
        
        // Gérer le cooldown de scan
        if (scanCooldown > 0) {
            scanCooldown--;
        }
        
        // Scanner ou rescanner les groupes de tanks
        if (scanCooldown <= 0) {
            scanForTankGroups();
            scanCooldown = SCAN_INTERVAL;
        }
        
        // Traiter la fermentation quantique
        processQuantumFermentation();
        
        // Debug périodique
        if (debugger != null) {
            debugger.periodicDebugLog();
        }
        
        // Mettre à jour l'état visuel du block
        updateBlockState();
    }

    /**
     * Scanne pour trouver des groupes de tanks connectés avec méthode optimisée.
     */
    private void scanForTankGroups() {
        TankGroup newGroup = null;
        
        if (useOptimizedScanning) {
            // Utiliser le scanner optimisé par carrés
            newGroup = optimizedScanner.scanWithCooldown();
            
            if (newGroup == null && debugger != null) {
                Amphora.LOGGER.debug("Optimized scanner found no square formation, falling back to BFS scanner");
                // Fallback vers BFS si aucun carré trouvé
                newGroup = legacyScanner.findNearestTankGroup();
            }
        } else {
            // Utiliser le scanner BFS legacy
            newGroup = legacyScanner.findNearestTankGroup();
        }
        
        if (newGroup != null && !newGroup.equals(currentTankGroup)) {
            // Nouveau groupe détecté
            currentTankGroup = newGroup;
            currentTankGroup.setArmed(true);
            
            // Créer un nouveau processeur pour ce groupe
            if (processor != null) {
                processor.reset();
            }
            processor = new QuantumFermentationProcessor(currentTankGroup);
            
            String scanMethod = useOptimizedScanning ? "optimized" : "legacy";
            Amphora.LOGGER.info("Armed tank group with {} tanks at center {} using {} scanner", 
                currentTankGroup.getTankCount(), currentTankGroup.getCenter(), scanMethod);
        }
        
        // Vérifier si le groupe actuel est encore valide
        if (currentTankGroup != null && !currentTankGroup.isValidGroup()) {
            disarmCurrentGroup();
        }
    }
    
    /**
     * Traite la fermentation quantique avec le processeur.
     */
    private void processQuantumFermentation() {
        if (processor == null || currentTankGroup == null) return;
        
        float currentRpm = Math.abs(getSpeed());
        
        // Vérifier les conditions minimales
        if (currentRpm < MIN_RPM_REQUIRED) {
            // Vitesse insuffisante
            if (wasActive) {
                Amphora.LOGGER.debug("Fermentation stopped: insufficient RPM ({} < {})", 
                    currentRpm, MIN_RPM_REQUIRED);
                wasActive = false;
            }
            return;
        }
        
        // Traiter un tick de fermentation
        boolean quantumProcessed = processor.tick(currentRpm);
        
        if (quantumProcessed) {
            Amphora.LOGGER.debug("Quantum fermentation completed at {} RPM", currentRpm);
        }
        
        // Mettre à jour l'état d'activité
        boolean isActive = processor.isActive();
        if (isActive != wasActive) {
            wasActive = isActive;
            if (isActive) {
                Amphora.LOGGER.debug("Fermentation started: {} at {} RPM", 
                    processor.getStatusInfo(), currentRpm);
            }
        }
        
        lastRpm = (int) currentRpm;
    }
    
    /**
     * Met à jour l'état visuel du block selon l'activité.
     */
    private void updateBlockState() {
        if (level == null) return;
        
        BlockState currentState = getBlockState();
        boolean shouldBeActive = processor != null && processor.isActive();
        boolean isCurrentlyActive = currentState.getValue(FermentationValveBlock.ACTIVE);
        
        if (shouldBeActive != isCurrentlyActive) {
            level.setBlock(worldPosition, 
                currentState.setValue(FermentationValveBlock.ACTIVE, shouldBeActive), 3);
        }
    }
    
    /**
     * Désarme le groupe actuel.
     */
    private void disarmCurrentGroup() {
        if (currentTankGroup != null) {
            currentTankGroup.setArmed(false);
            Amphora.LOGGER.info("Disarmed tank group at {}", currentTankGroup.getCenter());
        }
        
        currentTankGroup = null;
        
        if (processor != null) {
            processor.reset();
            processor = null;
        }
        
        wasActive = false;
    }
    
    /**
     * Désarme tous les tanks quand la valve est supprimée.
     */
    public void disarmTanks() {
        disarmCurrentGroup();
        Amphora.LOGGER.debug("Fermentation valve removed at {}", worldPosition);
    }

    /**
     * Affiche le statut de fermentation au joueur.
     * @param player Le joueur
     * @param detailed Si true, affiche le statut détaillé de debug
     */
    public void showStatusToPlayer(Player player, boolean detailed) {
        if (detailed && debugger != null) {
            debugger.showDetailedStatus(player);
            return;
        }
        
        if (currentTankGroup == null) {
            player.displayClientMessage(Component.literal(
                "Aucun groupe de tanks détecté dans la zone"), true);
            return;
        }
        
        String status = String.format("Groupe: %d tanks, RPM: %d", 
            currentTankGroup.getTankCount(), lastRpm);
            
        if (processor != null && processor.isActive()) {
            status += String.format(", %s", processor.getStatusInfo());
        } else if (lastRpm < MIN_RPM_REQUIRED) {
            status += String.format(", RPM insuffisant (min: %.0f)", MIN_RPM_REQUIRED);
        } else {
            status += ", Aucun jus fermentable détecté";
        }
        
        player.displayClientMessage(Component.literal(status), true);
    }
    
    /**
     * Affiche le statut simple au joueur.
     */
    public void showStatusToPlayer(Player player) {
        showStatusToPlayer(player, false);
    }
    
    /**
     * Gère l'interaction avec la valve (debug et diagnostic)
     */
    public void handlePlayerInteraction(Player player, boolean sneaking) {
        if (sneaking) {
            if (player.isCrouching() && player.isShiftKeyDown()) {
                // Ctrl + Shift + clic = toggle recipe matching method
                toggleRecipeMatchingMethod(player);
            } else if (player.isShiftKeyDown()) {
                // Alt + Shift + clic = toggle scanner method
                toggleScannerMethod(player);
            } else if (debugger != null) {
                // Shift + clic = toggle debug mode
                debugger.toggleDebugMode(player);
            }
        } else {
            // Clic droit normal = afficher statut
            showStatusToPlayer(player, debugger != null && debugger.isDebugMode());
        }
    }
    
    /**
     * Bascule entre enhanced et legacy recipe matching
     */
    private void toggleRecipeMatchingMethod(Player player) {
        if (processor != null) {
            processor.toggleMatchingMethod();
            boolean isEnhanced = processor.isUsingEnhancedMatching();
            String method = isEnhanced ? "Enhanced (tags, wildcards)" : "Legacy (direct fluids)";
            
            player.displayClientMessage(Component.literal(
                String.format("Recipe matching: %s", method)), true);
            
            Amphora.LOGGER.info("Recipe matching method toggled to {} at {}", method, worldPosition);
            
            // Afficher les stats détaillées si debug activé
            if (debugger != null && debugger.isDebugMode()) {
                processor.logDetailedStats();
            }
        } else {
            player.displayClientMessage(Component.literal(
                "Aucun processeur actif"), true);
        }
    }
    
    /**
     * Bascule entre scanner optimisé et scanner legacy
     */
    private void toggleScannerMethod(Player player) {
        useOptimizedScanning = !useOptimizedScanning;
        String method = useOptimizedScanning ? "optimisé (carrés)" : "legacy (BFS)";
        
        // Forcer un nouveau scan
        scanCooldown = 0;
        disarmCurrentGroup();
        
        player.displayClientMessage(Component.literal(
            String.format("Scanner changé vers: %s", method)), true);
        
        Amphora.LOGGER.info("Scanner method toggled to {} at {}", method, worldPosition);
    }
    
    /**
     * Diagnostic des problèmes
     */
    public void diagnoseProblems(Player player) {
        if (debugger != null) {
            debugger.diagnoseProblems(player);
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        
        scanCooldown = compound.getInt("ScanCooldown");
        lastRpm = compound.getInt("LastRpm");
        wasActive = compound.getBoolean("WasActive");
        useOptimizedScanning = compound.getBoolean("UseOptimizedScanning");
        
        // Forcer un nouveau scan au prochain tick
        if (scanCooldown > SCAN_INTERVAL) {
            scanCooldown = 0;
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        
        compound.putInt("ScanCooldown", scanCooldown);
        compound.putInt("LastRpm", lastRpm);
        compound.putBoolean("WasActive", wasActive);
        compound.putBoolean("UseOptimizedScanning", useOptimizedScanning);
    }
    
    // Getters pour le debug et l'interface
    public TankGroup getCurrentTankGroup() { return currentTankGroup; }
    public QuantumFermentationProcessor getProcessor() { return processor; }
    public boolean isActive() { return processor != null && processor.isActive(); }
    public float getCurrentRpm() { return Math.abs(getSpeed()); }
    public float getMinRpmRequired() { return MIN_RPM_REQUIRED; }
    public FermentationDebugger getDebugger() { return debugger; }
    public boolean isUsingOptimizedScanning() { return useOptimizedScanning; }
    public OptimizedTankScanner getOptimizedScanner() { return optimizedScanner; }
    public TankGroupScanner getLegacyScanner() { return legacyScanner; }
}
