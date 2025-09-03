package be.pierrelac.amphora.core.modules;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.detection.DetectionResult;
import be.pierrelac.amphora.compat.create.CreateCompat;

/**
 * Module pour l'intégration avec Create
 */
public class CreateModule implements ConditionalModule {
    
    private ModuleState state = ModuleState.UNKNOWN;
    
    @Override
    public String getRequiredModId() {
        return "create";
    }
    
    @Override
    public String getModuleName() {
        return "Create Integration";
    }
    
    @Override
    public boolean canInitialize(DetectionResult detection) {
        // Exiger au minimum que le mod soit présent et les classes disponibles
        return detection.modPresent && detection.classesAvailable;
    }
    
    @Override
    public void initialize() {
        state = ModuleState.INITIALIZING;
        
        try {
            Amphora.LOGGER.info("Initializing Create integration...");
            
            // Initialiser l'intégration Create
            CreateCompat.init();
            
            state = ModuleState.INITIALIZED;
            Amphora.LOGGER.info("✓ Create integration initialized successfully");
            
        } catch (Exception e) {
            state = ModuleState.FAILED;
            Amphora.LOGGER.error("✗ Failed to initialize Create integration", e);
            throw new RuntimeException("Create integration failed", e);
        }
    }
    
    @Override
    public void cleanup() {
        try {
            if (state == ModuleState.INITIALIZED) {
                // CreateCompat n'a pas de méthode cleanup, juste marquer comme non disponible
                Amphora.LOGGER.debug("Create integration marked for cleanup");
            }
        } catch (Exception e) {
            Amphora.LOGGER.warn("Error during Create integration cleanup", e);
        } finally {
            state = ModuleState.NOT_AVAILABLE;
        }
    }
    
    @Override
    public int getInitializationPriority() {
        return 100; // Haute priorité car Create est central
    }
    
    @Override
    public ModuleState getState() {
        return state;
    }
}
