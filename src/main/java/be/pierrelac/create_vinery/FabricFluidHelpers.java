package be.pierrelac.create_vinery;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.level.material.Fluid;
// client render registration is handled in ModClient; this helper only exposes item-storage helpers

/**
 * Small helpers to register Fabric APIs for fluids (item storages and render handlers).
 */
/**
 * Helpers Fabric côté commun pour enregistrer les providers d'item-storage
 * (bouteilles, seaux pleins / vides) pour les jus.
 *
 * Cette classe ne contient que des méthodes utilitaires statiques pour éviter
 * de disperser les appels Fabric dans plusieurs fichiers.
 */
public final class FabricFluidHelpers {

    private FabricFluidHelpers() {}

    /**
     * Register commonly needed item storage providers for a juice fluid.
     * - GLASS_BOTTLE acts as an empty bottle that can be filled.
     * - The mod-provided bucket item stores the full fluid.
     * - The vanilla BUCKET accepts the fluid when emptying the mod bucket.
     */
    public static void registerItemStoragesForJuice(Fluid source, Item bucketItem, long bottleAmount) {
        CreateVinery.LOGGER.info("Registering item storages for fluid {} (bucket={}) bottleAmount={}", source, bucketItem, bottleAmount);

        // Bottle -> empty into source
        FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(context ->
            new EmptyItemFluidStorage(context, bottle -> ItemVariant.of(Items.POTION), source, bottleAmount)
        );

        // Full bucket implementation
        FluidStorage.combinedItemApiProvider(bucketItem).register(context ->
            new FullItemFluidStorage(context, bucket -> ItemVariant.of(Items.BUCKET), FluidVariant.of(source), FluidConstants.BUCKET)
        );

        // Empty bucket accepts the fluid
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
            new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(bucketItem), source, FluidConstants.BUCKET)
        );
    }
}
