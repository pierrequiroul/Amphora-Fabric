package be.pierrelac.create_vinery;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateVinery implements ModInitializer {
    public static final String ID = "create_vinery";
    public static final String NAME = "Create: Vinery";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Create: Vinery with pure Fabric API approach");

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
