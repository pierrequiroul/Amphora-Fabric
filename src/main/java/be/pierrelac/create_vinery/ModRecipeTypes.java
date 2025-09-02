package be.pierrelac.create_vinery;

import be.pierrelac.create_vinery.content.kinetics.juice_press.JuicePressRecipeSerializer;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

/**
 * Types de recettes du mod Create: Vinery
 */
public enum ModRecipeTypes implements IRecipeTypeInfo {

    JUICE_PRESSING(() -> new JuicePressRecipeSerializer());

    private final ResourceLocation id;
    private final RecipeSerializer<?> serializerObject;
    private final RecipeType<?> typeObject;
    private final Supplier<RecipeType<?>> type;

    ModRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = new ResourceLocation(CreateVinery.ID, name);
        serializerObject = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializerSupplier.get());
        typeObject = simpleType(id);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id, typeObject);
        type = () -> typeObject;
    }

    // Constructeur alternatif pour les recettes de traitement standard
    ModRecipeTypes(ProcessingRecipeFactory<?> processingFactory) {
        String name = Lang.asId(name());
        id = new ResourceLocation(CreateVinery.ID, name);
        serializerObject = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, new ProcessingRecipeSerializer<>(processingFactory));
        typeObject = simpleType(id);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id, typeObject);
        type = () -> typeObject;
    }

    public static <T extends Recipe<?>> RecipeType<T> simpleType(ResourceLocation id) {
        String stringId = id.toString();
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return stringId;
            }
        };
    }

    public static void register() {
        CreateVinery.LOGGER.info("Registering Create: Vinery recipe types");
        // Charge la classe et initialise les enums
        CreateVinery.LOGGER.info("✓ Recipe types registered successfully");
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) type.get();
    }
}
