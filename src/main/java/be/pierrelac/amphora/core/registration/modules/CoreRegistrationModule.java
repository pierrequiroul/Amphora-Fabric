package be.pierrelac.amphora.core.registration.modules;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.registration.RegistrationModule;
import be.pierrelac.amphora.ModFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * Module d'enregistrement pour les éléments de base (non-dépendants).
 * Fluides, seaux, items génériques qui fonctionnent sans mods externes.
 */
public class CoreRegistrationModule implements RegistrationModule {
    
    @Override
    public String getModuleId() {
        return "core";
    }
    
    @Override
    public String getDescription() {
        return "Core items and fluids (no external dependencies)";
    }
    
    @Override
    public boolean shouldRegister() {
        // Les éléments core s'enregistrent toujours
        return true;
    }
    
    @Override
    public void registerContent() {
        logInfo("Registering core content...");
        
        // Enregistrement des fluides de base
        registerCoreFluid("red_grape_juice");
        registerCoreFluid("white_grape_juice");
        registerCoreFluid("savanna_grape_juice");
        registerCoreFluid("taiga_grape_juice");
        registerCoreFluid("jungle_grape_juice");
        registerCoreFluid("apple_juice");
        registerCoreFluid("cherry_juice");
        
        logInfo("Core registration complete - {} fluids registered", 7);
    }
    
    /**
     * Enregistre un fluide avec son seau associé
     */
    private void registerCoreFluid(String name) {
        try {
            logDebug("Registering fluid: {}", name);
            
            // Le fluide sera enregistré par ModFluids si nécessaire
            // Ici on s'assure que le seau existe
            ResourceLocation bucketId = new ResourceLocation(Amphora.MOD_ID, name + "_bucket");
            
            if (!BuiltInRegistries.ITEM.containsKey(bucketId)) {
                // Obtenir le fluide depuis ModFluids
                var fluid = ModFluids.STILL_FLUIDS.get(name.replace("_juice", ""));
                if (fluid != null) {
                    BucketItem bucketItem = new BucketItem(
                        fluid,
                        new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                    );
                    
                    Registry.register(BuiltInRegistries.ITEM, bucketId, bucketItem);
                    logDebug("Registered bucket: {}", bucketId);
                } else {
                    logDebug("Fluid not found for bucket: {}", name);
                }
            } else {
                logDebug("Bucket already exists: {}", bucketId);
            }
            
        } catch (Exception e) {
            logError("Failed to register core fluid: " + name, e);
        }
    }
    
    /**
     * Vérifie si un fluide core est disponible
     */
    public static boolean hasFluid(String name) {
        ResourceLocation fluidId = new ResourceLocation(Amphora.MOD_ID, name);
        return BuiltInRegistries.FLUID.containsKey(fluidId);
    }
    
    /**
     * Obtient la liste des fluides enregistrés
     */
    public static String[] getRegisteredFluids() {
        return new String[]{
            "red_grape_juice", "white_grape_juice", "savanna_grape_juice",
            "taiga_grape_juice", "jungle_grape_juice", "apple_juice", "cherry_juice"
        };
    }
}
