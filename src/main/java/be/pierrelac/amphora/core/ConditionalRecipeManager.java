package be.pierrelac.amphora.core;

import be.pierrelac.amphora.Amphora;

/**
 * Gestionnaire conditionnel des types de recettes.
 * Adapte l'enregistrement selon les mods disponibles.
 */
public class ConditionalRecipeManager {
    
    private static boolean initialized = false;
    
    /**
     * Initialise les types de recettes selon les mods disponibles
     */
    public static void init() {
        if (initialized) {
            Amphora.LOGGER.warn("ConditionalRecipeManager already initialized!");
            return;
        }
        
        Amphora.LOGGER.info("Initializing conditional recipe system...");
        
        if (ModCompatibility.CREATE_LOADED) {
            registerCreateRecipes();
        } else {
            registerStandaloneRecipes();
        }
        
        initialized = true;
        Amphora.LOGGER.info("✓ Conditional recipe system initialized");
    }
    
    /**
     * Enregistre les recettes utilisant le système Create
     */
    private static void registerCreateRecipes() {
        try {
            Amphora.LOGGER.info("Registering Create-based recipes...");
            
            // Déléguer à l'ancien ModRecipeTypes si Create est présent
            be.pierrelac.amphora.ModRecipeTypes.register();
            
            Amphora.LOGGER.info("✓ Create-based recipes registered");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to register Create recipes, falling back to standalone", e);
            registerStandaloneRecipes();
        }
    }
    
    /**
     * Enregistre des recettes autonomes (sans Create)
     */
    private static void registerStandaloneRecipes() {
        try {
            Amphora.LOGGER.info("Registering standalone recipes...");
            
            // Ici on pourrait enregistrer des types de recettes alternatives
            // qui ne dépendent pas de Create (ex: furnace recipes, crafting recipes)
            registerSimpleJuiceRecipes();
            
            Amphora.LOGGER.info("✓ Standalone recipes registered");
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to register standalone recipes", e);
        }
    }
    
    /**
     * Enregistre des recettes de jus simples (sans bassin Create)
     */
    private static void registerSimpleJuiceRecipes() {
        // TODO: Implémenter des recettes alternatives
        // - Crafting recipes pour créer des bouteilles de jus
        // - Brewing recipes pour fermenter les jus
        // - Cauldron interactions pour mélanger
        
        Amphora.LOGGER.info("Simple juice recipes would be registered here");
    }
    
    /**
     * Vérifie si le système a été initialisé
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Retourne le mode de recettes actuel
     */
    public static String getRecipeMode() {
        if (!initialized) return "UNINITIALIZED";
        return ModCompatibility.CREATE_LOADED ? "CREATE_INTEGRATION" : "STANDALONE";
    }
}
