package be.pierrelac.amphora.compat.emi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.ModBlocks;
import be.pierrelac.amphora.ModBlockAccess;
import be.pierrelac.amphora.ModRecipeTypes;
import be.pierrelac.amphora.compat.emi.recipes.JuicePressEmiRecipe;
import be.pierrelac.amphora.content.kinetics.juice_press.JuicePressRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.emi.DoubleItemIcon;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class AmphoraEmiPlugin implements EmiPlugin {
    
    static {
        Amphora.LOGGER.error("[EMI-PLUGIN] Static block - AmphoraEmiPlugin class loaded!");
    }
    
    public AmphoraEmiPlugin() {
        Amphora.LOGGER.error("[EMI-PLUGIN] AmphoraEmiPlugin constructor called!");
    }
    // Stockage de toutes les catégories EMI
    public static final Map<ResourceLocation, EmiRecipeCategory> ALL = new LinkedHashMap<>();

    // Catégorie pour le pressage de jus
    public static final EmiRecipeCategory JUICE_PRESSING = register("juice_pressing", 
        DoubleItemIcon.of(ModBlockAccess.getMechanicalJuicePress(), AllBlocks.BASIN.get()));

    @Override
    public void register(EmiRegistry registry) {
        Amphora.LOGGER.error("=== CREATE VINERY EMI PLUGIN: Starting registration ===");
        
        // Enregistrer toutes les catégories
        ALL.forEach((id, category) -> {
            registry.addCategory(category);
            Amphora.LOGGER.error("✓ Registered EMI category: {}", id);
        });

        // Ajouter les stations de travail pour le juice press
        registry.addWorkstation(JUICE_PRESSING, EmiStack.of(ModBlockAccess.getMechanicalJuicePress()));
        registry.addWorkstation(JUICE_PRESSING, EmiStack.of(AllBlocks.BASIN.get()));

        // Ajouter les recettes de pressage de jus
        addAllJuicePress(registry, ModRecipeTypes.JUICE_PRESSING.getType(), JUICE_PRESSING);

        Amphora.LOGGER.error("=== CREATE VINERY EMI PLUGIN: Registration completed ===");
    }

    /**
     * Helper method spécifique pour ajouter les recettes de juice press
     */
    private void addAllJuicePress(EmiRegistry registry, RecipeType<?> type, EmiRecipeCategory category) {
        Amphora.LOGGER.error("=== EMI RECIPE DIAGNOSTIC START ===");
        Amphora.LOGGER.error("Searching for recipes of type: {}", type);
        
        var recipeManager = registry.getRecipeManager();
        
        @SuppressWarnings("unchecked")
        var juicePressType = (RecipeType<JuicePressRecipe>) type;
        List<JuicePressRecipe> juicePressRecipes = recipeManager.getAllRecipesFor(juicePressType);
        Amphora.LOGGER.error("Found {} juice press recipes for type {}", juicePressRecipes.size(), type);
        
        if (juicePressRecipes.isEmpty()) {
            Amphora.LOGGER.error("⚠️ NO JUICE PRESS RECIPES FOUND!");
        }
        
        for (JuicePressRecipe recipe : juicePressRecipes) {
            try {
                Amphora.LOGGER.error("Processing recipe: {}", recipe.getId());
                
                var emiRecipe = new JuicePressEmiRecipe(category, recipe);
                registry.addRecipe(emiRecipe);
                Amphora.LOGGER.error("✓ Successfully added EMI recipe: {}", recipe.getId());
            } catch (Exception e) {
                Amphora.LOGGER.error("✗ Failed to add juice press recipe: {}", recipe.getId(), e);
            }
        }
        
        Amphora.LOGGER.error("✓ Added {} juice press EMI recipes", juicePressRecipes.size());
        Amphora.LOGGER.error("=== EMI RECIPE DIAGNOSTIC END ===");
    }

    /**
     * Enregistre une nouvelle catégorie EMI
     */
    private static EmiRecipeCategory register(String name, EmiRenderable icon) {
        ResourceLocation id = new ResourceLocation(Amphora.ID, name);
        EmiRecipeCategory category = new EmiRecipeCategory(id, icon);
        ALL.put(id, category);
        return category;
    }

    /**
     * Génère un ResourceLocation synthétique pour des recettes générées
     */
    public static ResourceLocation synthetic(String path) {
        return new ResourceLocation(Amphora.ID, "emi/" + path);
    }
}
