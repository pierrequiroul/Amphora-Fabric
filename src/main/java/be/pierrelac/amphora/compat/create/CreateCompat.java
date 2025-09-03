package be.pierrelac.amphora.compat.create;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.ModRecipeTypes;
import be.pierrelac.amphora.core.ModCompatibility;

/**
 * Intégration conditionnelle avec Create.
 * Cette classe n'est chargée que si Create est présent.
 */
public class CreateCompat {
    
    /**
     * Initialise l'intégration Create si le mod est présent
     */
    public static void init() {
        if (!ModCompatibility.CREATE_LOADED) {
            Amphora.LOGGER.warn("CreateCompat.init() called but Create is not loaded!");
            return;
        }
        
        try {
            Amphora.LOGGER.info("Initializing Create integration...");
            
            // Enregistrer les types de recettes liés à Create
            ModRecipeTypes.register();
            
            // Enregistrer les BlockEntities kinétiques
            CreateBlockEntities.register();
            
            // Enregistrer les blocs Create-spécifiques
            CreateBlocks.register();
            
            Amphora.LOGGER.info("✓ Create integration initialized successfully");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize Create integration", e);
            throw new RuntimeException("Create integration failed", e);
        }
    }
    
    /**
     * Initialisation côté client pour Create
     */
    public static void initClient() {
        if (!ModCompatibility.CREATE_LOADED) return;
        
        try {
            Amphora.LOGGER.info("Initializing Create client integration...");
            
            // Enregistrer les renderers Flywheel
            CreateClientRendering.register();
            
            Amphora.LOGGER.info("✓ Create client integration initialized successfully");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize Create client integration", e);
        }
    }
}
