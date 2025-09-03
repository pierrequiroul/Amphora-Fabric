package be.pierrelac.amphora.core.modules;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.detection.AdvancedModDetector;
import be.pierrelac.amphora.core.detection.DetectionResult;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Gestionnaire modulaire de contenu avec détection automatique
 */
public class ModularContentManager {
    
    private static final Map<String, ConditionalModule> MODULES = new LinkedHashMap<>();
    private static final Map<String, DetectionResult> DETECTION_CACHE = new HashMap<>();
    private static final ExecutorService DETECTION_EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "Amphora-Detection");
        t.setDaemon(true);
        return t;
    });
    
    private static boolean initialized = false;
    
    static {
        // Enregistrer les modules disponibles
        registerModule(new CreateModule());
        registerModule(new VineryModule());
        registerModule(new EmiModule());
    }
    
    /**
     * Enregistre un module conditionnel
     */
    public static void registerModule(ConditionalModule module) {
        MODULES.put(module.getRequiredModId(), module);
        Amphora.LOGGER.debug("Registered module: {}", module.getModuleName());
    }
    
    /**
     * Initialise automatiquement tous les modules basés sur la détection
     */
    public static void initializeAll() {
        if (initialized) {
            Amphora.LOGGER.warn("ModularContentManager already initialized");
            return;
        }
        
        Amphora.LOGGER.info("Starting automatic mod detection and initialization...");
        long startTime = System.currentTimeMillis();
        
        try {
            // Détecter tous les mods en parallèle
            detectAllMods();
            
            // Initialiser les modules par ordre de priorité
            initializeModulesByPriority();
            
            initialized = true;
            long totalTime = System.currentTimeMillis() - startTime;
            
            logInitializationSummary(totalTime);
            
        } catch (Exception e) {
            Amphora.LOGGER.error("Failed to initialize modular content", e);
            throw new RuntimeException("Modular initialization failed", e);
        }
    }
    
    /**
     * Détecte tous les mods requis en parallèle
     */
    private static void detectAllMods() {
        List<CompletableFuture<Void>> detectionFutures = MODULES.keySet().stream()
            .map(modId -> CompletableFuture.runAsync(() -> {
                DetectionResult result = AdvancedModDetector.detect(modId);
                DETECTION_CACHE.put(modId, result);
            }, DETECTION_EXECUTOR))
            .collect(Collectors.toList());
        
        // Attendre que toutes les détections soient terminées
        CompletableFuture.allOf(detectionFutures.toArray(new CompletableFuture[0])).join();
        
        Amphora.LOGGER.info("Mod detection completed for {} modules", MODULES.size());
    }
    
    /**
     * Initialise les modules par ordre de priorité
     */
    private static void initializeModulesByPriority() {
        List<ConditionalModule> sortedModules = MODULES.values().stream()
            .sorted(Comparator.comparingInt(ConditionalModule::getInitializationPriority).reversed())
            .collect(Collectors.toList());
        
        for (ConditionalModule module : sortedModules) {
            initializeModule(module);
        }
    }
    
    /**
     * Initialise un module spécifique
     */
    private static void initializeModule(ConditionalModule module) {
        String modId = module.getRequiredModId();
        DetectionResult detection = DETECTION_CACHE.get(modId);
        
        if (detection == null) {
            Amphora.LOGGER.warn("No detection result for module {}", module.getModuleName());
            return;
        }
        
        try {
            if (module.canInitialize(detection)) {
                Amphora.LOGGER.debug("Initializing module: {} (Priority: {})", 
                    module.getModuleName(), module.getInitializationPriority());
                
                module.initialize();
                
                Amphora.LOGGER.debug("✓ Module {} initialized successfully", module.getModuleName());
            } else {
                Amphora.LOGGER.info("⚠ Module {} cannot be initialized: {}", 
                    module.getModuleName(), detection.statusMessage);
            }
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize module {}: {}", 
                module.getModuleName(), e.getMessage());
        }
    }
    
    /**
     * Force la re-détection et re-initialisation
     */
    public static void reinitialize() {
        Amphora.LOGGER.info("Forcing module reinitialization...");
        
        // Nettoyer les modules existants
        cleanup();
        
        // Vider les caches
        DETECTION_CACHE.clear();
        AdvancedModDetector.clearCache();
        
        // Re-initialiser
        initialized = false;
        initializeAll();
    }
    
    /**
     * Nettoie tous les modules
     */
    public static void cleanup() {
        for (ConditionalModule module : MODULES.values()) {
            try {
                module.cleanup();
            } catch (Exception e) {
                Amphora.LOGGER.warn("Error cleaning up module {}: {}", 
                    module.getModuleName(), e.getMessage());
            }
        }
        
        initialized = false;
    }
    
    /**
     * Retourne l'état d'un module spécifique
     */
    public static ConditionalModule.ModuleState getModuleState(String modId) {
        ConditionalModule module = MODULES.get(modId);
        return module != null ? module.getState() : ConditionalModule.ModuleState.NOT_AVAILABLE;
    }
    
    /**
     * Retourne les résultats de détection pour tous les mods
     */
    public static Map<String, DetectionResult> getDetectionResults() {
        return new HashMap<>(DETECTION_CACHE);
    }
    
    /**
     * Retourne des statistiques sur les modules
     */
    public static String getModuleStats() {
        Map<ConditionalModule.ModuleState, Long> stateCounts = MODULES.values().stream()
            .collect(Collectors.groupingBy(ConditionalModule::getState, Collectors.counting()));
        
        return String.format("Modules: %d total, %s", 
            MODULES.size(), stateCounts.toString());
    }
    
    /**
     * Log le résumé d'initialisation
     */
    private static void logInitializationSummary(long totalTime) {
        int availableCount = 0;
        int initializedCount = 0;
        int failedCount = 0;
        
        for (ConditionalModule module : MODULES.values()) {
            String modId = module.getRequiredModId();
            DetectionResult detection = DETECTION_CACHE.get(modId);
            
            if (detection != null && detection.modPresent) {
                availableCount++;
                
                if (module.getState() == ConditionalModule.ModuleState.INITIALIZED) {
                    initializedCount++;
                } else if (module.getState() == ConditionalModule.ModuleState.FAILED) {
                    failedCount++;
                }
            }
        }
        
        Amphora.LOGGER.info("=== Amphora Initialization Summary ===");
        Amphora.LOGGER.info("Total modules: {}", MODULES.size());
        Amphora.LOGGER.info("Available mods: {}", availableCount);
        Amphora.LOGGER.info("Initialized successfully: {}", initializedCount);
        
        if (failedCount > 0) {
            Amphora.LOGGER.warn("Failed initializations: {}", failedCount);
        }
        
        Amphora.LOGGER.info("Total initialization time: {}ms", totalTime);
        Amphora.LOGGER.info("Cache stats: {}", AdvancedModDetector.getCacheStats());
        Amphora.LOGGER.info("======================================");
        
        // Log détaillé des détections
        for (Map.Entry<String, DetectionResult> entry : DETECTION_CACHE.entrySet()) {
            DetectionResult result = entry.getValue();
            Amphora.LOGGER.debug("Detection {}: {}", entry.getKey(), result.getDetailedStatus());
        }
    }
    
    /**
     * Arrête le service d'exécution
     */
    public static void shutdown() {
        DETECTION_EXECUTOR.shutdown();
        cleanup();
    }
}
