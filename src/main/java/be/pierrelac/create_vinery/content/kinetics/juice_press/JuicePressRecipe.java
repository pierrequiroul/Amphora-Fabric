package be.pierrelac.create_vinery.content.kinetics.juice_press;

import be.pierrelac.create_vinery.ModRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JuicePressRecipe extends BasinRecipe {

    private static final Random RANDOM = new Random();

    // Variables pour stocker les données de la recette
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final List<ProcessingOutput> itemOutputs;
    private final List<JuicePressRecipeSerializer.FluidOutput> fluidOutputs;
    private final int processingTime;

    public JuicePressRecipe(ResourceLocation id, List<Ingredient> ingredients,
                           List<ProcessingOutput> itemOutputs,
                           List<JuicePressRecipeSerializer.FluidOutput> fluidOutputs,
                           int processingTime) {
        super(ModRecipeTypes.JUICE_PRESSING, null);
        this.id = id;
        this.ingredients = NonNullList.create();
        if (ingredients != null) {
            this.ingredients.addAll(ingredients);
        }
        this.itemOutputs = itemOutputs != null ? itemOutputs : new ArrayList<>();
        this.fluidOutputs = fluidOutputs != null ? fluidOutputs : new ArrayList<>();
        this.processingTime = processingTime;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        // Retourner le premier résultat d'item s'il existe
        if (!itemOutputs.isEmpty()) {
            return itemOutputs.get(0).getStack().copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected int getMaxInputCount() {
        return 9; // Support pour plusieurs ingrédients
    }

    @Override
    protected int getMaxOutputCount() {
        return 9; // Support pour plusieurs résultats
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 0; // Pas de fluides en entrée pour le pressage
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 3; // Support pour plusieurs fluides en sortie
    }

    /**
     * Vérifie si cette recette peut être appliquée avec les ingrédients donnés
     */
    public boolean canApplyTo(List<ItemStack> availableItems) {
        if (ingredients.isEmpty()) {
            return false;
        }

        // Créer une copie des items disponibles pour la vérification
        List<ItemStack> tempItems = new ArrayList<>();
        for (ItemStack stack : availableItems) {
            if (!stack.isEmpty()) {
                tempItems.add(stack.copy());
            }
        }

        // Vérifier chaque ingrédient requis
        for (Ingredient ingredient : ingredients) {
            boolean found = false;

            for (int i = 0; i < tempItems.size(); i++) {
                ItemStack available = tempItems.get(i);
                if (ingredient.test(available) && !available.isEmpty()) {
                    available.shrink(1); // Simuler la consommation
                    if (available.isEmpty()) {
                        tempItems.remove(i);
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }

    /**
     * Consomme les ingrédients nécessaires du conteneur
     */
    public void consumeIngredients(Container container) {
        for (Ingredient ingredient : ingredients) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (ingredient.test(stack) && !stack.isEmpty()) {
                    stack.shrink(1);
                    container.setItem(i, stack);
                    break; // Un seul item par ingrédient
                }
            }
        }
    }

    /**
     * Génère les résultats en tenant compte des chances
     */
    public List<ItemStack> rollResults() {
        List<ItemStack> results = new ArrayList<>();

        for (ProcessingOutput output : itemOutputs) {
            if (RANDOM.nextFloat() <= output.getChance()) {
                results.add(output.getStack().copy());
            }
        }

        return results;
    }

    /**
     * Retourne les résultats d'items garantis (chance = 1.0)
     */
    public List<ItemStack> getGuaranteedResults() {
        List<ItemStack> results = new ArrayList<>();

        for (ProcessingOutput output : itemOutputs) {
            if (output.getChance() >= 1.0f) {
                results.add(output.getStack().copy());
            }
        }

        return results;
    }

    /**
     * Retourne tous les résultats d'items possibles (pour la vérification de place)
     */
    public List<ItemStack> getAllPossibleResults() {
        List<ItemStack> results = new ArrayList<>();

        for (ProcessingOutput output : itemOutputs) {
            results.add(output.getStack().copy());
        }

        return results;
    }

    /**
     * Retourne les sorties d'items
     */
    public List<ProcessingOutput> getItemOutputs() {
        return itemOutputs;
    }

    /**
     * Retourne les sorties de fluides pour Fabric
     */
    public List<JuicePressRecipeSerializer.FluidOutput> getFluidOutputs() {
        return fluidOutputs;
    }

    /**
     * Retourne les variants de fluides pour la compatibilité Fabric
     */
    public List<FluidVariant> getFluidVariants() {
        List<FluidVariant> variants = new ArrayList<>();
        for (JuicePressRecipeSerializer.FluidOutput output : fluidOutputs) {
            variants.add(output.getFluidVariant());
        }
        return variants;
    }

    /**
     * Retourne le temps de traitement pour cette recette
     */
    public int getProcessingDuration() {
        return processingTime;
    }

    @Override
    public IRecipeTypeInfo getTypeInfo() {
        return ModRecipeTypes.JUICE_PRESSING;
    }

    /**
     * Vérifie si les ingrédients correspondent exactement (pour éviter les doublons)
     */
    public boolean matchesIngredients(List<ItemStack> items) {
        if (ingredients.size() != items.size()) {
            return false;
        }

        List<ItemStack> tempItems = new ArrayList<>();
        for (ItemStack stack : items) {
            tempItems.add(stack.copy());
        }

        for (Ingredient ingredient : ingredients) {
            boolean found = false;
            for (int i = 0; i < tempItems.size(); i++) {
                ItemStack stack = tempItems.get(i);
                if (ingredient.test(stack)) {
                    tempItems.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return tempItems.isEmpty();
    }
}
