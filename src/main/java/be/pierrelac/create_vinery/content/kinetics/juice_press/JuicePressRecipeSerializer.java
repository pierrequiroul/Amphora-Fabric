package be.pierrelac.create_vinery.content.kinetics.juice_press;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializer personnalisé pour les recettes de pressage de jus
 * Compatible avec l'API Create Fabric et Fabric 1.20.1
 */
public class JuicePressRecipeSerializer implements RecipeSerializer<JuicePressRecipe> {

    @Override
    public JuicePressRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        // Lire les ingrédients
        List<Ingredient> ingredients = readIngredients(json);

        // Lire les résultats
        JsonArray resultsArray = GsonHelper.getAsJsonArray(json, "results");
        List<ProcessingOutput> itemOutputs = new ArrayList<>();
        List<FluidOutput> fluidOutputs = new ArrayList<>();

        for (JsonElement resultElement : resultsArray) {
            JsonObject result = resultElement.getAsJsonObject();

            if (result.has("item")) {
                // Résultat d'item
                ProcessingOutput output = readItemResult(result);
                if (output != null) {
                    itemOutputs.add(output);
                }
            } else if (result.has("fluid")) {
                // Résultat de fluide
                FluidOutput fluidOutput = readFluidResult(result);
                if (fluidOutput != null) {
                    fluidOutputs.add(fluidOutput);
                }
            }
        }

        // Lire le temps de traitement
        int processingTime = 200; // Valeur par défaut
        if (json.has("processingTime")) {
            processingTime = GsonHelper.getAsInt(json, "processingTime");
        }

        return new JuicePressRecipe(recipeId, ingredients, itemOutputs, fluidOutputs, processingTime);
    }

    private List<Ingredient> readIngredients(JsonObject json) {
        List<Ingredient> ingredients = new ArrayList<>();

        if (json.has("ingredients")) {
            JsonArray ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");

            for (JsonElement ingredientElement : ingredientsArray) {
                JsonObject ingredientJson = ingredientElement.getAsJsonObject();

                if (ingredientJson.has("item")) {
                    String itemId = GsonHelper.getAsString(ingredientJson, "item");
                    int count = GsonHelper.getAsInt(ingredientJson, "count", 1);

                    // Créer un ingrédient pour chaque count
                    for (int i = 0; i < count; i++) {
                        ingredients.add(Ingredient.of(BuiltInRegistries.ITEM.get(new ResourceLocation(itemId))));
                    }
                } else if (ingredientJson.has("tag")) {
                    String tag = GsonHelper.getAsString(ingredientJson, "tag");
                    int count = GsonHelper.getAsInt(ingredientJson, "count", 1);

                    // Créer un ingrédient pour chaque count
                    for (int i = 0; i < count; i++) {
                        ingredients.add(Ingredient.fromJson(ingredientJson));
                    }
                }
            }
        }

        return ingredients;
    }

    @Nullable
    private ProcessingOutput readItemResult(JsonObject result) {
        try {
            String itemId = GsonHelper.getAsString(result, "item");
            int count = GsonHelper.getAsInt(result, "count", 1);
            float chance = GsonHelper.getAsFloat(result, "chance", 1.0f);

            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(itemId)), count);
            return new ProcessingOutput(stack, chance);
        } catch (Exception e) {
            throw new JsonParseException("Erreur lors de la lecture du résultat d'item: " + e.getMessage());
        }
    }

    @Nullable
    private FluidOutput readFluidResult(JsonObject result) {
        try {
            String fluidId = GsonHelper.getAsString(result, "fluid");
            long amount = GsonHelper.getAsLong(result, "amount", 1000L);

            Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(fluidId));
            return new FluidOutput(fluid, amount);
        } catch (Exception e) {
            throw new JsonParseException("Erreur lors de la lecture du résultat de fluide: " + e.getMessage());
        }
    }

    @Override
    public JuicePressRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        // Lire les ingrédients
        int ingredientCount = buffer.readVarInt();
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientCount; i++) {
            ingredients.add(Ingredient.fromNetwork(buffer));
        }

        // Lire les sorties d'items
        int itemOutputCount = buffer.readVarInt();
        List<ProcessingOutput> itemOutputs = new ArrayList<>();
        for (int i = 0; i < itemOutputCount; i++) {
            itemOutputs.add(ProcessingOutput.read(buffer));
        }

        // Lire les sorties de fluides
        int fluidOutputCount = buffer.readVarInt();
        List<FluidOutput> fluidOutputs = new ArrayList<>();
        for (int i = 0; i < fluidOutputCount; i++) {
            Fluid fluid = BuiltInRegistries.FLUID.byId(buffer.readVarInt());
            long amount = buffer.readVarLong();
            fluidOutputs.add(new FluidOutput(fluid, amount));
        }

        // Lire la durée
        int processingTime = buffer.readVarInt();

        return new JuicePressRecipe(recipeId, ingredients, itemOutputs, fluidOutputs, processingTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, JuicePressRecipe recipe) {
        // Écrire les ingrédients
        buffer.writeVarInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        // Écrire les sorties d'items
        List<ProcessingOutput> itemOutputs = recipe.getItemOutputs();
        buffer.writeVarInt(itemOutputs.size());
        for (ProcessingOutput output : itemOutputs) {
            output.write(buffer);
        }

        // Écrire les sorties de fluides
        List<FluidOutput> fluidOutputs = recipe.getFluidOutputs();
        buffer.writeVarInt(fluidOutputs.size());
        for (FluidOutput fluidOutput : fluidOutputs) {
            buffer.writeVarInt(BuiltInRegistries.FLUID.getId(fluidOutput.fluid()));
            buffer.writeVarLong(fluidOutput.amount());
        }

        // Écrire la durée
        buffer.writeVarInt(recipe.getProcessingDuration());
    }

    /**
     * Record pour représenter une sortie de fluide
     */
    public record FluidOutput(Fluid fluid, long amount) {
        public FluidVariant getFluidVariant() {
            return FluidVariant.of(fluid);
        }
    }
}
