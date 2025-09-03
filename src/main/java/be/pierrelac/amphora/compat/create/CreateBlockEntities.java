package be.pierrelac.amphora.compat.create;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.ModBlockEntities;
import be.pierrelac.amphora.core.ModCompatibility;

/**
 * Gestion des BlockEntities spécifiques à Create
 */
public class CreateBlockEntities {
    
    public static void register() {
        if (!ModCompatibility.CREATE_LOADED) {
            Amphora.LOGGER.warn("Attempting to register Create BlockEntities without Create!");
            return;
        }
        
        try {
            // Déléguer à la classe principale mais avec vérification Create
            ModBlockEntities.register();
            
            Amphora.LOGGER.info("✓ Create BlockEntities registered successfully");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to register Create BlockEntities", e);
            throw e;
        }
    }
}
