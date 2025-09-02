package be.pierrelac.create_vinery.content.kinetics.juice_press;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;

/**
 * Serializer pour les recettes de pressage de jus
 * Utilise le ProcessingRecipeSerializer standard de Create
 */
public class JuicePressRecipeSerializer extends ProcessingRecipeSerializer<JuicePressRecipe> {

    public JuicePressRecipeSerializer() {
        super(JuicePressRecipe::create);
    }
}
