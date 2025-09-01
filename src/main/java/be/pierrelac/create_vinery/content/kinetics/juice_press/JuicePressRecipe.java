package be.pierrelac.create_vinery.content.kinetics.juice_press;

import be.pierrelac.create_vinery.ModRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

public class JuicePressRecipe extends BasinRecipe {

    public JuicePressRecipe(ProcessingRecipeParams params) {
        super(ModRecipeTypes.JUICE_PRESSING, params);
    }
}
