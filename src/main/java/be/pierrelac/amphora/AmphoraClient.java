package be.pierrelac.amphora;

import be.pierrelac.amphora.content.kinetics.juice_press.MechanicalJuicePressRenderer;
import be.pierrelac.amphora.content.core.ModCoreBlocks;
import be.pierrelac.amphora.content.core.ModCoreContent;
import be.pierrelac.amphora.content.core.ModCoreBlockEntities;
import be.pierrelac.amphora.content.vinery.ModVineryContent;
import be.pierrelac.amphora.client.ModPartials;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class AmphoraClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Amphora.LOGGER.info("Initializing Amphora client with pure Fabric fluid rendering");

        // Log de sanity pour confirmer le chargement
        System.out.println("[Amphora] AmphoraClient.onInitializeClient()");
        // Enregistrer les partials pour l'animation
        ModPartials.init();

        registerFluidRendering();
        registerBucketColors();
        registerBlockEntityRenderers();
        registerFlywheelVisuals(); // Ajouter l'enregistrement Flywheel
        registerBlockRenderLayers();

        Amphora.LOGGER.info("Create: Vinery client initialization complete");
    }

    private void registerFluidRendering() {
        Amphora.LOGGER.info("Registering fluid rendering for modern Registrate fluids");

        // Enregistrer les fluides Registrate avec textures communes et couleurs spécifiques
        registerRegistrateFluidRendering("apple_juice", ModCoreContent.APPLE_JUICE);
        registerRegistrateFluidRendering("cherry_juice", ModCoreContent.CHERRY_JUICE);
        
        // Enregistrer les fluides Vinery conditionnellement
        if (AmphoraRegistrate.isModLoaded("vinery")) {
            registerVineryFluidRendering();
        }

        Amphora.LOGGER.info("All juice fluid rendering registration complete via Registrate system");
    }
    
    private void registerRegistrateFluidRendering(String fluidId, 
            com.tterrag.registrate.util.entry.FluidEntry<? extends SimpleFlowableFluid> fluidEntry) {
        var color = AmphoraRegistrate.getRegistrateJuiceColor(fluidId);
        
        if (color != null) {
            // Utiliser les textures communes du jus de raisin
            var juiceStillTexture = new ResourceLocation("amphora", "fluid/grapejuice_still");
            var juiceFlowTexture = new ResourceLocation("amphora", "fluid/juice_flow");
            
            // Créer un renderer avec les textures communes et la couleur spécifique
            var handler = new SimpleFluidRenderHandler(juiceStillTexture, juiceFlowTexture, color);

            FluidRenderHandlerRegistry.INSTANCE.register(fluidEntry.get(), handler);
            FluidRenderHandlerRegistry.INSTANCE.register(fluidEntry.getSource(), handler);

            Amphora.LOGGER.info("✓ Registered Registrate juice rendering for {} with common textures and color 0x{}", 
                fluidId, Integer.toHexString(color));
        } else {
            Amphora.LOGGER.warn("✗ No color found for Registrate fluid {}", fluidId);
        }
    }
    
    private void registerVineryFluidRendering() {
        Amphora.LOGGER.info("Registering Vinery grape juice fluid rendering");
        
        // Fluides de raisin standards
        if (ModVineryContent.RED_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("red_grape_juice", ModVineryContent.RED_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("white_grape_juice", ModVineryContent.WHITE_GRAPE_JUICE);
        }
        
        // Fluides de raisin de savane
        if (ModVineryContent.RED_SAVANNA_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("red_savanna_grape_juice", ModVineryContent.RED_SAVANNA_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_SAVANNA_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("white_savanna_grape_juice", ModVineryContent.WHITE_SAVANNA_GRAPE_JUICE);
        }
        
        // Fluides de raisin de taïga
        if (ModVineryContent.RED_TAIGA_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("red_taiga_grape_juice", ModVineryContent.RED_TAIGA_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_TAIGA_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("white_taiga_grape_juice", ModVineryContent.WHITE_TAIGA_GRAPE_JUICE);
        }
        
        // Fluides de raisin de jungle
        if (ModVineryContent.RED_JUNGLE_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("red_jungle_grape_juice", ModVineryContent.RED_JUNGLE_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_JUNGLE_GRAPE_JUICE != null) {
            registerRegistrateFluidRendering("white_jungle_grape_juice", ModVineryContent.WHITE_JUNGLE_GRAPE_JUICE);
        }
        
        Amphora.LOGGER.info("Vinery grape juice fluid rendering registered");
    }
    
    private void registerVineryBucketColors() {
        Amphora.LOGGER.info("Registering Vinery grape juice bucket colors");
        
        // Fluides de raisin standards
        if (ModVineryContent.RED_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("red_grape_juice", ModVineryContent.RED_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("white_grape_juice", ModVineryContent.WHITE_GRAPE_JUICE);
        }
        
        // Fluides de raisin de savane
        if (ModVineryContent.RED_SAVANNA_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("red_savanna_grape_juice", ModVineryContent.RED_SAVANNA_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_SAVANNA_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("white_savanna_grape_juice", ModVineryContent.WHITE_SAVANNA_GRAPE_JUICE);
        }
        
        // Fluides de raisin de taïga
        if (ModVineryContent.RED_TAIGA_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("red_taiga_grape_juice", ModVineryContent.RED_TAIGA_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_TAIGA_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("white_taiga_grape_juice", ModVineryContent.WHITE_TAIGA_GRAPE_JUICE);
        }
        
        // Fluides de raisin de jungle
        if (ModVineryContent.RED_JUNGLE_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("red_jungle_grape_juice", ModVineryContent.RED_JUNGLE_GRAPE_JUICE);
        }
        if (ModVineryContent.WHITE_JUNGLE_GRAPE_JUICE != null) {
            registerRegistrateBucketColor("white_jungle_grape_juice", ModVineryContent.WHITE_JUNGLE_GRAPE_JUICE);
        }
        
        Amphora.LOGGER.info("Vinery grape juice bucket colors registered");
    }

    private void registerBucketColors() {
        Amphora.LOGGER.info("Registering bucket colors for modern Registrate fluids");

        // Enregistrer les couleurs pour les seaux Registrate
        registerRegistrateBucketColor("apple_juice", ModCoreContent.APPLE_JUICE);
        registerRegistrateBucketColor("cherry_juice", ModCoreContent.CHERRY_JUICE);
        
        // Enregistrer les seaux Vinery conditionnellement
        if (AmphoraRegistrate.isModLoaded("vinery")) {
            registerVineryBucketColors();
        }

        Amphora.LOGGER.info("All bucket color registration complete via Registrate system");
    }
    
    private void registerRegistrateBucketColor(String fluidId, 
            com.tterrag.registrate.util.entry.FluidEntry<? extends SimpleFlowableFluid> fluidEntry) {
        var color = AmphoraRegistrate.getRegistrateJuiceColor(fluidId);
        
        if (color != null) {
            var bucketItem = fluidEntry.getSource().getBucket();
            
            ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> tintIndex == 1 ? color : 0xFFFFFF,
                bucketItem
            );
            
            Amphora.LOGGER.info("✓ Registered Registrate bucket color for {} with color 0x{}", 
                fluidId, Integer.toHexString(color));
        } else {
            Amphora.LOGGER.warn("✗ No color found for Registrate bucket {}", fluidId);
        }
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ModCoreBlockEntities.MECHANICAL_JUICE_PRESS, MechanicalJuicePressRenderer::new);
        Amphora.LOGGER.info("Registered BlockEntity renderer for mechanical juice press");
    }

    private static void registerFlywheelVisuals() {
        // Temporairement désactivé pour débugger le rendu
        // com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry.configure(ModCoreBlockEntities.MECHANICAL_JUICE_PRESS)
        //     .factory(be.pierrelac.amphora.content.kinetics.juice_press.JuicePressVisual::new);

        Amphora.LOGGER.info("Flywheel visual registration DISABLED for debugging");
    }

    /**
     * Configure les RenderLayers pour les blocs avec transparence alpha
     */
    private static void registerBlockRenderLayers() {
        // Activer la transparence alpha pour le châssis de la presse à jus mécanique
        BlockRenderLayerMap.INSTANCE.putBlock(ModCoreBlocks.MECHANICAL_JUICE_PRESS.get(), RenderType.cutout());

        Amphora.LOGGER.info("Registered block render layers for transparency support");
    }
}
