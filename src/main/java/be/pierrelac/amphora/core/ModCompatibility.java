package be.pierrelac.amphora.core;

import net.fabricmc.loader.api.FabricLoader;
import be.pierrelac.amphora.Amphora;

/**
 * Système de détection des mods optionnels à l'exécution.
 * Centralise la logique de compatibilité pour éviter les duplications.
 */
public class ModCompatibility {
    
    // Détection des mods au chargement de la classe
    public static final boolean CREATE_LOADED = isModLoaded("create");
    public static final boolean VINERY_LOADED = isModLoaded("vinery");
    public static final boolean EMI_LOADED = isModLoaded("emi");
    public static final boolean JEI_LOADED = isModLoaded("jei");
    public static final boolean REI_LOADED = isModLoaded("roughlyenoughitems");
    public static final boolean FLYWHEEL_LOADED = isModLoaded("flywheel");
    
    // Debug - testons différents mod IDs possibles pour Vinery
    public static final boolean VINERY_ALT1 = isModLoaded("letsdo-vinery");
    public static final boolean VINERY_ALT2 = isModLoaded("vinery-fabric");
    public static final boolean VINERY_ALT3 = isModLoaded("letsdovinery");
    
    // État du mod et de ses fonctionnalités
    public static final boolean MECHANICAL_PROCESSING_ENABLED = CREATE_LOADED;
    public static final boolean JUICE_RECIPES_ENABLED = VINERY_LOADED || true; // Toujours activé (peut utiliser des items vanilla)
    public static final boolean RECIPE_VIEWER_INTEGRATION = EMI_LOADED || JEI_LOADED || REI_LOADED;
    
    static {
        Amphora.LOGGER.info("=== MOD COMPATIBILITY DETECTION ===");
        Amphora.LOGGER.info("Create: {} {}", CREATE_LOADED ? "✓" : "✗", CREATE_LOADED ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("Vinery: {} {}", VINERY_LOADED ? "✓" : "✗", VINERY_LOADED ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("Vinery Alt1 (letsdo-vinery): {} {}", VINERY_ALT1 ? "✓" : "✗", VINERY_ALT1 ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("Vinery Alt2 (vinery-fabric): {} {}", VINERY_ALT2 ? "✓" : "✗", VINERY_ALT2 ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("Vinery Alt3 (letsdovinery): {} {}", VINERY_ALT3 ? "✓" : "✗", VINERY_ALT3 ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("EMI: {} {}", EMI_LOADED ? "✓" : "✗", EMI_LOADED ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("JEI: {} {}", JEI_LOADED ? "✓" : "✗", JEI_LOADED ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("REI: {} {}", REI_LOADED ? "✓" : "✗", REI_LOADED ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("Flywheel: {} {}", FLYWHEEL_LOADED ? "✓" : "✗", FLYWHEEL_LOADED ? "LOADED" : "MISSING");
        Amphora.LOGGER.info("=== FEATURE COMPATIBILITY ===");
        Amphora.LOGGER.info("Mechanical Processing: {}", MECHANICAL_PROCESSING_ENABLED ? "ENABLED" : "DISABLED");
        Amphora.LOGGER.info("Juice Recipes: {}", JUICE_RECIPES_ENABLED ? "ENABLED" : "DISABLED");
        Amphora.LOGGER.info("Recipe Viewer Integration: {}", RECIPE_VIEWER_INTEGRATION ? "ENABLED" : "DISABLED");
        Amphora.LOGGER.info("=======================================");
    }
    
    /**
     * Vérifie si un mod est chargé
     */
    private static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    
    /**
     * Retourne la version d'un mod si il est chargé
     */
    public static String getModVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("N/A");
    }
    
    /**
     * Méthode utilitaire pour exécuter du code seulement si un mod est présent
     */
    public static void ifModLoaded(String modId, Runnable action) {
        if (isModLoaded(modId)) {
            try {
                action.run();
            } catch (Exception e) {
                Amphora.LOGGER.error("Error executing action for mod {}: ", modId, e);
            }
        }
    }
    
    /**
     * Retourne un message d'état pour l'affichage
     */
    public static String getCompatibilityStatus() {
        return String.format(
            "Amphora Compatibility: Create=%s, Vinery=%s, Recipe Viewers=%s", 
            CREATE_LOADED ? "✓" : "✗",
            VINERY_LOADED ? "✓" : "✗", 
            RECIPE_VIEWER_INTEGRATION ? "✓" : "✗"
        );
    }
    
    /**
     * Compatibilité avec l'ancien système
     */
    public static boolean isCreateAvailable() {
        return CREATE_LOADED;
    }
    
    public static boolean isVineryAvailable() {
        return VINERY_LOADED || VINERY_ALT1 || VINERY_ALT2 || VINERY_ALT3;
    }
    
    /**
     * Debug: liste tous les mod IDs chargés pour trouver le bon ID Vinery
     */
    public static void debugListAllMods() {
        Amphora.LOGGER.info("=== ALL LOADED MODS ===");
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            String id = mod.getMetadata().getId();
            String name = mod.getMetadata().getName();
            if (id.toLowerCase().contains("vinery") || name.toLowerCase().contains("vinery")) {
                Amphora.LOGGER.info("VINERY CANDIDATE: ID='{}' NAME='{}'", id, name);
            }
            Amphora.LOGGER.info("Mod: ID='{}' NAME='{}'", id, name);
        });
        Amphora.LOGGER.info("=======================");
    }
}
