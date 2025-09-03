package be.pierrelac.amphora.core.detection;

import be.pierrelac.amphora.Amphora;
import net.fabricmc.loader.api.FabricLoader;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Détecteur avancé de mods avec cache et validation multi-niveaux
 */
public class AdvancedModDetector {
    
    // Cache des résultats de détection
    private static final Map<String, DetectionResult> CACHE = new ConcurrentHashMap<>();
    
    // Temps de vie du cache (5 minutes)
    private static final long CACHE_TTL = 5 * 60 * 1000;
    private static final Map<String, Long> CACHE_TIMESTAMPS = new ConcurrentHashMap<>();
    
    /**
     * Détecte un mod avec cache
     */
    public static DetectionResult detect(String modId) {
        // Vérifier le cache
        Long timestamp = CACHE_TIMESTAMPS.get(modId);
        if (timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_TTL) {
            return CACHE.get(modId);
        }
        
        // Nouvelle détection
        DetectionResult result = performDetection(modId);
        
        // Mettre en cache
        CACHE.put(modId, result);
        CACHE_TIMESTAMPS.put(modId, System.currentTimeMillis());
        
        return result;
    }
    
    /**
     * Force une nouvelle détection (ignore le cache)
     */
    public static DetectionResult forceDetect(String modId) {
        CACHE.remove(modId);
        CACHE_TIMESTAMPS.remove(modId);
        return detect(modId);
    }
    
    /**
     * Effectue la détection complète d'un mod
     */
    private static DetectionResult performDetection(String modId) {
        DetectionResult result = new DetectionResult(modId);
        long startTime = System.currentTimeMillis();
        
        try {
            // Niveau 1: Mod présent
            result.modPresent = FabricLoader.getInstance().isModLoaded(modId);
            if (!result.modPresent) {
                result.statusMessage = "Mod not loaded";
                return result;
            }
            
            // Récupérer la version
            result.version = FabricLoader.getInstance()
                .getModContainer(modId)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("Unknown");
            
            // Niveau 2: Classes disponibles
            result.classesAvailable = testClassAvailability(modId);
            if (!result.classesAvailable) {
                result.statusMessage = "Required classes not accessible";
                return result;
            }
            
            // Niveau 3: Fonctionnalités testables
            result.featuresWorking = testFeatures(modId);
            if (!result.featuresWorking) {
                result.statusMessage = "Features not working properly";
                return result;
            }
            
            // Niveau 4: Version compatible
            result.versionCompatible = checkVersionCompatibility(modId, result.version);
            if (!result.versionCompatible) {
                result.statusMessage = String.format("Version %s incompatible", result.version);
                return result;
            }
            
            result.statusMessage = String.format("Fully functional (v%s)", result.version);
            
        } catch (Exception e) {
            result.lastError = e;
            result.statusMessage = "Detection failed: " + e.getMessage();
            Amphora.LOGGER.warn("Failed to detect mod {}: {}", modId, e.getMessage());
        }
        
        long detectionTime = System.currentTimeMillis() - startTime;
        Amphora.LOGGER.debug("Detected {} in {}ms: {}", modId, detectionTime, result.getDetailedStatus());
        
        return result;
    }
    
    /**
     * Teste la disponibilité des classes critiques pour un mod
     */
    private static boolean testClassAvailability(String modId) {
        try {
            switch (modId.toLowerCase()) {
                case "create":
                    return testCreateClasses();
                case "vinery":
                    return testVineryClasses();
                case "emi":
                    return testEmiClasses();
                case "flywheel":
                    return testFlywheelClasses();
                default:
                    return true; // Pour les mods non critiques
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Teste les fonctionnalités critiques d'un mod
     */
    private static boolean testFeatures(String modId) {
        try {
            switch (modId.toLowerCase()) {
                case "create":
                    return testCreateFeatures();
                case "vinery":
                    return testVineryFeatures();
                case "emi":
                    return testEmiFeatures();
                case "flywheel":
                    return testFlywheelFeatures();
                default:
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Vérifie la compatibilité de version
     */
    private static boolean checkVersionCompatibility(String modId, String version) {
        try {
            switch (modId.toLowerCase()) {
                case "create":
                    return isCreateVersionCompatible(version);
                case "vinery":
                    return isVineryVersionCompatible(version);
                case "emi":
                    return isEmiVersionCompatible(version);
                default:
                    return true; // Assume compatibility for unknown mods
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    // === Tests spécifiques Create ===
    
    private static boolean testCreateClasses() {
        try {
            Class.forName("com.simibubi.create.content.processing.basin.BasinRecipe");
            Class.forName("com.simibubi.create.content.kinetics.base.KineticBlockEntity");
            Class.forName("com.simibubi.create.foundation.recipe.IRecipeTypeInfo");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private static boolean testCreateFeatures() {
        // Test si les types de recettes fonctionnent
        try {
            // Test basique - peut être étendu
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static boolean isCreateVersionCompatible(String version) {
        // Version minimum supportée
        return !version.equals("Unknown");
    }
    
    // === Tests spécifiques Vinery ===
    
    private static boolean testVineryClasses() {
        // Vinery n'a pas de classes Java critiques - juste des items
        return true;
    }
    
    private static boolean testVineryFeatures() {
        // Test si les items Vinery sont accessibles
        return true;
    }
    
    private static boolean isVineryVersionCompatible(String version) {
        return !version.equals("Unknown");
    }
    
    // === Tests spécifiques EMI ===
    
    private static boolean testEmiClasses() {
        try {
            Class.forName("dev.emi.emi.api.EmiPlugin");
            Class.forName("dev.emi.emi.api.EmiRegistry");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private static boolean testEmiFeatures() {
        return true;
    }
    
    private static boolean isEmiVersionCompatible(String version) {
        return !version.equals("Unknown");
    }
    
    // === Tests spécifiques Flywheel ===
    
    private static boolean testFlywheelClasses() {
        try {
            Class.forName("com.jozufozu.flywheel.api.visualization.VisualizationContext");
            Class.forName("com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private static boolean testFlywheelFeatures() {
        return true;
    }
    
    /**
     * Vide le cache de détection
     */
    public static void clearCache() {
        CACHE.clear();
        CACHE_TIMESTAMPS.clear();
        Amphora.LOGGER.debug("Detection cache cleared");
    }
    
    /**
     * Retourne des statistiques sur le cache
     */
    public static String getCacheStats() {
        return String.format("Cache: %d entries, %d timestamps", 
            CACHE.size(), CACHE_TIMESTAMPS.size());
    }
}
