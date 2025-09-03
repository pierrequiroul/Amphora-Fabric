package be.pierrelac.amphora.core.registration;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.ModCompatibility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Gestionnaire centralisé pour l'enregistrement modulaire des éléments du mod.
 * Permet un enregistrement conditionnel basé sur les mods détectés.
 */
public class RegistrationManager {
    
    private static final Map<String, RegistrationModule> modules = new ConcurrentHashMap<>();
    private static final List<String> registrationOrder = new ArrayList<>();
    
    static {
        // Ordre d'enregistrement optimal
        registrationOrder.add("core");      // Fluides, items de base
        registrationOrder.add("create");    // Machines kinétiques
        registrationOrder.add("vinery");    // Intégration Vinery
        registrationOrder.add("emi");       // Support viewers
    }
    
    /**
     * Enregistre un module de registration
     */
    public static void registerModule(String id, RegistrationModule module) {
        modules.put(id, module);
        Amphora.LOGGER.debug("Registered registration module: {}", id);
    }
    
    /**
     * Initialise tous les modules selon les conditions
     */
    public static void initializeAll() {
        Amphora.LOGGER.info("=== MODULAR REGISTRATION START ===");
        
        int registered = 0;
        int skipped = 0;
        
        for (String moduleId : registrationOrder) {
            RegistrationModule module = modules.get(moduleId);
            if (module != null) {
                if (module.shouldRegister()) {
                    long startTime = System.nanoTime();
                    
                    try {
                        module.registerContent();
                        registered++;
                        
                        long duration = (System.nanoTime() - startTime) / 1_000_000;
                        Amphora.LOGGER.info("✓ Module '{}' registered in {}ms", moduleId, duration);
                        
                    } catch (Exception e) {
                        Amphora.LOGGER.error("✗ Failed to register module '{}'", moduleId, e);
                    }
                } else {
                    skipped++;
                    Amphora.LOGGER.info("⚠ Module '{}' skipped (dependencies not met)", moduleId);
                }
            }
        }
        
        Amphora.LOGGER.info("Registration summary: {} modules registered, {} skipped", registered, skipped);
        Amphora.LOGGER.info("=== MODULAR REGISTRATION COMPLETE ===");
    }
    
    /**
     * Obtient les statistiques d'enregistrement
     */
    public static String getRegistrationStats() {
        int total = modules.size();
        int active = (int) modules.values().stream()
                .filter(RegistrationModule::shouldRegister)
                .count();
        
        return String.format("Registration modules: %d total, %d active", total, active);
    }
    
    /**
     * Nettoie les ressources si nécessaire
     */
    public static void cleanup() {
        modules.values().forEach(module -> {
            try {
                module.cleanup();
            } catch (Exception e) {
                Amphora.LOGGER.warn("Failed to cleanup module", e);
            }
        });
    }
}
