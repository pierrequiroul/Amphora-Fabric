package be.pierrelac.amphora.content.kinetics.fermenter.recipe;

import be.pierrelac.amphora.Amphora;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.resources.ResourceLocation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire des recettes de fermentation quantique.
 * Fournit un cache performant et une API pour la correspondance des recettes.
 */
public class FermentationRecipeManager {
    
    private static final FermentationRecipeManager INSTANCE = new FermentationRecipeManager();
    
    // Cache des recettes par fluide d'entrée
    private final Map<Fluid, FermentationRecipe> recipeCache = new ConcurrentHashMap<>();
    private final List<FermentationRecipe> allRecipes = new ArrayList<>();
    
    // Recettes par défaut - pour commencer sans système de données JSON
    private boolean initialized = false;
    
    private FermentationRecipeManager() {}
    
    public static FermentationRecipeManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initialise les recettes par défaut.
     * Charge les recettes depuis les fichiers JSON.
     */
    public void initializeDefaultRecipes() {
        if (initialized) return;
        
        // Charger les recettes codées en dur pour l'instant
        // TODO: Remplacer par un vrai chargement JSON quand le système de recettes sera finalisé
        loadHardcodedRecipes();
        
        initialized = true;
        Amphora.LOGGER.info("Loaded {} fermentation recipes", recipeCache.size());
    }
    
    /**
     * Charge les recettes codées en dur.
     * Version temporaire en attendant l'intégration avec le système de recettes Minecraft.
     */
    private void loadHardcodedRecipes() {
        try {
            // On va essayer de récupérer les fluides par leur nom de registre
            // Ceci nécessite que Vinery soit chargé
            
            // Apple juice → Apple wine
            Fluid appleJuice = getFluidByName("vinery:apple_juice");
            Fluid appleWine = getFluidByName("vinery:apple_wine");
            if (appleJuice != null && appleWine != null) {
                addRecipe(new FermentationRecipe(
                    new ResourceLocation("amphora", "apple_wine_fermentation"),
                    appleJuice, appleWine, 200, 16, 100
                ));
            }
            
            // Cherry juice → Cherry wine  
            Fluid cherryJuice = getFluidByName("vinery:cherry_juice");
            Fluid cherryWine = getFluidByName("vinery:cherry_wine");
            if (cherryJuice != null && cherryWine != null) {
                addRecipe(new FermentationRecipe(
                    new ResourceLocation("amphora", "cherry_wine_fermentation"),
                    cherryJuice, cherryWine, 250, 18, 100
                ));
            }
            
            // Red grape juice → Red wine
            Fluid redGrapeJuice = getFluidByName("vinery:red_grape_juice");
            Fluid redWine = getFluidByName("vinery:red_wine");
            if (redGrapeJuice != null && redWine != null) {
                addRecipe(new FermentationRecipe(
                    new ResourceLocation("amphora", "red_wine_fermentation"),
                    redGrapeJuice, redWine, 300, 20, 100
                ));
            }
            
            // White grape juice → White wine
            Fluid whiteGrapeJuice = getFluidByName("vinery:white_grape_juice");
            Fluid whiteWine = getFluidByName("vinery:white_wine");
            if (whiteGrapeJuice != null && whiteWine != null) {
                addRecipe(new FermentationRecipe(
                    new ResourceLocation("amphora", "white_wine_fermentation"),
                    whiteGrapeJuice, whiteWine, 280, 18, 100
                ));
            }
            
        } catch (Exception e) {
            Amphora.LOGGER.error("Failed to load hardcoded fermentation recipes", e);
        }
    }
    
    /**
     * Récupère un fluide par son nom de registre.
     */
    private Fluid getFluidByName(String fluidName) {
        try {
            ResourceLocation location = new ResourceLocation(fluidName);
            Fluid fluid = BuiltInRegistries.FLUID.get(location);
            if (fluid != Fluids.EMPTY) {
                Amphora.LOGGER.debug("Found fluid: {}", fluidName);
                return fluid;
            } else {
                Amphora.LOGGER.warn("Fluid not found: {}", fluidName);
                return null;
            }
        } catch (Exception e) {
            Amphora.LOGGER.error("Error getting fluid {}: {}", fluidName, e.getMessage());
            return null;
        }
    }
    
    /**
     * Ajoute une recette au gestionnaire.
     */
    public void addRecipe(FermentationRecipe recipe) {
        allRecipes.add(recipe);
        recipeCache.put(recipe.getInputFluid(), recipe);
    }
    
    /**
     * Trouve une recette pour un fluide d'entrée donné.
     * 
     * @param inputFluid Fluide à fermenter
     * @return Recette correspondante ou null si aucune
     */
    public FermentationRecipe findRecipe(Fluid inputFluid) {
        if (!initialized) {
            initializeDefaultRecipes();
        }
        
        return recipeCache.get(inputFluid);
    }
    
    /**
     * Vérifie si un fluide peut être fermenté.
     */
    public boolean canFerment(Fluid fluid) {
        return findRecipe(fluid) != null;
    }
    
    /**
     * Obtient toutes les recettes disponibles.
     */
    public List<FermentationRecipe> getAllRecipes() {
        if (!initialized) {
            initializeDefaultRecipes();
        }
        
        return Collections.unmodifiableList(allRecipes);
    }
    
    /**
     * Vide le cache (pour le rechargement des recettes).
     */
    public void clearCache() {
        recipeCache.clear();
        allRecipes.clear();
        initialized = false;
    }
    
    /**
     * Statistiques pour le debug.
     */
    public int getRecipeCount() {
        return allRecipes.size();
    }
    
    /**
     * Information de debug sur les recettes chargées.
     */
    public void logRecipes() {
        System.out.println("=== Fermentation Recipes ===");
        System.out.println("Total recipes: " + getRecipeCount());
        
        for (FermentationRecipe recipe : allRecipes) {
            System.out.println("  " + recipe);
        }
        
        System.out.println("=============================");
    }
}
