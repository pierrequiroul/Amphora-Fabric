package be.pierrelac.create_vinery;

import be.pierrelac.create_vinery.items.JuiceBucketItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class CreateVineryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CreateVinery.LOGGER.info("Initializing Create: Vinery client with pure Fabric fluid rendering");
        
        registerFluidRendering();
        registerBucketColors();
        
        CreateVinery.LOGGER.info("Create: Vinery client initialization complete");
    }
    
    private void registerFluidRendering() {
        CreateVinery.LOGGER.info("Registering Fabric fluid rendering for {} juice types", ModFluids.STILL_FLUIDS.size());
        
        // Utiliser nos textures de jus personnalisées avec le bon chemin
        var juiceStillTexture = new ResourceLocation("create_vinery", "fluid/juice_still");
        var juiceFlowTexture = new ResourceLocation("create_vinery", "fluid/juice_flow");
        
        // Enregistrer les renderers pour chaque jus
        ModFluids.STILL_FLUIDS.forEach((juiceId, stillFluid) -> {
            var flowingFluid = ModFluids.FLOWING_FLUIDS.get(juiceId);
            var color = ModFluids.JUICE_COLORS.get(juiceId);
            
            if (flowingFluid != null && color != null) {
                // Créer un renderer avec nos textures de jus et coloration spécifique
                var handler = new SimpleFluidRenderHandler(juiceStillTexture, juiceFlowTexture, color);
                
                FluidRenderHandlerRegistry.INSTANCE.register(stillFluid, handler);
                FluidRenderHandlerRegistry.INSTANCE.register(flowingFluid, handler);
                
                CreateVinery.LOGGER.info("✓ Registered juice rendering for {} with color 0x{}", juiceId, Integer.toHexString(color));
            } else {
                CreateVinery.LOGGER.warn("✗ Missing data for fluid {}: flowing={}, color={}", juiceId, flowingFluid, color);
            }
        });
        
        CreateVinery.LOGGER.info("Juice fluid rendering registration complete");
    }
    
    private void registerBucketColors() {
        CreateVinery.LOGGER.info("Registering bucket colors for {} juice types", ModFluids.STILL_FLUIDS.size());
        
        // Enregistrer les couleurs pour chaque seau de jus
        ModFluids.STILL_FLUIDS.forEach((juiceId, stillFluid) -> {
            var bucketItem = stillFluid.getBucket();
            var color = ModFluids.JUICE_COLORS.get(juiceId);
            
            if (bucketItem instanceof JuiceBucketItem && color != null) {
                ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> tintIndex == 1 ? color : 0xFFFFFF,
                    bucketItem
                );
                CreateVinery.LOGGER.info("✓ Registered bucket color for {} with color 0x{}", juiceId, Integer.toHexString(color));
            } else {
                CreateVinery.LOGGER.warn("✗ Could not register bucket color for {}: bucket={}, color={}", juiceId, bucketItem.getClass().getSimpleName(), color);
            }
        });
        
        CreateVinery.LOGGER.info("Bucket color registration complete");
    }
}
