package be.pierrelac.amphora.content.kinetics.fermenter.recipe;

import be.pierrelac.amphora.Amphora;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire JSON des recettes de fermentation.
 * Charge les recettes depuis les fichiers JSON et les intègre avec l'enhanced matcher.
 */
public class JsonRecipeManager {
    
    private static final JsonRecipeManager INSTANCE = new JsonRecipeManager();
    
    // Cache des recettes JSON chargées
    private final Map<Fluid, FermentationRecipeCreate> jsonRecipeCache = new ConcurrentHashMap<>();
    private final List<FermentationRecipeCreate> allJsonRecipes = new ArrayList<>();
    
    // État du système
    private boolean initialized = false;
    private Level lastKnownLevel = null;
    
    private JsonRecipeManager() {}
    
    public static JsonRecipeManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initialise le gestionnaire JSON avec un level pour accéder au RecipeManager.
     */
    public void initialize(Level level) {
        if (initialized && level == lastKnownLevel) return;
        
        this.lastKnownLevel = level;
        loadJsonRecipes(level);
        
        initialized = true;
        
        Amphora.LOGGER.info("JSON Recipe Manager initialized with {} recipes", 
            allJsonRecipes.size());
    }
    
    /**
     * Charge les recettes JSON depuis le RecipeManager de Minecraft.
     */
    private void loadJsonRecipes(Level level) {
        if (level == null) return;
        
        clearCache();
        
        RecipeManager recipeManager = level.getRecipeManager();
        
        // Récupérer toutes les recettes de fermentation
        Collection<FermentationRecipeCreate> fermentationRecipes = 
            recipeManager.getAllRecipesFor(FermentationRecipeCreate.Type.INSTANCE);
        
        for (FermentationRecipeCreate recipe : fermentationRecipes) {
            addJsonRecipe(recipe);
        }
        
        Amphora.LOGGER.info("Loaded {} JSON fermentation recipes", allJsonRecipes.size());
        logLoadedRecipes();
    }
    
    /**
     * Ajoute une recette JSON au cache.
     */
    private void addJsonRecipe(FermentationRecipeCreate recipe) {
        allJsonRecipes.add(recipe);
        jsonRecipeCache.put(recipe.getInputFluid().getFluid(), recipe);
        
        Amphora.LOGGER.debug("Added JSON recipe: {} -> {} ({}ticks, {}RPM)", 
            recipe.getInputFluid().getFluid(), 
            recipe.getOutputFluid().getFluid(),
            recipe.getProcessingTime(),
            recipe.getMinRpm());
    }
    
    /**
     * Trouve une recette JSON pour un fluide d'entrée.
     */
    public FermentationRecipeCreate findJsonRecipe(Fluid inputFluid) {
        if (!initialized) {
            Amphora.LOGGER.warn("JsonRecipeManager not initialized, cannot find recipes");
            return null;
        }
        
        return jsonRecipeCache.get(inputFluid);
    }
    
    /**
     * Convertit une recette JSON en recette legacy.
     */
    public FermentationRecipe findLegacyRecipe(Fluid inputFluid) {
        FermentationRecipeCreate jsonRecipe = findJsonRecipe(inputFluid);
        return jsonRecipe != null ? jsonRecipe.toLegacyRecipe() : null;
    }
    
    /**
     * Vérifie si un fluide peut être fermenté via JSON.
     */
    public boolean canFerment(Fluid fluid) {
        return findJsonRecipe(fluid) != null;
    }
    
    /**
     * Obtient toutes les recettes JSON chargées.
     */
    public List<FermentationRecipeCreate> getAllJsonRecipes() {
        return Collections.unmodifiableList(allJsonRecipes);
    }
    
    /**
     * Obtient toutes les recettes legacy converties.
     */
    public List<FermentationRecipe> getAllLegacyRecipes() {
        List<FermentationRecipe> legacyRecipes = new ArrayList<>();
        for (FermentationRecipeCreate jsonRecipe : allJsonRecipes) {
            legacyRecipes.add(jsonRecipe.toLegacyRecipe());
        }
        return legacyRecipes;
    }
    
    /**
     * Force le rechargement des recettes.
     */
    public void reload(Level level) {
        initialized = false;
        initialize(level);
    }
    
    /**
     * Vide le cache.
     */
    public void clearCache() {
        jsonRecipeCache.clear();
        allJsonRecipes.clear();
    }
    
    /**
     * Statistiques pour le debug.
     */
    public int getRecipeCount() {
        return allJsonRecipes.size();
    }
    
    /**
     * Affiche les recettes chargées.
     */
    private void logLoadedRecipes() {
        if (allJsonRecipes.isEmpty()) {
            Amphora.LOGGER.warn("No JSON fermentation recipes loaded! Check recipe files in data/amphora/recipes/");
            return;
        }
        
        Amphora.LOGGER.info("=== Loaded JSON Fermentation Recipes ===");
        for (FermentationRecipeCreate recipe : allJsonRecipes) {
            Amphora.LOGGER.info("  {} -> {} ({}ticks, {}RPM, {}mB quantum)",
                recipe.getInputFluid().getFluid(),
                recipe.getOutputFluid().getFluid(),
                recipe.getProcessingTime(),
                recipe.getMinRpm(),
                recipe.getQuantumSize());
        }
        Amphora.LOGGER.info("=======================================");
    }
    
    /**
     * Log des statistiques détaillées.
     */
    public void logStatistics() {
        Amphora.LOGGER.info("=== JSON Recipe Manager Statistics ===");
        Amphora.LOGGER.info("Initialized: {}", initialized);
        Amphora.LOGGER.info("Total JSON recipes: {}", allJsonRecipes.size());
        Amphora.LOGGER.info("Cached direct lookups: {}", jsonRecipeCache.size());
        Amphora.LOGGER.info("Last known level: {}", lastKnownLevel != null ? "Valid" : "None");
        Amphora.LOGGER.info("=====================================");
    }
}
