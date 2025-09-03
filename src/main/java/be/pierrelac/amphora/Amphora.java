package be.pierrelac.amphora;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Amphora implements ModInitializer {
    public static final String ID = "amphora";
    public static final String NAME = "Amphora";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Amphora with pure Fabric API approach");
        System.out.println("[AMPHORA] Mod initialization started");

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

        // Enregistrer les types de recettes
        ModRecipeTypes.register();

        // Enregistrer les fluides avec Fabric API pur
        ModFluids.register();

        // Enregistrer les blocs
        ModBlocks.register();

        // Enregistrer les BlockEntities
        ModBlockEntities.register();

        // Enregistrer le Creative Tab
        ModCreativeTab.register();

        // PLACEMENT EVENT HANDLER DÉSACTIVÉ - Cause crash HORIZONTAL_FACING
        // PlacementEventHandler.registerEvents(); 

        LOGGER.info("Successfully registered {} juice fluids with Fabric API", ModFluids.STILL_FLUIDS.size());
        LOGGER.info("Placement event handlers DISABLED due to HORIZONTAL_FACING crash");
        LOGGER.info("Recipe types registered: JUICE_PRESSING = {}", ModRecipeTypes.JUICE_PRESSING.getId());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
