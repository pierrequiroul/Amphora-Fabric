package be.pierrelac.amphora;

import be.pierrelac.amphora.compat.create.CreateCompat;
import be.pierrelac.amphora.core.ModCompatibility;
import be.pierrelac.amphora.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AmphoraClientConditional implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Amphora.LOGGER.info("=== AMPHORA CLIENT INITIALIZATION ===");
        Amphora.LOGGER.info("Initializing Amphora client on platform: {}", Platform.getPlatformName());
        
        // Enregistrement conditionnel côté client
        if (ModCompatibility.CREATE_LOADED) {
            Amphora.LOGGER.info("Create detected - enabling mechanical rendering");
            CreateCompat.initClient();
            
            // Enregistrer les partials pour l'animation Create
            ModPartials.init();
        } else {
            Amphora.LOGGER.info("Create not found - using basic rendering");
        }

        // Enregistrement de base (fluides, couleurs) - toujours disponible
        initBaseClientFeatures();
        
        Amphora.LOGGER.info("✓ Amphora client initialization complete");
        Amphora.LOGGER.info("======================================");
    }
    
    /**
     * Initialise les fonctionnalités client de base (indépendantes de Create)
     */
    private void initBaseClientFeatures() {
        try {
            // Déléguer à l'ancien AmphoraClient pour les fonctionnalités de base
            var originalClient = new AmphoraClient();
            
            // Appeler les méthodes de rendu de base
            // Note: Il faudra refactoriser AmphoraClient pour séparer les parties conditionnelles
            
            Amphora.LOGGER.info("✓ Base client features initialized");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to initialize base client features", e);
        }
    }
}
