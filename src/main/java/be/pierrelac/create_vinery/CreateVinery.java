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
        
        // Enregistrer les fluides avec Fabric API pur
        ModFluids.register();
        
        // Enregistrer le Creative Tab 
        ModCreativeTab.register();
        
        LOGGER.info("Successfully registered {} juice fluids with Fabric API", ModFluids.STILL_FLUIDS.size());
    }
    
    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
