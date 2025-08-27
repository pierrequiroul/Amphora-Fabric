package be.pierrelac.create_vinery;

import be.pierrelac.create_vinery.ModFluids;
import be.pierrelac.create_vinery.ModCreativeTab;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateVinery implements ModInitializer {
    public static final String ID = "create_vinery";
    public static final String NAME = "Create: Vinery";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
    
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);
    public static final CreateVinery INSTANCE = new CreateVinery();

    @Override
    public void onInitialize() {
        LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
        
        // Enregistrer d'abord le Creative Tab
        ModCreativeTab.register();
        
        // Configurer le REGISTRATE pour utiliser notre tab et le placer après l'onglet de Create
        REGISTRATE.setCreativeTab(ModCreativeTab.getTabKey());
        
        // Enregistrer les fluides (ceci enregistrera aussi les seaux et autres items associés)
        ModFluids.register();

        // S'assurer que le REGISTRATE a fini d'enregistrer tous les objets
        REGISTRATE.register();
        
        LOGGER.info("Successfully registered juice fluids for Create integration");
    }
    
    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
