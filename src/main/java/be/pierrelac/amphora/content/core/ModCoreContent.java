package be.pierrelac.amphora.content.core;

import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.entry.FluidEntry;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.AmphoraRegistrate;

/**
 * Contenu de base d'Amphora - fluides et items toujours disponibles
 * (pomme, cerise, etc. - pas de dépendances externes)
 */
public class ModCoreContent {
    
    // ===== REGISTRATE FLUIDS =====
    // Fluides de base toujours disponibles (indépendants de Vinery)
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> APPLE_JUICE = 
        AmphoraRegistrate.coloredJuiceFluid("apple_juice", 0xe8be72)
            .lang("Apple Juice")
            .tag(AmphoraRegistrate.JUICE_TAG)
            .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("fluid.amphora.apple_juice"))
            .source(SimpleFlowableFluid.Source::new)
            .bucket()
            .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
            .build()
            .register();
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> CHERRY_JUICE = 
        AmphoraRegistrate.coloredJuiceFluid("cherry_juice", 0xc44b55)
            .lang("Cherry Juice")
            .tag(AmphoraRegistrate.JUICE_TAG)
            .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("fluid.amphora.cherry_juice"))
            .source(SimpleFlowableFluid.Source::new)
            .bucket()
            .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
            .build()
            .register();
    
    public static void register() {
        AmphoraRegistrate.logModularRegistration("core", "base content");
        
        // Enregistrer les blocs de base
        ModCoreBlocks.register();
        
        // Enregistrer les items de base
        ModCoreItems.register();
        
        // Enregistrer les fluides de base (moderne)
        ModCoreFluids.register();
        
        Amphora.LOGGER.info("✓ Registrate core content registered: {} apple juice, {} cherry juice, {} mechanical press", 
            APPLE_JUICE.getId(), CHERRY_JUICE.getId(), ModCoreBlocks.MECHANICAL_JUICE_PRESS.getId());
        
        // Note: L'enregistrement effectif se fait automatiquement par Registrate
        // Les FluidEntry ci-dessus sont créés lors du chargement de la classe
    }
}
