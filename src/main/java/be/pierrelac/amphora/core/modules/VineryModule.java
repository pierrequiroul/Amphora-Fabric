package be.pierrelac.amphora.core.modules;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.detection.DetectionResult;
import be.pierrelac.amphora.compat.vinery.VineryCompat;

/**
 * Module pour l'intégration avec Vinery
 */
public class VineryModule implements ConditionalModule {
    
    private ModuleState state = ModuleState.UNKNOWN;
    
    @Override
    public String getRequiredModId() {
        return "vinery";
    }
    
    @Override
    public String getModuleName() {
        return "Vinery Integration";
    }
    
    @Override
    public boolean canInitialize(DetectionResult detection) {
        // Vinery peut être initialisé même si le mod n'est pas présent (fallback)
        return true;
    }
    
    @Override
    public void initialize() {
        state = ModuleState.INITIALIZING;
        
        try {
            Amphora.LOGGER.info("Initializing Vinery integration...");
            
            // Initialiser l'intégration Vinery (avec fallback si nécessaire)
            VineryCompat.init();
            
            state = ModuleState.INITIALIZED;
            Amphora.LOGGER.info("✓ Vinery integration initialized successfully");
            
        } catch (Exception e) {
            state = ModuleState.FAILED;
            Amphora.LOGGER.error("✗ Failed to initialize Vinery integration", e);
            throw new RuntimeException("Vinery integration failed", e);
        }
    }
    
    @Override
    public void cleanup() {
        try {
            if (state == ModuleState.INITIALIZED) {
                // VineryCompat n'a pas de méthode cleanup
                Amphora.LOGGER.debug("Vinery integration marked for cleanup");
            }
        } catch (Exception e) {
            Amphora.LOGGER.warn("Error during Vinery integration cleanup", e);
        } finally {
            state = ModuleState.NOT_AVAILABLE;
        }
    }
    
    @Override
    public int getInitializationPriority() {
        return 50; // Priorité moyenne
    }
    
    @Override
    public ModuleState getState() {
        return state;
    }
}
