package be.pierrelac.amphora.core.registration;

import be.pierrelac.amphora.Amphora;

/**
 * Interface pour les modules d'enregistrement conditionnels.
 * Chaque module gère l'enregistrement d'éléments spécifiques selon ses dépendances.
 */
public interface RegistrationModule {
    
    /**
     * Vérifie si ce module doit s'enregistrer
     * @return true si les conditions sont réunies
     */
    boolean shouldRegister();
    
    /**
     * Enregistre le contenu de ce module
     */
    void registerContent();
    
    /**
     * Nettoie les ressources si nécessaire
     */
    default void cleanup() {
        // Implémentation par défaut vide
    }
    
    /**
     * Obtient l'ID unique du module
     */
    String getModuleId();
    
    /**
     * Obtient la description du module
     */
    default String getDescription() {
        return "Registration module: " + getModuleId();
    }
    
    /**
     * Log utilitaire pour les modules
     */
    default void logInfo(String message, Object... args) {
        Amphora.LOGGER.info("[{}] " + message, getModuleId(), args);
    }
    
    default void logDebug(String message, Object... args) {
        Amphora.LOGGER.debug("[{}] " + message, getModuleId(), args);
    }
    
    default void logError(String message, Throwable throwable) {
        Amphora.LOGGER.error("[{}] " + message, getModuleId(), throwable);
    }
}
