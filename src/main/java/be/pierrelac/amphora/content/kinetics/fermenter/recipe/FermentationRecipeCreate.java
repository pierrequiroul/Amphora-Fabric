package be.pierrelac.amphora.content.kinetics.fermenter.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.RegistryAccess;

/**
 * Recette de fermentation avec sérialisation JSON pour Fabric.
 * Compatible avec le système de recettes Minecraft vanilla.
 */
public class FermentationRecipeCreate implements Recipe<Container> {
    
    // Propriétés de la recette
    private final ResourceLocation id;
    private final FluidIngredient inputFluid;
    private final FluidIngredient outputFluid;
    private final int processingTime;
    private final float minRpm;
    private final int quantumSize;
    
    public FermentationRecipeCreate(ResourceLocation id, FluidIngredient inputFluid, 
                                   FluidIngredient outputFluid, int processingTime, 
                                   float minRpm, int quantumSize) {
        this.id = id;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.processingTime = processingTime;
        this.minRpm = minRpm;
        this.quantumSize = quantumSize;
    }
    
    @Override
    public boolean matches(Container container, Level level) {
        // La logique de matching est gérée par le QuantumFermentationProcessor
        return true;
    }
    
    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY; // Pas d'output item
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY; // Pas d'output item
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }
    
    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
    
    // Getters pour les propriétés de fermentation
    public FluidIngredient getInputFluid() { return inputFluid; }
    public FluidIngredient getOutputFluid() { return outputFluid; }
    public int getProcessingTime() { return processingTime; }
    public float getMinRpm() { return minRpm; }
    public int getQuantumSize() { return quantumSize; }
    
    /**
     * Convertit vers FermentationRecipe legacy pour compatibilité
     */
    public FermentationRecipe toLegacyRecipe() {
        return new FermentationRecipe(
            getId(),
            inputFluid.getFluid(),
            outputFluid.getFluid(),
            processingTime,
            (int) minRpm,
            quantumSize
        );
    }
    
    // Type de recette
    public static class Type implements RecipeType<FermentationRecipeCreate> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "fermentation";
        
        @Override
        public String toString() {
            return "amphora:" + ID;
        }
    }
    
    // Serializer JSON
    public static class Serializer implements RecipeSerializer<FermentationRecipeCreate> {
        public static final Serializer INSTANCE = new Serializer();
        
        @Override
        public FermentationRecipeCreate fromJson(ResourceLocation recipeId, JsonObject json) {
            // Parser les propriétés de fermentation
            FluidIngredient inputFluid = parseFluidIngredient(
                GsonHelper.getAsJsonObject(json, "input_fluid"));
            
            FluidIngredient outputFluid = parseFluidIngredient(
                GsonHelper.getAsJsonObject(json, "output_fluid"));
            
            // Temps de traitement (en ticks)
            int processingTime = GsonHelper.getAsInt(json, "processing_time", 200);
            
            // RPM minimum
            float minRpm = GsonHelper.getAsFloat(json, "min_rpm", 16.0f);
            
            // Taille du quantum
            int quantumSize = GsonHelper.getAsInt(json, "quantum_size", 100);
            
            return new FermentationRecipeCreate(recipeId, inputFluid, outputFluid, 
                processingTime, minRpm, quantumSize);
        }
        
        @Override
        public FermentationRecipeCreate fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            FluidIngredient inputFluid = readFluidIngredient(buffer);
            FluidIngredient outputFluid = readFluidIngredient(buffer);
            int processingTime = buffer.readInt();
            float minRpm = buffer.readFloat();
            int quantumSize = buffer.readInt();
            
            return new FermentationRecipeCreate(recipeId, inputFluid, outputFluid,
                processingTime, minRpm, quantumSize);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, FermentationRecipeCreate recipe) {
            writeFluidIngredient(buffer, recipe.inputFluid);
            writeFluidIngredient(buffer, recipe.outputFluid);
            buffer.writeInt(recipe.processingTime);
            buffer.writeFloat(recipe.minRpm);
            buffer.writeInt(recipe.quantumSize);
        }
        
        /**
         * Parse un FluidIngredient depuis JSON
         */
        private FluidIngredient parseFluidIngredient(JsonObject fluidJson) {
            String fluidName = GsonHelper.getAsString(fluidJson, "fluid");
            long amount = GsonHelper.getAsLong(fluidJson, "amount", 1000L);
            
            ResourceLocation fluidLocation = new ResourceLocation(fluidName);
            Fluid fluid = BuiltInRegistries.FLUID.get(fluidLocation);
            
            if (fluid == Fluids.EMPTY) {
                throw new IllegalArgumentException("Unknown fluid: " + fluidName);
            }
            
            return new FluidIngredient(fluid, amount);
        }
        
        /**
         * Lit un FluidIngredient depuis le network buffer
         */
        private FluidIngredient readFluidIngredient(FriendlyByteBuf buffer) {
            ResourceLocation fluidLocation = buffer.readResourceLocation();
            long amount = buffer.readLong();
            
            Fluid fluid = BuiltInRegistries.FLUID.get(fluidLocation);
            return new FluidIngredient(fluid, amount);
        }
        
        /**
         * Écrit un FluidIngredient vers le network buffer
         */
        private void writeFluidIngredient(FriendlyByteBuf buffer, FluidIngredient ingredient) {
            ResourceLocation fluidLocation = BuiltInRegistries.FLUID.getKey(ingredient.getFluid());
            buffer.writeResourceLocation(fluidLocation);
            buffer.writeLong(ingredient.getAmount());
        }
    }
}
