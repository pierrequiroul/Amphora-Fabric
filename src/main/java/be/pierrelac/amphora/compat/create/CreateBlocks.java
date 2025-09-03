package be.pierrelac.amphora.compat.create;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.ModBlocks;
import be.pierrelac.amphora.core.ModCompatibility;

/**
 * Gestion des blocs spécifiques à Create
 */
public class CreateBlocks {
    
    public static void register() {
        if (!ModCompatibility.CREATE_LOADED) {
            Amphora.LOGGER.warn("Attempting to register Create blocks without Create!");
            return;
        }
        
        try {
            // Déléguer à la classe principale mais avec vérification Create
            ModBlocks.register();
            
            Amphora.LOGGER.info("✓ Create blocks registered successfully");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to register Create blocks", e);
            throw e;
        }
    }
}
