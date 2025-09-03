package be.pierrelac.amphora;

import be.pierrelac.amphora.content.kinetics.juice_press.MechanicalJuicePressRenderer;
import be.pierrelac.amphora.content.fluids.juice.JuiceBucketItem;
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
        Amphora.LOGGER.info("Registering Fabric fluid rendering for {} juice types", ModFluids.STILL_FLUIDS.size());

        // Enregistrer les renderers pour chaque jus avec leurs textures spécifiques
        ModFluids.STILL_FLUIDS.forEach((juiceId, stillFluid) -> {
            var flowingFluid = ModFluids.FLOWING_FLUIDS.get(juiceId);
            var color = ModFluids.JUICE_COLORS.get(juiceId);
            var stillTexturePath = ModFluids.getStillTexture(juiceId);
            var flowTexturePath = ModFluids.getFlowTexture(juiceId);

            if (flowingFluid != null && color != null && stillTexturePath != null && flowTexturePath != null) {
                // Créer les ResourceLocations pour les textures spécifiques à ce fluide
                var juiceStillTexture = new ResourceLocation("amphora", stillTexturePath);
                var juiceFlowTexture = new ResourceLocation("amphora", flowTexturePath);
                
                // Créer un renderer avec les textures spécifiques et coloration
                var handler = new SimpleFluidRenderHandler(juiceStillTexture, juiceFlowTexture, color);

                FluidRenderHandlerRegistry.INSTANCE.register(stillFluid, handler);
                FluidRenderHandlerRegistry.INSTANCE.register(flowingFluid, handler);

                Amphora.LOGGER.info("✓ Registered juice rendering for {} with textures [still: {}, flow: {}] and color 0x{}", 
                    juiceId, stillTexturePath, flowTexturePath, Integer.toHexString(color));
            } else {
                Amphora.LOGGER.warn("✗ Missing data for fluid {}: flowing={}, color={}, stillTexture={}, flowTexture={}", 
                    juiceId, flowingFluid, color, stillTexturePath, flowTexturePath);
            }
        });

        Amphora.LOGGER.info("Juice fluid rendering registration complete");
    }

    private void registerBucketColors() {
        Amphora.LOGGER.info("Registering bucket colors for {} juice types", ModFluids.STILL_FLUIDS.size());

        // Enregistrer les couleurs pour chaque seau de jus
        ModFluids.STILL_FLUIDS.forEach((juiceId, stillFluid) -> {
            var bucketItem = stillFluid.getBucket();
            var color = ModFluids.JUICE_COLORS.get(juiceId);

            if (bucketItem instanceof JuiceBucketItem && color != null) {
                ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> tintIndex == 1 ? color : 0xFFFFFF,
                    bucketItem
                );
                Amphora.LOGGER.info("✓ Registered bucket color for {} with color 0x{}", juiceId, Integer.toHexString(color));
            } else {
                Amphora.LOGGER.warn("✗ Could not register bucket color for {}: bucket={}, color={}", juiceId, bucketItem.getClass().getSimpleName(), color);
            }
        });

        Amphora.LOGGER.info("Bucket color registration complete");
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ModBlockEntities.MECHANICAL_JUICE_PRESS, MechanicalJuicePressRenderer::new);
        Amphora.LOGGER.info("Registered BlockEntity renderer for mechanical juice press");
    }

    private static void registerFlywheelVisuals() {
        // Temporairement désactivé pour débugger le rendu
        // com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry.configure(ModBlockEntities.MECHANICAL_JUICE_PRESS)
        //     .factory(be.pierrelac.amphora.content.kinetics.juice_press.JuicePressVisual::new);

        Amphora.LOGGER.info("Flywheel visual registration DISABLED for debugging");
    }

    /**
     * Configure les RenderLayers pour les blocs avec transparence alpha
     */
    private static void registerBlockRenderLayers() {
        // Activer la transparence alpha pour le châssis de la presse à jus mécanique
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MECHANICAL_JUICE_PRESS, RenderType.cutout());

        Amphora.LOGGER.info("Registered block render layers for transparency support");
    }
}
