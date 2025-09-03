package be.pierrelac.amphora.core.modules;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.detection.DetectionResult;
import be.pierrelac.amphora.compat.emi.ConditionalEmiIntegration;

/**
 * Module pour l'intégration avec EMI
 */
public class EmiModule implements ConditionalModule {
    
    private ModuleState state = ModuleState.UNKNOWN;
    
    @Override
    public String getRequiredModId() {
        return "emi";
    }
    
    @Override
    public String getModuleName() {
        return "EMI Recipe Integration";
    }
    
    @Override
    public boolean canInitialize(DetectionResult detection) {
        // EMI peut être initialisé même si non présent (skip gracefully)
        return true;
    }
    
    @Override
    public void initialize() {
        state = ModuleState.INITIALIZING;
        
        try {
            Amphora.LOGGER.info("Initializing EMI integration...");
            
            // Initialiser l'intégration EMI conditionnelle
            ConditionalEmiIntegration.init();
            
            state = ModuleState.INITIALIZED;
            Amphora.LOGGER.info("✓ EMI integration initialized successfully");
            
        } catch (Exception e) {
            state = ModuleState.FAILED;
            Amphora.LOGGER.error("✗ Failed to initialize EMI integration", e);
            throw new RuntimeException("EMI integration failed", e);
        }
    }
    
    @Override
    public void cleanup() {
        try {
            if (state == ModuleState.INITIALIZED) {
                // ConditionalEmiIntegration n'a pas de méthode cleanup
                Amphora.LOGGER.debug("EMI integration marked for cleanup");
            }
        } catch (Exception e) {
            Amphora.LOGGER.warn("Error during EMI integration cleanup", e);
        } finally {
            state = ModuleState.NOT_AVAILABLE;
        }
    }
    
    @Override
    public int getInitializationPriority() {
        return 10; // Basse priorité - après Create et Vinery
    }
    
    @Override
    public ModuleState getState() {
        return state;
    }
}
