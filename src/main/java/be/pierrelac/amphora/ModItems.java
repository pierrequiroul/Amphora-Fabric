package be.pierrelac.amphora;

import be.pierrelac.amphora.content.fluids.juice.JuiceBucketItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestion de tous les items du mod Create: Vinery
 */
public class ModItems {

    // Registre pour tous les seaux de jus
    public static final Map<String, Item> FLUID_BUCKETS = new HashMap<>();

    // Items de bloc
    public static final Item MECHANICAL_JUICE_PRESS_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        new ResourceLocation(Amphora.ID, "mechanical_juice_press"),
        new BlockItem(ModBlocks.MECHANICAL_JUICE_PRESS, new Item.Properties())
    );

    // Items de bouteilles
    public static final Item CHERRY_JUICE_BOTTLE = Registry.register(
        BuiltInRegistries.ITEM,
        new ResourceLocation(Amphora.ID, "cherry_juice_bottle"),
        new Item(new Item.Properties().stacksTo(16))
    );

    /**
     * Enregistre un seau de jus avec couleur pour tinting
     */
    public static void registerJuiceBucket(String juiceId, Fluid stillFluid, int color) {
        String bucketName = juiceId + "_juice_bucket";
        ResourceLocation bucketId = new ResourceLocation("amphora", bucketName);

        var bucketItem = new JuiceBucketItem(stillFluid, color, new Item.Properties()
                .craftRemainder(Items.BUCKET)
                .stacksTo(1));

        Registry.register(BuiltInRegistries.ITEM, bucketId, bucketItem);
        FLUID_BUCKETS.put(juiceId, bucketItem);

        Amphora.LOGGER.info("✓ Registered juice bucket: {} with color 0x{}", bucketName, Integer.toHexString(color));
    }

    /**
     * Getter pour un seau de jus spécifique
     */
    public static Item getBucketItem(String juiceId) {
        return FLUID_BUCKETS.get(juiceId);
    }
}
