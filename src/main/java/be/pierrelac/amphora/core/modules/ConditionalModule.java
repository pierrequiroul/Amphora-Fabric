package be.pierrelac.amphora.core.modules;

import be.pierrelac.amphora.core.detection.DetectionResult;

/**
 * Interface pour les modules conditionnels basés sur la détection de mods
 */
public interface ConditionalModule {
    
    /**
     * ID du mod requis pour ce module
     */
    String getRequiredModId();
    
    /**
     * Nom descriptif du module
     */
    String getModuleName();
    
    /**
     * Vérifie si le module peut être initialisé
     */
    boolean canInitialize(DetectionResult detection);
    
    /**
     * Initialise le module
     */
    void initialize();
    
    /**
     * Nettoie les ressources du module
     */
    default void cleanup() {
        // Par défaut, rien à nettoyer
    }
    
    /**
     * Priorité d'initialisation (plus élevé = plus prioritaire)
     */
    default int getInitializationPriority() {
        return 0;
    }
    
    /**
     * Retourne l'état du module
     */
    default ModuleState getState() {
        return ModuleState.UNKNOWN;
    }
    
    /**
     * États possibles d'un module
     */
    enum ModuleState {
        NOT_AVAILABLE,    // Mod non disponible
        AVAILABLE,        // Mod disponible mais module non initialisé
        INITIALIZING,     // En cours d'initialisation
        INITIALIZED,      // Initialisé avec succès
        FAILED,          // Échec d'initialisation
        UNKNOWN          // État inconnu
    }
}
