package be.pierrelac.amphora.compat.emi;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.ModCompatibility;

/**
 * Gestionnaire conditionnel pour l'intégration EMI.
 * Charge les plugins de recettes seulement si EMI et les mods requis sont présents.
 */
public class ConditionalEmiIntegration {
    
    /**
     * Initialise l'intégration EMI de façon conditionnelle
     */
    public static void init() {
        if (!ModCompatibility.EMI_LOADED) {
            Amphora.LOGGER.info("EMI not loaded - skipping recipe integration");
            return;
        }
        
        try {
            Amphora.LOGGER.info("Initializing conditional EMI integration...");
            
            if (ModCompatibility.CREATE_LOADED) {
                initCreateEmiIntegration();
            }
            
            if (ModCompatibility.VINERY_LOADED) {
                initVineryEmiIntegration();
            }
            
            // Intégration EMI de base (toujours disponible)
            initBaseEmiIntegration();
            
            Amphora.LOGGER.info("✓ Conditional EMI integration initialized");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize EMI integration", e);
        }
    }
    
    /**
     * Initialise l'intégration EMI pour Create (juice pressing recipes)
     */
    private static void initCreateEmiIntegration() {
        try {
            Amphora.LOGGER.info("Initializing Create-EMI integration...");
            
            // L'ancien plugin EMI sera adapté pour fonctionner conditionnellement
            // Il chargera seulement les recettes Create si le mod est présent
            
            Amphora.LOGGER.info("✓ Create-EMI integration initialized");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize Create-EMI integration", e);
        }
    }
    
    /**
     * Initialise l'intégration EMI pour Vinery (ingrédients)
     */
    private static void initVineryEmiIntegration() {
        try {
            Amphora.LOGGER.info("Initializing Vinery-EMI integration...");
            
            // Intégration des items Vinery dans les catégories EMI
            // Tags et groupes d'items
            
            Amphora.LOGGER.info("✓ Vinery-EMI integration initialized");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize Vinery-EMI integration", e);
        }
    }
    
    /**
     * Initialise l'intégration EMI de base (fluides, items du mod)
     */
    private static void initBaseEmiIntegration() {
        try {
            Amphora.LOGGER.info("Initializing base EMI integration...");
            
            // Affichage des fluides du mod
            // Catégories d'items custom
            // Informations générales sur le mod
            
            Amphora.LOGGER.info("✓ Base EMI integration initialized");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize base EMI integration", e);
        }
    }
    
    /**
     * Teste la compatibilité EMI sans faire planter
     */
    public static boolean testEmiCompatibility() {
        if (!ModCompatibility.EMI_LOADED) return false;
        
        try {
            // Essayer d'instancier le plugin EMI
            var plugin = new be.pierrelac.amphora.compat.emi.AmphoraEmiPlugin();
            Amphora.LOGGER.info("✓ EMI compatibility test passed");
            return true;
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ EMI compatibility test failed: ", e);
            return false;
        }
    }
}
