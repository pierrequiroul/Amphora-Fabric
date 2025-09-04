package be.pierrelac.amphora.content.core;

import com.tterrag.registrate.util.entry.ItemEntry;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.AmphoraRegistrate;
import be.pierrelac.amphora.content.fluids.juice.JuiceBucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

/**
 * Items de base d'Amphora - items toujours disponibles
 * Utilise Registrate pour un enregistrement proper et éviter les problèmes de timing
 */
public class ModCoreItems {
    
    // ===== REGISTRATE ITEMS =====
    // Items de base toujours disponibles
    
    // Registre pour tous les seaux de jus avec Registrate
    public static final Map<String, Item> FLUID_BUCKETS = new HashMap<>();
    
    // NOTE: Le bloc MECHANICAL_JUICE_PRESS est maintenant géré dans ModCoreBlocks
    // L'item sera créé automatiquement par Registrate via .item() dans ModCoreBlocks
    
    // Bouteille de jus de cerise
    public static final ItemEntry<Item> CHERRY_JUICE_BOTTLE = 
        AmphoraRegistrate.REGISTRATE.item("cherry_juice_bottle", Item::new)
            .lang("Cherry Juice Bottle")
            .properties(p -> p.stacksTo(16))
            .register();

    /**
     * Enregistre un seau de jus avec couleur pour tinting en utilisant Registrate
     */
    public static void registerJuiceBucket(String juiceId, Fluid stillFluid, int color) {
        String bucketName = juiceId + "_juice_bucket";

        var bucketItem = AmphoraRegistrate.REGISTRATE
            .item(bucketName, properties -> new JuiceBucketItem(stillFluid, color, properties
                .craftRemainder(Items.BUCKET)
                .stacksTo(1)))
            .register();

        FLUID_BUCKETS.put(juiceId, bucketItem.get());

        Amphora.LOGGER.info("✓ Registered juice bucket: {} with color 0x{}", bucketName, Integer.toHexString(color));
    }

    /**
     * Getter pour un seau de jus spécifique
     */
    public static Item getBucketItem(String juiceId) {
        return FLUID_BUCKETS.get(juiceId);
    }
    
    public static void register() {
        AmphoraRegistrate.logModularRegistration("core", "base items");
        
        Amphora.LOGGER.info("✓ Registrate core items registered: {} cherry bottle", 
            CHERRY_JUICE_BOTTLE.getId());
        
        // Note: L'enregistrement effectif se fait automatiquement par Registrate
        // Les ItemEntry ci-dessus sont créés lors du chargement de la classe
    }
}
