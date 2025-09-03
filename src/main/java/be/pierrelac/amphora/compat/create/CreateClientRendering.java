package be.pierrelac.amphora.compat.create;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.ModCompatibility;

/**
 * Gestion du rendu côté client spécifique à Create (Flywheel, etc.)
 */
public class CreateClientRendering {
    
    public static void register() {
        if (!ModCompatibility.CREATE_LOADED) {
            Amphora.LOGGER.warn("Attempting to register Create client rendering without Create!");
            return;
        }
        
        if (!ModCompatibility.FLYWHEEL_LOADED) {
            Amphora.LOGGER.warn("Create is loaded but Flywheel is missing - skipping advanced rendering");
            return;
        }
        
        try {
            // Enregistrer les Flywheel visuals conditionnellement
            registerFlywheelVisuals();
            
            // Enregistrer les block entity renderers
            registerBlockEntityRenderers();
            
            Amphora.LOGGER.info("✓ Create client rendering registered successfully");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to register Create client rendering", e);
            // Ne pas faire planter - le rendu n'est pas critique
        }
    }
    
    private static void registerFlywheelVisuals() {
        // Cette méthode sera implémentée avec la logique Flywheel
        // Elle sera déplacée depuis AmphoraClient
        Amphora.LOGGER.info("Registering Flywheel visuals...");
    }
    
    private static void registerBlockEntityRenderers() {
        // Cette méthode sera implémentée avec les renderers Create
        // Elle sera déplacée depuis AmphoraClient  
        Amphora.LOGGER.info("Registering Create block entity renderers...");
    }
}
