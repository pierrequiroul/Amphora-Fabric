package be.pierrelac.amphora.platform;

/**
 * Interface d'abstraction pour les opérations spécifiques à la plateforme.
 * Permet de supporter Fabric, Forge, et potentiellement d'autres loaders.
 */
public interface PlatformHelper {
    
    /**
     * Retourne le nom de la plateforme actuelle
     */
    String getPlatformName();
    
    /**
     * Vérifie si un mod est chargé
     */
    boolean isModLoaded(String modId);
    
    /**
     * Retourne la version d'un mod
     */
    String getModVersion(String modId);
    
    /**
     * Retourne le répertoire de configuration
     */
    java.nio.file.Path getConfigDirectory();
    
    /**
     * Vérifie si on est côté client
     */
    boolean isClient();
    
    /**
     * Vérifie si on est côté serveur
     */
    boolean isServer();
    
    /**
     * Exécute du code côté client uniquement
     */
    void executeOnClient(Runnable clientCode);
    
    /**
     * Exécute du code côté serveur uniquement  
     */
    void executeOnServer(Runnable serverCode);
    
    /**
     * Retourne des informations sur l'environnement de développement
     */
    boolean isDevelopmentEnvironment();
}
