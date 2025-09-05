package be.pierrelac.amphora.content.kinetics.fermenter.processor;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.content.kinetics.fermenter.scanner.TankGroup;
import be.pierrelac.amphora.content.kinetics.fermenter.recipe.FermentationRecipeManager;
import be.pierrelac.amphora.content.kinetics.fermenter.recipe.EnhancedRecipeMatcher;
import be.pierrelac.amphora.content.kinetics.fermenter.recipe.FermentationRecipe;
import be.pierrelac.amphora.content.kinetics.fermenter.metrics.FermentationMetrics;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Processeur quantique de fermentation.
 * Gère la fermentation par chunks de 100mB avec calculs de vitesse basés sur les RPM.
 * Implémente un système de buffers internes et d'I/O fluide transactionnel.
 */
public class QuantumFermentationProcessor {
    
    private final TankGroup tankGroup;
    private final FermentationRecipeManager recipeManager;
    private final EnhancedRecipeMatcher enhancedMatcher;
    
    // État du processus avec enhanced matching
    private FermentationRecipe currentRecipe;
    private int processingTicks = 0;
    private int requiredTicks = 0;
    private Fluid currentInputFluid = Fluids.EMPTY;
    private boolean useEnhancedMatching = true; // Toggle pour basculer entre les systèmes
    
    // Configuration quantique
    private static final int QUANTUM_SIZE = 100; // mB par quantum
    
    // Buffers internes pour éviter les conflits
    private Fluid inputReserveFluid = Fluids.EMPTY;
    private long inputReserveAmount = 0; // mB réservés pour le quantum en cours
    private Fluid outputBufferFluid = Fluids.EMPTY;
    private long outputBufferAmount = 0; // mB en attente d'évacuation
    private static final long OUTPUT_BUFFER_CAPACITY = 1000; // mB maximum dans le buffer de sortie
    
    public QuantumFermentationProcessor(TankGroup tankGroup) {
        this.tankGroup = tankGroup;
        this.recipeManager = FermentationRecipeManager.getInstance();
        this.enhancedMatcher = EnhancedRecipeMatcher.getInstance();
    }
    
    /**
     * Met à jour le processus de fermentation.
     * Appelé chaque tick par la valve.
     * 
     * @param rpm Vitesse actuelle en RPM
     * @return true si un quantum a été traité
     */
    public boolean tick(float rpm) {
        // Vérifier si le groupe est encore valide
        if (!tankGroup.isValidGroup()) {
            reset();
            return false;
        }
        
        // Essayer d'évacuer le buffer de sortie en priorité
        tryFlushOutputBuffer();
        
        // Déterminer le fluide à traiter (seulement si pas de réserve active)
        if (inputReserveAmount <= 0) {
            Fluid fluidToProcess = findFluidToProcess();
            
            if (fluidToProcess == Fluids.EMPTY) {
                reset();
                return false;
            }
            
            // Changement de fluide ou nouvelle recette
            if (!fluidToProcess.equals(currentInputFluid)) {
                currentInputFluid = fluidToProcess;
                
                // Utiliser le matcher approprié pour trouver la recette
                if (useEnhancedMatching) {
                    currentRecipe = enhancedMatcher.findRecipe(fluidToProcess);
                    if (currentRecipe == null) {
                        // Fallback vers le legacy matcher
                        currentRecipe = recipeManager.findRecipe(fluidToProcess);
                        Amphora.LOGGER.debug("Enhanced matcher failed, fallback to legacy found: {}", 
                            currentRecipe != null);
                    } else {
                        Amphora.LOGGER.debug("Enhanced matcher found recipe for {}", currentInputFluid);
                    }
                } else {
                    currentRecipe = recipeManager.findRecipe(fluidToProcess);
                    Amphora.LOGGER.debug("Legacy matcher found recipe for {}: {}", 
                        currentInputFluid, currentRecipe != null);
                }
                
                if (currentRecipe == null) {
                    reset();
                    return false;
                }
                
                requiredTicks = currentRecipe.calculateProcessingTime(rpm);
                processingTicks = 0;
            }
            
            // Réserver 100mB pour le quantum en cours
            if (!reserveQuantumInput()) {
                reset();
                return false;
            }
        }
        
        // Vérifier que la vitesse est suffisante
        if (currentRecipe != null && !currentRecipe.hasValidSpeed(rpm)) {
            // Vitesse insuffisante, arrêter le processus mais garder la réserve
            processingTicks = 0;
            return false;
        }
        
        // Recalculer le temps requis si nécessaire (vitesse variable)
        if (currentRecipe != null) {
            int newRequiredTicks = currentRecipe.calculateProcessingTime(rpm);
            if (newRequiredTicks != requiredTicks) {
                // Ajuster le progrès proportionnellement
                float progress = (float) processingTicks / requiredTicks;
                requiredTicks = newRequiredTicks;
                processingTicks = (int) (progress * requiredTicks);
            }
        }
        
        // Avancer le processus
        processingTicks++;
        
        // Vérifier si un quantum est prêt
        if (processingTicks >= requiredTicks) {
            boolean processed = processQuantum();
            processingTicks = 0; // Reset pour le prochain quantum
            return processed;
        }
        
        return false;
    }
    
    /**
     * Trouve le premier fluide fermentable dans le groupe avec enhanced matching.
     */
    private Fluid findFluidToProcess() {
        var fluids = tankGroup.getTotalFluids();
        
        // Initialiser les matchers avec le level si disponible
        if (tankGroup.getLevel() != null) {
            enhancedMatcher.initialize(tankGroup.getLevel());
        }
        
        Amphora.LOGGER.info("Scanning fluids in tank group with {} matcher:", 
            useEnhancedMatching ? "enhanced" : "legacy");
        
        for (var entry : fluids.entrySet()) {
            Fluid fluid = entry.getKey();
            int amount = entry.getValue();
            
            String fluidName = fluid.toString();
            boolean canFerment = false;
            boolean hasEnough = amount >= QUANTUM_SIZE;
            
            // Utiliser le matcher approprié
            if (useEnhancedMatching) {
                canFerment = enhancedMatcher.canFerment(fluid);
                if (canFerment) {
                    Amphora.LOGGER.info("  - Fluid: {} | Amount: {}mB | Enhanced match: YES | Has enough: {}", 
                        fluidName, amount, hasEnough);
                } else {
                    // Fallback vers le matcher legacy
                    canFerment = recipeManager.canFerment(fluid);
                    Amphora.LOGGER.info("  - Fluid: {} | Amount: {}mB | Enhanced match: NO, Legacy: {} | Has enough: {}", 
                        fluidName, amount, canFerment, hasEnough);
                }
            } else {
                canFerment = recipeManager.canFerment(fluid);
                Amphora.LOGGER.info("  - Fluid: {} | Amount: {}mB | Legacy match: {} | Has enough: {}", 
                    fluidName, amount, canFerment, hasEnough);
            }
            
            // Vérifier qu'il y a assez pour un quantum et qu'une recette existe
            if (hasEnough && canFerment) {
                String matcherType = useEnhancedMatching ? "enhanced" : "legacy";
                Amphora.LOGGER.info("  ✓ Selected fluid {} for fermentation using {} matcher", fluidName, matcherType);
                return fluid;
            }
        }
        
        Amphora.LOGGER.info("  ❌ No fermentable fluid found with {} matcher", 
            useEnhancedMatching ? "enhanced" : "legacy");
        return Fluids.EMPTY;
    }
    
    /**
     * Traite un quantum de fermentation (100mB).
     * Implémentation complète avec extraction/insertion transactionnelle.
     */
    private boolean processQuantum() {
        if (currentRecipe == null || !tankGroup.isValidGroup() || inputReserveAmount < QUANTUM_SIZE) {
            return false;
        }
        
        // Vérifier que le buffer de sortie peut accueillir le produit
        Fluid outputFluid = currentRecipe.getOutputFluid();
        if (outputBufferAmount > 0 && !outputBufferFluid.equals(outputFluid)) {
            Amphora.LOGGER.debug("Cannot process: output buffer contains different fluid");
            return false;
        }
        
        if (outputBufferAmount + QUANTUM_SIZE > OUTPUT_BUFFER_CAPACITY) {
            Amphora.LOGGER.debug("Cannot process: output buffer full");
            return false;
        }
        
        // La réserve d'entrée est déjà drainée, transférer vers le buffer de sortie
        outputBufferFluid = outputFluid;
        outputBufferAmount += QUANTUM_SIZE;
        inputReserveAmount -= QUANTUM_SIZE;
        
        if (inputReserveAmount <= 0) {
            inputReserveFluid = Fluids.EMPTY;
        }
        
        Amphora.LOGGER.info("Quantum fermentation: {}mB {} -> {} in group at {} (buffered)",
            QUANTUM_SIZE,
            currentRecipe.getInputFluid(),
            currentRecipe.getOutputFluid(),
            tankGroup.getCenter()
        );
        
        // Enregistrer les métriques
        FermentationMetrics.getInstance().recordQuantumProcessed(
            currentRecipe.getInputFluid().toString(), QUANTUM_SIZE);
        
        return true;
    }
    
    /**
     * Réserve 100mB du fluide d'entrée pour le quantum en cours.
     */
    private boolean reserveQuantumInput() {
        if (currentRecipe == null) return false;
        
        Fluid inputFluid = currentRecipe.getInputFluid();
        long drained = tankGroup.drainFluid(inputFluid, QUANTUM_SIZE);
        
        if (drained >= QUANTUM_SIZE) {
            inputReserveFluid = inputFluid;
            inputReserveAmount = drained;
            
            Amphora.LOGGER.debug("Reserved {}mB of {} for quantum processing", 
                drained, inputFluid);
            return true;
        } else {
            Amphora.LOGGER.debug("Failed to reserve quantum input: only {}mB available", drained);
            return false;
        }
    }
    
    /**
     * Tente d'évacuer le buffer de sortie vers les tanks.
     */
    private void tryFlushOutputBuffer() {
        if (outputBufferAmount <= 0 || outputBufferFluid == Fluids.EMPTY) {
            return;
        }
        
        // Essayer d'injecter le maximum possible
        long filled = tankGroup.fillFluid(outputBufferFluid, outputBufferAmount);
        
        if (filled > 0) {
            outputBufferAmount -= filled;
            
            if (outputBufferAmount <= 0) {
                outputBufferFluid = Fluids.EMPTY;
                outputBufferAmount = 0;
            }
            
            Amphora.LOGGER.debug("Flushed {}mB of {} from output buffer to tanks", 
                filled, outputBufferFluid);
        }
    }
    
    /**
     * Remet à zéro le processeur.
     */
    public void reset() {
        currentRecipe = null;
        processingTicks = 0;
        requiredTicks = 0;
        currentInputFluid = Fluids.EMPTY;
        
        // Retourner la réserve d'entrée aux tanks si nécessaire
        if (inputReserveAmount > 0 && inputReserveFluid != Fluids.EMPTY) {
            tankGroup.fillFluid(inputReserveFluid, inputReserveAmount);
            Amphora.LOGGER.debug("Returned {}mB of {} from input reserve to tanks", 
                inputReserveAmount, inputReserveFluid);
        }
        
        inputReserveFluid = Fluids.EMPTY;
        inputReserveAmount = 0;
        
        // Note: on garde le buffer de sortie pour éviter la perte
    }
    
    /**
     * Obtient le progrès de fermentation actuel.
     * @return Pourcentage entre 0.0 et 1.0
     */
    public float getProgress() {
        if (requiredTicks <= 0) return 0.0f;
        return Math.min(1.0f, (float) processingTicks / requiredTicks);
    }
    
    /**
     * Vérifie si le processeur est actif.
     */
    public boolean isActive() {
        return currentRecipe != null && processingTicks > 0;
    }
    
    /**
     * Obtient des informations de statut pour l'affichage.
     */
    public String getStatusInfo() {
        if (currentRecipe == null) {
            if (outputBufferAmount > 0) {
                return String.format("Buffer: %dmB %s en attente", 
                    outputBufferAmount, outputBufferFluid);
            }
            return "Aucune recette active";
        }
        
        StringBuilder status = new StringBuilder();
        status.append(String.format("Fermentation: %s -> %s (%.1f%%)", 
            currentRecipe.getInputFluid().toString(),
            currentRecipe.getOutputFluid().toString(),
            getProgress() * 100));
            
        if (inputReserveAmount > 0) {
            status.append(String.format(" | Réservé: %dmB", inputReserveAmount));
        }
        
        if (outputBufferAmount > 0) {
            status.append(String.format(" | Buffer: %dmB", outputBufferAmount));
        }
        
        return status.toString();
    }
    
    // Getters pour le debug
    public FermentationRecipe getCurrentRecipe() { return currentRecipe; }
    public int getProcessingTicks() { return processingTicks; }
    public int getRequiredTicks() { return requiredTicks; }
    public Fluid getCurrentInputFluid() { return currentInputFluid; }
    public long getInputReserveAmount() { return inputReserveAmount; }
    public long getOutputBufferAmount() { return outputBufferAmount; }
    public Fluid getOutputBufferFluid() { return outputBufferFluid; }
    public boolean isUsingEnhancedMatching() { return useEnhancedMatching; }
    
    /**
     * Indique si le processeur est en train de traiter un quantum
     */
    public boolean isProcessing() {
        return currentRecipe != null && (processingTicks > 0 || inputReserveAmount > 0);
    }
    
    /**
     * Bascule entre enhanced et legacy recipe matching.
     */
    public void toggleMatchingMethod() {
        useEnhancedMatching = !useEnhancedMatching;
        
        // Reset le processus actuel pour forcer une réévaluation
        if (currentRecipe != null) {
            reset();
        }
        
        String method = useEnhancedMatching ? "enhanced" : "legacy";
        Amphora.LOGGER.info("Recipe matching method switched to: {}", method);
    }
    
    /**
     * Force l'utilisation d'une méthode de matching spécifique.
     */
    public void setMatchingMethod(boolean useEnhanced) {
        if (useEnhancedMatching != useEnhanced) {
            toggleMatchingMethod();
        }
    }
    
    /**
     * Affiche les statistiques détaillées pour le debug.
     */
    public void logDetailedStats() {
        Amphora.LOGGER.info("=== Quantum Fermentation Processor Stats ===");
        Amphora.LOGGER.info("Matching method: {}", useEnhancedMatching ? "Enhanced" : "Legacy");
        Amphora.LOGGER.info("Active recipe: {}", currentRecipe != null ? currentRecipe.getId() : "None");
        Amphora.LOGGER.info("Current input fluid: {}", currentInputFluid);
        Amphora.LOGGER.info("Processing progress: {}/{} ticks ({}%)", 
            processingTicks, requiredTicks, Math.round(getProgress() * 100));
        Amphora.LOGGER.info("Input reserve: {}mB", inputReserveAmount);
        Amphora.LOGGER.info("Output buffer: {}mB {} (capacity: {}mB)", 
            outputBufferAmount, outputBufferFluid, OUTPUT_BUFFER_CAPACITY);
        
        // Statistiques des matchers
        if (useEnhancedMatching) {
            enhancedMatcher.logStatistics();
        } else {
            recipeManager.logRecipes();
        }
        
        Amphora.LOGGER.info("============================================");
    }
}
