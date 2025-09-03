package be.pierrelac.amphora.content.kinetics.juice_press;

import be.pierrelac.amphora.ModRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

/**
 * Recette de pressage de jus qui utilise le système standard de Create
 * Compatible avec BasinRecipe et ProcessingRecipeParams
 */
public class JuicePressRecipe extends BasinRecipe {

    public JuicePressRecipe(ProcessingRecipeParams params) {
        super(ModRecipeTypes.JUICE_PRESSING, params);
    }

    /**
     * Factory method pour le ProcessingRecipeSerializer
     * Compatible avec le pattern Create standard
     */
    public static JuicePressRecipe create(ProcessingRecipeParams params) {
        return new JuicePressRecipe(params);
    }
}
