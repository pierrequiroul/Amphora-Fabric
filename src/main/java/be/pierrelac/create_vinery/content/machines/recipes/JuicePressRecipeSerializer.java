package be.pierrelac.create_vinery.content.machines.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;

/**
 * Serializer pour les recettes de pressage de jus
 */
public class JuicePressRecipeSerializer extends ProcessingRecipeSerializer<JuicePressRecipe> {

    public static final JuicePressRecipeSerializer INSTANCE = new JuicePressRecipeSerializer();

    public JuicePressRecipeSerializer() {
        super(JuicePressRecipe::new);
    }
}
