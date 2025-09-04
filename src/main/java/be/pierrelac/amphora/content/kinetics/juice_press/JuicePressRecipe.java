package be.pierrelac.amphora.content.kinetics.juice_press;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

import be.pierrelac.amphora.content.core.ModCoreRecipeTypes;

/**
 * Recette de pressage de jus qui utilise le système standard de Create
 * Compatible avec BasinRecipe et ProcessingRecipeParams
 */
public class JuicePressRecipe extends BasinRecipe {

    public JuicePressRecipe(ProcessingRecipeParams params) {
        super(ModCoreRecipeTypes.JUICE_PRESSING, params);
    }

    /**
     * Factory method pour le ProcessingRecipeSerializer
     * Compatible avec le pattern Create standard
     */
    public static JuicePressRecipe create(ProcessingRecipeParams params) {
        return new JuicePressRecipe(params);
    }
}
