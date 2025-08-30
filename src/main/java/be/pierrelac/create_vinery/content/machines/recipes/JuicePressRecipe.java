package be.pierrelac.create_vinery.content.machines.recipes;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.AllRecipeTypes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Type de recette pour la presse à jus mécanique
 * Utilise le système de recettes Basin de Create
 */
public class JuicePressRecipe extends BasinRecipe {

    // Créer un nouveau RecipeType en utilisant une classe anonyme
    public static final RecipeType<JuicePressRecipe> TYPE = new RecipeType<JuicePressRecipe>() {
        @Override
        public String toString() {
            return "create_vinery:juice_pressing";
        }
    };

    public JuicePressRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(AllRecipeTypes.BASIN, params);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JuicePressRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }
}
