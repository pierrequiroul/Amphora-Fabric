package be.pierrelac.amphora;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.HashMap;

/**
 * Instance Registrate centrale pour Amphora - Create: Vinery Fabric
 * Permet l'enregistrement modulaire et conditionnel du contenu
 */
public class AmphoraRegistrate {
    
    /** Instance Registrate principale pour tous les enregistrements */
    public static final Registrate REGISTRATE = Registrate.create(Amphora.ID);
    
    /** Map des couleurs pour les fluides Registrate */
    private static final Map<String, Integer> REGISTRATE_JUICE_COLORS = new HashMap<>();
    
    // ===== TAGS POUR FLUIDES =====
    /** Tag pour tous les fluides de jus */
    public static final TagKey<Fluid> JUICE_TAG = TagKey.create(Registries.FLUID, new ResourceLocation(Amphora.ID, "juices"));
    
    /** Tag pour tous les buckets de jus */
    public static final TagKey<Item> JUICE_BUCKET_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(Amphora.ID, "juice_buckets"));
    
    // ===== MÉTHODES UTILITAIRES POUR FLUIDES =====
    
    /**
     * Crée un FluidBuilder pour un fluide standard avec blocs et buckets
     * @param name le nom du fluide
     * @return FluidBuilder configuré
     */
    public static FluidBuilder<SimpleFlowableFluid.Flowing, Registrate> standardFluid(String name) {
        // Utiliser les textures communes de jus pour les fluides Registrate
        return REGISTRATE.fluid(name, 
            new ResourceLocation(Amphora.ID, "fluid/grapejuice_still"), 
            new ResourceLocation(Amphora.ID, "fluid/juice_flow"));
    }
    
    /**
     * Crée un FluidBuilder pour un fluide de jus avec couleur personnalisée
     * @param name le nom du fluide
     * @param color la couleur en format hexadécimal (ex: 0xe8be72)
     * @return FluidBuilder configuré avec couleur
     */
    public static FluidBuilder<SimpleFlowableFluid.Flowing, Registrate> coloredJuiceFluid(String name, int color) {
        // Stocker la couleur pour le rendu client
        REGISTRATE_JUICE_COLORS.put(name, color);
        return standardFluid(name);
    }
    
    /**
     * Crée des attributs de fluide pour les jus compatibles Fabric
     * @param translationKey la clé de traduction du fluide
     * @return FluidVariantAttributeHandler configuré pour les jus
     */
    public static FluidVariantAttributeHandler createJuiceAttributes(String translationKey) {
        return new JuiceAttributeHandler(translationKey);
    }
    
    /**
     * Handler d'attributs de fluide spécialisé pour les jus - compatible Fabric
     */
    private static class JuiceAttributeHandler implements FluidVariantAttributeHandler {
        private final Component name;
        
        public JuiceAttributeHandler(String translationKey) {
            this.name = Component.translatable(translationKey);
        }
        
        @Override
        public Component getName(FluidVariant fluidVariant) {
            return name.copy();
        }
        
        @Override
        public int getViscosity(FluidVariant variant, @Nullable Level world) {
            return 1000; // Viscosité légèrement plus épaisse que l'eau pour les jus
        }
        
        @Override
        public boolean isLighterThanAir(FluidVariant variant) {
            return false; // Les jus sont plus lourds que l'air
        }
    }
    
    /**
     * Enregistre du contenu seulement si un mod spécifique est chargé
     * @param modId l'ID du mod requis
     * @param supplier le fournisseur de contenu à enregistrer
     * @param <T> le type de contenu
     * @return le contenu si le mod est chargé, sinon null
     */
    public static <T> T whenModLoaded(String modId, NonNullSupplier<T> supplier) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            return supplier.get();
        }
        return null;
    }
    
    /**
     * Vérifie si un mod est chargé
     * @param modId l'ID du mod à vérifier
     * @return true si le mod est chargé
     */
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    
    /**
     * Obtient la couleur d'un fluide Registrate
     * @param fluidName le nom du fluide
     * @return la couleur ou null si non définie
     */
    public static Integer getRegistrateJuiceColor(String fluidName) {
        return REGISTRATE_JUICE_COLORS.get(fluidName);
    }
    
    /**
     * Active les logs de débogage pour l'enregistrement modulaire
     * @param modId l'ID du mod en cours d'enregistrement
     * @param contentType le type de contenu (ex: "fluids", "blocks")
     */
    public static void logModularRegistration(String modId, String contentType) {
        if (isModLoaded(modId)) {
            Amphora.LOGGER.info("Registering {} content for mod: {}", contentType, modId);
        } else {
            Amphora.LOGGER.debug("Skipping {} content - mod {} not loaded", contentType, modId);
        }
    }
}
