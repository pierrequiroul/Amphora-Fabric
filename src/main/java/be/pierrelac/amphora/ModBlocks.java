package be.pierrelac.amphora;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestion de tous les blocs du mod Create: Vinery (LEGACY SYSTEM)
 * NOTE: Mechanical blocks are now managed by CreateRegistrationModule
 */
public class ModBlocks {

    // Registre pour tous les blocs de fluide
    public static final Map<String, Block> FLUID_BLOCKS = new HashMap<>();

    /**
     * Méthode d'enregistrement pour s'assurer que tous les blocs sont initialisés
     */
    public static void register() {
        Amphora.LOGGER.info("Registering Create: Vinery blocks");
        // Les blocs statiques sont déjà enregistrés, cette méthode force l'initialisation de la classe
        Amphora.LOGGER.info("✓ Blocks registered successfully");
    }

    /**
     * Enregistre un bloc de fluide liquide
     */
    public static LiquidBlock registerFluidBlock(String juiceId, FlowingFluid stillFluid) {
        String blockName = juiceId + "_juice";
        ResourceLocation blockId = new ResourceLocation("amphora", blockName);

        // Utiliser exactement la même signature que dans ModFluids
        var fluidBlock = new LiquidBlock(stillFluid, FabricBlockSettings.copy(Blocks.WATER));

        Registry.register(BuiltInRegistries.BLOCK, blockId, fluidBlock);
        FLUID_BLOCKS.put(juiceId, fluidBlock);

        Amphora.LOGGER.info("✓ Registered fluid block: {}", blockName);
        return fluidBlock;
    }

    /**
     * Getter pour un bloc de fluide spécifique
     */
    public static Block getFluidBlock(String juiceId) {
        return FLUID_BLOCKS.get(juiceId);
    }
}
