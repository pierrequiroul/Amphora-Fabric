package be.pierrelac.amphora.content.kinetics.fermenter.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import be.pierrelac.amphora.Amphora;

/**
 * Enregistrement des types et serializers de recettes de fermentation.
 */
public class ModRecipeTypes {
    
    public static final RecipeType<FermentationRecipeCreate> FERMENTATION = 
        FermentationRecipeCreate.Type.INSTANCE;
    
    public static final RecipeSerializer<FermentationRecipeCreate> FERMENTATION_SERIALIZER = 
        FermentationRecipeCreate.Serializer.INSTANCE;
    
    /**
     * Enregistre les types et serializers de recettes.
     */
    public static void register() {
        // Enregistrer le type de recette
        Registry.register(BuiltInRegistries.RECIPE_TYPE, 
            new ResourceLocation("amphora", "fermentation"), FERMENTATION);
        
        // Enregistrer le serializer
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, 
            new ResourceLocation("amphora", "fermentation"), FERMENTATION_SERIALIZER);
        
        Amphora.LOGGER.info("Registered fermentation recipe type and serializer");
    }
}
