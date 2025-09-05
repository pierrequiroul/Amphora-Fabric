package be.pierrelac.amphora;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import be.pierrelac.amphora.content.core.ModCoreContent;
import be.pierrelac.amphora.content.core.ModCoreRecipeTypes;
import be.pierrelac.amphora.content.core.ModCoreCreativeTab;
import be.pierrelac.amphora.content.core.ModCoreBlockEntities;
import be.pierrelac.amphora.content.vinery.ModVineryContent;
import be.pierrelac.amphora.content.kinetics.fermenter.recipe.ModRecipeTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Amphora implements ModInitializer {
    public static final String ID = "amphora";
    public static final String NAME = "Amphora";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Amphora with Registrate modular architecture");
        System.out.println("[AMPHORA] Mod initialization started - Registrate architecture");

        // Test EMI plugin instantiation
        if (FabricLoader.getInstance().isModLoaded("emi")) {
            System.out.println("[AMPHORA] EMI mod is loaded, testing plugin instantiation...");
            LOGGER.error("[EMI-TEST] EMI mod is loaded, testing plugin instantiation...");
            try {
                var plugin = new be.pierrelac.amphora.compat.emi.AmphoraEmiPlugin();
                System.out.println("[AMPHORA] ✓ EMI plugin instantiated successfully!");
                LOGGER.error("[EMI-TEST] ✓ EMI plugin instantiated successfully!");
            } catch (Exception e) {
                System.out.println("[AMPHORA] ✗ Failed to instantiate EMI plugin: " + e.getMessage());
                e.printStackTrace();
                LOGGER.error("[EMI-TEST] ✗ Failed to instantiate EMI plugin", e);
            }
        } else {
            System.out.println("[AMPHORA] EMI mod is not loaded");
            LOGGER.error("[EMI-TEST] EMI mod is not loaded");
        }

        // ===== NOUVELLE ARCHITECTURE MODULAIRE =====
        LOGGER.info("=== Starting modular content registration ===");
        
        // Enregistrer le contenu de base (toujours présent)
        ModCoreContent.register();
        
        // Enregistrer les modules conditionnels
        ModVineryContent.init(); // Enregistrer les fluides Vinery avec Registrate
        
        // ===== ANCIENNE ARCHITECTURE (TEMPORAIRE) =====
        // Garder l'ancien système en parallèle pendant la transition
        LOGGER.info("=== Starting legacy registration (temporary) ===");

        // Enregistrer les types de recettes
        ModCoreRecipeTypes.register();
        ModRecipeTypes.register(); // Recettes de fermentation JSON

        // Finaliser l'enregistrement Registrate D'ABORD (pour les fluides core)
        AmphoraRegistrate.REGISTRATE.register();
        LOGGER.info("✓ Registrate fluids finalized");

        // Enregistrer les blocs core (Registrate gère l'enregistrement automatiquement)
        // ModBlocks.register() remplacé par l'architecture moderne
        LOGGER.info("✓ Core blocks managed by Registrate");

        // Enregistrer les BlockEntities
        ModCoreBlockEntities.register();

        // Enregistrer le Creative Tab
        ModCoreCreativeTab.register();

        // PLACEMENT EVENT HANDLER DÉSACTIVÉ - Cause crash HORIZONTAL_FACING
        // PlacementEventHandler.registerEvents(); 

        LOGGER.info("All fluids registered via modern Registrate system");
        LOGGER.info("Placement event handlers DISABLED due to HORIZONTAL_FACING crash");
        LOGGER.info("Recipe types registered: JUICE_PRESSING = {}", ModCoreRecipeTypes.JUICE_PRESSING.getId());
        LOGGER.info("=== Modular architecture initialized ===");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
