package be.pierrelac.create_vinery;

import be.pierrelac.create_vinery.events.PlacementEventHandler;
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

        // Enregistrer les fluides avec Fabric API pur
        ModFluids.register();

        // Enregistrer les blocs
        ModBlocks.register();

        // Enregistrer les BlockEntities
        ModBlockEntities.register();

        // Enregistrer le Creative Tab
        ModCreativeTab.register();

        // Enregistrer les gestionnaires d'événements pour le placement intelligent
        PlacementEventHandler.registerEvents();

        LOGGER.info("Successfully registered {} juice fluids with Fabric API", ModFluids.STILL_FLUIDS.size());
        LOGGER.info("Registered placement event handlers for smart basin placement");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
