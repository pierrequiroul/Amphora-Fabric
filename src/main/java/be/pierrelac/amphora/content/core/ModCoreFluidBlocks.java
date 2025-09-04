package be.pierrelac.amphora.content.core;

import be.pierrelac.amphora.Amphora;
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
 * Gestionnaire spécialisé pour les blocs de fluides
 * Séparé de ModBlocks pour une responsabilité claire
 */
public class ModCoreFluidBlocks {

    // Registre pour tous les blocs de fluide
    private static final Map<String, LiquidBlock> FLUID_BLOCKS = new HashMap<>();

    /**
     * Enregistre un bloc de fluide liquide
     */
    public static LiquidBlock registerFluidBlock(String juiceId, FlowingFluid stillFluid) {
        String blockName = juiceId + "_juice";
        ResourceLocation blockId = new ResourceLocation("amphora", blockName);

        // Créer le bloc de fluide avec les propriétés standard
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

    /**
     * Récupère tous les blocs de fluides enregistrés
     */
    public static Map<String, LiquidBlock> getAllFluidBlocks() {
        return new HashMap<>(FLUID_BLOCKS);
    }

    /**
     * Vérifie si un bloc de fluide existe pour un juice ID donné
     */
    public static boolean hasFluidBlock(String juiceId) {
        return FLUID_BLOCKS.containsKey(juiceId);
    }
}
