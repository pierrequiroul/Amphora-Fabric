package be.pierrelac.amphora.platform;

import be.pierrelac.amphora.platform.fabric.FabricPlatformHelper;

/**
 * Point d'accès statique pour les opérations spécifiques à la plateforme.
 * Initialise automatiquement la bonne implémentation selon la plateforme détectée.
 */
public class Platform {
    
    private static final PlatformHelper HELPER = createPlatformHelper();
    
    /**
     * Retourne l'instance du helper de plateforme
     */
    public static PlatformHelper getHelper() {
        return HELPER;
    }
    
    /**
     * Créer le bon helper selon la plateforme détectée
     */
    private static PlatformHelper createPlatformHelper() {
        // Pour l'instant, on assume Fabric
        // Dans une version multiplateforme, on détecterait automatiquement
        try {
            Class.forName("net.fabricmc.loader.api.FabricLoader");
            return new FabricPlatformHelper();
        } catch (ClassNotFoundException e) {
            // Si Fabric n'est pas disponible, on pourrait essayer Forge
            // Pour l'instant, on lance une exception
            throw new RuntimeException("No supported platform detected! Expected Fabric.", e);
        }
    }
    
    // Méthodes de commodité qui délèguent au helper
    
    public static String getPlatformName() {
        return HELPER.getPlatformName();
    }
    
    public static boolean isModLoaded(String modId) {
        return HELPER.isModLoaded(modId);
    }
    
    public static String getModVersion(String modId) {
        return HELPER.getModVersion(modId);
    }
    
    public static boolean isClient() {
        return HELPER.isClient();
    }
    
    public static boolean isServer() {
        return HELPER.isServer();
    }
    
    public static boolean isDevelopmentEnvironment() {
        return HELPER.isDevelopmentEnvironment();
    }
}
