package be.pierrelac.amphora;

import be.pierrelac.amphora.core.ConditionalRecipeManager;
import be.pierrelac.amphora.core.ModCompatibility;
import be.pierrelac.amphora.core.registration.RegistrationManager;
import be.pierrelac.amphora.core.registration.modules.CoreRegistrationModule;
import be.pierrelac.amphora.core.registration.modules.CreateRegistrationModule;
import be.pierrelac.amphora.core.registration.modules.VineryRegistrationModule;
import be.pierrelac.amphora.platform.Platform;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Amphora implements ModInitializer {
    public static final String MOD_ID = "amphora";
    public static final String ID = MOD_ID; // Backwards compatibility  
    public static final String NAME = "Amphora";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("=== AMPHORA MODULAR INITIALIZATION ===");
        LOGGER.info("Starting Amphora {} on platform: {}", NAME, Platform.getPlatformName());
        LOGGER.info("Development mode: {}", Platform.isDevelopmentEnvironment());
        
        // Afficher les informations de compatibilité
        LOGGER.info(ModCompatibility.getCompatibilityStatus());

        // === NOUVEAU SYSTÈME MODULAIRE ===
        
        // Enregistrer les modules de registration
        registerRegistrationModules();
        
        // Initialiser le système modulaire
        RegistrationManager.initializeAll();
        
        // Debug: voir tous les mods chargés pour trouver le bon ID Vinery
        ModCompatibility.debugListAllMods();
        
        // === ENREGISTREMENT LEGACY (sera progressivement déplacé) ===
        
        // Enregistrer les types de recettes selon les mods détectés
        ConditionalRecipeManager.init();

        // Enregistrer les fluides restants (non-modulaires)
        if (!CoreRegistrationModule.hasFluid("red_grape_juice")) {
            ModFluids.register();
        }

        // Enregistrer les blocs restants (non-Create)
        ModBlocks.register();

        // Block entities (déjà géré par CreateRegistrationModule)
        if (!CreateRegistrationModule.isContentAvailable()) {
            ModBlockEntities.register();
        }

        // Enregistrer le Creative Tab
        ModCreativeTab.register();

        // === RÉSUMÉ FINAL ===
        
        LOGGER.info("=== MODULAR INITIALIZATION COMPLETE ===");
        LOGGER.info("Recipe mode: {}", ConditionalRecipeManager.getRecipeMode());
        LOGGER.info("Registration: {}", RegistrationManager.getRegistrationStats());
        
        LOGGER.info("Amphora {} ready with modular registration!", NAME);
        LOGGER.info("===============================================");
    }
    
    /**
     * Enregistre tous les modules de registration
     */
    private void registerRegistrationModules() {
        LOGGER.info("Registering modular registration system...");
        
        // Module CORE - toujours actif
        RegistrationManager.registerModule("core", new CoreRegistrationModule());
        
        // Module CREATE - conditionnel
        RegistrationManager.registerModule("create", new CreateRegistrationModule());
        
        // Module VINERY - conditionnel
        RegistrationManager.registerModule("vinery", new VineryRegistrationModule());
        
        LOGGER.info("Registration modules configured");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
