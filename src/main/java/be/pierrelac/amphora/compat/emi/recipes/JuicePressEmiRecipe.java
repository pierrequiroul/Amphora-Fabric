package be.pierrelac.amphora.compat.emi.recipes;

import java.util.List;

import com.google.common.collect.Lists;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import be.pierrelac.amphora.compat.emi.AmphoraEmiAnimations;
import be.pierrelac.amphora.content.kinetics.juice_press.JuicePressRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

/**
 * Recette EMI pour le juice press, basée sur le pattern de Create BasinEmiRecipe
 */
public class JuicePressEmiRecipe extends CreateEmiRecipe<JuicePressRecipe> {
    private final List<EmiIngredient> catalysts = Lists.newArrayList();
    private final boolean needsHeating;

    public JuicePressEmiRecipe(EmiRecipeCategory category, JuicePressRecipe recipe) {
        this(category, recipe, true); // Par défaut, on suppose que le chauffage n'est pas nécessaire pour le juice press
    }

    public JuicePressEmiRecipe(EmiRecipeCategory category, JuicePressRecipe recipe, boolean needsHeating) {
        super(category, recipe, 177, 108);
        if (!needsHeating) {
            height = 90;
        }
        this.needsHeating = needsHeating;
        
        // Gestion de la chaleur si nécessaire (pour les recettes qui ont besoin de chaleur)
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (!requiredHeat.testBlazeBurner(HeatLevel.NONE)) {
            catalysts.add(EmiStack.of(AllBlocks.BLAZE_BURNER.get()));
        }
        if (!requiredHeat.testBlazeBurner(HeatLevel.KINDLED)) {
            catalysts.add(EmiStack.of(AllItems.BLAZE_CAKE.get()));
        }
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return catalysts;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int inputSize = input.size();
        int outputSize = output.size();
        int vRows = (1 + outputSize) / 2;
        HeatCondition requiredHeat = recipe.getRequiredHeat();

        // Flèche vers le bas si on a 2 lignes ou moins d'outputs
        if (vRows <= 2) {
            addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, 136, 32 - 19 * (vRows - 1));
        }

        // Animation 3D de la juice press avec bassin (remplace les textures ombre/éclairage)
        AmphoraEmiAnimations.addJuicePress(widgets, 81, 40);

        // Variable pour la chaleur (encore utilisée plus bas)
        boolean noHeat = requiredHeat == HeatCondition.NONE;

        // Barre de chaleur si nécessaire
        if (needsHeating) {
            AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
            addTexture(widgets, heatBar, 4, 80);
            // Note: Texte de chaleur omis pour éviter les dépendances manquantes
        }

        // Positionnement des ingredients d'entrée
        int xOff = inputSize < 3 ? (3 - inputSize) * 19 / 2 : 0;
        int yOff = 0;

        for (int i = 0; i < inputSize; i++) {
            EmiIngredient stack = input.get(i);
            addSlot(widgets, stack, xOff + 16 + (i % 3) * 19, yOff + 50 - (i / 3) * 19);
        }

        // Positionnement des résultats de sortie
        for (int i = 0; i < outputSize; i++) {
            int x = 140 - (outputSize % 2 != 0 && i == outputSize - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int y = 50 - 20 * (i / 2) + yOff;

            EmiStack stack = output.get(i);
            addSlot(widgets, stack, x, y).recipeContext(this);
        }

        // Slots pour les catalyseurs de chaleur
        if (!requiredHeat.testBlazeBurner(HeatLevel.NONE)) {
            widgets.addSlot(EmiStack.of(AllBlocks.BLAZE_BURNER.get()), 133, 81).drawBack(false).catalyst(true);
        }
        if (!requiredHeat.testBlazeBurner(HeatLevel.KINDLED)) {
            widgets.addSlot(EmiStack.of(AllItems.BLAZE_CAKE.get()), 152, 81).drawBack(false);
        }
    }
}
