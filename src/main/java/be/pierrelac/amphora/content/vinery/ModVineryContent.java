package be.pierrelac.amphora.content.vinery;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.AmphoraRegistrate;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.entry.FluidEntry;

/**
 * Contenu Registrate conditionnel pour le mod Vinery
 * Fluides de raisin par biomes (nécessite Vinery)
 */
public class ModVineryContent {

    // ===== FLUIDES VINERY (CONDITIONNELS) =====
    // Enregistrés seulement si Vinery est présent
    
    // Raisins standards
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_grape_juice", 0x6e386c)
                .lang("Red Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.red_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_grape_juice", 0x80c04c)
                .lang("White Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.white_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    // Raisins de savane
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_SAVANNA_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_savanna_grape_juice", 0xa23661)
                .lang("Red Savanna Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.red_savanna_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_SAVANNA_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_savanna_grape_juice", 0xbbbd44)
                .lang("White Savanna Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.white_savanna_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    // Raisins de taïga
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_TAIGA_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_taiga_grape_juice", 0x6633a4)
                .lang("Red Taiga Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.red_taiga_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_TAIGA_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_taiga_grape_juice", 0x77b476)
                .lang("White Taiga Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.white_taiga_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    // Raisins de jungle
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_JUNGLE_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_jungle_grape_juice", 0x943682)
                .lang("Red Jungle Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.red_jungle_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_JUNGLE_GRAPE_JUICE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_jungle_grape_juice", 0xabbb5c)
                .lang("White Jungle Grape Juice")
                .tag(AmphoraRegistrate.JUICE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createJuiceAttributes("block.amphora.white_jungle_grape_juice"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.JUICE_BUCKET_TAG)
                .build()
                .register()
        );
    
    // ===== VINERY WINES (CONDITIONNELS) =====
    // Vins obtenus par fermentation des jus de raisin
    
    // Vins standards
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_grape_wine", 0x5c2a5a)
                .lang("Red Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.red_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_grape_wine", 0xa19e3c)
                .lang("White Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.white_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    // Vins de savane
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_SAVANNA_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_savanna_grape_wine", 0x7b4b3a)
                .lang("Red Savanna Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.red_savanna_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_SAVANNA_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_savanna_grape_wine", 0x9ba038)
                .lang("White Savanna Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.white_savanna_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    // Vins de taïga
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_TAIGA_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_taiga_grape_wine", 0x522792)
                .lang("Red Taiga Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.red_taiga_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_TAIGA_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_taiga_grape_wine", 0x8ca344)
                .lang("White Taiga Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.white_taiga_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    // Vins de jungle
    public static final FluidEntry<SimpleFlowableFluid.Flowing> RED_JUNGLE_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("red_jungle_grape_wine", 0x7a2e6a)
                .lang("Red Jungle Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.red_jungle_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    public static final FluidEntry<SimpleFlowableFluid.Flowing> WHITE_JUNGLE_GRAPE_WINE = 
        AmphoraRegistrate.whenModLoaded("vinery", () ->
            AmphoraRegistrate.coloredJuiceFluid("white_jungle_grape_wine", 0x8fa04a)
                .lang("White Jungle Grape Wine")
                .tag(AmphoraRegistrate.WINE_TAG)
                .fluidAttributes(() -> AmphoraRegistrate.createWineAttributes("block.amphora.white_jungle_grape_wine"))
                .source(SimpleFlowableFluid.Source::new)
                .bucket()
                .tag(AmphoraRegistrate.WINE_BUCKET_TAG)
                .build()
                .register()
        );
    
    /**
     * Initialise le contenu Vinery (appel statique pour trigger l'enregistrement)
     */
    public static void init() {
        if (AmphoraRegistrate.isModLoaded("vinery")) {
            AmphoraRegistrate.logModularRegistration("vinery", "fluids");
            Amphora.LOGGER.info("Vinery grape juice fluids will be conditionally registered via Registrate");
        } else {
            Amphora.LOGGER.info("Vinery not loaded - grape juice fluids will use legacy Fabric API");
        }
    }
}
